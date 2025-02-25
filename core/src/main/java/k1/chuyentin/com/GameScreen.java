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
    Shot shot;
    BaseActor hitCounter;
    BaseActor scoreCounter;

    OrthographicCamera camera;

    GlyphLayout layout;
    int round = 0;
    int score;

    Stage stage;
    Background background;
    BaseActor roundActor;

    Music sungAudio;
    boolean isTouchedLastFrame = false; // Cờ lưu trạng thái chuột ở khung trước
    float mouseSpeed = 0;
    boolean isShooting = false;

    Duck duck;
    Dog dog;
    BaseActor ground;

    Music caughtDuckAudio;
    Sound dogCryAudio;
    Sound blastAudio;
    Music bgMusic;
    Music newroundMusic;

    int duckShotX = 333;

    Array<HitDuckCounter> hitDuckCounters = new Array<>();

    public GameScreen(Master game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        game.batch = new SpriteBatch();
        layout = new GlyphLayout();
        layout.setText(game.font,"" + score);
        crossHairImage = new Texture("crosshair.png");

        stage = new Stage();
        background = new Background(new Texture("background.png"), 0,50, stage);

        roundActor = new BaseActor(new Texture("round.png"),0, 0, stage );
        roundActor.setY(Gdx.graphics.getHeight() - roundActor.getHeight());
        dog = new Dog(new Texture("dog/dogcaughtduck.png"), 0, 0, stage);
        ground = new BaseActor(new Texture("ground.png"), 0, -32, stage);
        crossHair = new CrossHair(crossHairImage, 0, 0, 1, 1, 0.02f, stage);

        duck = new Duck(new Texture("duck/duckshot.png"), 0, 0, stage);

        shot = new Shot(new Texture("shot.png"), 16, 0, stage);
        hitCounter = new BaseActor(new Texture("hitcounter.png"), 0, 0, stage);
        hitCounter.setSize(512+128, 86);
        hitCounter.setPosition(Gdx.graphics.getWidth()/2 - hitCounter.getWidth()/2, 0);

        scoreCounter = new BaseActor(new Texture("scorecounter.png"), 0, 0, stage);
        scoreCounter.setSize(128, 86);
        scoreCounter.setPosition(Gdx.graphics.getWidth() - scoreCounter.getWidth() - 16, 0);

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
        blastAudio = Gdx.audio.newSound(Gdx.files.internal("audio/blast.wav"));
      //  bgMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm.wav"));
        newroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/newround.wav"));

        sungAudio.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                isShooting = false;
                shot = new Shot(new Texture("shot.png"), 16, 0, stage);
            }
        });

        caughtDuckAudio.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                dog.addAction(Actions.moveBy(0, -250, 1));
                HitDuckCounter duckCounter = new HitDuckCounter(new Texture("duck.png"), duckShotX, 42, stage);
                duckShotX += 32;
                hitDuckCounters.add(duckCounter);
                if(HitDuckCounter.total == 3){
                    newroundMusic.play();
                } else {
                    System.out.println(HitDuckCounter.total);
                    duck.reset();
                }
            }
        });

        newroundMusic.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                nextRound();
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
     //   bgMusic.play();
      //  bgMusic.setLooping(true);
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

        if(!duck.hasFallen && !duck.isAlive() && duck.getY() < 0){
            caughtDuckAudio.play();
            dog.setPosition(duck.getX(), 0);
            dog.addAction(Actions.moveBy(0, 250, 1));

            score += 100;
            duck.hasFallen = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (!isTouchedLastFrame && !isShooting && shot.bullets > 0) { // Chỉ xử lý khi phát hiện nhấp mới

                isTouchedLastFrame = true; // Đánh dấu đã xử lý lần nhấp này
                crossHair.setCostumes(new Texture("explosion.png"), 8, 1, Gdx.graphics.getDeltaTime());
                shot.bullets--;
                if(shot.bullets == 0){
                    sungAudio.play();
                    isShooting = true;
                } else {
                    blastAudio.play();
                }
                vibrationEffect();
                if(duck != null && duck.getBounds().contains(crossHair.getX() + crossHair.getWidth()/2, crossHair.getY() + crossHair.getHeight()/2)){
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

    public void vibrationEffect(){
        int count = 3;
        float amountY = 10;
        float duration = 0.02f;
        scoreCounter.addAction(Actions.repeat(count, Actions.sequence(
            Actions.moveBy(0, amountY, duration),
            Actions.moveBy(0, -amountY, duration)
        )));
        hitCounter.addAction(Actions.repeat(count, Actions.sequence(
            Actions.moveBy(0, amountY, duration),
            Actions.moveBy(0, -amountY, duration)
        )));
        shot.addAction(Actions.repeat(count, Actions.sequence(
            Actions.moveBy(0, amountY, duration),
            Actions.moveBy(0, -amountY, duration)
        )));
        ground.addAction(Actions.repeat(count, Actions.sequence(
            Actions.moveBy(0, amountY, duration),
            Actions.moveBy(0, -amountY, duration)
        )));
        background.addAction(Actions.repeat(count, Actions.sequence(
            Actions.moveBy(0, amountY, duration),
            Actions.moveBy(0, -amountY, duration)
        )));
        roundActor.addAction(Actions.repeat(count, Actions.sequence(
            Actions.moveBy(0, amountY, duration),
            Actions.moveBy(0, -amountY, duration)
        )));
        crossHair.addAction(Actions.repeat(count, Actions.sequence(
            Actions.moveBy(0, amountY, duration),
            Actions.moveBy(0, -amountY, duration)
        )));
    }

    private void reset(){

    }

    private void nextRound(){
        round++;
        duck.reset();
        for (HitDuckCounter duckCounter: hitDuckCounters) {
            duckCounter.remove();
        }
        hitDuckCounters.clear();
        HitDuckCounter.total = 0;
        duckShotX = 333;
    }
}

