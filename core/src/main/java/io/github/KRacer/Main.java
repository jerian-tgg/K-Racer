package io.github.KRacer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class Main implements ApplicationListener {

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
    Texture motor1Texture;
    Texture car1Texture;
    Texture car2Texture;
    Texture tryc1Texture;
    Texture bus1Texture;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite motor1Sprite;  // Player 1 sprite
    Sprite motor2Sprite;  // Player 2 sprite

    Array<Car> carObjects; // List of car objects

    float laneSpawnDelay = 3.75f; // delay between car spawns in a lane
    boolean collisionDetected = false; // flag for collision detection
    float roadSpeed = 4; // scrolling speed of the road
    float roadY1;  // first road position
    float roadY2;  // second road position
    float score = 0; // score based on distance
    float spawnTimer = 0f; // Timer for vehicle spawning

    ShapeRenderer shapeRenderer;
    private boolean isDimmed;


    @Override
    public void create() {
        viewport = new FitViewport(12.8f, 10.8f);

        // Initialize animations for Player 1
        motorAtlasPlayer1 = new TextureAtlas(Gdx.files.internal("motorMovementPlayer1.atlas"));
        turnRightAnimationPlayer1 = new Animation<>(0.05f, motorAtlasPlayer1.createSprite("4"), motorAtlasPlayer1.createSprite("5"));
        turnLeftAnimationPlayer1 = new Animation<>(0.05f, motorAtlasPlayer1.createSprite("2"), motorAtlasPlayer1.createSprite("1"));
        turnRightAnimationPlayer1.setPlayMode(Animation.PlayMode.NORMAL);
        turnLeftAnimationPlayer1.setPlayMode(Animation.PlayMode.NORMAL);

        // Initialize animations for Player 2
        motorAtlasPlayer2 = new TextureAtlas(Gdx.files.internal("motorMovementPlayer2.atlas"));
        turnRightAnimationPlayer2 = new Animation<>(0.05f, motorAtlasPlayer2.createSprite("4"), motorAtlasPlayer2.createSprite("5"));
        turnLeftAnimationPlayer2 = new Animation<>(0.05f, motorAtlasPlayer2.createSprite("2"), motorAtlasPlayer2.createSprite("1"));
        turnRightAnimationPlayer2.setPlayMode(Animation.PlayMode.NORMAL);
        turnLeftAnimationPlayer2.setPlayMode(Animation.PlayMode.NORMAL);

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
        motor1Texture = new Texture("motorMovementPlayer1.png");
        car1Texture = new Texture("car1.png");
        car2Texture = new Texture("car2.png");
        tryc1Texture = new Texture("tryc1.png");
        bus1Texture = new Texture("bus1.png");

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12.8f, 10.8f);

        // Initialize road positions
        roadY1 = 0;
        roadY2 = viewport.getWorldHeight();

        carObjects = new Array<>();

        shapeRenderer = new ShapeRenderer();

        // Initialize spawn timers for lanes
        spawnTimer = MathUtils.random(0f, laneSpawnDelay);
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
        escMenu();
    }

    private void escMenu () {
        // Check for ESC key press to toggle dimming
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            isDimmed = !isDimmed; // Toggle dimming
        }

        // Draw the dim overlay if dimming is active
        if (isDimmed) {
            Gdx.gl.glEnable(GL20.GL_BLEND); // Enable transparency blending
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.63f, 0.13f, 0.94f,0.9f); // values are percentage
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND); // Disable blending to prevent affecting other rendering
        }
    }

    private void input() {

        // if isDimmed = true, inputs isn't processed
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
            motor1Sprite.setRegion(turnRightAnimationPlayer1.getKeyFrame(animationTimePlayer1));
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX1 -= 1;
            animationTimePlayer1 += delta;
            motor1Sprite.setRegion(turnLeftAnimationPlayer1.getKeyFrame(animationTimePlayer1));
        } else {
            animationTimePlayer1 = 0f;
            motor1Sprite.setRegion(motorAtlasPlayer1.findRegion("3")); // Idle frame for Player 1
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY1 += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY1 -= 1;
        }

        // Player 2 controls
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX2 += 1;
            animationTimePlayer2 += delta;
            motor2Sprite.setRegion(turnRightAnimationPlayer2.getKeyFrame(animationTimePlayer2));
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX2 -= 1;
            animationTimePlayer2 += delta;
            motor2Sprite.setRegion(turnLeftAnimationPlayer2.getKeyFrame(animationTimePlayer2));
        } else {
            animationTimePlayer2 = 0f;
            motor2Sprite.setRegion(motorAtlasPlayer2.findRegion("3")); // Idle frame for Player 2
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveY2 += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveY2 -= 1;
        }

        // Diagonal movement speed adjustment
        if (moveX1 != 0 && moveY1 != 0) {
            speed *= 0.7f;
        }
        if (moveX2 != 0 && moveY2 != 0) {
            speed *= 0.7f;
        }

        motor1Sprite.translateX(moveX1 * speed * delta);
        motor1Sprite.translateY(moveY1 * speed * delta);

        motor2Sprite.translateX(moveX2 * speed * delta);
        motor2Sprite.translateY(moveY2 * speed * delta);
    }

    private void logic() {

        if (isDimmed) {
            return; // Exit the logic method early
        }

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float motor1Width = motor1Sprite.getWidth();
        float motor1Height = motor1Sprite.getHeight();

        float motor2Width = motor2Sprite.getWidth();
        float motor2Height = motor2Sprite.getHeight();

        // Clamp player movement
        motor1Sprite.setX(MathUtils.clamp(motor1Sprite.getX(), 0, worldWidth - motor1Width));
        motor1Sprite.setY(MathUtils.clamp(motor1Sprite.getY(), 0, worldHeight - motor1Height));
        motor2Sprite.setX(MathUtils.clamp(motor2Sprite.getX(), 0, worldWidth - motor2Width));
        motor2Sprite.setY(MathUtils.clamp(motor2Sprite.getY(), 0, worldHeight - motor2Height));

        float delta = Gdx.graphics.getDeltaTime();

        // Collision detection
        if (!collisionDetected) {
            roadUpdate(delta);
            vehicleUpdate(delta);
            vehicleSpawner(delta);

            Rectangle motor1Hitbox = setMotorHitbox(motor1Sprite);
            Rectangle motor2Hitbox = setMotorHitbox(motor2Sprite);

            for (Car car : carObjects) {
                if (motor1Hitbox.overlaps(car.sprite.getBoundingRectangle()) || motor2Hitbox.overlaps(car.sprite.getBoundingRectangle())) {
                    collisionDetected = true;
                    break;
                }
            }
        } else {
            for (Car car : carObjects) {
                car.sprite.translateY(delta * roadSpeed);
            }
        }

        // Update score based on distance
        score += delta * roadSpeed;
    }

    private Rectangle setMotorHitbox(Sprite sprite) {
        float hitboxX = sprite.getX() + sprite.getWidth() * 0.20f;
        float hitboxY = sprite.getY() + sprite.getHeight() * 0.10f;
        float hitboxWidth = sprite.getWidth() * 0.60f;
        float hitboxHeight = sprite.getHeight() * 0.80f;

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
        for (int i = carObjects.size - 1; i >= 0; i--) {
            Car car = carObjects.get(i);
            car.sprite.translateY(-car.speed * delta);

            if (car.sprite.getY() + car.sprite.getHeight() < 0) {
                carObjects.removeIndex(i);
            }
        }
    }

    private void vehicleSpawner(float delta) {
        spawnTimer += delta;

        if (spawnTimer >= laneSpawnDelay) {
            if (carObjects.size < 5) { // Limit the number of spawned cars
                createCar();
            }
            spawnTimer = 0f; // Reset spawn timer
        }
    }

    private void createCar() {
        float carWidth;
        float carHeight;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite carSprite;
        Texture carsTexture;
        float speed = MathUtils.random(1f, 1.5f);

        int carType = MathUtils.random(0, 2);

        if (carType == 0) {
            carsTexture = car1Texture;
            carWidth = 2;
            carHeight = 3;
        } else if (carType == 1) {
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

        float divisionStart = worldWidth * MathUtils.random(0.25f, 0.75f);

        carSprite.setX(divisionStart - carWidth / 2);
        carSprite.setY(worldHeight);

        Car car = new Car(carSprite, speed);
        carObjects.add(car);
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

        // Draw motorbikes
        motor1Sprite.draw(spriteBatch);
        motor2Sprite.draw(spriteBatch);

        // Draw cars
        for (Car car : carObjects) {
            car.sprite.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        motorAtlasPlayer1.dispose();
        motorAtlasPlayer2.dispose();
        roadTexture.dispose();
        motor1Texture.dispose();
        spriteBatch.dispose();
        car1Texture.dispose();
        tryc1Texture.dispose();
        bus1Texture.dispose();
    }

    // Car class to hold car sprite and speed together
    static class Car {
        Sprite sprite;
        float speed;

        Car(Sprite sprite, float speed) {
            this.sprite = sprite;
            this.speed = speed;
        }
    }
}
