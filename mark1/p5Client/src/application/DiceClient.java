package application;

import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DiceClient extends Application{
	
	private NetworkConnection  conn;
	private TextArea messages = new TextArea();
	
    Random rand = new Random();
    
	private Parent createContent() {
		
		VBox center = new VBox(messages);
		messages.setPrefHeight(600);
		Image imageDecline = new Image(getClass().getResourceAsStream("dice.png"),70, 100, true, true);
		Button dice = new Button();
		dice.setGraphic(new ImageView(imageDecline));

		Button connect = new Button("Connect");
		TextField portNum = new TextField("5555");
		TextField ipAddress = new TextField("127.0.0.1");
		
		Region r = new Region();
		r.setPrefWidth(100);
		Region x = new Region();
		x.setPrefWidth(100);
		Text message = new Text("Dice Roll Game");
		HBox t = new HBox(portNum, ipAddress, connect,x,message,r,dice);
		
		center.setPadding(new Insets(8,8,0,8));
		t.setPadding(new Insets(8,8,8,8));
		VBox top = new VBox(t);

		dice.setOnAction(event ->{
			int ran = generateRandom();
			try {
				conn.send(ran);
			}
			catch(Exception e) {
				
			}
		});
		
		connect.setOnAction(event ->{
			int portNumber=Integer.parseInt(portNum.getText());
			String ip =ipAddress.getText();
			conn=createClient(portNumber,ip);
			try {
				conn.startConn();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		
		Text right = new Text();
		Text left = new Text();
		Text bottom = new Text();
		BorderPane root = new BorderPane(center,top,right,bottom,left);
		return root;
	
	}
	public int generateRandom() {
		return rand.nextInt((6 - 1) + 1) + 1;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setScene(new Scene(createContent(),800,505));
		primaryStage.show();
		
	}
	

	@Override
	public void stop() throws Exception{
		conn.closeConn();
	}
	

	
	private Client createClient(int portNumber, String ipAddress) {
		return new Client(ipAddress, portNumber, data -> {
			Platform.runLater(()->{
				messages.appendText(data.toString() + "\n");
			});
		});
	}

}
