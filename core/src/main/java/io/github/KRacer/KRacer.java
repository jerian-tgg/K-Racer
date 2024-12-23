package io.github.KRacer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
<<<<<<< HEAD


public class KRacer extends Game {

=======
import io.github.KRacer.PowerUpValues;

public class KRacer extends Game {

    // Instance of PowerUpValues to manage power-up effects
    public PowerUpValues powerUpValues;

>>>>>>> 4fdbe3e54f9a60d3da31c69bee6285b9c41c742d
    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

<<<<<<< HEAD

    public void create() {
        batch = new SpriteBatch();
        // use libGDX's default font
        font = new BitmapFont();
        viewport = new FitViewport(8, 5);

        //font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); // important!
    }

=======
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(8, 5);

        // Scale font to match viewport
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        // Initialize PowerUpValues with default values
        powerUpValues = new PowerUpValues(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
        initializePowerUpValues();

        // Pass PowerUpValues instance to MainMenuScreen
        this.setScreen(new MainMenuScreen(this, powerUpValues));
    }

    /**
     * Initializes default values for PowerUpValues.
     */
    private void initializePowerUpValues() {
        powerUpValues.P1diagonal = 1.0f;
        powerUpValues.P1backward = 1.0f;
        powerUpValues.P1forward = 1.0f;
        powerUpValues.P2diagonal = 1.0f;
        powerUpValues.P2backward = 1.0f;
        powerUpValues.P2forward = 1.0f;
    }

    @Override
    public void render() {
        super.render(); // Required for rendering the active screen
    }

    @Override
>>>>>>> 4fdbe3e54f9a60d3da31c69bee6285b9c41c742d
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
<<<<<<< HEAD

=======
>>>>>>> 4fdbe3e54f9a60d3da31c69bee6285b9c41c742d
}
