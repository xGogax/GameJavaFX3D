package dungeonrunner.items;

import dungeonrunner.Constants;
import dungeonrunner.Player;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class HeartPickup {

    private static final double HEART_SCALE   = Constants.CELL_SIZE * 0.09;
    private static final double COLLECT_RADIUS = Constants.CELL_SIZE * 0.35;
    private static final double BOB_HEIGHT     = Constants.CELL_SIZE * 0.12;
    private static final double BOB_SPEED      = 2.5;
    private static final double SPIN_SPEED     = 60.0; // stepeni u sekundi

    private final double worldX;
    private final double worldZ;
    private final double spawnTime;

    private final Node node;
    private final Translate bobTranslate;
    private final Rotate spinRotate;

    private boolean collected;

    public HeartPickup(double worldX, double worldZ, double spawnTime) {
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.spawnTime = spawnTime;
        this.collected = false;

        PhongMaterial heartMaterial = new PhongMaterial();
        heartMaterial.setDiffuseColor(Color.CRIMSON);
        heartMaterial.setSpecularColor(Color.WHITE);

        double lobeRadius = HEART_SCALE * 0.6;
        double lobeOffset = HEART_SCALE * 0.4;
        double bottomSize = HEART_SCALE * 0.80;

        Sphere left = new Sphere(lobeRadius);
        left.setTranslateX(-lobeOffset);
        left.setMaterial(heartMaterial);

        Sphere right = new Sphere(lobeRadius);
        right.setTranslateX(lobeOffset);
        right.setMaterial(heartMaterial);

        Box bottom = new Box(bottomSize, bottomSize, bottomSize);
        bottom.setTranslateY(lobeOffset);
        bottom.setRotationAxis(Rotate.Z_AXIS);
        bottom.setRotate(45);
        bottom.setMaterial(heartMaterial);

        Group heartShape = new Group(left, right, bottom);

        this.spinRotate = new Rotate(0, Rotate.Y_AXIS);
        this.bobTranslate = new Translate(0, 0, 0);
        Translate baseTranslate = new Translate(worldX, Constants.CELL_SIZE * 0.3, worldZ);

        heartShape.getTransforms().addAll(baseTranslate, bobTranslate, spinRotate);

        this.node = heartShape;
    }

    public Node getNode() {
        return node;
    }

    public void update(double t) {
        if (collected) {
            return;
        }
        double localT = t - spawnTime;
        bobTranslate.setY(-Math.abs(Math.sin(localT * BOB_SPEED)) * BOB_HEIGHT);
        spinRotate.setAngle((localT * SPIN_SPEED) % 360.0);
    }

    public boolean tryCollect(Player player) {
        if (collected) {
            return false;
        }

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

    public boolean isCollected() {
        return collected;
    }
}