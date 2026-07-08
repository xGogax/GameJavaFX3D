package dungeonrunner.enemies;

import dungeonrunner.Constants;
import dungeonrunner.Player;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Translate;

public class FloorSpike {
    private static final double PERIOD = 3.0;
    private static final double EXTEND_FRAC = 0.12;
    private static final double HOLD_FRAC = 0.28;
    private static final double RETRACT_FRAC = 0.12;
    private static final double SPIKE_HEIGHT = 0.8;
    private static final double SPIKE_BASE = 0.1;
    private static final double SPIKE_OFFSET = 0.44;

    private final Translate animTranslate;
    private final Group node;
    private final double phase;
    private final double extendedY;
    private final double retractedY;
    private final double tileCenterX;
    private final double tileCenterZ;
    private double currentRatio;

    public FloorSpike(double tileCenterX, double tileCenterZ, double phase) {
        this.phase = phase;
        this.tileCenterX = tileCenterX;
        this.tileCenterZ = tileCenterZ;

        double floorY = 1.0;
        this.extendedY = floorY - SPIKE_HEIGHT;
        this.retractedY = floorY + 0.05;

        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseColor(Color.rgb(110, 110, 120));
        mat.setSpecularColor(Color.rgb(220, 220, 240));

        double[] dx = new double[]{-SPIKE_OFFSET, -SPIKE_OFFSET, SPIKE_OFFSET, SPIKE_OFFSET};
        double[] dz = new double[]{-SPIKE_OFFSET, SPIKE_OFFSET, -SPIKE_OFFSET, SPIKE_OFFSET};
        Group spikesGroup = new Group();

        for (int i = 0; i < 4; ++i) {
            MeshView spike = buildSpike(mat);
            spike.getTransforms().add(new Translate(tileCenterX + dx[i], 0.0, tileCenterZ + dz[i]));
            spikesGroup.getChildren().add(spike);
        }

        this.animTranslate = new Translate(0.0, this.retractedY, 0.0);
        spikesGroup.getTransforms().add(this.animTranslate);
        this.node = spikesGroup;
    }

    public void update(double t) {
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
        this.animTranslate.setY(this.retractedY + ratio * (this.extendedY - this.retractedY));
    }

    public boolean hitsPlayer(Player player) {
        if (this.currentRatio < 0.3) {
            return false;
        }
        double px = player.getPositionX() * Constants.CELL_SIZE;
        double pz = player.getPositionY() * Constants.CELL_SIZE;
        double dx = px - this.tileCenterX;
        double dz = pz - this.tileCenterZ;
        double r = 0.9;
        return dx * dx + dz * dz < r * r;
    }

    public Group getNode() {
        return this.node;
    }

    private static MeshView buildSpike(PhongMaterial mat) {
        float h = (float) SPIKE_HEIGHT;
        float s = (float) SPIKE_BASE;
        float[] points = new float[]{
                0.0F, 0.0F, 0.0F,
                -s, h, -s,
                s, h, -s,
                s, h, s,
                -s, h, s
        };
        float[] tex = new float[]{0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
        int[] faces = new int[]{
                0, 0, 2, 2, 1, 1,
                0, 0, 3, 2, 2, 1,
                0, 0, 4, 2, 3, 1,
                0, 0, 1, 2, 4, 1
        };
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(tex);
        mesh.getFaces().setAll(faces);
        MeshView view = new MeshView(mesh);
        view.setMaterial(mat);
        view.setCullFace(CullFace.NONE);
        return view;
    }

    private static double smoothstep(double x) {
        return x * x * (3.0 - 2.0 * x);
    }
}