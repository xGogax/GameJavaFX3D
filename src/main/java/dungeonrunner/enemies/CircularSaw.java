package dungeonrunner.enemies;

import dungeonrunner.Constants;
import dungeonrunner.DungeonMap;
import dungeonrunner.Player;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

public class CircularSaw {
    private static final double PERIOD = 2.5;
    private static final double EXTEND_FRAC = 0.15;
    private static final double HOLD_FRAC = 0.3;
    private static final double RETRACT_FRAC = 0.15;
    private static final double SPIN_SPEED = 240.0;

    private final Translate slideTranslate;
    private final Rotate spinRotate;
    private final Group node;
    private final double phase;
    private final double retractedPos;
    private final double extendedPos;
    private final boolean slideInX;
    private final double blade;
    private double currentRatio;

    public CircularSaw(DungeonMap map, int column, int row, double phase, boolean opposite) {
        this.phase = phase;
        double trapCenterX = column * Constants.CELL_SIZE + 1.0;
        double trapCenterZ = row * Constants.CELL_SIZE + 1.0;

        PhongMaterial sawMat = new PhongMaterial();
        sawMat.setDiffuseColor(Color.rgb(160, 160, 175));
        sawMat.setSpecularColor(Color.rgb(240, 240, 255));
        this.blade = 1.2;

        boolean wallWest = isWall(map, column - 1, row);
        boolean wallEast = isWall(map, column + 1, row);
        boolean wallNorth = isWall(map, column, row - 1);
        boolean wallSouth = isWall(map, column, row + 1);

        double wallFace;
        double extensionDir;
        boolean inX;

        if (!opposite) {
            if (wallWest) {
                wallFace = column * Constants.CELL_SIZE;
                extensionDir = 1.0;
                inX = true;
            } else if (wallNorth) {
                wallFace = row * Constants.CELL_SIZE;
                extensionDir = 1.0;
                inX = false;
            } else if (wallEast) {
                wallFace = (column + 1) * Constants.CELL_SIZE;
                extensionDir = -1.0;
                inX = true;
            } else {
                wallFace = (row + 1) * Constants.CELL_SIZE;
                extensionDir = -1.0;
                inX = false;
            }
        } else if (wallEast) {
            wallFace = (column + 1) * Constants.CELL_SIZE;
            extensionDir = -1.0;
            inX = true;
        } else if (wallSouth) {
            wallFace = (row + 1) * Constants.CELL_SIZE;
            extensionDir = -1.0;
            inX = false;
        } else if (wallWest) {
            wallFace = column * Constants.CELL_SIZE;
            extensionDir = 1.0;
            inX = true;
        } else {
            wallFace = row * Constants.CELL_SIZE;
            extensionDir = 1.0;
            inX = false;
        }

        this.slideInX = inX;
        this.retractedPos = wallFace - extensionDir * (this.blade * Math.sqrt(2.0) / 2.0 + 0.1);
        this.extendedPos = wallFace;

        Box b1 = new Box(inX ? 0.1 : this.blade, this.blade, inX ? this.blade : 0.1);
        Box b2 = new Box(inX ? 0.1 : this.blade, this.blade, inX ? this.blade : 0.1);
        b1.setMaterial(sawMat);
        b2.setMaterial(sawMat);
        b2.getTransforms().add(new Rotate(45.0, inX ? Rotate.X_AXIS : Rotate.Z_AXIS));

        this.spinRotate = new Rotate(0.0, inX ? Rotate.X_AXIS : Rotate.Z_AXIS);
        Group bladeGroup = new Group(new Node[]{b1, b2});
        bladeGroup.getTransforms().addAll(new Transform[]{new Rotate(90.0, Rotate.Y_AXIS), this.spinRotate});

        this.slideTranslate = new Translate(
                inX ? this.retractedPos : trapCenterX,
                0.0,
                inX ? trapCenterZ : this.retractedPos
        );
        this.node = new Group(new Node[]{bladeGroup});
        this.node.getTransforms().add(this.slideTranslate);
    }

    public void update(double t) {
        this.spinRotate.setAngle(t * SPIN_SPEED % 360.0);
        double tn = ((t / PERIOD + this.phase) % 1.0 + 1.0) % 1.0;

        double ratio;
        if (tn < EXTEND_FRAC) {
            ratio = smoothstep(tn / EXTEND_FRAC);
        } else if (tn < EXTEND_FRAC + HOLD_FRAC) {
            ratio = 1.0;
        } else if (tn < EXTEND_FRAC + HOLD_FRAC + RETRACT_FRAC) {
            ratio = smoothstep(1.0 - (tn - EXTEND_FRAC - HOLD_FRAC) / RETRACT_FRAC);
        } else {
            ratio = 0.0;
        }

        this.currentRatio = ratio;
        double pos = this.retractedPos + ratio * (this.extendedPos - this.retractedPos);
        if (this.slideInX) {
            this.slideTranslate.setX(pos);
        } else {
            this.slideTranslate.setZ(pos);
        }
    }

    public boolean hitsPlayer(Player player) {
        if (this.currentRatio < 0.3) {
            return false;
        }
        double px = player.getPositionX() * Constants.CELL_SIZE;
        double pz = player.getPositionY() * Constants.CELL_SIZE;
        double dx = px - this.slideTranslate.getX();
        double dz = pz - this.slideTranslate.getZ();
        double r = this.blade / 2.0;
        return dx * dx + dz * dz < r * r;
    }

    public Group getNode() {
        return this.node;
    }

    private static double smoothstep(double x) {
        return x * x * (3.0 - 2.0 * x);
    }

    private static boolean isWall(DungeonMap map, int col, int row) {
        if (col >= 0 && col < map.getCols() && row >= 0 && row < map.getRows()) {
            int tile = map.get(col, row);
            return tile == Constants.WALL || tile == Constants.PILLAR;
        } else {
            return true;
        }
    }
}