package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Dog extends BaseActor {

    float stateTime = 0;
    Animation<TextureRegion> animationCry;
    Animation<TextureRegion> animationSniff;
    Animation<TextureRegion> animationCurrent;
    public Dog(Texture texture, float x, float y) {
        super(texture, x, y);

        animationCry = createAnimation("dog/", 0.2f);
        animationSniff = createAnimation("dog/sniff", 0.3f);
        animationSniff.setPlayMode(Animation.PlayMode.NORMAL);
        animationCurrent = animationCry;
        setSize(128, 128);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(alive){
            super.draw(batch, parentAlpha);
        } else {
            stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = animationCurrent.getKeyFrame(stateTime);
            batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), scaleX, scaleY, getRotation());
        }
    }
}
