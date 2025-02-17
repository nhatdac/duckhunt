package k1.chuyentin.com;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HitDuckCounter extends BaseActor{
    TextureRegion tRegion;
    public HitDuckCounter(Texture texture, float x, float y) {
        super(texture, x, y);
        setSize(30,30);
        tRegion = textureRegion;
        textureRegion.setRegion(30,0, 30,30);
    }
}
