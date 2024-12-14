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

    float bg1Y = 0;
    float bg1Speed = 2f;

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
        float speed = 4f; // Speed of the bike
        float delta = Gdx.graphics.getDeltaTime();

        // Initialize movement variables
        float moveX = 0f;
        float moveY = 0f;

        // Check for key presses
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY -= 0.7f;
        }

        // Scale speed if moving diagonally
        if (moveX != 0 && moveY != 0) {
            speed *= 0.7f;
        }

        // Apply movement
        motor1Sprite.translateX(moveX * speed * delta);
        motor1Sprite.translateY(moveY * speed * delta);

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
        bg1Y -= bg1Speed * Gdx.graphics.getDeltaTime();
        if (bg1Y <= -viewport.getWorldHeight()) {
            bg1Y += viewport.getWorldHeight(); // Reset the position for looping
        }

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Draw the background twice to create a looping effect
        spriteBatch.draw(backgroundTexture, 0, bg1Y, worldWidth, worldHeight);
        spriteBatch.draw(backgroundTexture, 0, bg1Y + worldHeight, worldWidth, worldHeight);

        motor1Sprite.draw(spriteBatch); // Draw the sprite on top of the background

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
