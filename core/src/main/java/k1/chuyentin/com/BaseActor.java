package k1.chuyentin.com;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class BaseActor extends Actor {
    TextureRegion textureRegion;
    float scaleX = 1.0f;
    float scaleY = 1.0f;
    boolean alive = true;
    public BaseActor(Texture texture, float x, float y){
        this.textureRegion = new TextureRegion(texture);
        setPosition(x, y);
        setSize(texture.getWidth(), texture.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //batch.draw(texture, getX(), getY());
        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), scaleX, scaleY, getRotation());
    }

    protected Animation<TextureRegion> createAnimation(String folderPath, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        FileHandle indexFile = Gdx.files.internal(folderPath + "/files.txt");
        String[] fileNames = indexFile.readString().split("\n");

        for (String fileName : fileNames) {
            FileHandle file = Gdx.files.internal(folderPath + "/" + fileName.trim());
            Texture texture = new Texture(file);
            frames.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

    public Rectangle getBounds(){ // lấy ra khung hình chữ nhật theo kích thước nhân vật
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
    public void setAlive(boolean alive){
        this.alive = alive;
    }
    public boolean isAlive(){
        return alive;
    }
}

