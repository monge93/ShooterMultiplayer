package object_managers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import objects.Chat;
import objects.Player;
import stages.GameStage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class ClientManager implements Runnable {
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private Player player;
	private GameStage stg;
	private PlayerListManager playerList;
	
	private String serverIP;
	private int serverPort;
	
	private Texture playerTex;
	private Chat chat;
	static String nickname;
	public static void setNickname(String arg0){ nickname = arg0; }
	
	private static long pacotesRecebidos;
	private static long pacotesEnviados;
	static double bytesRecebidos;
	static double bytesEnviados;
	
	public ClientManager( PlayerListManager pm, String serverIP, int serverPort, GameStage gameStage ){
		pacotesEnviados = 0;
		pacotesRecebidos = 0;
		bytesRecebidos = 0;
		bytesEnviados = 0;
		playerTex  = new Texture(Gdx.files.internal("assets/sprites/mercenary.png"));
		stg = gameStage;
		playerList = pm;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		try{
			//serverAddress = InetAddress.getByName(serverIP);
			socket = new DatagramSocket();
			byte[] buf = new byte[1000];
			packet = new DatagramPacket(buf, buf.length);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
//		this.serverIP = "localhost";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		String bs = new String("0≈ip≈"+nickname);
		byte[] buf = bs.getBytes();
		
		try{
			System.out.println(serverIP + " : "+ serverPort);
			DatagramPacket out = new DatagramPacket(buf, buf.length, InetAddress.getByName(serverIP), serverPort );
			socket.send(out);
			pacotesEnviados+=1;
			bytesEnviados+=buf.length;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		while(true){
			try{
				//System.out.println("client waiting for packet");
				socket.receive(packet);
				pacotesRecebidos+=1;
				bytesRecebidos+=packet.getData().length;
				parsePacket( packet );
				//System.out.println("pck recebido");
				
			}catch (Exception e) {
				// TODO: handle exception
				//System.out.println("client-"+e);
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public void sendPacket(String data){
		
		if(player.isActive()){
			byte[] buf = data.getBytes();
			try{
				DatagramPacket out = new DatagramPacket(buf, buf.length, InetAddress.getByName(serverIP), serverPort );
				socket.send(out);
				pacotesEnviados+=1;
				bytesEnviados+=buf.length;
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("server_parse-"+e);
			}
		}
	}
	public void sendStateToServer(){
		if(player!=null && player.isActive()){
			byte[] buf = player.getState().getBytes();
			try{
				DatagramPacket out = new DatagramPacket
						(buf, buf.length, InetAddress.getByName(serverIP),serverPort);
				socket.send(out);
				bytesEnviados+=buf.length;
				pacotesEnviados+=1;
				if(player.getLife()<=0) player.setActive(false);
			}catch(Exception e){ e.printStackTrace(); }
		}
	}
	public boolean isConnected(){ return socket.isConnected(); }
	
	void parsePacket(DatagramPacket pkt){
		String data = new String(packet.getData(), 0, packet.getLength() );
		String delim = "[≈]+";
		String[] params = data.split(delim);
		
		int type = Integer.parseInt(params[0]);
		if(type==1){ //confirmaÁ„o de join
			
			//System.out.println("client recebeu pacote 1-id="+params[1]);
			
			int id = Integer.parseInt(params[1]);
			//playerList.addPlayer("", 0);
			//playerList.getPlayer(0).setId(id);
			for(int i=0; i<id+1;i++){
				if(i!=id)
					playerList.addPlayer("", 0);
				else
					playerList.addPlayer("", 0,playerTex);
				playerList.getPlayer(i).setPositionById();			
				stg.addActor(playerList.getPlayer(i));
			}
			for(int i=0; i<playerList.getSize()-1 ; i++){
				try{
					if(Integer.parseInt(params[i+2]) == 1)
						playerList.getPlayer(i).setActive(false);
				}catch (Exception e) {
					//e.printStackTrace();
				}
				playerList.getPlayer(i).toBack();
				playerList.getPlayer(i).setNickname(params[i+2]);
			}
			player = playerList.getPlayerById(id);
			playerList.getPlayer(id).toBack();
			stg.setPlayer(player);
		}
		else if(type==2){ //atualizar movimento de um player
			//System.out.println("client recebeu pack tipo2");
			
			Player t = playerList.getPlayerById(Integer.parseInt(params[1]));
			boolean moving=false;
			boolean shooting=false;
			
			if(Integer.parseInt(params[2]) == 1)
				moving = true;
			if(Integer.parseInt(params[2]) == 2)
				shooting = true;
			if(Integer.parseInt(params[2]) == 3)
				shooting = false;
			
			if(Integer.parseInt(params[2]) == 1 || Integer.parseInt(params[2]) == 0){
				int dir = Integer.parseInt(params[3]);
				if(dir == 4) t.moveLeft(moving);
				else if(dir == 6) t.moveRight(moving);
				else if(dir ==2)	t.moveDown(moving);
				else if (dir==8)	t.moveUp(moving);
			}
			else
				t.setShootin(shooting);
		}
		else if(type==3){//new player entrou
			playerList.addPlayer("", 0);
			stg.addActor(playerList.getPlayer(playerList.getSize()-1));
			playerList.getPlayer(playerList.getSize()-1).setNickname(params[2]);
			playerList.getPlayer(playerList.getSize()-1).toBack();
		}
		else if(type == 4){
			//System.out.println("client recebeu: "+data);
			chat.addMessage(params[1]);
		}
		else if (type==6){
			Player p = playerList.getPlayerById( Integer.parseInt(params[1]) );
			System.out.println(Chat.getNickname()+" "+ p.getId());
			p.setActive(false);
			//playerList.removePlayer(p);
		}
		else if(type == 9){
			int id = Integer.parseInt(params[1]);
			//System.out.println("client "+player.getId()+" recebeu pack 9 (update player "+id+")");
			boolean movLeft = false;
			boolean movRight = false;
			boolean movDown = false;
			boolean movUp = false;
			
			if( Character.getNumericValue(params[3].charAt(0))  == 1 )
				movLeft = true;
			if( Character.getNumericValue(params[3].charAt(1))  == 1 )
				movRight = true;
			if( Character.getNumericValue(params[3].charAt(2))  == 1 )
				movDown = true;
			if( Character.getNumericValue(params[3].charAt(3))  == 1 )
				movUp = true;
			
			boolean shooting = false;
			if(Integer.parseInt(params[4]) == 1 )
				shooting = true;
			
			Player p = playerList.getPlayerById(id);
			//p.setDirection(movLeft, movRight, movDown, movUp);
			if(p!=null){
				p.moveLeft(movLeft);
				p.moveRight(movRight);
				p.moveDown(movDown);
				p.moveUp(movUp);
				p.setPosition(Float.parseFloat(params[5]), Float.parseFloat(params[6]));
				p.setShootin(shooting);
			}
		}
	}
	public void setChat(Chat c){ chat = c; }
	public static long getPacotesRecebido(){ return pacotesRecebidos; }
	public static long getPacotesEnviados(){ return pacotesEnviados; }
	public static double getBytesEnviados(){ return bytesEnviados; }
	public static double getBytesRecebidos(){ return bytesRecebidos; }
	public void deactivatePlayer(){
		if(player!=null){
			sendPacket(new String("6≈"+player.getId()));
			player.setActive(false);
		}
	}
}
