package dungeonrunner.hud;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class HealthIndicator {
    private static final double HEART_RADIUS = 11.0;
    private static final double STROKE_WIDTH = 2.0;
    private static final double SPACING = 8.0;
    private static final double PADDING = 14.0;

    private final HBox node;
    private final Circle[] hearts;
    private int currentHealth;

    public HealthIndicator(int maxHealth) {
        this.hearts = new Circle[maxHealth];
        this.node = new HBox(SPACING);
        this.node.setPadding(new Insets(PADDING));
        this.currentHealth = maxHealth;

        for(int i = 0; i < maxHealth; i++) {
            Circle heart = new Circle(HEART_RADIUS, Color.RED);
            heart.setStroke(Color.DARKRED);
            heart.setStrokeWidth(STROKE_WIDTH);
            this.hearts[i] = heart;
            this.node.getChildren().add(heart);
        }
    }

    public void update(int health) {
        this.currentHealth = health;
        for(int i = 0; i < this.hearts.length; i++) {
            this.hearts[i].setFill(i < health ? Color.RED : Color.DARKRED);
        }
    }

    public HBox getNode() {
        return this.node;
    }

    public int getCurrentHealth() {
        return this.currentHealth;
    }
}
