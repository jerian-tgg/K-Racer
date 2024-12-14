package io.github.KRacer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
//========================
    TextureAtlas motorAtlas;
    Animation<Sprite> turnRightAnimation;
    Animation<Sprite> turnLeftAnimation;
    float animationTime = 0f;
//    ====================


    Texture backgroundTexture;
    Texture motor1Texture;
    Texture car1Texture;
    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite motor1Sprite;

    float bg1Y = 0;
    float bg1Speed = 2f;

    Array<Sprite> car1Sprites;
    float car1Timer;

    @Override
    public void create() {

        // Initialize motorAtlas (make sure "motor.atlas" exists in your assets folder)
        motorAtlas = new TextureAtlas(Gdx.files.internal("motorMovement.atlas"));

        // Create animations (assuming frames 4 & 5 are for turning right, 2 & 1 for turning left)
        turnRightAnimation = new Animation<>(0.05f, motorAtlas.createSprite("4"), motorAtlas.createSprite("5"));
        turnLeftAnimation = new Animation<>(0.05f, motorAtlas.createSprite("2"), motorAtlas.createSprite("1"));
        turnRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        turnLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // Set the initial sprite
        motor1Sprite = motorAtlas.createSprite("3"); // Assuming "3" is the idle frame


        backgroundTexture = new Texture("bg1.png");
        motor1Texture = new Texture("motor1.png");
        car1Texture = new Texture("car1.png");
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12.8f, 10.8f);
        motor1Sprite = new Sprite(motor1Texture); // Initialize the sprite based on the texture
        motor1Sprite.setSize(1, 2); // Define the size of the sprite
        car1Sprites = new Array<>();

        createCar1();
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

        // Determine movement direction
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX += 1;
            animationTime += delta; // Increment animation time
            motor1Sprite.setRegion(turnRightAnimation.getKeyFrame(animationTime)); // Use right-turn animation
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX -= 1;
            animationTime += delta;
            motor1Sprite.setRegion(turnLeftAnimation.getKeyFrame(animationTime)); // Use left-turn animation
        } else {
            animationTime = 0f; // Reset animation time when no input
            motor1Sprite.setRegion(motorAtlas.findRegion("3")); // Idle frame
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY -= 1;
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

        float delta = Gdx.graphics.getDeltaTime();

        for (int i = car1Sprites.size - 1; i >= 0; i--) { // Handles car1 downward movement
            Sprite car1Sprite = car1Sprites.get(i);
            car1Sprite.translateY(-2f * delta); // Moves car1 downward

            if (car1Sprite.getY() < -car1Sprite.getHeight()) car1Sprites.removeIndex(i); // Removes car1 when off-screen
        }

        car1Timer += delta;
        if (car1Timer > 2f) { // Spawns new car1 periodically
            car1Timer = 0;
            createCar1(); // Creates new car1 at random X position
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

        for (Sprite car1Sprite : car1Sprites) {
            car1Sprite.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void createCar1() {
        float car1Width = 2;
        float car1Height = 2;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Divide the background into 4 equal parts
        float sectionWidth = worldWidth / 4;

        // Randomly select one of the 4 sections
        int section = MathUtils.random(0, 3);

        // Calculate the x-position of the car within the chosen section
        float car1X = section * sectionWidth + (sectionWidth - car1Width) / 2; // Center in section

        // Create and position the car
        Sprite car1Sprite = new Sprite(car1Texture);
        car1Sprite.setSize(car1Width, car1Height);
        car1Sprite.setX(car1X); // Set to the calculated x-position
        car1Sprite.setY(worldHeight); // Start from the top of the screen
        car1Sprites.add(car1Sprite);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {

        // Destroy application's resources here.
        motorAtlas.dispose();

        motor1Texture.dispose();
        spriteBatch.dispose();
    }
}
