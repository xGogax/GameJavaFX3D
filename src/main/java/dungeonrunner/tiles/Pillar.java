package dungeonrunner.tiles;

import dungeonrunner.Constants;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Pillar extends MeshView {
    private MeshView pillarMesh;

    public Pillar(int column, int row, PhongMaterial wallMaterial) {
        double cx = (double) (column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0);
        double cz = (double) (row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0);
        double s = 0.6;
        double hy = (double) Constants.WALL_HEIGHT/2.0;

        float[] points = {
                (float) (cx), (float) (-hy), (float) (cz),
                (float) (cx - s), (float) 0.0F, (float) (cz - s),
                (float) (cx + s), (float) 0.0F, (float) (cz - s),
                (float) (cx + s), (float) 0.0F, (float) (cz + s),
                (float) (cx - s), (float) 0.0F, (float) (cz + s),
                (float) (cx), (float) (hy), (float) (cz)
        };

        float[] texCoords = {
                0.5F, 0.0F, 0.0F,
                1.0F, 1.0F, 1.0F,
                0.5F, 1.0F, 0.0F,
                0.0F, 1.0F, 0.0F
        };

        int[] faces = {
                0, 0, 2, 2, 1, 1,
                0, 0, 3, 2, 2, 1,
                0, 0, 4, 2, 3, 1,
                0, 0, 1, 2, 4, 1,
                5, 3, 1, 4, 2, 5,
                5, 3, 2, 4, 3, 5,
                5, 3, 3, 4, 4, 5,
                5, 3, 4, 4, 1, 5
        };

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);

        this.pillarMesh = new MeshView(mesh);
        this.pillarMesh.setMaterial(wallMaterial);
        this.pillarMesh.setCullFace(CullFace.NONE);
    }

    public MeshView getPillarMesh() { return this.pillarMesh; }
}
