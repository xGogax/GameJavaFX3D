package dungeonrunner.items.spawners;

import dungeonrunner.Constants;
import dungeonrunner.DungeonMap;
import dungeonrunner.Player;
import dungeonrunner.hud.HealthIndicator;
import dungeonrunner.items.HeartPickup;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class HeartSpawner {

    private static final double MIN_SPAWN_INTERVAL = 10;
    private static final double MAX_SPAWN_INTERVAL = 20;
    private static final int MAX_ACTIVE_HEARTS = 2;

    private final DungeonMap map;
    private final Group world;
    private final Random random;
    private final List<HeartPickup> hearts;

    private double nextSpawnTime;

    public HeartSpawner(DungeonMap map, Group world) {
        this.map = map;
        this.world = world;
        this.random = new Random();
        this.hearts = new ArrayList<>();
        this.nextSpawnTime = randomInterval();
    }

    private double randomInterval() {
        return MIN_SPAWN_INTERVAL + random.nextDouble() * (MAX_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL);
    }

    public void update(double t, Player player, HealthIndicator healthIndicator) {
        if (t >= nextSpawnTime && hearts.size() < MAX_ACTIVE_HEARTS) {
            spawnHeart(t);
            nextSpawnTime = t + randomInterval();
        }

        Iterator<HeartPickup> it = hearts.iterator();
        while (it.hasNext()) {
            HeartPickup heart = it.next();
            heart.update(t);
            if (heart.tryCollect(player)) {
                if(player.getHealth() < player.getMaxHealth()) {
                    player.gainHealth();
                    healthIndicator.update(player.getHealth());
                }

                world.getChildren().remove(heart.getNode());
                it.remove();
            }
        }
    }

    private void spawnHeart(double t) {
        int column = -1;
        int row = -1;
        int attempts = 0;

        while (attempts < 200) {
            int c = random.nextInt(map.getCols());
            int r = random.nextInt(map.getRows());
            if (isSpawnable(c, r)) {
                column = c;
                row = r;
                break;
            }
            attempts++;
        }

        if (column == -1) {
            return; // nije nadjeno slobodno mesto ovog puta
        }

        double cx = column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        double cz = row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;

        HeartPickup heart = new HeartPickup(cx, cz, t);
        System.out.println("Spawned heart at (" + column + ", " + row + ")");
        hearts.add(heart);
        world.getChildren().add(heart.getNode());
    }

    private boolean isSpawnable(int column, int row) {
        int tile = map.get(column, row);
        return tile != Constants.WALL
                && tile != Constants.EXIT
                && tile != Constants.PILLAR
                && tile != Constants.SAW
                && tile != Constants.SPIKE
                && tile != Constants.KEY;
    }
}