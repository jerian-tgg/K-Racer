package io.github.KamoteRacer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class KRacer extends Game {

    // Instance of PowerUpValues to manage power-up effects
    public PowerUpValues powerUpValues;

    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

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
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
