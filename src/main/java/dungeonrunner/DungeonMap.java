package dungeonrunner;

public class DungeonMap {
    private int map[][];

    public DungeonMap ( int map[][] ) {
        this.map = new int[map.length][map[0].length];

        for ( int i = 0; i < map.length; i++ ) {
            for ( int j = 0; j < map[i].length; j++ ) {
                this.map[i][j] = map[i][j];
            }
        }
    }

    public int getRows ( ) {
        return this.map.length;
    }

    public int getCols ( ) {
        return this.map[0].length;
    }

    public int get ( int x, int y ) {
        return this.map[y][x];
    }
}