package objects;

import object_managers.ClientManager;
import stages.GameStage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class Chat extends Window{
	TextField textField;
	Label label;
	static String nickname;
	ClientManager cm;
	GameStage gameStage;
	public Chat(Skin skin, String nick, ClientManager cm) {
		
		super("Chat", skin);
		setActivated(false);
		this.cm = cm;
		this.setMovable(false);
		nickname = nick;
		//setSize(Gdx.graphics.getWidth() - Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/4);
		setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/4);
		//setPosition(Gdx.graphics.getWidth()/2-getWidth()/2, 0);
		textField = new TextField("", skin);
		textField.setMessageText("message");
		label = new Label("", skin);
		add(label).width(getWidth()-10);
		row();
		bottom();
		add(textField).width(getWidth()-10);
		label.setWrap(true);
			
		
		textField.setTextFieldListener(new textListener());
	}
	public static String getNickname(){ return nickname; }
	public TextField getTextField(){ return textField; }
	//------------------------textfieldlistener
	class textListener implements TextFieldListener{

		@Override
		public void keyTyped(TextField textField, char key) {
			if(Gdx.input.isKeyPressed(Keys.ENTER)){
				if(!textField.getText().isEmpty()){
					//label.setText(  label.getText() + "\n "+nickname +": "+ textField.getText()  );
					if(cm!=null){
						cm.sendPacket("4Å"+nickname +": "+ textField.getText());
					}
					//focusedTextField = false;
					textField.setText("");
					gameStage.setKeyboardFocus(null);
					setActivated(false);
				}
			}
		}	
	}
	///------------------------------textfieldlistener
	
	public void setActivated(boolean arg){
		setVisible(arg);
	}
	public boolean isActivated(){
		return isVisible();
	}
	public void addMessage(String line){
		label.setText(  label.getText() +"\n"+ line  );
	}
	public void removeTextField(){
		clear();
		add(label).width(getWidth()-10);
	}
	public void setGameStage(GameStage s){ gameStage = s; }
}
