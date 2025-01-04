package io.github.infotest.util.Overlay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.infotest.MainGameScreen;
import io.github.infotest.character.Player;
import io.github.infotest.util.GameRenderer;

public class UI_Layer implements ApplicationListener {

    MainGameScreen mainScreen  ;
    SpriteBatch batch;
    Camera camera;
    Player player;


    private Texture[] healthbar;

    public UI_Layer(MainGameScreen mainScreen) {
        this.mainScreen = mainScreen;
        this.camera = mainScreen.getCamera();
        create();
    }


    public void create() {
        this.batch = new SpriteBatch();

        healthbar = new Texture[6];
        healthbar[0] = new Texture(Gdx.files.internal("healthbar_start.png"));
        healthbar[1] = new Texture(Gdx.files.internal("healthbar_start_full.png"));
        healthbar[2] = new Texture(Gdx.files.internal("healthbar_middle.png"));
        healthbar[3] = new Texture(Gdx.files.internal("healthbar_middle_full.png"));
        healthbar[4] = new Texture(Gdx.files.internal("healthbar_ende.png"));
        healthbar[5] = new Texture(Gdx.files.internal("healthbar_ende_full.png"));

    }

    @Override
    public void resize(int i, int i1) {

    }

    public void render() {
        batch.begin();
        System.out.println("Rendering Bar");
        GameRenderer.renderBar(batch, camera, healthbar, player.getMaxHealthPoints(), camera.viewportHeight*1/5);
        System.out.println("Filling Bar");
        GameRenderer.fillBar(batch,camera, healthbar, player.getHealthPoints(), player.getMaxHealthPoints(), camera.viewportHeight*1/5-11);
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        mainScreen.dispose();
        batch.dispose();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
