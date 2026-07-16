package dungeonrunner.tiles;

import dungeonrunner.Constants;
import dungeonrunner.Player;
import javafx.scene.Node;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Translate;

public class DoorSwitch {

    private static final double ACTIVATE_RADIUS = 0.6;

    private final Door targetDoor;
    private final double worldX;
    private final double worldZ;
    private final Cylinder plate;
    private boolean activated = false;

    public DoorSwitch(int col, int row, Door targetDoor) {
        this.targetDoor = targetDoor;
        this.worldX = col * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        this.worldZ = row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(targetDoor.getColor());
        material.setSpecularColor(targetDoor.getColor().brighter());

        this.plate = new Cylinder(0.3, 0.12);
        this.plate.setMaterial(material);
        this.plate.setTranslateY(0.9);
        this.plate.getTransforms().add(new Translate(worldX, 0, worldZ));
    }

    public Node getNode() {
        return plate;
    }

    public void update(Player player) {
        if (activated) return;

        double px = player.getPositionX() * Constants.CELL_SIZE;
        double pz = player.getPositionY() * Constants.CELL_SIZE;
        double dx = px - worldX;
        double dz = pz - worldZ;

        if (dx * dx + dz * dz < ACTIVATE_RADIUS * ACTIVATE_RADIUS) {
            activated = true;
            targetDoor.open();
            plate.setVisible(false);
        }
    }
}