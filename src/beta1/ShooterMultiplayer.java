package beta1;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class ShooterMultiplayer {
	public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Mamones Assassinos";
        cfg.useGL20 = true;
        cfg.width = 640;
        cfg.height = 480;
        new LwjglApplication(new Game(),cfg);
	}
}
