package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DiceServer extends Application{


	private NetworkConnection  conn;
	private TextArea messages = new TextArea();
	
	private Parent createContent() {
		messages.setPrefHeight(600);
		
		VBox center = new VBox(messages);
		
		Button sOn = new Button("Start Server");
		
		TextField portNum = new TextField("5555");
		
		sOn.setOnAction(event ->{
			int portNumber=Integer.parseInt(portNum.getText());
			conn=createServer(portNumber);
			messages.appendText("Connected to port " + portNumber +"\n");
			try {
				conn.startConn();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			portNum.clear();
		});

		Text warn = new Text("Create Server before connecting Clients!");
		Region r = new Region();
		r.setPrefSize(90, 90);
		HBox t = new HBox(portNum,sOn,r,warn);
		t.setPrefSize(736, 27);
		t.setPadding(new Insets(8,8,8,8));

		VBox top = new VBox(t);
		top.setPrefSize(752, 43);
		Text right = new Text("");
		Text left = new Text("");
		Text bottom = new Text("");
		
		center.setAlignment(Pos.CENTER);
		top.setAlignment(Pos.TOP_LEFT);
		center.setPadding(new Insets(8,8,0,8));
		top.setPadding(new Insets(5,5,5,5));
		
		BorderPane root = new BorderPane(center,top,right,bottom,left);	
		root.setPrefSize(630, 350);
		return root;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setScene(new Scene(createContent()));
		primaryStage.setTitle(("Server"));
		primaryStage.show();
		
	}

	
	@Override
	public void stop() throws Exception{
		conn.closeConn();
	}
	
	private Server createServer(int portNumber) {
		return new Server(portNumber, data-> {
			Platform.runLater(()->{
				messages.appendText(data.toString() + "\n");
			});
		});
	}
	


}
