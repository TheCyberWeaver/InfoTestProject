package io.github.infotest.util.Overlay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.*;
import io.github.infotest.item.Item;
import io.github.infotest.util.GameRenderer;
import io.github.infotest.util.MyAssetManager;
import io.github.infotest.util.MyMath;

import java.util.ArrayList;

import static io.github.infotest.MainGameScreen.hasInitializedMap;
import static io.github.infotest.MainGameScreen.localPlayer;

public class UI_Layer implements ApplicationListener {

    MyAssetManager assetManager;

    SpriteBatch batch;
    Camera uiCamera; // UI-specific camera
    Viewport viewport;
    ShapeRenderer shapeRenderer;
    Vector2 windowSize;

    private final Texture[] healthbar;
    private final Texture[] manabar;
    private final Texture[] ausdauerbar;

    private float signTimer = 0;
    private boolean isRenderingSign = false;
    private float duration = 3;
    private float fadeDuration = 2;
    private float base;

    //DeathMessage
    private BitmapFont messageFont;
    private String deathMessage = null;
    private float deathMsgTimer = 0f;
    private float deathMsgDuration = 5f;      // How long the message is fully visible
    private float deathMsgFadeDuration = 2f; // How long the message takes to fade out
    private boolean isDeathMessageVisible = false;
    private GlyphLayout glyphLayout; // For text measurement

    //GoldBar
    private BitmapFont goldBarfont;

    public UI_Layer( MyAssetManager assetManager) {
        this.assetManager = assetManager;
        this.uiCamera = new OrthographicCamera(); // Create a new OrthographicCamera for UI
        viewport = new ScreenViewport(uiCamera);
        windowSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.healthbar = assetManager.getHealthBarAssets();
        this.manabar = assetManager.getManaBarAssets();
        this.ausdauerbar = assetManager.getAusdauerBarAssets();

        create();
    }

    public void create() {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        messageFont = new BitmapFont();
        messageFont.getData().setScale(2.0f);

        goldBarfont = new BitmapFont();
        goldBarfont.getData().setScale(2.0f);

        glyphLayout = new GlyphLayout();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void render() {

        viewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);

        if (hasInitializedMap) {
            batch.begin();
            GameRenderer.renderBar(batch, healthbar, localPlayer.getHealthPoints(), localPlayer.getMaxHealthPoints(),
                viewport.getWorldWidth()-50,
                viewport.getWorldHeight()-70,
                1, 1);
            GameRenderer.renderBar(batch, manabar, localPlayer.getMana(), localPlayer.getMaxMana(),
                viewport.getWorldWidth()-50,
                viewport.getWorldHeight()-70 - 60,
                1, 1);
            GameRenderer.renderBar(batch, ausdauerbar, localPlayer.getAusdauer(), localPlayer.getMaxAusdauer(),
                viewport.getWorldWidth()-50,
                viewport.getWorldHeight()-70 - 60*2,
                1, 1);
            renderDeathMessage();

            renderSkillSymbol();
            renderSkillBar();

            renderItemBarAndItems();

            renderGoldBar();
            batch.end();
        }
    }

    float nScale = 3f;
    public float getNScale() {
        return nScale;
    }
    public Vector2 getWindowSize() {
        return windowSize;
    }
    private void renderGoldBar(){
        Texture texture=assetManager.getGoldBarAsset();
        float barStartX=viewport.getWorldWidth()-texture.getWidth()-20;
        float barStartY=20;
        batch.draw(texture,barStartX,barStartY);

        glyphLayout.setText(goldBarfont,localPlayer.getGold()+"");
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;

        messageFont.draw(batch, glyphLayout, barStartX+140, barStartY+texture.getHeight()/2f);
    }
    private void renderSkillSymbol() {

        float startX=-15;
        float startY=10;
        float size=220;
        if(localPlayer.getMainSkillSymbol()!=null){
            batch.draw(localPlayer.getMainSkillSymbol(), startX,startY,size,size);
        }

        //render skill cooldown
        batch.end();
        // 4) Draw semi‐transparent background rectangle
        Gdx.gl.glEnable(GL20.GL_BLEND); // For proper alpha blending
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        float percentage=Math.min(localPlayer.getT1SkillCoolDownTimer(),localPlayer.getT1SkillCoolDownTime())/localPlayer.getT1SkillCoolDownTime();
        //Logger.log("percentage: "+percentage);
        shapeRenderer.rect(0, 0, size, MyMath.interpolate(0,size,1-percentage));
        shapeRenderer.end();

        batch.begin();
    }
    private void renderSkillBar(){
        batch.draw(assetManager.getSkillBarAsset(),0,0,500,500);
    }
    private void renderItemBarAndItems(){
        Texture texture=assetManager.getItemBarAssets();
        float scale=0.75f;
        float itemBarStartX=viewport.getWorldWidth()/2-texture.getWidth()*scale/2f;
        batch.draw(texture,itemBarStartX,0,texture.getWidth()*scale,texture.getHeight()*scale);

        for(int itemIndex=0 ; itemIndex<localPlayer.getItems().size() ; itemIndex++) {
            Item item=localPlayer.getItems().get(itemIndex);
            if(item!=null){
                item.render(batch,itemBarStartX+scale*(134+itemIndex*121),80*scale, scale);
            }
        }
    }
    private void renderDeathMessage() {
        if (isDeathMessageVisible && deathMessage != null) {
            deathMsgTimer += Gdx.graphics.getDeltaTime();

            // 2) Calculate current alpha
            float alpha = 1f;
            if (deathMsgTimer > deathMsgDuration) {
                float fadeTime = deathMsgTimer - deathMsgDuration;
                if (fadeTime > deathMsgFadeDuration) {
                    isDeathMessageVisible = false;
                    alpha = 0f;
                } else {
                    alpha = 1f - (fadeTime / deathMsgFadeDuration);
                }
            }

            // 3) Only render if alpha > 0
            if (alpha > 0f) {
                // Measure text with glyphLayout, if you want proper sizing
                glyphLayout.setText(messageFont, deathMessage);
                float textWidth = glyphLayout.width;
                float textHeight = glyphLayout.height;

                // Position top-left or wherever
                float padding = 20f;
                float x = 40f;
                float y = viewport.getWorldHeight() - 40f;

                float rectWidth  = textWidth + padding * 2;
                float rectHeight = textHeight + padding * 2;
                float rectX = x;
                float rectY = y - textHeight - padding*2;

                // --- IMPORTANT: End the batch before shape rendering ---
                batch.end();

                // 4) Draw semi‐transparent background rectangle
                Gdx.gl.glEnable(GL20.GL_BLEND); // For proper alpha blending
                shapeRenderer.setProjectionMatrix(uiCamera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(0f, 0f, 0f, 0.5f * alpha);
                shapeRenderer.rect(rectX, rectY, rectWidth, rectHeight);
                shapeRenderer.end();

                // 5) Re‐begin the batch for text
                batch.begin();
                messageFont.setColor(1f, 1f, 1f, alpha);
                messageFont.draw(batch, glyphLayout, x + padding, y - padding);
            }
        }
    }


    public void renderMarket(Batch batch, Texture texture) {
        float screenScaleX = Gdx.graphics.getWidth()/windowSize.x * nScale;
        float screenX = localPlayer.getX()-50f*screenScaleX;
        float screenY = localPlayer.getY()-75f* screenScaleX;
        batch.draw(texture, screenX, screenY, texture.getWidth()*screenScaleX, texture.getHeight()* screenScaleX);
    }
    public void renderMarketItems(Batch batch, Item[] items, ArrayList<Vector2> offset){
        float screenScaleX = Gdx.graphics.getWidth()/windowSize.x * nScale;

        for (int i=0; i<items.length; i++){

            Item item = items[i];
            Vector2 itemOffset = offset.get(i);
            if (item != null){



                batch.draw(item.getTexture(),
                    localPlayer.getX()+(itemOffset.x-51f)*screenScaleX,
                    localPlayer.getY()+(itemOffset.y-76f)*screenScaleX,
                    14f*screenScaleX, 14f*screenScaleX);
            }
        }
    }

    public void startSignRendering(float duration, float fadeDuration, float base) {
        isRenderingSign = true;
        this.duration = duration;
        this.fadeDuration = fadeDuration;
        this.signTimer = 0f;
        this.base = base;
    }
    public void showDeathMessage(String attacker, String target) {
        //Logger.log("[UI Debug]: showDeathMessage: " + attacker + " killed " + target);
        // Construct the message
        deathMessage = attacker + " killed " + target;
        isDeathMessageVisible = true;
        deathMsgTimer = 0f;  // reset timer
    }
    public boolean isRenderingSign(){
        return isRenderingSign;
    }
    public void resetRenderingSign(){
        isRenderingSign = false;
    }
    public float getDuration(){
        return duration;
    }
    public float getFadeDuration(){
        return fadeDuration;
    }
    public float getBase(){
        return base;
    }
    public float getSignTimer(){
        return signTimer;
    }
    public void addSignTimer(float delta){
        signTimer += delta;
    }
    public void resetTimer(){
        signTimer = 0;
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        for (Texture tex : healthbar) {
            tex.dispose();
        }
        batch.dispose();
        // Dispose font if you created it in create()
        if (messageFont != null) {
            messageFont.dispose();
        }
    }

}

