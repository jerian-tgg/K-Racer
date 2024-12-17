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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
//========================
TextureAtlas motorAtlas;
    Animation<Sprite> turnRightAnimation;
    Animation<Sprite> turnLeftAnimation;
    float animationTime = 0f;

    Texture roadTexture;
    Texture motor1Texture;
    Texture car1Texture;
    Texture car2Texture;
    Texture tryc1Texture;
    Texture bus1Texture;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite motor1Sprite;

    Array<Sprite> carSprites;
    Array<Float> carFallingSpeeds;

    // New arrays for spawn timers for each lane
    float[] laneTimers;

    // 3.75 minimum ; lower than that cause car collision
    float laneSpawnDelay = 3.75f;  // delay between car spawns in a lane (in seconds) increase = less car spawn; decrease = more car spawn

    boolean collisionDetected = false; // flagged to indicate collision

    float roadSpeed = 4; //road scrolling speed
    float roadY1;  // first road position
    float roadY2;  // second road position






    @Override
    public void create() {

        motorAtlas = new TextureAtlas(Gdx.files.internal("motorMovement.atlas"));

        // Create animations (assuming frames 4 & 5 are for turning right, 2 & 1 for turning left)
        turnRightAnimation = new Animation<>(0.05f, motorAtlas.createSprite("4"), motorAtlas.createSprite("5"));
        turnLeftAnimation = new Animation<>(0.05f, motorAtlas.createSprite("2"), motorAtlas.createSprite("1"));
        turnRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        turnLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // Set the initial sprite
        motor1Sprite = motorAtlas.createSprite("3"); // Assuming "3" is the idle frame


        roadTexture = new Texture("road.png");
        motor1Texture = new Texture("motor1.png");
        car1Texture = new Texture("car1.png");
        car2Texture = new Texture("car2.png");
        tryc1Texture = new Texture("tryc1.png");
        bus1Texture = new Texture("bus1.png");

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(12.8f, 10.8f);

//        motor1Sprite = new Sprite(motor1Texture);
        motor1Sprite.setSize(1,2);

        //initialize road position
        roadY1 = 0; // sets the 1st road initial position to the bottom
        roadY2 = viewport.getWorldHeight(); //sets the 2nd road initial position to the bottom

        carSprites = new Array<>();
        carFallingSpeeds = new Array<>();

        laneTimers = new float[4];
        for (int i = 0; i < laneTimers.length; i++) {
            laneTimers[i] = MathUtils.random(0f, laneSpawnDelay);
        }
//        createCars();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();

    }

    private void input () {

        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();
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

        //reduces speed if move diagonally
        if (moveX != 0 && moveY != 0) {
            speed *= 0.7f;
        }

        //translates the movement, speed and delta value
        motor1Sprite.translateX(moveX * speed * delta);
        motor1Sprite.translateY(moveY * speed * delta);


    }

    private void logic () {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float motor1Width = motor1Sprite.getWidth();
        float motor1Height = motor1Sprite.getHeight();

        //clamps player movement not to go overbound(clamp = limit)
        motor1Sprite.setX(MathUtils.clamp(motor1Sprite.getX(),0,worldWidth - motor1Width));
        motor1Sprite.setY(MathUtils.clamp(motor1Sprite.getY(),0,worldHeight - motor1Height));

        float delta = Gdx.graphics.getDeltaTime();

        //collision detection function
        if (!collisionDetected) {
            roadUpdate(delta);
            vehicleUpdate(delta);
            vehicleSpawner();



            Rectangle motorHitbox = setMotorHitbox(); // initialized a Rectangle <variable> = <function = setMotorHitbox>
            for (Sprite carSprite : carSprites) {
                if (motorHitbox.overlaps(carSprite.getBoundingRectangle())) {
                    collisionDetected = true;
                    break;
                }
            }

        } else {
            for (Sprite carSprite : carSprites) {
                carSprite.translateY(delta * roadSpeed);
            }
        }
    }


    private Rectangle setMotorHitbox() {
        //  defined an adjustable hitbox for motor
        float hitboxX = motor1Sprite.getX() + motor1Sprite.getWidth() * 0.20f; // Offset by 20% of the width
        float hitboxY = motor1Sprite.getY() + motor1Sprite.getHeight() * 0.10f; // Offset by 10% of the height
        float hitboxWidth = motor1Sprite.getWidth() * 0.60f; // Reduce width to 60%
        float hitboxHeight = motor1Sprite.getHeight() * 0.80f; // Reduce height to 80%

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight); //returns parameters value when calledc
    }


    private void roadUpdate(float delta) {

//        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        //road scroll (movement downwards)
        roadY1 -= roadSpeed * delta;
        roadY2 -= roadSpeed * delta;

        //resets position when it goes over the screen
        if (roadY1 + worldHeight <= 0) {
            roadY1 = roadY2 + worldHeight;
        }
        if (roadY2 + worldHeight <= 0) {
            roadY2 = roadY1 + worldHeight;
        }
    }

    private void vehicleUpdate(float delta) {
        // updates car positions
        for (int i = carSprites.size - 1; i >= 0; i--) { // car falling loop
            Sprite carSprite = carSprites.get(i);
            float carFallingSpeed = carFallingSpeeds.get(i);
            carSprite.translateY(-carFallingSpeed * delta); // fallspeed = carFallingSpeed

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

    private void draw () {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        // Loop start
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();


        //prints/draw road
        spriteBatch.draw(roadTexture,0,roadY1,worldWidth,worldHeight);
        spriteBatch.draw(roadTexture,0,roadY2,worldWidth,worldHeight);

        //prints/draw motor sprite
        motor1Sprite.draw(spriteBatch);

        for (Sprite carSprite: carSprites ) {
            carSprite.draw(spriteBatch);
        }

        spriteBatch.end();
        //Loop end
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

            float randomSpeed = MathUtils.random(1f, 1.5f); // assigns random speeds to new car (every index value creates a new car)
            carFallingSpeeds.add(randomSpeed);
        }
    }



    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
        motorAtlas.dispose();

        roadTexture.dispose();
        motor1Texture.dispose();
        spriteBatch.dispose();
        car1Texture.dispose();
    }
}
