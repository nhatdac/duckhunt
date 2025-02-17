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
    CrossHair crossHair;
    Texture roundImage;
    Shot shot;
    BaseActor hitCounter;
    BaseActor scoreCounter;

    OrthographicCamera camera;

    GlyphLayout layout;
    int round = 0;
    int score;

    Stage stage;
    Background background;

    Music sungAudio;
    boolean isTouchedLastFrame = false; // Cờ lưu trạng thái chuột ở khung trước
    float mouseSpeed = 0;
    boolean isShooting = false;

    Duck duck;
    Dog dog;
    BaseActor ground;

    Music caughtDuckAudio;
    Sound dogCryAudio;

    int duckShotX = 333;

    public GameScreen(Master game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        game.batch = new SpriteBatch();
        layout = new GlyphLayout();
        layout.setText(game.font,"" + score);
        crossHairImage = new Texture("crosshair.png");

        stage = new Stage();
        background = new Background(new Texture("background.png"), 0,0);
        ground = new BaseActor(new Texture("ground.png"), 0, -32);
        crossHair = new CrossHair(crossHairImage, 0, 0, 1, 1, 0.02f);

        dog = new Dog(new Texture("dog/dogcaughtduck.png"), 0, 0);
        roundImage = new Texture("round.png");
        shot = new Shot(new Texture("shot.png"), 16, 0);
        hitCounter = new BaseActor(new Texture("hitcounter.png"), 0, 0);
        hitCounter.setSize(512+128, 86);
        hitCounter.setPosition(Gdx.graphics.getWidth()/2 - hitCounter.getWidth()/2, 0);

        scoreCounter = new BaseActor(new Texture("scorecounter.png"), 0, 0);
        scoreCounter.setSize(128, 86);
        scoreCounter.setPosition(Gdx.graphics.getWidth() - scoreCounter.getWidth() - 16, 0);

        stage.addActor(background);
        stage.addActor(dog);
        stage.addActor(ground);
        stage.addActor(crossHair);
        stage.addActor(shot);
        stage.addActor(hitCounter);
        stage.addActor(scoreCounter);

        duck = new Duck(new Texture("duck/duckshot.png"), 0, 0, stage);

        stage.addListener(new InputListener(){
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                mouseSpeed = 5;
                return true;
            }
        });

        caughtDuckAudio = Gdx.audio.newMusic(Gdx.files.internal("audio/caughtDuck.wav"));
        dogCryAudio = Gdx.audio.newSound(Gdx.files.internal("audio/dogcry.wav"));
        sungAudio = Gdx.audio.newMusic(Gdx.files.internal("audio/shoot.wav"));

        sungAudio.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                crossHair.setCostumes(crossHairImage, 1, 1, 0.002f);
                isShooting = false;
            }
        });

        caughtDuckAudio.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                dog.addAction(Actions.moveBy(0, -250, 1));
                duck = new Duck(new Texture("duck/duckshot.png"), 0, 0, stage);
            }
        });
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // ẩn hình con trỏ chuột
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        round = 1;
        score = 0;

    }
    @Override
    public void render(float v) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

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
            dog.addAction(Actions.moveBy(0, 250, 1));

            HitDuckCounter duckCounter = new HitDuckCounter(new Texture("duck.png"), duckShotX, 42);
            duckShotX += 32;
            stage.addActor(duckCounter);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (!isTouchedLastFrame && !isShooting && shot.bullets > 0) { // Chỉ xử lý khi phát hiện nhấp mới
                isShooting = true;
                sungAudio.play(); // Phát âm thanh hoặc xử lý logic
                isTouchedLastFrame = true; // Đánh dấu đã xử lý lần nhấp này
                crossHair.setCostumes(new Texture("explosion.png"), 8, 1, Gdx.graphics.getDeltaTime());
                shot.bullets--;
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

        game.batch.begin();
        game.batch.draw(roundImage, 0, Gdx.graphics.getHeight() - roundImage.getHeight());
        game.font.draw(game.batch, "" + round, 220, Gdx.graphics.getHeight() - 20);
        layout.setText(game.font, "" + score);
        game.font.draw(game.batch, layout, Gdx.graphics.getWidth() - 16 - scoreCounter.getWidth()/2 - layout.width/2, 70);
        game.batch.end();

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

