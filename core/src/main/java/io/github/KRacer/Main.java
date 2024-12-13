package io.github.KRacer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Main implements ApplicationListener {

    Texture backgroundTexture;
    Texture motor1Texture;
    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite motor1Sprite;

    @Override
    public void create() {
        backgroundTexture = new Texture("bg1.png");
        motor1Texture = new Texture("motor1.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12.8f, 10.8f);
        motor1Sprite = new Sprite(motor1Texture); // Initialize the sprite based on the texture
        motor1Sprite.setSize(1, 2); // Define the size of the sprite
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
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            motor1Sprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            motor1Sprite.translateX(-speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            motor1Sprite.translateY(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            motor1Sprite.translateY(-speed * delta);
        }

        // Prevent sprite from going out of bounds
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float spriteX = motor1Sprite.getX();
        float spriteY = motor1Sprite.getY();

        if (spriteX < 0) motor1Sprite.setX(0);
        if (spriteX + motor1Sprite.getWidth() > worldWidth)
            motor1Sprite.setX(worldWidth - motor1Sprite.getWidth());

        if (spriteY < 0) motor1Sprite.setY(0);
        if (spriteY + motor1Sprite.getHeight() > worldHeight)
            motor1Sprite.setY(worldHeight - motor1Sprite.getHeight());
    }

    private void logic() {
        // Handle game logic here
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // store the worldWidth and worldHeight as local variables for brevity
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // draw the background
        motor1Sprite.draw(spriteBatch); // Sprites have their own draw method

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
