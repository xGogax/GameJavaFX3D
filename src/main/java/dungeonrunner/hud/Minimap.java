package dungeonrunner.hud;

import dungeonrunner.Constants;
import dungeonrunner.DungeonMap;
import dungeonrunner.Player;
import dungeonrunner.enemies.Guard;
import dungeonrunner.items.Key;
import dungeonrunner.items.Potion;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class Minimap {

    private static final double TILE = 16.0;
    private static final double PAD = 5.0;

    private final Canvas canvas;
    private final DungeonMap map;

    public Minimap(DungeonMap map) {
        this.map = map;

        this.canvas = new Canvas(
                map.getCols() * TILE + PAD * 2,
                map.getRows() * TILE + PAD * 2
        );

        canvas.setMouseTransparent(true);
        canvas.setOpacity(0.75);
    }


    public Canvas getNode() {
        return canvas;
    }


    public void update(Player player, Key key, List<Potion> potions, List<Guard> guards) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        clear(gc);
        drawMap(gc, key);
        drawKey(gc, key);
        drawPotions(gc, potions);
        drawGuards(gc, guards);
        drawPlayer(gc, player);
    }


    private void clear(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.75));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    private void drawMap(GraphicsContext gc, Key key) {
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                gc.setFill(tileColor(map.get(col,row), key));
                gc.fillRect(PAD + col * TILE, PAD + row * TILE, TILE - 1, TILE - 1);
            }
        }
    }


    private Color tileColor(int tile, Key key) {
        if(tile == Constants.WALL || tile == Constants.PILLAR) return Color.GRAY;
        if(tile == Constants.EXIT) return isExitOpen(key) ? Color.LIMEGREEN : Color.GRAY;

        return Color.rgb(28,28,28);
    }


    private boolean isExitOpen(Key key) {
        return key == null || key.isCollected();
    }


    private void drawKey(GraphicsContext gc, Key key) {
        if(key == null || key.isCollected()) return;
        drawCircle(gc, key.getColumn(), key.getRow(), Color.GOLD);
    }


    private void drawPotions(GraphicsContext gc, List<Potion> potions) {
        for(Potion potion : potions) {
            if(potion.isCollected()) continue;

            int col = (int)(potion.getWorldX() / Constants.CELL_SIZE);
            int row = (int)(potion.getWorldZ() / Constants.CELL_SIZE);

            drawCircle(gc, col, row, Color.rgb(90,20,130));
        }
    }


    private void drawGuards(GraphicsContext gc, List<Guard> guards) {
        for (Guard guard : guards) {
            double x = PAD + (guard.getWorldX() / Constants.CELL_SIZE) * TILE;
            double y = PAD + (guard.getWorldZ() / Constants.CELL_SIZE) * TILE;

            gc.setFill(Color.rgb(220, 20, 20));
            gc.fillOval(x - 4, y - 4, 8, 8);
        }
    }


    private void drawCircle(GraphicsContext gc, int col, int row, Color color) {
        double x = PAD + col * TILE + TILE/2.0;
        double y = PAD + row * TILE + TILE/2.0;

        gc.setFill(color);
        gc.fillOval(x - 3, y - 3, 6, 6);
    }


    private void drawPlayer(GraphicsContext gc, Player player) {
        double px = PAD + player.getPositionX() * TILE;
        double py = PAD + player.getPositionY() * TILE;

        double hx = px + player.getDirectionX() * TILE * 0.75;
        double hy = py + player.getDirectionY() * TILE * 0.75;

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeLine(px, py, hx, hy);

        gc.setFill(Color.WHITE);
        gc.fillOval(px-3, py-3, 6, 6);
    }
}