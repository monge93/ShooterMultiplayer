package object_managers;


import objects.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class PlayerListManager {
	Texture tex;
	private Array<Player> playerList;
	
	public PlayerListManager() {
		// TODO Auto-generated constructor stub
		playerList = new Array<Player>();
		tex = new Texture(Gdx.files.internal("assets/sprites/gentleman.png"));
	}
	public int getSize(){
		return playerList.size;
	}
	public void addPlayer(Player p){
		playerList.add(p);
	}
	public void addPlayer(String ip, int port){
		Player p = new Player(tex);
		p.setId(playerList.size);
		p.setIP(ip);
		p.setPort(port);
		playerList.add(p);
	}
	public void addPlayer(String ip, int port, Texture tex){
		Player p = new Player(tex);
		p.setId(playerList.size);
		p.setIP(ip);
		p.setPort(port);
		playerList.add(p);
	}
	public boolean removePlayer(Player p){
		for(int i=0 ; i<playerList.size ; i++){
			if(playerList.get(i) == p){
				playerList.removeIndex(i);
				return true;
			}
		}
		return false;
	}
	public Player getPlayerById(int id){
		for(int i=0;i<playerList.size;i++){
			if(playerList.get(i).getId()==id)
				return playerList.get(i);
		}
		return null;
	}
	public Player getPlayer(int index){ return playerList.get(index); }
	public void removePlayer(int id){
		playerList.removeValue(getPlayerById(id), true);
	}
	
}
