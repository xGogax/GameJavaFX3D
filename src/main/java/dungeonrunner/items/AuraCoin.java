package dungeonrunner.items;

import dungeonrunner.Constants;
import dungeonrunner.Player;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class AuraCoin {
    public static final double EFFECT_DURATION = 6.0;

    private static final double AURA_SCALE   = Constants.CELL_SIZE * 0.09;
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

    public AuraCoin(double worldX, double worldZ, double spawnTime) {
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.spawnTime = spawnTime;
        this.collected = false;

        PhongMaterial auraMaterial = new PhongMaterial();
        auraMaterial.setDiffuseColor(Color.rgb(46, 203, 250));
        auraMaterial.setSpecularColor(Color.rgb(188, 240, 255));

        double bodyRadius = AURA_SCALE;
        double bodyHeight = AURA_SCALE * 0.2;

        Cylinder aura = new Cylinder(bodyRadius, bodyHeight);
        aura.setMaterial(auraMaterial);
        aura.setRotationAxis(Rotate.Z_AXIS);
        aura.setRotate(90);

        Group auraCoin = new Group(aura);

        this.spinRotate = new Rotate(0, Rotate.Y_AXIS);
        this.bobTranslate = new Translate(0, 0, 0);
        Translate baseTranslate = new Translate(worldX, Constants.CELL_SIZE * 0.3, worldZ);

        auraCoin.getTransforms().addAll(baseTranslate, bobTranslate, spinRotate);
        this.node = auraCoin;
    }

    public Node getNode() { return node; }

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
