package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Background extends BaseActor {
    public Background(Texture texture, float x, float y) {
        super(texture, x, y);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
}

