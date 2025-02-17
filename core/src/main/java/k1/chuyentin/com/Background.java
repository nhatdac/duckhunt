package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Background extends BaseActor {
    public Background(Texture texture, float x, float y, Stage s) {
        super(texture, x, y, s);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}

