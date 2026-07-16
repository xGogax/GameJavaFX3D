package dungeonrunner.tiles;

import dungeonrunner.Constants;
import dungeonrunner.DungeonMap;
import javafx.scene.Group;
import javafx.scene.paint.Color;

import java.util.*;

public class DoorSystem {

    private static final Color[] PALETTE = {
            Color.rgb(200, 40, 40),
            Color.rgb(40, 170, 60),
            Color.rgb(40, 90, 210),
            Color.rgb(170, 40, 190),
            Color.rgb(220, 140, 20),
            Color.rgb(30, 180, 190)
    };

    private final DungeonMap map;
    private final Group world;
    private final List<Door> doors = new ArrayList<>();
    private final List<DoorSwitch> switches = new ArrayList<>();
    private final Random random = new Random();

    public DoorSystem(DungeonMap map, Group world) {
        this.map = map;
        this.world = world;
    }

    public List<DoorSwitch> getSwitches() { return switches; }

    public void build(int startCol, int startRow) {
        doors.addAll(findDoors());

        for (Door door : doors) {
            world.getChildren().addAll(door.getNodes());
        }

        assignSwitches(startCol, startRow);

        for (DoorSwitch s : switches) {
            world.getChildren().add(s.getNode());
        }
    }

    private List<Door> findDoors() {
        List<Door> result = new ArrayList<>();
        boolean[][] visited = new boolean[map.getRows()][map.getCols()];
        int doorIndex = 0;

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                if (map.get(col, row) == Constants.DOOR && !visited[row][col]) {
                    List<int[]> group = collectConnectedDoorCells(col, row, visited);
                    Color color = PALETTE[doorIndex % PALETTE.length];
                    result.add(new Door(doorIndex, group, map, color));
                    doorIndex++;
                }
            }
        }

        return result;
    }

    private List<int[]> collectConnectedDoorCells(int startCol, int startRow, boolean[][] visited) {
        List<int[]> group = new ArrayList<>();
        Deque<int[]> queue = new ArrayDeque<>();

        queue.add(new int[]{startCol, startRow});
        visited[startRow][startCol] = true;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            group.add(cur);

            for (int[] dir : dirs) {
                int nc = cur[0] + dir[0];
                int nr = cur[1] + dir[1];

                if (nc < 0 || nr < 0 || nc >= map.getCols() || nr >= map.getRows()) continue;
                if (visited[nr][nc]) continue;
                if (map.get(nc, nr) != Constants.DOOR) continue;

                visited[nr][nc] = true;
                queue.add(new int[]{nc, nr});
            }
        }

        return group;
    }

    private void assignSwitches(int startCol, int startRow) {
        Set<Door> processed = new HashSet<>();

        while (true) {
            Set<String> visitedKeys = new HashSet<>();
            List<int[]> reachable = floodFill(startCol, startRow, visitedKeys);

            List<Door> frontier = new ArrayList<>();
            for (Door door : doors) {
                if (!processed.contains(door) && isDoorAdjacent(door, visitedKeys)) {
                    frontier.add(door);
                }
            }

            if (frontier.isEmpty()) break;

            for (Door door : frontier) {
                int[] spot = pickRandomFloorTile(reachable, startCol, startRow);
                if (spot != null) {
                    switches.add(new DoorSwitch(spot[0], spot[1], door));
                } else {
                    System.out.println("UPOZORENJE: nema mesta za prekidac vrata #" + door.getIndex());
                }
                door.open();
                processed.add(door);
            }
        }

        Set<String> finalKeys = new HashSet<>();
        List<int[]> finalReachable = floodFill(startCol, startRow, finalKeys);
        if (!containsExit(finalReachable)) {
            System.out.println("UPOZORENJE: izlaz nije dostupan - proveri raspored vrata na mapi!");
        }

        // Vrati mapu u zatvoreno stanje - "open()" iznad je bilo samo za planiranje.
        for (Door door : doors) {
            door.reset();
        }
    }

    private boolean isDoorAdjacent(Door door, Set<String> reachableKeys) {
        for (int[] cell : door.getCells()) {
            int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] dir : dirs) {
                String key = (cell[0] + dir[0]) + "," + (cell[1] + dir[1]);
                if (reachableKeys.contains(key)) return true;
            }
        }
        return false;
    }

    private int[] pickRandomFloorTile(List<int[]> candidates, int startCol, int startRow) {
        List<int[]> filtered = new ArrayList<>();
        for (int[] c : candidates) {
            boolean isStart = (c[0] == startCol && c[1] == startRow);
            if (map.get(c[0], c[1]) == Constants.EMPTY && !isStart) {
                filtered.add(c);
            }
        }
        if (filtered.isEmpty()) return null;
        return filtered.get(random.nextInt(filtered.size()));
    }

    private boolean containsExit(List<int[]> reachable) {
        for (int[] cell : reachable) {
            if (map.get(cell[0], cell[1]) == Constants.EXIT) return true;
        }
        return false;
    }

    private List<int[]> floodFill(int startCol, int startRow, Set<String> visitedKeysOut) {
        List<int[]> result = new ArrayList<>();
        Deque<int[]> queue = new ArrayDeque<>();

        queue.add(new int[]{startCol, startRow});
        visitedKeysOut.add(startCol + "," + startRow);

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            result.add(current);

            for (int[] dir : dirs) {
                int nc = current[0] + dir[0];
                int nr = current[1] + dir[1];
                String key = nc + "," + nr;

                if (visitedKeysOut.contains(key)) continue;
                if (nc < 0 || nr < 0 || nc >= map.getCols() || nr >= map.getRows()) continue;
                if (map.get(nc, nr) == Constants.WALL) continue;
                if (map.get(nc, nr) == Constants.DOOR) continue; // zatvorena vrata blokiraju

                visitedKeysOut.add(key);
                queue.add(new int[]{nc, nr});
            }
        }

        return result;
    }
}