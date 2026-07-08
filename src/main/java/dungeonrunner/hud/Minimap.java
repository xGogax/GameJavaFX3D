package dungeonrunner.hud;

import dungeonrunner.Constants;
import dungeonrunner.DungeonMap;
import dungeonrunner.Player;
import dungeonrunner.items.Key;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Minimap {

    private static final double TILE = 16.0;
    private static final double PAD  = 5.0;

    private final Canvas canvas;
    private final DungeonMap map;

    public Minimap(DungeonMap map) {
        this.map = map;
        this.canvas = new Canvas(
                map.getCols() * TILE + PAD * 2,
                map.getRows() * TILE + PAD * 2
        );
        this.canvas.setMouseTransparent(true);
        this.canvas.setOpacity(0.75);
    }

    public Canvas getNode() {
        return canvas;
    }

    public void update(Player player, Key key) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        clearCanvas(gc);
        drawTile(gc, key);
        drawKey(gc, key);
        drawPlayer(gc, player);
    }

    private void clearCanvas(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.75));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawTile(GraphicsContext gc, Key key) {
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                gc.setFill(tileColor(map.get(col, row), key));
                gc.fillRect(PAD + col * TILE, PAD + row * TILE, TILE - 1, TILE - 1);
            }
        }
    }

    private Color tileColor(int tile, Key key) {
        if (tile == Constants.WALL || tile == Constants.PILLAR) {
            return Color.rgb(110, 110, 110);
        } else if (tile == Constants.EXIT) {
            return isExitOpen(key) ? Color.rgb(0, 200, 80) : Color.rgb(110, 110, 110);
        }
        return Color.rgb(28, 28, 28);
    }

    private boolean isExitOpen(Key key) {
        return key == null || key.isCollected();
    }

    private void drawKey(GraphicsContext gc, Key key) {
        if (key == null || key.isCollected()) {
            return;
        }

        double cx = keyCanvasX(key);
        double cy = keyCanvasY(key);

        gc.setFill(Color.GOLD);
        gc.fillOval(cx - 3.0, cy - 3.0, 6.0, 6.0);
    }

    private double keyCanvasX(Key key) {
        return PAD + key.getColumn() * TILE + TILE / 2.0;
    }

    private double keyCanvasY(Key key) {
        return PAD + key.getRow() * TILE + TILE / 2.0;
    }

    private void drawPlayer(GraphicsContext gc, Player player) {
        double px = PAD + player.getPositionX() * TILE;
        double py = PAD + player.getPositionY() * TILE;

        drawPlayerHeading(gc, player, px, py);

        gc.setFill(Color.WHITE);
        gc.fillOval(px - 3.0, py - 3.0, 6.0, 6.0);
    }

    private void drawPlayerHeading(GraphicsContext gc, Player player, double px, double py) {
        double hx = px + player.getDirectionX() * TILE * 0.75;
        double hy = py + player.getDirectionY() * TILE * 0.75;

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeLine(px, py, hx, hy);
    }
}