package stages;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import object_managers.ClientManager;
import object_managers.PlayerListManager;
import object_managers.ServerManager;
import objects.AvailableRoomList;
import objects.Chat;
import beta1.Game;

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
	Window createLocalGameWindow;
	TextButton createLocalButton;
	Window joinGameWindow;
	TextField ipField;
	TextButton joinButton;
	
	AvailableRoomList findGameWindow;
	TextButton refreshButton;
	
	GameStage gameStage;
	
	PlayerListManager playerList;
	
	ServerManager server;
	Chat chat;
	boolean active;
	
	static boolean onlineMode;
	
	public StartMenu(GameStage stg){
		onlineMode = false;
		//onlineMode = true;
		gameStage = stg;
		active = false;
		Gdx.input.setInputProcessor(this);
		Skin skin = new Skin(Gdx.files.internal("assets/data/uiskin.json"));
		
		joinGameWindow = new  Window("Join Game", skin);
		ipField = new TextField("", skin);
		ipField.setMessageText("IP");
		//ipField.setText("localhost");
		joinButton = new TextButton("Join", skin);
		joinGameWindow.setX(joinGameWindow.getWidth());
		//joinGameWindow.setY(0);
		
		joinGameWindow.add(ipField);
		joinGameWindow.row();
		joinGameWindow.add(joinButton);
		
		nickTextField = new TextField("", skin);
		nickTextField.setMessageText("Nickname");
		nickTextField.setPosition(Gdx.graphics.getWidth()/2 - nickTextField.getWidth()/2, Gdx.graphics.getHeight() - nickTextField.getHeight());
		
		
		
		addActor(nickTextField);
		addActor(joinGameWindow);
		
		if(onlineMode){
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
			findGameWindow.add(refreshButton);
			refreshButton.addListener(new ChangeListener(){
	
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// TODO Auto-generated method stub
					findGameWindow.refreshList();
				}
			});
			addActor(createGameWindow);
			addActor(findGameWindow);
			createGameButton.addListener(new createGameButtonListener());
		}
		else{
			createLocalGameWindow = new Window("CreateGame", skin);
			createLocalButton = new TextButton("Create", skin);
			
			createLocalGameWindow.add(createLocalButton);
			addActor(createLocalGameWindow);
			
			createLocalButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// TODO Auto-generated method stub
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
				}
				
			});
			joinButton.addListener(new ChangeListener() {
				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// TODO Auto-generated method stub
					String nick = getNickname();
					String ip = "localhost";
					if(nick.isEmpty()) nick = "Player "+(int)(Math.random()*1000);
					if(!ipField.getText().isEmpty()) ip = ipField.getText();
					PlayerListManager pl = new PlayerListManager();
					gameStage.setPlayerList(pl);
					ClientManager cm = new ClientManager(pl, ip, 59111, gameStage);
					setActive(false);
					gameStage.setActive(true);
					gameStage.setClientManager(cm);
					ClientManager.setNickname(nick);
					Skin skin = new Skin(Gdx.files.internal("assets/data/uiskin.json"));
					Chat chat = new Chat(skin, nick, cm);
					chat.setGameStage(gameStage);
					cm.setChat(chat);
					gameStage.setChat(chat);
					new Thread(cm).start();
					Game.setRunningTime(0);
				}
			});
		}
		
		
	}
	public String getNickname(){ return nickTextField.getText(); }
	public boolean isActive(){	return active;	}
	public void setActive(boolean arg0){	active = arg0; if(arg0) Gdx.input.setInputProcessor(this);	}
	public void setGameStage(GameStage s){ gameStage = s; }
	public ServerManager getServerManager(){ return server; }
	public AvailableRoomList getDatabaseManager(){ return findGameWindow; }
	
	public void reset(){
		
	}
	class createGameButtonListener extends ChangeListener{

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			// TODO Auto-generated method stub
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
		    }
		}
		
	}
	public static boolean isOnline(){ return onlineMode; }
}

