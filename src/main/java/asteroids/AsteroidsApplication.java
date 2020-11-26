package asteroids;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AsteroidsApplication extends Application {

    public static void main(String[] args) {
        launch(AsteroidsApplication.class);
    }

    private static int HIGH_SCORE;

    public static void setHighScore(int highScore) {
        HIGH_SCORE = highScore;
    }

    public static void writeHighScore(int highScore) {
        try {
            FileWriter myWriter = new FileWriter("highScore.txt");
            myWriter.write(String.valueOf(highScore));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int partsCompleted() {
        // State how many parts you have completed using the return value of this method
        return 4;
    }

    public static int WIDTH = 300;
    public static int HEIGHT = 200;

    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        int highScoreFromFile = 0;

        Scanner reader = new Scanner(new File("highScore.txt"));
        while (reader.hasNextLine()) {
            highScoreFromFile = Integer.valueOf(reader.nextLine());
        }

        setHighScore(highScoreFromFile);


        Text text = new Text(10, 20, "Points: 0");
        Text highScore = new Text(10,30,"High Score: "+ HIGH_SCORE);

        AtomicInteger points = new AtomicInteger();

//        Polygon ship = new Polygon(-5, -5, 10 , 0, -5, 5);
//        ship.setTranslateX(300);
//        ship.setTranslateY(200);
        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);

        List<Asteroid> asteroids = new ArrayList<>();

        List<Projectile> projectiles = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }

        pane.getChildren().add(ship.getCharacter());
        pane.getChildren().add(text);
        pane.getChildren().add(highScore);

        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));

        asteroids.forEach(asteroid -> asteroid.accelerate());
        asteroids.forEach(asteroid -> asteroid.accelerate());

        Scene scene = new Scene(pane);

//        scene.setOnKeyPressed(keyEvent -> {
//            if (keyEvent.getCode() == KeyCode.LEFT) {
//                ship.setRotate(ship.getRotate()-10);
//            }
//            if (keyEvent.getCode() == KeyCode.RIGHT) {
//                ship.setRotate(ship.getRotate()+10);
//            }
//        });
//
//        this has been avoided because key press is paused after first press

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });

        new AnimationTimer() {

            @Override
            public void handle(long now) {


                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
//                    ship.setRotate(ship.getRotate() - 5);
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
//                    ship.setRotate(ship.getRotate() + 5);
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) && projectiles.size() < 3) {
                    // we shoot
                    Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                    projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                    projectiles.add(projectile);

                    projectile.accelerate();
                    projectile.setMovement(projectile.getMovement().normalize().multiply(3));

                    pane.getChildren().add(projectile.getCharacter());
                }

                ship.move();

                asteroids.forEach(asteroid -> asteroid.move());

                projectiles.forEach(projectile -> projectile.move());

                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        writeHighScore(HIGH_SCORE);
                        stop();

//                        System.out.println(projectiles);
                    }
                });

                asteroids.forEach(asteroid -> {
                    projectiles.forEach(projectile -> {
                        if (projectile.collide(asteroid)) {
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                        }

                    });
                    if (!asteroid.isAlive()) {

                        if (asteroid.getClass().toString().contains("RedAsteroid")) {
                            text.setText("Points: " + points.addAndGet(100));
                            if (points.get() > HIGH_SCORE) {
                                setHighScore(points.get());
                            }
                            highScore.setText("High Score: " + HIGH_SCORE);


                        } else {
                            text.setText("Points: " + points.addAndGet(10));
                            if (points.get() > HIGH_SCORE) {
                                setHighScore(points.get());
                            }
                            highScore.setText("High Score: " + HIGH_SCORE);

                        }
                    }
                });

                projectiles.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .forEach(projectile -> pane.getChildren().remove(projectile.getCharacter()));
                projectiles.removeAll(projectiles.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .collect(Collectors.toList()));

                asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .forEach(asteroid -> pane.getChildren().remove(asteroid.getCharacter()));
                asteroids.removeAll(asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .collect(Collectors.toList()));

                if (Math.random() < 0.01) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }

                if (Math.random() < 0.001) {
                    Asteroid asteroid = new RedAsteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }

            }
        }.start();

        stage.setScene(scene);
        stage.setTitle("Asteroids!");
        stage.show();
    }
}
