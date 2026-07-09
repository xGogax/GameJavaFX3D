package dungeonrunner.items.spawners;

import dungeonrunner.Constants;
import dungeonrunner.DungeonMap;
import dungeonrunner.Player;
import dungeonrunner.hud.HealthIndicator;
import dungeonrunner.items.AuraCoin;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AuraCoinSpawner {
    private static final double MIN_SPAWN_INTERVAL = 20;
    private static final double MAX_SPAWN_INTERVAL = 30;
    private static final int MAX_ACTIVE_AURAS = 2;

    private final DungeonMap map;
    private final Group world;
    private final Random random;
    private final List<AuraCoin> auraCoins;

    private double nextSpawnTime;

    public AuraCoinSpawner(DungeonMap map, Group world) {
        this.map = map;
        this.world = world;
        this.random = new Random();
        this.auraCoins = new ArrayList<>();
        this.nextSpawnTime = randomInterval();
    }

    private double randomInterval() {
        return MIN_SPAWN_INTERVAL + random.nextDouble() * (MAX_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL);
    }

    public void update(double t, Player player) {
        if(t >= nextSpawnTime && auraCoins.size() < MAX_ACTIVE_AURAS) {
            spawnAuraCoin(t);
            nextSpawnTime = t + randomInterval();
        }

        Iterator<AuraCoin> it = auraCoins.iterator();
        while(it.hasNext()) {
            AuraCoin ac = it.next();
            ac.update(t);
            if(ac.tryCollect(player)){
                player.activateShield(t, AuraCoin.EFFECT_DURATION);

                world.getChildren().remove(ac.getNode());
                it.remove();
            }
        }
    }

    private void spawnAuraCoin(double t) {
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

        if(column == -1) {
            return;
        }

        double cx = column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;
        double cz = row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0;

        AuraCoin aura = new AuraCoin(cx, cz, t);
        System.out.println("Spawner aura at (" + column + ", " + row + ")");
        auraCoins.add(aura);
        world.getChildren().add(aura.getNode());
    }

    private boolean isSpawnable(int column, int row) {
        int tile = map.get(column, row);
        return tile != Constants.WALL
                && tile != Constants.EXIT
                && tile != Constants.PILLAR
                && tile != Constants.SAW
                && tile != Constants.SPIKE
                && tile != Constants.KEY
                && tile != Constants.POTION;
    }
}
