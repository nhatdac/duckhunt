package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
public class GameScreen implements Screen {
    Master game;
    Texture crossHairImage;
    Texture crossHairImageAnimation;
    CrossHair crossHair;
    Round round;
    BaseActor shot;
    BaseActor hitCounter;
    BaseActor scoreCounter;

    OrthographicCamera camera;

    Texture backgroundImage;
    GlyphLayout layout;
    int score;

    Stage stage;
    Background background;

    Music sungAudio;
    boolean isTouchedLastFrame = false; // Cờ lưu trạng thái chuột ở khung trước
    float mouseSpeed = 0;
    int shooting = 0;

    Duck duck;
    Dog dog;
    BaseActor ground;

    Sound caughtDuckAudio;
    Sound dogCryAudio;

    public GameScreen(Master game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        game.batch = new SpriteBatch();
        layout = new GlyphLayout();
        layout.setText(game.font," " + score + " ");
        backgroundImage = new Texture("background.png");
        crossHairImage = new Texture("crosshair.png");
        crossHairImageAnimation = new Texture("explosion.png");
        sungAudio = Gdx.audio.newMusic(Gdx.files.internal("audio/shoot.wav"));

        stage = new Stage();
        background = new Background(backgroundImage, 0,0);
        ground = new BaseActor(new Texture("ground.png"), 0, -90);
        crossHair = new CrossHair(crossHairImage, 0, 0, 1, 1, 0.02f);

        duck = new Duck(new Texture("duck/duckshot.png"), 0, 0);
        dog = new Dog(new Texture("dog/dogcaughtduck.png"), 0, 0);
        round = new Round(new Texture("round.png"), 0, Gdx.graphics.getHeight() - 64);
        shot = new Shot(new Texture("shot.png"), 0, 0, 4, 1, 0.1f);
        hitCounter = new BaseActor(new Texture("hitcounter.png"), 200, 0);
        scoreCounter = new BaseActor(new Texture("scorecounter.png"), Gdx.graphics.getWidth() - 150, 0);
        scoreCounter.setSize(128, 80);

        stage.addActor(background);
        stage.addActor(duck);
        stage.addActor(dog);
        stage.addActor(ground);
        stage.addActor(crossHair);
        stage.addActor(round);
        stage.addActor(shot);
        stage.addActor(hitCounter);
        stage.addActor(scoreCounter);

        stage.addListener(new InputListener(){
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                mouseSpeed = 5;
                return true;
            }
        });

        caughtDuckAudio = Gdx.audio.newSound(Gdx.files.internal("audio/caughtDuck.wav"));
        dogCryAudio = Gdx.audio.newSound(Gdx.files.internal("audio/dogcry.wav"));

        sungAudio.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                crossHair.setCostumes(crossHairImage, 1, 1, 0.002f);
            }
        });
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // ẩn hình con trỏ chuột
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);

    }
    @Override
    public void render(float v) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(game.image, 0,0);
        game.batch.end();

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float crossX = crossHair.getX() + crossHair.getWidth()/2;
        float crossY = crossHair.getY() + crossHair.getHeight()/2;
        float angle = (float) Math.toDegrees(Math.atan2(mouseY - crossY, mouseX - crossX));
        crossHair.setRotation(angle);

        if(mouseSpeed > 0){
            mouseSpeed -= 0.1;
        }
        float dx = (float) (Math.cos(Math.toRadians(angle))*mouseSpeed);
        float dy = (float) (Math.sin(Math.toRadians(angle))*mouseSpeed);
        crossHair.moveBy(dx, dy);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if(!duck.isAlive() && duck.getY() < 0 && dog.getY() == 0){
            caughtDuckAudio.play();
            dog.setX(duck.getX(), 0);
            dog.addAction(Actions.moveBy(0, 150, 1));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (!isTouchedLastFrame && shooting < 1) { // Chỉ xử lý khi phát hiện nhấp mới
                sungAudio.play(); // Phát âm thanh hoặc xử lý logic
                isTouchedLastFrame = true; // Đánh dấu đã xử lý lần nhấp này
                crossHair.setCostumes(crossHairImageAnimation, 8, 1, Gdx.graphics.getDeltaTime());
                if(duck.getBounds().contains(crossHair.getX() + crossHair.getWidth()/2, crossHair.getY() + crossHair.getHeight()/2)){
                    duck.justShot();
                }
                if(dog.getBounds().contains(crossHair.getX() + crossHair.getWidth()/2, crossHair.getY() + crossHair.getHeight()/2)){
                    if(dog.isAlive()){
                        dogCryAudio.play();
                        dog.setAlive(false);
                        dog.textureRegion = new TextureRegion(new Texture("dog/doglaughing2.png"));
                    }
                }
            }
        } else {
            isTouchedLastFrame = false; // phím đã thả, sẵn sàng cho lần nhấp mới
        }

    }
    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
    @Override
    public void hide() {

    }
    @Override
    public void dispose() {

    }

    public void collision(){
    }
}

