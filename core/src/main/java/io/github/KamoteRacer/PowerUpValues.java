package io.github.KamoteRacer;

public class PowerUpValues {
    public float P1diagonal, P1backward, P1forward;
    public float P2diagonal, P2backward, P2forward;

    // Constructor to initialize power-up values
    public PowerUpValues(float p1dia, float p1back, float p1for, float p2dia, float p2back, float p2for) {
        this.P1diagonal = p1dia;
        this.P1backward = p1back;
        this.P1forward = p1for;
        this.P2diagonal = p2dia;
        this.P2backward = p2back;
        this.P2forward = p2for;
    }
}

