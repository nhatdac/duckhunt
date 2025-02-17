package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Shot extends BaseActor {
    int bullets = 0;
    public Shot(Texture _texture, float x, float y) {
        super(_texture, x, y);
        setSize(120,86);
        bullets = 3;
        alive = false;
    }

    @Override
    public void act(float delta) {
        int position = 3 - bullets;
        textureRegion.setRegion(position*120, 0, 120, 86);
    }
}
