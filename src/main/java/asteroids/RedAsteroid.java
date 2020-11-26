package asteroids;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import java.util.Random;

public class RedAsteroid extends Asteroid {

    public RedAsteroid(int x, int y) {
        super(x, y);
        Random rnd = new Random();
        super.getCharacter().setRotate(rnd.nextInt(360));
        int accelerationAmount = 50 + rnd.nextInt(10);
        for (int i = 0; i < accelerationAmount; i++) {
            super.accelerate();
        }
        super.setRotationalMovement(1 - rnd.nextDouble());
        super.getCharacter().setFill(Color.RED);

    }
}
