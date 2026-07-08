package dungeonrunner.items;

import dungeonrunner.Constants;
import dungeonrunner.Player;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Potion {
    public static final double EFFECT_DURATION = 6.0;

    private static final double POTION_SCALE   = Constants.CELL_SIZE * 0.18;
    private static final double COLLECT_RADIUS = Constants.CELL_SIZE * 0.35;
    private static final double BOB_HEIGHT     = Constants.CELL_SIZE * 0.10;
    private static final double BOB_SPEED      = 2.0;
    private static final double SPIN_SPEED     = 45.0;

    private final double worldX;
    private final double worldZ;
    private final double spawnTime;

    private final Node node;
    private final Translate bobTranslate;
    private final Rotate spinRotate;

    private boolean collected;

    public Potion(double worldX, double worldZ, double spawnTime) {
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.spawnTime = spawnTime;
        this.collected = false;

        PhongMaterial potionMaterial = new PhongMaterial();
        potionMaterial.setDiffuseColor(Color.rgb(90, 20, 130));
        potionMaterial.setSpecularColor(Color.rgb(180, 100, 255));

        double bodyRadius = POTION_SCALE * 0.5;
        double bodyHeight = POTION_SCALE;
        double neckRadius = POTION_SCALE * 0.18;
        double neckHeight = POTION_SCALE * 0.4;

        Cylinder body = new Cylinder(bodyRadius, bodyHeight);
        body.setMaterial(potionMaterial);

        Cylinder neck = new Cylinder(neckRadius, neckHeight);
        neck.setTranslateY(-(bodyHeight / 2.0 + neckHeight / 2.0));
        neck.setMaterial(potionMaterial);

        Sphere cork = new Sphere(neckRadius * 1.1);
        cork.setTranslateY(-(bodyHeight / 2.0 + neckHeight + neckRadius * 0.3));
        PhongMaterial corkMaterial = new PhongMaterial(Color.rgb(60, 40, 20));
        cork.setMaterial(corkMaterial);

        Group potionShape = new Group(body, neck, cork);

        this.spinRotate = new Rotate(0, Rotate.Y_AXIS);
        this.bobTranslate = new Translate(0, 0, 0);
        Translate baseTranslate = new Translate(worldX, Constants.CELL_SIZE * 0.3, worldZ);

        potionShape.getTransforms().addAll(baseTranslate, bobTranslate, spinRotate);

        this.node = potionShape;
    }

    public Node getNode() { return node; }

    public void update(double t) {
        if(collected) return;
        double localT = t - spawnTime;
        bobTranslate.setY(-Math.abs(Math.sin(localT * BOB_SPEED)) * BOB_HEIGHT);
        spinRotate.setAngle((localT * SPIN_SPEED) % 360);
    }

    public boolean tryCollect(Player player) {
        if(collected) return false;

        double px = player.getPositionX() * Constants.CELL_SIZE;
        double pz = player.getPositionY() * Constants.CELL_SIZE;

        double dx = px - worldX;
        double dz = pz - worldZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance <= COLLECT_RADIUS) {
            collected = true;
            return true;
        }
        return false;
    }

    public double getWorldX() { return worldX; }
    public double getWorldZ() { return worldZ; }

    public boolean isCollected() { return collected; }
}
