package k1.chuyentin.com;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class HitDuckCounter extends BaseActor{
    TextureRegion tRegion;
    public HitDuckCounter(Texture texture, float x, float y, Stage s) {
        super(texture, x, y, s);
        setSize(30,30);
        tRegion = textureRegion;
        textureRegion.setRegion(30,0, 30,30);
    }
}
