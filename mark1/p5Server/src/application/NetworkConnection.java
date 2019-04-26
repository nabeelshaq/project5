package application;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;

import javafx.scene.control.TextArea;

public abstract class NetworkConnection {
	
	private ConnThread connthread = new ConnThread();
	private Consumer<Serializable> callback;
	
    ArrayList<ClientThread> clientThreads;
    ArrayList<String> clientData = new ArrayList<String>();
    
    String winner;
    
    int p1=0, p2=0, p3=0, p4=0;
    int p1Points=0, p2Points=0, p3Points=0, p4Points=0;

    
	public NetworkConnection(Consumer<Serializable> callback) {
		this.callback = callback;
		connthread.setDaemon(true);
        clientThreads = new ArrayList<ClientThread>();
	}
	
	public void startConn() throws Exception{
		connthread.start();
	}
	

	
	public void send(Serializable data) throws Exception{
		connthread.out.writeObject(data);
	}
	
	public void closeConn() throws Exception{
		connthread.socket.close();
	}
    
    
	abstract protected boolean isServer();
	abstract protected String getIP();
	abstract protected int getPort();
	
	class ConnThread extends Thread{
		private Socket socket;
		private ObjectOutputStream out;
		
		public void run() {
            int number =1;
            if(isServer()){
                try{
                    ServerSocket server = new ServerSocket(getPort());
                    while(true){
                        ClientThread thread = new ClientThread(server.accept(),number);
                        number++;
                        clientThreads.add(thread);
                        thread.start();
                        
                    }
                    
                }
                catch(Exception e){
                    
                }
            }
            
            else{
                try{
                    Socket socket = new Socket(getIP(), getPort());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    
                    this.socket =socket;
                    this.out =out;
                    socket.setTcpNoDelay(true);
                    
                    while(true){
                        Serializable data = (Serializable) in.readObject();
                        callback.accept(data);
                    }
                }
                catch(Exception e) {
                	
                }
            }
            
		}// close run
	}// close connthread
                       
    class ClientThread extends Thread{
        Socket s;
        Integer number;
        ObjectOutputStream tout;
        ObjectInputStream tin;
        
        
        ClientThread(Socket socket, int num){
            this.s = socket;
            this.number = num;
        
        }
        
      
        
        public void run(){
            try(
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(s.getInputStream())){
                s.setTcpNoDelay(true);
                this.tout = out;
                this.tin = in;
                
                callback.accept("Player " + number + " connected");
                tout.writeObject("Welcome to the dice roll game player " + number);
                
                clientThreads.forEach(t ->{ 
                	try{t.tout.writeObject("# of Clients connected: " +number); }
                catch(Exception e) {e.printStackTrace();}
                });  
                
                if (number == 4) {
                	clientThreads.forEach(t ->{ 
                    	try{t.tout.writeObject("4 Players connected to the server, roll dice to begin"); }
                    catch(Exception e) {e.printStackTrace();}
                    });  
                }

                while(true){
           	
                    Serializable data = (Serializable) in.readObject();
                    
                    if(number == 1){
                        p1 = (int) data;
                    }
                    else if (number == 2){
                        p2 = (int) data;    
                    }
                    else if (number == 3){
                        p3 = (int) data;      
                    }
                    else if (number == 4){
                        p4 = (int) data;          
                    }
                    

                    callback.accept("Player " + number + " rolled a " + data);
                    clientThreads.forEach(t ->{ 
                    	try{t.tout.writeObject("Player " +number +" rolled a " + data); }
                    catch(Exception e) {e.printStackTrace();}
                    });  

                    if (p1>0 && p2>0 && p3>0 && p4>0) { // if all the players have rolled, check for the winner 
	                    if ( p1 > p2 && p1 > p3 && p1 > p4 ) {
	                    	winner = "Player 1";
	                    	p1Points++;
	                    }
	                    else if ( p2 > p1 && p2 > p3 && p2 > p4 ) {
	                    	winner = "Player 2";
	                    	p2Points++;
	                    }
	                    else if ( p3 > p1 && p3 > p2 && p3 > p4 ) {
	                    	winner = "Player 3";
	                    	p3Points++;
	                    }
	                    else if ( p4 > p1 && p4 > p2 && p4 > p3 ) {
	                    	winner = "Player 4";
	                    	p4Points++;
	                    }
	                    else if (p1==p2 && p2==p3 && p3==p4){
	                    	winner = "Draw, nobody";
	                    }
	                    clientThreads.forEach(t ->{ 
	                    	try{t.tout.writeObject(winner + " is the winner \n"
	                    			+ "Player 1 Points: " +p1Points + "\n"
	                    			+ "Player 2 Points: " +p2Points + "\n"   
	                    			+ "Player 3 Points: " +p3Points + "\n"   
	                    			+ "Player 4 Points: " +p4Points + "\n"  
	                    			+"New game started" ); }
	                    catch(Exception e) {e.printStackTrace();}
	                    });
	                    p1 =0;  // reset the players numbers after a winner is found
	                    p2 =0;
	                    p3 =0;
	                    p4 =0;
                    }
                    
                    
                }
                
            }
            catch(Exception e){
                callback.accept("Player " + number + " disconnected");
                
            }
            
        }
        
	
    }
}

