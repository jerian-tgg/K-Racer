package io.github.KRacer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

    private final io.github.KRacer.KRacer game;
    private SpriteBatch spriteBatch;
    private FitViewport viewport;

    private Texture backgroundTexture;
    private Sprite character1Sprite, character2Sprite, motorcycleSprite, motorcycle2Sprite;
    private TextureAtlas characterAtlasPlayer1, characterAtlasPlayer2;
    private Animation<Sprite> turnRightAnimationPlayer1;
    private Animation<Sprite> turnRightAnimationPlayer2;
    private Animation<Sprite> turnLeftAnimationPlayer1;
    private Animation<Sprite> turnLeftAnimationPlayer2;
    private Animation<Sprite> turnUpAnimationPlayer1;
    private Animation<Sprite> turnUpAnimationPlayer2;
    private Animation<Sprite> turnDownAnimationPlayer1;
    private Animation<Sprite> turnDownAnimationPlayer2;
    private float animationTimePlayer1 = 0f, animationTimePlayer2 = 0f;


    private Texture homeBut, popUpEscBG, xBut;
    private boolean gameStartTriggered;
    private ShapeRenderer shapeRenderer;
    private boolean isDimmed;

    private Music backgroundMusic;


    public MainMenuScreen(final io.github.KRacer.KRacer game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(12.8f, 9.6f);
        spriteBatch = new SpriteBatch();

        // Load background texture and motorcycle texture
        backgroundTexture = new Texture("menu_background.png");
        Texture motorcycleTexture = new Texture("menuMotor1.png");
        Texture motorcycle2Texture = new Texture("menuMotor2.png");

        // Load and configure the background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_bg_music.mp3"));
        backgroundMusic.setLooping(true); // Loop the music
        backgroundMusic.setVolume(0.5f); // Set volume (0.0 to 1.0)
        backgroundMusic.play(); // Start playing the music

        // Initialize motorcycle sprite
        motorcycleSprite = new Sprite(motorcycleTexture);
        motorcycleSprite.setSize(1f, 0.55f);
        motorcycleSprite.setPosition(8.3f, 4.7f);

        motorcycle2Sprite = new Sprite(motorcycle2Texture);
        motorcycle2Sprite.setSize(1f, 0.55f);
        motorcycle2Sprite.setPosition(8.3f, 5.82f);

        // Load the character texture atlas
        try {
            characterAtlasPlayer1 = new TextureAtlas(Gdx.files.internal("spriteMovement1.atlas"));
            characterAtlasPlayer2 = new TextureAtlas(Gdx.files.internal("spriteMovement2.atlas"));

            // Load animations using provided frame regions
            turnRightAnimationPlayer1 = new Animation<>(0.15f,
                characterAtlasPlayer1.createSprite("right1"),
                characterAtlasPlayer1.createSprite("right2"));
            turnRightAnimationPlayer1.setPlayMode(Animation.PlayMode.LOOP);

            turnLeftAnimationPlayer1 = new Animation<>(0.15f,
                characterAtlasPlayer1.createSprite("left1"),
                characterAtlasPlayer1.createSprite("left2"));
            turnLeftAnimationPlayer1.setPlayMode(Animation.PlayMode.LOOP);

            turnUpAnimationPlayer1 = new Animation<>(0.15f,
                characterAtlasPlayer1.createSprite("back1"),
                characterAtlasPlayer1.createSprite("back2"));
            turnUpAnimationPlayer1.setPlayMode(Animation.PlayMode.LOOP);

            turnDownAnimationPlayer1 = new Animation<>(0.15f,
                characterAtlasPlayer2.createSprite("front1"),
                characterAtlasPlayer2.createSprite("front2"));
            turnDownAnimationPlayer1.setPlayMode(Animation.PlayMode.LOOP);

            turnRightAnimationPlayer2 = new Animation<>(0.15f,
                characterAtlasPlayer2.createSprite("right1"),
                characterAtlasPlayer2.createSprite("right2"));
            turnRightAnimationPlayer2.setPlayMode(Animation.PlayMode.LOOP);

            turnLeftAnimationPlayer2 = new Animation<>(0.15f,
                characterAtlasPlayer2.createSprite("left1"),
                characterAtlasPlayer2.createSprite("left2"));
            turnLeftAnimationPlayer2.setPlayMode(Animation.PlayMode.LOOP);

            turnUpAnimationPlayer2 = new Animation<>(0.15f,
                characterAtlasPlayer2.createSprite("back1"),
                characterAtlasPlayer2.createSprite("back2"));
            turnUpAnimationPlayer2.setPlayMode(Animation.PlayMode.LOOP);

            turnDownAnimationPlayer2 = new Animation<>(0.15f,
                characterAtlasPlayer2.createSprite("front1"),
                characterAtlasPlayer2.createSprite("front2"));
            turnDownAnimationPlayer2.setPlayMode(Animation.PlayMode.LOOP);
            // Default character sprites for idle state
            character1Sprite = new Sprite(characterAtlasPlayer1.findRegion("frontIdle"));
            character2Sprite = new Sprite(characterAtlasPlayer2.findRegion("frontIdle"));
            character1Sprite.setSize(0.25f, 0.5f);
            character2Sprite.setSize(0.25f, 0.5f);
            character1Sprite.setPosition(0, 0);
            character2Sprite.setPosition(0, 0);

        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Error loading spriteMovement atlas", e);
        }

        // Load other UI elements
        popUpEscBG = new Texture("popUpEsc.png");
        homeBut = new Texture("homeBut.png");
        xBut = new Texture("xBut.png");

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(Color.BLACK);

        // Handle ESC menu toggle
        escMenu();

        // Render the game only if not paused
        if (!isDimmed) {
            input(); // Process input
            checkGameStart(); // Handle game logic

            spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
            spriteBatch.begin();

            // Draw game objects
            spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            motorcycleSprite.draw(spriteBatch);
            motorcycle2Sprite.draw(spriteBatch);
            character1Sprite.draw(spriteBatch);
            character2Sprite.draw(spriteBatch);


            spriteBatch.end();
        }
    }

    private void escMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isDimmed = !isDimmed;
        }

        if (isDimmed) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 0.9f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            spriteBatch.begin();
            float popupWidth = viewport.getWorldWidth() * 0.6f;
            float popupHeight = viewport.getWorldHeight() * 0.3f;
            float popupX = (viewport.getWorldWidth() - popupWidth) / 2;
            float popupY = (viewport.getWorldHeight() - popupHeight) / 2;

            if (popUpEscBG != null) {
                spriteBatch.draw(popUpEscBG, popupX, popupY, popupWidth, popupHeight);
            }

            float buttonWidth = viewport.getWorldWidth() * 0.05f;
            float buttonHeight = viewport.getWorldHeight() * 0.06f;
            float homeButX = 6.5f, homeButY = 3.2f;
            float xButX = 8.5f, xButY = 3.2f;

            spriteBatch.draw(homeBut, homeButX, homeButY, buttonWidth, buttonHeight);
            spriteBatch.draw(xBut, xButX, xButY, buttonWidth, buttonHeight);

            spriteBatch.end();

            // Check for button clicks
            if (Gdx.input.isTouched()) {
                float touchX = Gdx.input.getX() * viewport.getWorldWidth() / Gdx.graphics.getWidth();
                float touchY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * viewport.getWorldHeight() / Gdx.graphics.getHeight();

                if (touchX > homeButX && touchX < homeButX + buttonWidth && touchY > homeButY && touchY < homeButY + buttonHeight) {
                    game.setScreen(new MainMenuScreen(game)); // Go to Main Menu
                    dispose();
                }

                if (touchX > xButX && touchX < xButX + buttonWidth && touchY > xButY && touchY < xButY + buttonHeight) {
                    Gdx.app.exit(); // Quit the game
                }
            }
        }
    }

    private void input() {
        if (isDimmed) {
            return; // Exit the input method early
        }

        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();
        float moveX1 = 0f, moveY1 = 0f;
        float moveX2 = 0f, moveY2 = 0f;

        // Player 1 controls
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX1 += 1;
            animationTimePlayer1 += delta;
            character1Sprite.setRegion(turnRightAnimationPlayer1.getKeyFrame(animationTimePlayer1));
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX1 -= 1;
            animationTimePlayer1 += delta;
            character1Sprite.setRegion(turnLeftAnimationPlayer1.getKeyFrame(animationTimePlayer1));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY1 += 1;
            if (moveX1 == 0) { // Use "TurnUp" animation only when not moving diagonally
                animationTimePlayer1 += delta;
                character1Sprite.setRegion(turnUpAnimationPlayer1.getKeyFrame(animationTimePlayer1));
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY1 -= 1;
            animationTimePlayer1 += delta;
            character1Sprite.setRegion(turnDownAnimationPlayer1.getKeyFrame(animationTimePlayer1));
        }

        if (moveX1 == 0 && moveY1 == 0) { // Use idle frame if no movement
            animationTimePlayer1 = 0f;
            character1Sprite.setRegion(characterAtlasPlayer1.findRegion("frontIdle"));
        }

        // Player 2 controls
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX2 += 1;
            animationTimePlayer2 += delta;
            character2Sprite.setRegion(turnRightAnimationPlayer2.getKeyFrame(animationTimePlayer2));
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX2 -= 1;
            animationTimePlayer2 += delta;
            character2Sprite.setRegion(turnLeftAnimationPlayer2.getKeyFrame(animationTimePlayer2));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY2 += 1;
            if (moveX2 == 0) { // Use "TurnUp" animation only when not moving diagonally
                animationTimePlayer2 += delta;
                character2Sprite.setRegion(turnUpAnimationPlayer2.getKeyFrame(animationTimePlayer2));
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY2 -= 1;
            animationTimePlayer2 += delta;
            character2Sprite.setRegion(turnDownAnimationPlayer2.getKeyFrame(animationTimePlayer2));
        }

        if (moveX2 == 0 && moveY2 == 0) { // Use idle frame if no movement
            animationTimePlayer2 = 0f;
            character2Sprite.setRegion(characterAtlasPlayer2.findRegion("frontIdle"));
        }

        // Adjust speed for diagonal movement
        if (moveX1 != 0 && moveY1 != 0) {
            speed *= 0.7f;
        }
        if (moveX2 != 0 && moveY2 != 0) {
            speed *= 0.7f;
        }

        // Move the sprites according to player input
        character1Sprite.translateX(moveX1 * speed * delta);
        character1Sprite.translateY(moveY1 * speed * delta);

        character2Sprite.translateX(moveX2 * speed * delta);
        character2Sprite.translateY(moveY2 * speed * delta);

        // Keep characters within screen bounds
        character1Sprite.setX(MathUtils.clamp(character1Sprite.getX(), 0, viewport.getWorldWidth() - character1Sprite.getWidth()));
        character1Sprite.setY(MathUtils.clamp(character1Sprite.getY(), 0, viewport.getWorldHeight() - character1Sprite.getHeight()));

        character2Sprite.setX(MathUtils.clamp(character2Sprite.getX(), 0, viewport.getWorldWidth() - character2Sprite.getWidth()));
        character2Sprite.setY(MathUtils.clamp(character2Sprite.getY(), 0, viewport.getWorldHeight() - character2Sprite.getHeight()));
    }


    private void checkGameStart() {
        // Collision detection with the motorcycle
        Rectangle char1Bounds = character1Sprite.getBoundingRectangle();
        Rectangle char2Bounds = character2Sprite.getBoundingRectangle();
        Rectangle motorcycleBounds = motorcycleSprite.getBoundingRectangle();
        Rectangle motorcycle2Bounds = motorcycle2Sprite.getBoundingRectangle();

        if (char1Bounds.overlaps(motorcycleBounds) && char2Bounds.overlaps(motorcycle2Bounds)) {
            gameStartTriggered = true;
        }

        // Trigger game start
        if (gameStartTriggered) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        characterAtlasPlayer1.dispose();
        characterAtlasPlayer2.dispose();
        motorcycleSprite.getTexture().dispose();
        motorcycle2Sprite.getTexture().dispose();

        backgroundMusic.dispose();
    }
}
