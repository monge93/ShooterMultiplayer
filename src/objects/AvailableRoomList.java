package objects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import object_managers.ClientManager;
import object_managers.PlayerListManager;

import stages.GameStage;
import stages.StartMenu;

import beta1.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class AvailableRoomList extends Window{
	
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://sql2.freesqldatabase.com/sql29927";
	private static final String USER = "sql29927";
	private static final String PASS = "tI7*kA6*";
	
	private Connection con;
	private Statement stmt;
	
	Array<RoomObject> roomList;
	Skin skin;
	TextButton refreshButton;
	//Hashtable<String, RoomObject> roomList;
	StartMenu startMenu;
	GameStage gameStage;
	
	String ip;
	
	public AvailableRoomList(TextButton refreshb) {
		// TODO Auto-generated constructor stub
		super("Available Rooms", new Skin(Gdx.files.internal("assets/data/uiskin.json")));
		setSize(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight());
		skin = new Skin(Gdx.files.internal("assets/data/uiskin.json"));
		refreshButton = refreshb;
		
		roomList = new Array<RoomObject>();
		//roomList = new Hashtable<String, RoomObject>();
		con = null;
		stmt = null;
		
		try{
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = con.createStatement();			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void AddServer(String ServerName, String address){
		try{
			stmt.executeUpdate("insert into serverlist(roomname,address) values ('"+ServerName+"','"+address+"')");
		}
		catch(SQLException e){ System.out.print(e+"\n"); }
	}
	public void removeServer(String ip){
		try{
			stmt.executeUpdate("delete from serverlist where address='"+ip+"'");}
	catch(SQLException e){ System.out.print(e+"\n"); }
	}
	public ResultSet getServerList(){
		
		try{
			ResultSet res = stmt.executeQuery("select roomname,address from serverlist");
			return res;	
		}catch(SQLException e){
			System.out.print(e+"\n");
		}
		return null;
	}
	
	public void refreshList(){
		ResultSet set = getServerList();
		roomList.clear();
		clear();
		try{
			while(set.next()){
				System.out.print(set.getString(1) + "-" + set.getString(2)+"\n" );
				roomList.add(new RoomObject(set.getString(1), set.getString(2)));
				//roomList.put(set.getString(1), new RoomObject(set.getString(1), set.getString(2)));
				
			}
			top();
			for(int i=0; i<roomList.size; i++){
				//add(roomList.get(i));
				add(new Label(roomList.get(i).getRoomName(), skin));
				TextButton joinButton = new TextButton("Join", skin);
				joinButton.addListener( new JoinListener(i) );
				add(joinButton);
				row();
			}
			bottom();
			add(refreshButton);
		}catch(SQLException e){ System.out.print(e+"\n"); }
	}
	public void setStartMenu(StartMenu s){ startMenu = s; }
	public void setGameStage(GameStage s){ gameStage = s; }
	public void setIP(String ip){
		this.ip = ip;
	}
	public String getIP(){ return ip; }
	
	class JoinListener extends ChangeListener{
		
		int idx;
		JoinListener(int roomIdx){ idx = roomIdx; }
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			// TODO Auto-generated method stub
			String nick = startMenu.getNickname();
			if(nick.isEmpty()) nick = "Player "+(int)(Math.random()*1000);
			PlayerListManager pl = new PlayerListManager();
			gameStage.setPlayerList(pl);
			ClientManager cm = new ClientManager(pl, roomList.get(idx).getIP(), 59111, gameStage);
			startMenu.setActive(false);
			gameStage.setActive(true);
			gameStage.setClientManager(cm);
			ClientManager.setNickname(nick);
			Chat chat = new Chat(skin, nick, cm);
			chat.setGameStage(gameStage);
			cm.setChat(chat);
			gameStage.setChat(chat);
			new Thread(cm).start();
			Game.setRunningTime(0);
		}
		
	}
}
