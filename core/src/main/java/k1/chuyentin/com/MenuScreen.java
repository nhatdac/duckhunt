package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen implements Screen {
    GlyphLayout layout;
    Master game;
    OrthographicCamera camera;
    Music aboutAudio;

    public MenuScreen(Master game){
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        layout = new GlyphLayout();
        layout.setText(game.font, "CLICK TO PLAY");
        game.buttonStage = new Stage();
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = game.font;
        style.fontColor = Color.YELLOW;
        TextButton startButton = new TextButton("START", style);
        startButton.setPosition(Gdx.graphics.getWidth()/2 - startButton.getWidth()/2, Gdx.graphics.getHeight()/2 - 2*startButton.getHeight());
        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(game.gameScreen);
                aboutAudio.stop();
                aboutAudio.dispose();
            }
        });
        game.buttonStage.addActor(startButton);
        Gdx.input.setInputProcessor(game.buttonStage);

        aboutAudio = Gdx.audio.newMusic(Gdx.files.internal("audio/about.wav"));
        aboutAudio.setLooping(true);
        aboutAudio.play();
    }
    @Override
    public void render(float v) {
        ScreenUtils.clear(Color.GRAY);
        camera.update();
        game.batch.begin();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.draw(game.image, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.font.draw(game.batch, layout, Gdx.graphics.getWidth()/2 - layout.width/2,Gdx.graphics.getHeight()/2 + 2*layout.height);
        //game.batch.draw(birdImage, Gdx.graphics.getWidth()/2 - birdImage.getWidth()/2, Gdx.graphics.getHeight()/2);
        game.batch.end();

        game.buttonStage.act(Gdx.graphics.getDeltaTime());
        game.buttonStage.draw();

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
}

