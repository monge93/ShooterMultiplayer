package stages;

import object_managers.ClientManager;
import object_managers.PlayerListManager;
import objects.Bullet;
import objects.Chat;
import objects.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class GameStage extends Stage{
	
	private boolean active;
	Player player;
	PlayerListManager playersList;
	ClientManager client;
	Chat chat;
	static Skin darkSkin;
	public GameStage(){
		active = false;
		darkSkin = new Skin(Gdx.files.internal("assets/data/darkuiskin.json"));
	}
	public static Skin getDarkSkin(){ return darkSkin; }
	public void setChat(Chat c){ chat = c; addActor(chat); chat.toFront(); }
	public boolean isActive(){	return active;	}
	public void setActive(boolean arg0){	active = arg0; if(arg0) Gdx.input.setInputProcessor(this);	}
	public void setPlayerList(PlayerListManager pl){ playersList = pl; }
	public void setClientManager(ClientManager m){ client = m; }
	public ClientManager getClientManager(){ return client; }
	public void reset(){
		
	}
	
	public boolean keyDown(int keycode){
		if(player!=null){
			String msg = null;
			if(keycode == Keys.UP){
				player.moveUp(true);
				msg = "2Å"+player.getId()+"Å1Å8";
			}
			if(keycode == Keys.LEFT){
				player.moveLeft(true);
				msg = "2Å"+player.getId()+"Å1Å4";
			}
			if(keycode == Keys.RIGHT){
				player.moveRight(true);
				msg = "2Å"+player.getId()+"Å1Å6";
			}
			if(keycode == Keys.DOWN){
				player.moveDown(true);
				msg = "2Å"+player.getId()+"Å1Å2";
			}
			/*if(keycode == Keys.Z){
				player.setShootin(true);
				msg = "2Å"+player.getId()+"Å2";
			}*/
			if(msg!=null){
				if(client!=null)
					client.sendPacket(msg);
				msg = null;
			}
			if(keycode == Keys.Z){
				player.setShootin(true);
				msg = "2Å"+player.getId()+"Å2";
			}
			if(msg!=null){
				if(client!=null)
					client.sendPacket(msg);
				msg = null;
			}
			
			
			
			if(keycode == Keys.ENTER){
				if(!chat.isActivated()) chat.setActivated(true);
				//chat.setActivated(!chat.isActivated());
				if(chat.isActivated())
					setKeyboardFocus(chat.getTextField());
				//else setKeyboardFocus(null);
			}
			
		}
		return false;
	}
	public boolean keyUp(int keycode){
		if(player!=null){
			String msg = null;
			if(keycode == Keys.UP){
				player.moveUp(false);
				msg = "2Å"+player.getId()+"Å0Å8";
			}
			if(keycode == Keys.LEFT){
				player.moveLeft(false);
				msg = "2Å"+player.getId()+"Å0Å4";
			}
			if(keycode == Keys.RIGHT){
				player.moveRight(false);
				msg = "2Å"+player.getId()+"Å0Å6";
			}
			if(keycode == Keys.DOWN){
				player.moveDown(false);
				msg = "2Å"+player.getId()+"Å0Å2";
			}
			/*if(keycode == Keys.Z){
				player.setShootin(false);
				msg = "2Å"+player.getId()+"Å3";
			}*/
			
			if(msg!=null){
				if(client!=null)
					client.sendPacket(msg);
				msg = null;
			}
			if(keycode == Keys.Z){
				player.setShootin(false);
				msg = "2Å"+player.getId()+"Å3";
			}
			if(msg!=null){
				if(client!=null)
					client.sendPacket(msg);
				msg = null;
			}
		}
		if(keycode==Keys.ENTER && client==null){
			chat.setVisible(!chat.isVisible());
		}
		return false;
	}
	public boolean isColliding(Player a1, Bullet a2){
		
		float cimaA1 = a1.getY() + a1.getHeight();
		float cimaA2 = a2.getY() + a2.getHeight();
		float direitaA1 = a1.getX() + a1.getWidth();
		float direitaA2 = a2.getX() + a2.getWidth();
		float esquerdaA1 = a1.getX();
		float esquerdaA2 = a2.getX();
		float baixoA1 = a1.getY();
		float baixoA2 = a2.getY();

		if(esquerdaA1 > direitaA2)
			return false;
		if(direitaA1 < esquerdaA2)
			return false;
		if(cimaA1 < baixoA2)
			return false;
		if(baixoA1 > cimaA2)
			return false;
		return true;
	}
	
	public void checkCollisions(){
		Array<Actor> actor1 = getActors();
		Array<Actor> actor2 = getActors();
		for(int i=0; i<actor1.size; i++){
			Object a1 = actor1.get(i);
			if(a1 instanceof Player && ((Player) a1).isActive()){
				for(int j=i+1; j<actor2.size; j++){
					Object a2 = actor2.get(j);
					if(a2 instanceof Bullet){
						if(isColliding((Player)a1, (Bullet)a2)){
							if(((Player) a1).getId() != ((Bullet) a2).getId()){
								//System.out.println("colliding");
								((Player) a1).decreaseLife(1);
								((Bullet) a2).remove();
							}
						}
					}
				}
			}
		}
	}
	public void setPlayer(Player pl){
		player = pl;
	}
}
