package beta1;

import object_managers.ClientManager;
import object_managers.ServerManager;
import objects.AvailableRoomList;
import objects.Chat;
import stages.*;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

public class Game implements ApplicationListener {

	GameStage gamestage;
	StartMenu startStage;
	float syncTime;
	float syncDelay;
	static double runningTime;
	@Override
	public void create() {
		// TODO Auto-generated method stub
		gamestage = new GameStage();
		startStage = new StartMenu(gamestage);
		gamestage.setActive(false);
		startStage.setActive(true);
		
		startStage.setGameStage(gamestage);
		syncTime = 0;
		syncDelay = 0.05f;
		//runningTime = 0;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if(startStage.isActive()){
			startStage.draw();
		}
		else if(gamestage.isActive()){
			gamestage.draw();
			gamestage.checkCollisions();
			if(syncTime>=syncDelay){
				//if(startStage.getServerManager()!=null)
				//	startStage.getServerManager().synchronizeClients();
				//else
					if(gamestage.getClientManager()!=null)
					gamestage.getClientManager().sendStateToServer();
				syncTime = 0;
			}
			syncTime += Gdx.graphics.getDeltaTime();
			runningTime += Gdx.graphics.getDeltaTime();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		AvailableRoomList db = startStage.getDatabaseManager();
		if(startStage.getServerManager()!=null){
			if(StartMenu.isOnline()) db.removeServer(db.getIP());
			System.out.println("(Servidor)\nPacotes Recebidos = "+ServerManager.getPacotesRecebido()
					+" ("+ ServerManager.getBytesRecebidos()/1024 +" KB)\nPacotes Enviados = "
					+ServerManager.getPacotesEnviados() + " ("+ServerManager.getBytesEnviados()/1024+" KB)");
			double downrate = (ServerManager.getBytesRecebidos()/1024 )/ runningTime;
			double uprate = (ServerManager.getBytesEnviados()/1024)/runningTime;
			System.out.println("Down rate = "+downrate+" KB/s\nUp rate = "+uprate+ " KB/s");
		}
		if(gamestage.getClientManager()!=null){
			gamestage.getClientManager().deactivatePlayer();
			System.out.println("("+Chat.getNickname()+")\nPacotes Recebidos = "+ClientManager.getPacotesRecebido()
					+" ("+ ClientManager.getBytesRecebidos()/1024 +" KB)\nPacotes Enviados = "
					+ClientManager.getPacotesEnviados() + " ("+ ClientManager.getBytesEnviados()/1024+" KB)");
			double downrate = (ClientManager.getBytesRecebidos()/1024 )/ runningTime;
			double uprate = (ClientManager.getBytesEnviados()/1024)/runningTime;
			System.out.println("Down rate = "+downrate+" KB/s\nUp rate = "+uprate+" KB/s");
		}
	}
	public static void setRunningTime(double arg0) { runningTime = arg0; }

}
