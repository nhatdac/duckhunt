package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Random;

public class Duck extends BaseActor{

    Sound duckSound;
    Sound duckDrop;

    static final String STRAIGHT = "straight";
    static final String TILTED = "tilted";
    static final String NO_ANIMATION = "no_animation";
    String animationType = NO_ANIMATION;
    int justShot = 0;

     Animation<TextureRegion> flyStraightAnimation; // Animation bay thẳng
     Animation<TextureRegion> flyTiltedAnimation;   // Animation bay nghiêng
     Animation<TextureRegion> currentAnimation;     // Animation hiện tại
     float stateTime;

    // Thông số vật lý
     float gravity = -9.8f; // Trọng lực ban đầu (pixel/s^2)
     float velocityX = 100f; // Vận tốc ban đầu theo trục X (pixel/s)
     float velocityY = 100f; // Vận tốc ban đầu theo trục Y (pixel/s)
    float timeTrigger = 0;

    // Kích thước màn hình
    private final float screenWidth = Gdx.graphics.getWidth();
    private final float screenHeight = Gdx.graphics.getHeight();

    // Trạng thái bay trước đó
    boolean wasGoingUp = true;

    public Duck(Texture texture, float x, float y, Stage s) {
        super(texture, x, y);
        stateTime = 0f;

        duckSound = Gdx.audio.newSound(Gdx.files.internal("audio/ducksound.wav"));
        duckDrop = Gdx.audio.newSound(Gdx.files.internal("audio/duckdrop.wav"));

        // Tạo animation bay đầu 15 độ
        flyStraightAnimation = createAnimation("duck/fly1/", 0.1f);
        // Tạo animation bay đầu 45 độ
        flyTiltedAnimation = createAnimation("duck/fly2/", 0.1f);

        // Đặt animation mặc định là bay thẳng
        animationType = TILTED;
        setAnimation(animationType);
        setSize(64, 64);

        s.addActor(this);
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
        timeTrigger += delta;

        setX(getX() + velocityX * delta);
        setY(getY() + velocityY * delta + 0.5f * gravity * delta * delta);
        velocityY += gravity * delta;

        if(isAlive()){

//            if(timeTrigger > 2){
//                timeTrigger = 0;
//                if(new Random().nextBoolean()){
//                    velocityY = -velocityY;
//                    gravity = -gravity;
//                }
//            }

            boolean isGoingUp = velocityY > 0;
            if (isGoingUp != wasGoingUp) { // Chỉ thay đổi hình khi trạng thái đổi
                if (isGoingUp) {
                    animationType = TILTED;
                    gravity = -9.8f;
                } else {
                    animationType = STRAIGHT;
                    gravity = -2.5f;
                }
                setAnimation(animationType);
                wasGoingUp = isGoingUp; // Cập nhật trạng thái
            }

            // Kiểm tra chạm vào các cạnh và thay đổi hướng lượn
            if (getX() <= 0 || getX() >= screenWidth) {
                velocityX = -velocityX;
            }

            if (getY() >= screenHeight) {
                velocityY = -velocityY;
            }

            if (velocityX > 0) {
                scaleX = 1; // Quay mặt sang phải
            } else {
                scaleX = -1; // Quay mặt sang trái sang trái
            }

            // Reset nếu chạm đáy
            if (getY() < 0) {
                reset();
            }
        } else {
            animationType = NO_ANIMATION;
            if(justShot < 60){
                justShot += 1;
                if(justShot == 60){
                    textureRegion = new TextureRegion(new Texture(Gdx.files.internal("duck/duckfall.png")));
                    duckDrop.play();
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(animationType.equals(NO_ANIMATION)){
            super.draw(batch, parentAlpha);
        } else {
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime);
            batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), scaleX, scaleY, getRotation());
        }
    }


    // Đổi animation
    public void setAnimation(String type) {
        if (type.equals(STRAIGHT)) {
            currentAnimation = flyStraightAnimation;
            duckSound.play();
        } else if (type.equals(TILTED)) {
            currentAnimation = flyTiltedAnimation;
        }
    }

    private void reset() {
        setPosition(MathUtils.random(0, Gdx.graphics.getWidth()), 50);
        velocityX = MathUtils.random(30, 80);
        velocityY = MathUtils.random(30, 80);
        gravity = -9.8f;
    }
    public void justShot(){
        setAlive(false);
        justShot = 0;
        velocityX = 0;
        velocityY = -100;
        gravity = -9.8f;
        textureRegion = new TextureRegion(new Texture("duck/duckshot.png"));
    }
}
