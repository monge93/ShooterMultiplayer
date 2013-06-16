package object_managers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import beta1.Game;

import objects.Chat;
import objects.Player;
import stages.GameStage;


public class ServerManager implements Runnable{
	private PlayerListManager playerList;
	private DatagramSocket socket;
	private DatagramPacket packet;
	private GameStage stg;
	private Chat chat;
	static long pacotesRecebidos;
	static long pacotesEnviados;
	static double bytesRecebidos;
	static double bytesEnviados;
	public ServerManager( PlayerListManager pm, int port, GameStage stg){
		pacotesRecebidos = 0;
		pacotesEnviados = 0;
		bytesRecebidos = 0;
		bytesEnviados = 0;
		this.stg = stg;
		playerList = pm;
		
		try{
			socket = new DatagramSocket(port);
			//System.out.println("listening port "+ port);
			byte[] buf = new byte[1000];
			packet = new DatagramPacket(buf, buf.length);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("server-"+e);
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true){
			try{
				//System.out.println("waiting for packet");////
				socket.receive(packet);
				pacotesRecebidos+=1;
				bytesRecebidos+=packet.getData().length;
				parsePacket( packet );
				
			}catch (Exception e) {
				// TODO: handle exception
				//System.out.println("server while-"+e);
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	void parsePacket(DatagramPacket pkt){
		
		String data = new String(packet.getData(), 0, packet.getLength() );
		String delim = "[Å]+";
		String[] params = data.split(delim);
		
		int type = Integer.parseInt(params[0]);
		if(type == 0){
			if(playerList.getSize() == 0) 
	        	Game.setRunningTime(0);
			//System.out.println("server recebeu pacote 0");
			//stg.addActor(playerList.getPlayer(playerList.getSize()-1));
			String bufStr = (new String("1Å"+ (playerList.getSize()) ));
			
			for(int i=0; i<playerList.getSize(); i++){
				if(playerList.getPlayer(i).isActive())
					bufStr += "Å"+ playerList.getPlayer(i).getNickname();
				else bufStr += "Å"+ "1";
			}
			byte[] buf = bufStr.getBytes();
			try{
				DatagramPacket out = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort() );
				socket.send(out);
				bytesEnviados+=buf.length;
				pacotesEnviados+=1;
				buf = (new String("3Å"+playerList.getSize()+"Å"+params[2] ) ).getBytes();
				/*for(int i=0;i <playerList.getSize(); i++){
					out = new DatagramPacket(buf, buf.length, 
							InetAddress.getByName( playerList.getPlayer(i).getIP() ), playerList.getPlayer(i).getPort() );
					socket.send(out);
				}
				*/
				resendToClients( new String("3Å"+playerList.getSize()+"Å"+params[2] ) , playerList.getSize()+1 );
				playerList.addPlayer(pkt.getAddress().getHostAddress(), pkt.getPort());
				playerList.getPlayer(playerList.getSize()-1).setNickname(params[2]);
				for(int i=0;i<playerList.getSize(); i++) {
					stg.addActor(playerList.getPlayer(i));
					playerList.getPlayer(i).toBack();
				}
			}catch (Exception e) {
				// TODO: handle exception
				//System.out.println("server_parse-"+e);
				e.printStackTrace();
			}
		}
		else if(type == 2){
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
			
			resendToClients(data, t.getId());
		}
		else if(type == 4){
			//System.out.println("server recebeu: "+data);
			chat.addMessage(params[1]);
			resendToClients(data, -1);
		}
		else if (type==6){
			Player p = playerList.getPlayerById( Integer.parseInt(params[1]) );
			System.out.println(Chat.getNickname()+" "+ p.getId());
			p.setActive(false);
			resendToClients(data, -1);
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
			p.moveLeft(movLeft);
			p.moveRight(movRight);
			p.moveDown(movDown);
			p.moveUp(movUp);
			p.setPosition(Float.parseFloat(params[5]), Float.parseFloat(params[6]));
			p.setShootin(shooting);
			
			if(p.getLife()<=0){ 
				//p.setActive(false);
				p.remove();
				resendToClients(new String("6Å"+p.getId()), -1);
			}
			resendToClients(data, id);
		}
	}
	private void resendToClients(String data, int senderId){
		
		try{
			byte[] buf = data.getBytes();
			for(int i=0; i<playerList.getSize(); i++){
				Player target = playerList.getPlayer(i);
				if(target.isActive()){
					if(target.getId()!= senderId){
						InetAddress targetAddress = InetAddress.getByName(target.getIP());
						int targetPort = target.getPort();
						DatagramPacket out = new DatagramPacket(buf, buf.length, targetAddress, targetPort);
						socket.send(out);
						pacotesEnviados+=1;
						bytesEnviados+=buf.length;
					}
				}
			}
		}catch (Exception e) { e.printStackTrace(); }
	}
	public void sendPacket( String data, String ip, int port ){
		byte[] buf = data.getBytes();
		try{
			DatagramPacket out = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort() );
			socket.send(out);
			pacotesEnviados+=1;
			bytesEnviados+=buf.length;
			//System.out.println("pac envido");
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("server_parse-"+e);
		}
	}
	public void synchronizeClients(){
		try{
			for(int i=0; i<playerList.getSize(); i++){
				Player p = playerList.getPlayer(i);
				byte[] buf = p.getState().getBytes();
				if(p.getLife() <= 0){ 
					buf = (new String("6Å"+p.getId())).getBytes();
					p.setActive(false);
					playerList.removePlayer(p);
					DatagramPacket out = new DatagramPacket
							(buf, buf.length, InetAddress.getByName(p.getIP()), p.getPort());
					socket.send(out);
				}
				for(int j=0;j<playerList.getSize(); j++){
					Player p2 = playerList.getPlayer(j);
					if(p2.isActive()){
						DatagramPacket out = new DatagramPacket
								(buf, buf.length, InetAddress.getByName(p2.getIP()), p2.getPort());
						socket.send(out);
						pacotesEnviados +=1;
						bytesEnviados += buf.length;
					}
				}
			}
		}catch(Exception e){ e.printStackTrace(); }
	}
	public boolean isConnected(){
		return socket.isConnected();
	}
	public void setChat(Chat c){ chat = c; }
	public static long getPacotesRecebido(){ return pacotesRecebidos; }
	public static long getPacotesEnviados(){ return pacotesEnviados; }
	public static double getBytesEnviados(){ return bytesEnviados; }
	public static double getBytesRecebidos(){ return bytesRecebidos; }
	
}
