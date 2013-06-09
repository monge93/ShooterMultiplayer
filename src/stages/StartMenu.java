package stages;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import object_managers.PlayerListManager;
import object_managers.ServerManager;
import objects.AvailableRoomList;
import objects.Chat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class StartMenu extends Stage{
		
	TextField nickTextField;
	TextField roomNameField;
	Window createGameWindow;
	TextButton createGameButton;
	
	AvailableRoomList findGameWindow;
	TextButton refreshButton;
	
	GameStage gameStage;
	
	PlayerListManager playerList;
	
	ServerManager server;
	Chat chat;
	boolean active;
	public StartMenu(GameStage stg){
		gameStage = stg;
		active = false;
		Gdx.input.setInputProcessor(this);
		Skin skin = new Skin(Gdx.files.internal("assets/data/uiskin.json"));
		
		nickTextField = new TextField("", skin);
		nickTextField.setMessageText("Nickname");
		nickTextField.setPosition(Gdx.graphics.getWidth()/2 - nickTextField.getWidth()/2, Gdx.graphics.getHeight() - nickTextField.getHeight());
		roomNameField = new TextField("",skin);
		roomNameField.setMessageText("Room Name");

		createGameButton = new TextButton("CreateGame", skin);
		
		createGameWindow = new Window("Host Game",skin);
		createGameWindow.add(roomNameField);
		createGameWindow.row();
		createGameWindow.add(createGameButton);
		//createGameWindow.setPosition(Gdx.graphics.getWidth()/2- createGameWindow.getWidth()/2, Gdx.graphics.getHeight()/2 - createGameWindow.getHeight()/2);
		
		refreshButton = new TextButton("Refresh", skin);
		findGameWindow = new AvailableRoomList(refreshButton);
		findGameWindow.setStartMenu(this);
		findGameWindow.setGameStage(gameStage);
		
		findGameWindow.setPosition(Gdx.graphics.getWidth() - findGameWindow.getWidth(), Gdx.graphics.getHeight() - findGameWindow.getHeight());
		refreshButton.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				findGameWindow.refreshList();
			}
		});
		
		findGameWindow.add(refreshButton);
		addActor(nickTextField);
		addActor(createGameWindow);
		addActor(findGameWindow);
		
		createGameButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
		        String ip="";
			    try {  
			        URL url = new URL("http://www.myip.com.br/index2.php");  
			        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();  
			        conexao.connect();  
			        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conexao.getInputStream()));  
			        String title = "";  
			        while(br.ready())  {
			            title += br.readLine();
			        }			        	        
			        br.close();
			        Pattern pattern = Pattern.compile("[0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?[.][0-9][0-9]?[0-9]?");
			        
			        Matcher m = pattern.matcher(title);
			        if(m.find())
			        	ip = m.group(0);
			        
			    } catch (Exception e) {  
			        e.printStackTrace();  
			    } 
			    
			    if(!ip.isEmpty()){
			    	if(roomNameField.getText().isEmpty())
			    		roomNameField.setText("Default");
			    	
		        	findGameWindow.AddServer(roomNameField.getText(), ip);
		        	findGameWindow.setIP(ip);		 
		        	Skin skin = new Skin(Gdx.files.internal("assets/data/uiskin.json"));
		        	chat = new Chat( skin , "Server", null);
		        	gameStage.setChat(chat);
		        	setActive(false);
		        	playerList = new PlayerListManager();
		        	server = new ServerManager(playerList, 59111, gameStage);
		        	server.setChat(chat);
		        	chat.setActivated(true);
		        	chat.removeTextField();
		        	chat.setMovable(true);
		        	new Thread(server).start();
		        	gameStage.setActive(true);
		        	//gameStage.setPlayerList(playerList);
		        	//gameStage.setActive(true);
			    }
			}
		});
	}
	public String getNickname(){ return nickTextField.getText(); }
	public boolean isActive(){	return active;	}
	public void setActive(boolean arg0){	active = arg0; if(arg0) Gdx.input.setInputProcessor(this);	}
	public void setGameStage(GameStage s){ gameStage = s; }
	public ServerManager getServerManager(){ return server; }
	public AvailableRoomList getDatabaseManager(){ return findGameWindow; }
	
	public void reset(){
		
	}
}
