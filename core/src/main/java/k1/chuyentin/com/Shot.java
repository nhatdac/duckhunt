package k1.chuyentin.com;

import com.badlogic.gdx.graphics.Texture;

public class Shot extends BaseActorAnimation{
    int bullets = 0;
    public Shot(Texture texture, float x, float y, int cot, int hang, float speed) {
        super(texture, x, y, cot, hang, speed);
        bullets = 3;
        alive = false;
    }

    @Override
    public void act(float delta) {

    }
}
