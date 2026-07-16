package dungeonrunner.tiles;

import dungeonrunner.Constants;
import dungeonrunner.DungeonMap;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;

public class Door {

    private final int index;
    private final Color color;
    private final List<int[]> cells;
    private final List<Box> meshes = new ArrayList<>();
    private final DungeonMap map;
    private boolean open = false;

    public Door(int index, List<int[]> cells, DungeonMap map, Color color) {
        this.index = index;
        this.cells = cells;
        this.map = map;
        this.color = color;

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color.brighter());

        for (int[] cell : cells) {
            Box box = new Box(Constants.CELL_SIZE, Constants.WALL_HEIGHT, Constants.CELL_SIZE);
            Translate t = new Translate(
                    cell[0] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0,
                    0,
                    cell[1] * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0
            );
            box.getTransforms().add(t);
            box.setMaterial(material);
            meshes.add(box);
        }
    }

    public int getIndex() { return index; }
    public Color getColor() { return color; }
    public List<int[]> getCells() { return cells; }
    public boolean isOpen() { return open; }

    public List<Node> getNodes() {
        return new ArrayList<>(meshes);
    }

    public void open() {
        if (open) return;
        open = true;

        for (int i = 0; i < cells.size(); i++) {
            int[] cell = cells.get(i);
            map.set(cell[0], cell[1], Constants.EMPTY);
            meshes.get(i).setVisible(false);
        }
    }

    public boolean containsCell(int col, int row) {
        for (int[] cell : cells) {
            if (cell[0] == col && cell[1] == row) return true;
        }
        return false;
    }

    public void reset() {
        if (!open) return;
        open = false;

        for (int[] cell : cells) {
            map.set(cell[0], cell[1], Constants.DOOR);
        }
        for (Box mesh : meshes) {
            mesh.setVisible(true);
        }
    }
}