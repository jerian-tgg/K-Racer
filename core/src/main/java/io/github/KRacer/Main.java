package io.github.KRacer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.Color;

public class Main implements ApplicationListener {

    Texture motor1Texture;
    SpriteBatch spriteBatch;
    FitViewport viewport;

    @Override
    public void create() {
        motor1Texture = new Texture("motor1.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12.8f, 10.8f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        // Handle input logic here
    }

    private void logic() {
        // Handle game logic here
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.draw(motor1Texture, 0, 0, 1, 2);
        spriteBatch.end();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        motor1Texture.dispose();
        spriteBatch.dispose();
    }
}
