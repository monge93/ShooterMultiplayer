package objects;

import stages.GameStage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Player extends Actor {
	
	private int id;
	private String ipAddress;
	private int port;
	
	private TextureRegion[] framesParado;
	private TextureRegion[] framesAtirando;
	private TextureRegion[][] framesAndando;
	private int LastDirection;
	private float stateTime;
	private Animation animation;
	private Vector2 direction;
	private Vector2 objectDirection;
	private float delta;
	private float movementSpeed;
	private float shootDelay;
	private boolean active;
	private Label label;
	Sound shootSound;
	
	private boolean movUP, movLEFT, movRIGHT, movDOWN, shootin;
	private int healthPoints;
	
	public Player(Texture spriteSheet){
		Skin skin = GameStage.getDarkSkin();
		label = new Label(Chat.getNickname(), skin);
		healthPoints = 5;
		active = true;
		TextureRegion[][] regions = TextureRegion.split(spriteSheet, spriteSheet.getWidth()/4, spriteSheet.getHeight()/12);
		framesParado = new TextureRegion[8];
		framesAtirando = new TextureRegion[8];
		framesAndando = new TextureRegion[8][4];
		int n = 0;
		int i;
		for(i = 0; i<2; i++)
			for(int j=0;j<4;j++)
				framesParado[n++] = regions[i][j];
		
		int i2=0;
		for(;i<10;i++){
			for(int j=0;j<4;j++)
				framesAndando[i2][j] = regions[i][j];
			i2++;
		}
		
		n=0;
		for(;i<12;i++)
			for(int j=0 ; j<4 ; j++)
				framesAtirando[n++] = regions[i][j];
		
		animation = new Animation(0.25f, framesAndando[3]);
		stateTime = 0;
		LastDirection = 4;
		movementSpeed = 100;
		shootDelay = 0.5f;
		direction = new Vector2();
		objectDirection = new Vector2(0,-2);
		setWidth(framesParado[0].getRegionWidth());
		setHeight(framesParado[0].getRegionHeight());
		//System.out.println(getWidth()+" "+getHeight());
		//shootSound = Gdx.audio.newSound(Gdx.files.internal("assets/sound/M4A1_Single-Kibblesbob-8540445.mp3"));
	}
	public void setNickname(String arg0){ label.setText( new String(arg0) ); }
	public String getNickname() { return label.getText().toString(); }
	public void setPositionById(){
		float x = (float)(Math.random()*Gdx.graphics.getWidth());
		float y = (float)(Math.random()*Gdx.graphics.getHeight());
		setPosition(x, y);
		//setPosition(Gdx.graphics.getWidth()/4 + id*40,Gdx.graphics.getHeight()/2);
	}
	public int getDirection(){ return LastDirection; }
	public void draw(SpriteBatch batch, float parentAlpha){
		if(isActive()){
			stateTime += Gdx.graphics.getDeltaTime();
			
	        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
			batch.draw(currentFrame, getX(), getY());
			
			direction.set(0, 0);
			delta = Gdx.graphics.getDeltaTime() * movementSpeed;
			shootDelay += Gdx.graphics.getDeltaTime();
	
			if(shootDelay >0.15f)
				stopAnimation(LastDirection);
			
			if(shootDelay > 0.35f){
	
				if( movRIGHT ){
		        	walkAnimation(2);
		        	LastDirection = 2;
					direction.x = 1 * delta;
		        }
		        if( movLEFT ){
		        	walkAnimation(6);
		        	LastDirection = 6;
					direction.x = -1 * delta;
		        }
		        if( movUP ){
		        	walkAnimation(0);
		        	LastDirection = 0;
					direction.y = 1 * delta;
		        }
		        if( movDOWN ){
		        	walkAnimation(4);
		        	LastDirection = 4;
					direction.y = -1 * delta;        	
		        }
		        if(movDOWN&&movRIGHT){
		        	walkAnimation(3);
		        	direction.mul(0.7f);
		        	LastDirection = 3;
		        }
		        if(movDOWN&&movLEFT){
		        	walkAnimation(5);
		        	direction.mul(0.7f);
		        	LastDirection = 5;
		        }
		        if(movUP&&movLEFT){
		        	walkAnimation(7);
		        	direction.mul(0.7f);
		        	LastDirection = 7;
		        }
		        if(movUP&&movRIGHT){
		        	walkAnimation(1);
		        	direction.mul(0.7f);
		        	LastDirection = 1;
		        }
		        
			}
			
	        if (direction.x != 0 || direction.y != 0) {
	        	setX(getX() + direction.x);
	            setY(getY() + direction.y);
	            if(getX()<0) setX(0);
	            if(getY()<0) setY(0);
	            float screenWidth = Gdx.graphics.getWidth();
	            float screenHeight = Gdx.graphics.getHeight();
	            float width =  animation.getKeyFrame(stateTime, true).getRegionWidth();
	            float height = animation.getKeyFrame(stateTime, true).getRegionHeight();
	            if(getX()>screenWidth - width) setX(screenWidth - width);
	            if(getY()>screenHeight - height) setY(screenHeight - height);
	            objectDirection.set(direction);            
	        }
	        
	        if(shootin && shootDelay > 0.6f) shoot();
	        
	        label.setPosition(getX()+getWidth()/2 - label.getWidth()/2, getY()+getHeight());
	        label.draw(batch, parentAlpha);
	        //if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && shootDelay > 0.6f){
	        //	shoot();
	       // }
		}
	}
	public String getState(){
		String moving="0";
		if(movDOWN||movUP||movLEFT||movRIGHT)
			moving = "1";
		String dir="";
		if(movLEFT) dir+="1"; else dir+="0";
		if(movRIGHT) dir+="1"; else dir+="0";
		if(movDOWN) dir+="1"; else dir+="0";
		if(movUP) dir+="1"; else dir+="0";
		String shooting = "0";
		if(shootin) shooting = "1";
		String state = "9Å"+getId()+"Å"+moving+"Å"+dir+"Å"+shooting+"Å"+getX()+"Å"+getY()+"Å"+healthPoints;
		//System.out.println(state);
		return state;
	}
	public void setDirection(boolean movLeft, boolean movRight, boolean movDown, boolean movUp){
		if( movRIGHT )
        	LastDirection = 2;
        if( movLEFT )
        	LastDirection = 6;
        if( movUP )
        	LastDirection = 0;
        if( movDOWN )
        	LastDirection = 4; 
        if(movDOWN&&movRIGHT)
        	LastDirection = 3;
        if(movDOWN&&movLEFT)
        	LastDirection = 5;
        if(movUP&&movLEFT)
        	LastDirection = 7;
        if(movUP&&movRIGHT)
        	LastDirection = 1;
	}
	public void setShootin(boolean arg0){ shootin = arg0; }
	public boolean canShoot(){ return shootDelay>0.6f; }
	public void shoot(){
		//shootSound.play();
    	shootAnimation(LastDirection);
    	Vector2 bulletPos = new Vector2(getX() + animation.getKeyFrame(stateTime, true).getRegionWidth()/2 ,
    			getY() + animation.getKeyFrame(stateTime, true).getRegionHeight()/2);
    	
    	Vector2 bulletDir = new Vector2();
    	bulletDir.set(objectDirection);
    	bulletDir.nor();
    	getParent().addActor(new Bullet(bulletPos,bulletDir,LastDirection,id));
    	shootDelay = 0;
	}
	public void moveLeft(boolean arg){
		movLEFT = arg;
	}
	public void moveRight(boolean arg){
		movRIGHT = arg;
	}
	public void moveUp(boolean arg){
		movUP = arg;
	}
	public void moveDown(boolean arg){
		movDOWN = arg;
	}
	
	
	void walkAnimation(int direction){
		animation = new Animation(0.15f,framesAndando[direction]);
	}
	void stopAnimation(int direction){
		animation = new Animation(0.25f,framesParado[direction]);
	}
	void shootAnimation(int direction){
		animation = new Animation(0.25f,framesAtirando[direction]);
	}
	public void decreaseLife(int arg){ healthPoints -= 1; }
	public int getLife(){ return healthPoints; }
	public boolean isActive(){ return active; }
	public void setActive(boolean arg0){ active = arg0; if(!active) remove(); }
	public int getId(){ return id; }
	public void setId(int id){ this.id = id; setPositionById(); }
	public String getIP(){ return ipAddress; }
	public void setIP(String ip) { ipAddress = ip; };
	public int getPort(){ return port; }
	public void setPort(int port) { this.port = port; }
	public int getTag(){ return 2; }
}
