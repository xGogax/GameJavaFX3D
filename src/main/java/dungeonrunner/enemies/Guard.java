package dungeonrunner.enemies;

import dungeonrunner.Constants;
import dungeonrunner.Player;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.List;

public class Guard {
    private static final double MOVE_SPEED = 0.012;
    private static final double CATCH_RADIUS = 0.5;

    private final List<int[]> waypoints;
    private final Cylinder body;
    private final Translate positionTransform;

    private double worldX;
    private double worldZ;

    private int targetIndex = 1;
    private int step = 1;

    public Guard(List<int[]> waypoints) {
        if(waypoints == null || waypoints.size() < 2) throw new IllegalArgumentException("Guard-u je potrebno bar dve tacke");
        this.waypoints = waypoints;

        int[] start = waypoints.get(0);
        this.worldX = tileToWorld(start[0]);
        this.worldZ = tileToWorld(start[1]);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.rgb(160, 30, 30));
        material.setSpecularColor(Color.rgb(255, 120, 120));

        this.body = new Cylinder(0.4, 1.6);
        this.body.setMaterial(material);
        this.body.setTranslateY(0.2);

        this.positionTransform = new Translate(worldX, 0.2, worldZ);
        this.body.getTransforms().add(positionTransform);
    }

    public Node getNode() { return body; }

    public void update(double t) {
        int[] target = waypoints.get(targetIndex);
        double targetX = tileToWorld(target[0]);
        double targetZ = tileToWorld(target[1]);

        double dx = targetX - worldX;
        double dz = targetZ - worldZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < MOVE_SPEED) {
            worldX = targetX;
            worldZ = targetZ;
            advanceTarget();
        } else {
            worldX += dx / distance * MOVE_SPEED;
            worldZ += dz / distance * MOVE_SPEED;
        }

        positionTransform.setX(worldX);
        positionTransform.setZ(worldZ);
    }

    public boolean hitsPlayer(Player player) {
        double px = player.getPositionX() * Constants.CELL_SIZE;
        double pz = player.getPositionY() * Constants.CELL_SIZE;
        double dx = px - worldX;
        double dz = pz - worldZ;
        return dx * dx + dz * dz < CATCH_RADIUS * CATCH_RADIUS;
    }

    private void advanceTarget() {
        targetIndex += step;
        if (targetIndex >= waypoints.size()) {
            targetIndex = waypoints.size() - 2;
            step = -1;
        } else if (targetIndex < 0) {
            targetIndex = Math.min(1, waypoints.size() - 1);
            step = 1;
        }
    }

    private double tileToWorld(int tileCoord) {
        return tileCoord * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
    }

    public double getWorldX() { return worldX; }
    public double getWorldZ() { return worldZ; }
}
