package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Bullet extends Actor{
	TextureRegion frame;
	Vector2 direction;
	float speed;
	Sound shootSound;
	int id;
	Bullet(Vector2 position, Vector2 direction,int frameDirection, int id){
		this.id = id;
		Texture texture = new Texture(Gdx.files.internal("assets/sprites/misc.png"));
		shootSound = Gdx.audio.newSound(Gdx.files.internal("assets/sound/M4A1_Single-Kibblesbob-8540445.mp3"));
		TextureRegion[][] allFrames = TextureRegion.split(texture, texture.getWidth()/8, texture.getHeight()/21 );
		frame = allFrames[11][frameDirection];
		this.direction = new Vector2(direction);
		speed=8;
		setX(position.x + direction.x*5); setY(position.y+direction.y*5);
		setWidth(frame.getRegionWidth());
		setHeight(frame.getRegionHeight());
		//System.out.println(getWidth()+" "+getHeight());
		shootSound.play();
	}
	public void draw(SpriteBatch batch, float parentAlpha){
		setX(getX()+direction.x*speed);
		setY(getY()+direction.y*speed);
		//System.out.println(getX());
		if(getX() < 0) remove();
		if(getY() < 0) remove();
		if(getX() > Gdx.graphics.getWidth() - frame.getRegionWidth()) remove();
		if(getY() > Gdx.graphics.getHeight() - frame.getRegionHeight()) remove();
		batch.draw(frame, getX(), getY());
	}
	public int getId(){ return id; }
	public int getTag(){ return 1; }
}

