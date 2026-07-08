package dungeonrunner.items;

import dungeonrunner.Constants;
import dungeonrunner.Player;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Key {
    private static final double SPIN_SPEED = 80.0;
    private static final double BOB_SPEED = 1.5;
    private static final double BOB_AMOUNT = 0.07;
    private static final double BOB_ANGULAR_FREQ = 2.0 * Math.PI * BOB_SPEED;

    private static final double BOW_THICKNESS = 0.04;
    private static final double BOW_SIZE = 0.12;
    private static final double SHAFT_LENGTH = 0.28;
    private static final double COLLECT_RADIUS = 0.8;

    private final Group node;
    private final Rotate spinRotate;
    private final Translate bobTranslate;
    private final double worldX;
    private final double worldZ;
    private final int tileColumn;
    private final int tileRow;
    private boolean collected = false;

    public Key(int column, int row) {
        this.tileColumn = column;
        this.tileRow = row;
        this.worldX = column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        this.worldZ = row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;

        PhongMaterial gold = new PhongMaterial();
        gold.setDiffuseColor(Color.rgb(255, 200, 0));
        gold.setSpecularColor(Color.rgb(255, 255, 180));

        double th = BOW_THICKNESS;
        double bs = BOW_SIZE;

        Box bowTop = new Box(bs * 2.0, th, th);
        Box bowLeft = new Box(th, bs * 2.0, th);
        Box bowRight = new Box(th, bs * 2.0, th);
        bowTop.setTranslateY(-bs);
        bowLeft.setTranslateX(-bs);
        bowRight.setTranslateX(bs);

        double shaftLen = SHAFT_LENGTH;
        Box shaft = new Box(th, shaftLen, th);
        shaft.setTranslateY(bs + shaftLen / 2.0);

        Box tooth1 = new Box(0.08, th, th);
        tooth1.setTranslateX(0.05);
        tooth1.setTranslateY(bs + shaftLen * 0.55);

        Box tooth2 = new Box(0.06, th, th);
        tooth2.setTranslateX(0.04);
        tooth2.setTranslateY(bs + shaftLen * 0.8);

        for (Box b : new Box[]{bowTop, bowLeft, bowRight, shaft, tooth1, tooth2}) {
            b.setMaterial(gold);
        }

        this.spinRotate = new Rotate(0.0, Rotate.Y_AXIS);
        this.bobTranslate = new Translate(this.worldX, 0.0, this.worldZ);

        Group keyShape = new Group(bowTop, bowLeft, bowRight, shaft, tooth1, tooth2);
        this.node = new Group(keyShape);
        this.node.getTransforms().addAll(this.bobTranslate, this.spinRotate);
    }

    public void update(double t) {
        if (!this.collected) {
            this.spinRotate.setAngle(t * SPIN_SPEED % 360.0);
            this.bobTranslate.setY(BOB_AMOUNT * Math.sin(BOB_ANGULAR_FREQ * t));
        }
    }

    public boolean tryCollect(Player player) {
        if (this.collected) {
            return false;
        }

        double px = player.getPositionX() * Constants.CELL_SIZE;
        double pz = player.getPositionY() * Constants.CELL_SIZE;
        double dx = px - this.worldX;
        double dz = pz - this.worldZ;

        if (dx * dx + dz * dz < COLLECT_RADIUS * COLLECT_RADIUS) {
            this.collected = true;
            this.node.setVisible(false);
            return true;
        }
        return false;
    }

    public int getColumn() {
        return this.tileColumn;
    }

    public int getRow() {
        return this.tileRow;
    }

    public boolean isCollected() {
        return this.collected;
    }

    public Group getNode() {
        return this.node;
    }
}
