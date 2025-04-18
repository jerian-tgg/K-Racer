package io.github.KamoteRacer;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class GameScreen implements Screen {
    final KRacer game;
    final PowerUpValues powerUpValues;

    // Player 1 variables
    TextureAtlas motorAtlasPlayer1;
    Animation<Sprite> turnRightAnimationPlayer1;
    Animation<Sprite> turnLeftAnimationPlayer1;
    float animationTimePlayer1 = 0f;

    // Player 2 variables
    TextureAtlas motorAtlasPlayer2;
    Animation<Sprite> turnRightAnimationPlayer2;
    Animation<Sprite> turnLeftAnimationPlayer2;
    float animationTimePlayer2 = 0f;

    Texture roadTexture;
    Texture car1Texture;
    Texture car2Texture;
    Texture tryc1Texture;
    Texture bus1Texture;

    Texture resBut;
    Texture homeBut;
    Texture popUpEscBG;
    Texture resetBut;

    Texture p1WinsTexture;
    Texture p2WinsTexture;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite motor1Sprite;  // Player 1 sprite
    Sprite motor2Sprite;  // Player 2 sprite

    Array<Sprite> carSprites; // List of car objects
    Array<Float> carFallingSpeeds;
    float[] laneTimers;

    float laneSpawnDelay = 4f; // delay between car spawns in a lane
    boolean motor1Collided = false;
    boolean motor2Collided = false;
    boolean collisionDetected = false;// flag for collision detection
    float roadSpeed = 4; // scrolling speed of the road
    float roadY1;  // first road position
    float roadY2;  // second road position
    float score = 0; // score based on distance
    float spawnTimer; // Timer for vehicle spawning

    ShapeRenderer shapeRenderer;
    private boolean isDimmed;

    private final Sound collisionSound;
    private final Music backgroundMusic;

    public GameScreen(KRacer game, PowerUpValues powerUpValues) {
        this.game = game;
        this.powerUpValues = powerUpValues;


        // Initialize animations for Player 1
        motorAtlasPlayer1 = new TextureAtlas(Gdx.files.internal("motorMovementPlayer1.atlas"));
        turnRightAnimationPlayer1 = new Animation<>(0.05f,
            motorAtlasPlayer1.createSprite("4"),
            motorAtlasPlayer1.createSprite("5"));
        turnLeftAnimationPlayer1 = new Animation<>(0.05f,
            motorAtlasPlayer1.createSprite("2"),
            motorAtlasPlayer1.createSprite("1"));
        turnRightAnimationPlayer1.setPlayMode(Animation.PlayMode.NORMAL);
        turnLeftAnimationPlayer1.setPlayMode(Animation.PlayMode.NORMAL);

        // Initialize animations for Player 2
        motorAtlasPlayer2 = new TextureAtlas(Gdx.files.internal("motorMovementPlayer2.atlas"));
        turnRightAnimationPlayer2 = new Animation<>(0.05f, motorAtlasPlayer2.createSprite("4"), motorAtlasPlayer2.createSprite("5"));
        turnLeftAnimationPlayer2 = new Animation<>(0.05f, motorAtlasPlayer2.createSprite("2"), motorAtlasPlayer2.createSprite("1"));
        turnRightAnimationPlayer2.setPlayMode(Animation.PlayMode.NORMAL);
        turnLeftAnimationPlayer2.setPlayMode(Animation.PlayMode.NORMAL);

        // Initialize spawn timers for lanes
        spawnTimer = MathUtils.random(0f, laneSpawnDelay);

        collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision_sound.wav"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
        backgroundMusic.setVolume(0.25f); // Set volume (0.0 to 1.0)

        // Start the background music
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Set initial sprites for both players
        motor1Sprite = motorAtlasPlayer1.createSprite("3"); // Player 1 idle frame
        motor1Sprite.setSize(1, 2);

        motor2Sprite = motorAtlasPlayer2.createSprite("3"); // Player 2 idle frame
        motor2Sprite.setSize(1, 2);

        // Set Player 1 spawn
        motor1Sprite.setX(8.55f); // Center X position
        motor1Sprite.setY(0); // Adjust the Y value for spawn height

        // Set Player 2 spawn
        motor2Sprite.setX(3.15f); // Left edge of the screen
        motor2Sprite.setY(0); // Bottom of the screen

        roadTexture = new Texture("road.png");
        car1Texture = new Texture("car1.png");
        car2Texture = new Texture("car2.png");
        tryc1Texture = new Texture("tryc1.png");
        bus1Texture = new Texture("car3.png");

        popUpEscBG = new Texture("popUpEsc.png");
        resBut = new Texture("resBut.png");
        homeBut = new Texture("homeBut.png");
        resetBut = new Texture("resBut.png"); // Load your reset button texture

        p1WinsTexture = new Texture("p1Wins.png");
        p2WinsTexture = new Texture("p2Wins.png");

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12f, 10f);

        // Initialize road positions
        roadY1 = 0;
        roadY2 = viewport.getWorldHeight();

        carSprites = new Array<>();
        carFallingSpeeds = new Array<>();
        laneTimers = new float[4];
        for (int i = 0; i < laneTimers.length; i++) {
            laneTimers[i] = MathUtils.random(0f, laneSpawnDelay);
        }

        shapeRenderer = new ShapeRenderer();


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        draw();
        escMenu();
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
            float popupHeight = viewport.getWorldHeight() * 0.4f;
            float popupX = (viewport.getWorldWidth() - popupWidth) / 2;
            float popupY = (viewport.getWorldHeight() - popupHeight) / 2;

            spriteBatch.draw(popUpEscBG, popupX, popupY, popupWidth, popupHeight);

            float buttonWidth = viewport.getWorldWidth() * 0.05f;
            float buttonHeight = viewport.getWorldHeight() * 0.08f;
            float homeButX = 5f, homeButY = 3.5f;
            float resButX = 6.5f, resButY = 3.5f;

            spriteBatch.draw(homeBut, homeButX, homeButY, buttonWidth, buttonHeight);
            spriteBatch.draw(resBut, resButX, resButY, buttonWidth, buttonHeight);

            spriteBatch.end();

            // Check for button clicks
            if (Gdx.input.isTouched()) {
                float touchX = Gdx.input.getX() * viewport.getWorldWidth() / Gdx.graphics.getWidth();
                float touchY = (Gdx.graphics.getHeight() - Gdx.input.getY()) * viewport.getWorldHeight() / Gdx.graphics.getHeight();

                if (touchX > homeButX && touchX < homeButX + buttonWidth && touchY > homeButY && touchY < homeButY + buttonHeight) {
                    game.setScreen(new MainMenuScreen(game, powerUpValues)); // Go to Main Menu
                    dispose();
                }

                if (touchX > resButX && touchX < resButX + buttonWidth && touchY > resButY && touchY < resButY + buttonHeight) {
                    game.setScreen(new GameScreen(game, powerUpValues)); // Restart the game
                    dispose();
                }
            }
        }
    }

    private void input() {
        if (isDimmed || motor1Collided || motor2Collided) {
            return; // Block input if dimmed or a collision occurred
        }

        float delta = Gdx.graphics.getDeltaTime();
        float baseSpeed = 4f; // Base movement speed
        float moveX1 = 0f, moveY1 = 0f;
        float moveX2 = 0f, moveY2 = 0f;

        float P1forward = powerUpValues.P1forward;
        float P1backward = powerUpValues.P1backward;
        float P1diagonal = powerUpValues.P1diagonal;

        float P2forward = powerUpValues.P2forward;
        float P2backward = powerUpValues.P2backward;
        float P2diagonal = powerUpValues.P2diagonal;

        // Player 1 controls
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX1 += 1; // Move right
            animationTimePlayer1 += delta;
            motor1Sprite.setRegion(turnRightAnimationPlayer1.getKeyFrame(animationTimePlayer1));
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX1 -= 1; // Move left
            animationTimePlayer1 += delta;
            motor1Sprite.setRegion(turnLeftAnimationPlayer1.getKeyFrame(animationTimePlayer1));
        } else {
            animationTimePlayer1 = 0f; // Reset animation when no turning
            motor1Sprite.setRegion(motorAtlasPlayer1.findRegion("3")); // Idle frame for Player 1
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY1 += 1 * P1forward; // Apply Player 1 forward multiplier
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY1 -= 1 * P1backward; // Apply Player 1 backward multiplier
        }

        // Player 2 controls
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX2 += 1; // Move right
            animationTimePlayer2 += delta;
            motor2Sprite.setRegion(turnRightAnimationPlayer2.getKeyFrame(animationTimePlayer2));
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX2 -= 1; // Move left
            animationTimePlayer2 += delta;
            motor2Sprite.setRegion(turnLeftAnimationPlayer2.getKeyFrame(animationTimePlayer2));
        } else {
            animationTimePlayer2 = 0f; // Reset animation when no turning
            motor2Sprite.setRegion(motorAtlasPlayer2.findRegion("3")); // Idle frame for Player 2
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY2 += 1 * P2forward; // Apply Player 2 forward multiplier
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY2 -= 1 * P2backward; // Apply Player 2 backward multiplier
        }

        // Normalize diagonal movement to avoid faster movement on diagonals
        if (moveX1 != 0 && moveY1 != 0) {
            moveX1 *= P1diagonal;
            moveY1 *= P1diagonal;
        }

        if (moveX2 != 0 && moveY2 != 0) {
            moveX2 *= P2diagonal;
            moveY2 *= P2diagonal;
        }

        // Apply movement with base speed
        motor1Sprite.translate(moveX1 * baseSpeed * delta, moveY1 * baseSpeed * delta);
        motor2Sprite.translate(moveX2 * baseSpeed * delta, moveY2 * baseSpeed * delta);

        // Clamp positions within viewport
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        motor1Sprite.setX(MathUtils.clamp(motor1Sprite.getX(), 0, worldWidth - motor1Sprite.getWidth()));
        motor1Sprite.setY(MathUtils.clamp(motor1Sprite.getY(), 0, worldHeight - motor1Sprite.getHeight()));

        motor2Sprite.setX(MathUtils.clamp(motor2Sprite.getX(), 0, worldWidth - motor2Sprite.getWidth()));
        motor2Sprite.setY(MathUtils.clamp(motor2Sprite.getY(), 0, worldHeight - motor2Sprite.getHeight()));
    }



    private void logic() {
        if (isDimmed || collisionDetected) {
            return; // Exit the logic method early
        }

        float delta = Gdx.graphics.getDeltaTime();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float motor1Width = motor1Sprite.getWidth();
        float motor1Height = motor1Sprite.getHeight();

        float motor2Width = motor2Sprite.getWidth();
        float motor2Height = motor2Sprite.getHeight();

        // Update road and vehicles
        roadUpdate(delta);
        vehicleUpdate(delta);
        vehicleSpawner();

        //clamping
        motor1Sprite.setX(MathUtils.clamp(motor1Sprite.getX(), 0, worldWidth - motor1Width));
        motor1Sprite.setY(MathUtils.clamp(motor1Sprite.getY(), 0, worldHeight - motor1Height));
        motor2Sprite.setX(MathUtils.clamp(motor2Sprite.getX(), 0, worldWidth - motor2Width));
        motor2Sprite.setY(MathUtils.clamp(motor2Sprite.getY(), 0, worldHeight - motor2Height));


        Rectangle motor1Hitbox = setMotorHitbox(motor1Sprite);
        Rectangle motor2Hitbox = setMotorHitbox(motor2Sprite);

        for (Sprite car : carSprites) {
            if (!motor1Collided && motor1Hitbox.overlaps(car.getBoundingRectangle())) {
                motor1Collided = true;
                collisionSound.play();  // Play collision sound for Player 1
                collisionDetected = true;
                backgroundMusic.stop(); // Stop background music on collision
            }
            if (!motor2Collided && motor2Hitbox.overlaps(car.getBoundingRectangle())) {
                motor2Collided = true;
                collisionSound.play();  // Play collision sound for Player 2
                collisionDetected = true;
                backgroundMusic.stop(); // Stop background music on collision
            }

        }

        // Update score if no collision
        if (!motor1Collided && !motor2Collided) {
            score += delta * roadSpeed;
        }
    }

    private Rectangle setMotorHitbox(Sprite sprite) {
        float hitboxX = sprite.getX() + sprite.getWidth() * 0.30f;
        float hitboxY = sprite.getY() + sprite.getHeight() * 0.20f;
        float hitboxWidth = sprite.getWidth() * 0.35f;
        float hitboxHeight = sprite.getHeight() * 0.60f;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    private void roadUpdate(float delta) {
        float worldHeight = viewport.getWorldHeight();

        roadY1 -= roadSpeed * delta;
        roadY2 -= roadSpeed * delta;

        if (roadY1 + worldHeight <= 0) {
            roadY1 = roadY2 + worldHeight;
        }
        if (roadY2 + worldHeight <= 0) {
            roadY2 = roadY1 + worldHeight;
        }
    }

    private void vehicleUpdate(float delta) {

        for (int i = carSprites.size - 1; i >= 0; i--) { // car falling loop
            Sprite carSprite = carSprites.get(i);
            float carFallingSpeed = carFallingSpeeds.get(i);
            carSprite.translateY(-carFallingSpeed * delta); // fall speed = carFallingSpeed

            if (carSprite.getY() + carSprite.getHeight() < 0) { // removes car if out of the window
                carSprites.removeIndex(i); // removes the car from the array
                carFallingSpeeds.removeIndex(i);
            }
        }
    }

    private void vehicleSpawner () {
        // Increment the lane timers and check if it's time to spawn a car in each lane
        for (int i = 0; i < laneTimers.length; i++) {
            laneTimers[i] += Gdx.graphics.getDeltaTime();  // Increase timer for each lane

            if (laneTimers[i] >= laneSpawnDelay) {
                laneTimers[i] = 0f;  // Reset the timer
                createCars(i);  // Spawn a car in the lane
            }
        }
    }

    private void createCars(int laneIndex) {
        float carWidth; //car WIDTH with respect to FitViewport
        float carHeight; //car HEIGHT with respect to FitViewport
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite carSprite;
        Texture carsTexture;


        // here's where to add new vehicle (add 1 to the second parameter)
        int carType = MathUtils.random(0, 3);

        if (carType == 0) {
            carsTexture = car1Texture;
            carWidth = 2;
            carHeight = 3;
        } else if (carType == 1) {
            carsTexture = car2Texture;
            carWidth = 2;
            carHeight = 3;
        } else if (carType == 2) {
            carsTexture = tryc1Texture;
            carWidth = 1.71875f;
            carHeight = 2;
        } else {
            carsTexture = bus1Texture;
            carWidth = 2;
            carHeight = 4;
        }

        carSprite = new Sprite(carsTexture);
        carSprite.setSize(carWidth, carHeight);

        //added adjustable lane division
        float[] divisionStarts = {0.01f, worldWidth * 0.25f, worldWidth * 0.5f, worldWidth * 0.74f}; // You can adjust these positions
        float divisionStart = divisionStarts[laneIndex];// Get the starting X position for the lane

        carSprite.setX(divisionStart + MathUtils.random(0f, (worldWidth * 0.25f) - carWidth)); // random X within the division

        // Set the car's Y position to spawn at the top of the screen
        carSprite.setY(worldHeight); // Ensure the car spawns at the top of the screen, but within bounds

        boolean overlapDetected = false;
        for (Sprite existingCar : carSprites) {
            if (Math.abs(existingCar.getX() - carSprite.getX()) < carWidth &&
                Math.abs(existingCar.getY() - carSprite.getY()) < carHeight) {
                overlapDetected = true;
                break;  // exits the loop early if an overlap is detected
            }
        }

        // If overlap detected, don't add the car
        if (!overlapDetected) {
            carSprites.add(carSprite);

            float randomSpeed = MathUtils.random(1.75f, 2f); // assigns random speeds to new car (every index value creates a new car)
            carFallingSpeeds.add(randomSpeed);
        }
    }


    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Draw road
        spriteBatch.draw(roadTexture, 0, roadY1, worldWidth, worldHeight);
        spriteBatch.draw(roadTexture, 0, roadY2, worldWidth, worldHeight);

        // Draw motorbikes if no collision
        if (!motor1Collided) {
            motor1Sprite.draw(spriteBatch);
        }
        if (!motor2Collided) {
            motor2Sprite.draw(spriteBatch);
        }

        // Draw cars
        for (Sprite car : carSprites) {
            car.draw(spriteBatch);
        }

        // Draw win texture on top of everything if a collision occurred
        if (motor1Collided) {
            spriteBatch.draw(p2WinsTexture, 0, 0, worldWidth, worldHeight);
        }
        if (motor2Collided) {
            spriteBatch.draw(p1WinsTexture, 0, 0, worldWidth, worldHeight);
        }

        spriteBatch.end();
    }


    @Override
    public void dispose() {
        roadTexture.dispose();
        car1Texture.dispose();
        car2Texture.dispose();
        tryc1Texture.dispose();
        bus1Texture.dispose();
        spriteBatch.dispose();
        shapeRenderer.dispose();
        p1WinsTexture.dispose();
        p2WinsTexture.dispose();

        collisionSound.dispose();
        backgroundMusic.dispose();
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
    public void show() {
    }

}


