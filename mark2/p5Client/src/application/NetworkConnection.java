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
    ArrayList<ClientThread> ct;

	public NetworkConnection(Consumer<Serializable> callback) {
		this.callback = callback;
		connthread.setDaemon(true);
        ct = new ArrayList<ClientThread>();

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
                        ClientThread t1 = new ClientThread(server.accept(),number);
                        number++;
                        ct.add(t1);
                        t1.start();
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
        int number;
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
                tout.writeObject("Welcome Player: " + number);
                while(true){
                    Serializable data = (Serializable) in.readObject();
                    
                }
                
            }
            catch(Exception e){
                callback.accept("Connection Closed");
            }
        }
	
    }
}

