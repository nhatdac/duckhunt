package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class CrossHair extends BaseActorAnimation {

    float dx = 0;
    float dy = 0;
    float deltaXY = 1;
    float dec = 0.9f; // quán tính giảm dần

    public CrossHair(Texture texture, float x, float y, int cot, int hang, float speed, Stage s) {
        super(texture, x, y, cot, hang, speed, s);
        setOrigin(getWidth()/2, getHeight()/2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(getX() + getWidth() > Gdx.graphics.getWidth()) {
            setX(Gdx.graphics.getWidth() - getWidth());
        }
        if(getX() < 0) {
            setX(0);
        }
        if(getY() + getHeight() > Gdx.graphics.getHeight()) {
            setY(Gdx.graphics.getHeight() - getHeight());
        }
        if(getY() < 0) {
            setY(0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            dx -= deltaXY;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            dx += deltaXY;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            dy -= deltaXY;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            dy += deltaXY;
        }
        dx *= dec;
        dy *= dec;
        moveBy(dx, dy);
    }

    @Override
    public void setCostumes(Texture texture, int cot, int hang, float speed) {
        super.setCostumes(texture, cot, hang, speed);
        setPosition(getX() + getWidth()/2 - texture.getWidth()/(2*cot), getY() + getHeight()/2 - texture.getHeight()/2);
    }
}
