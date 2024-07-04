import java.util.ArrayList;

public class MapGenerator {

    public boolean[][] generateMap(){
        //Create a new map
        boolean[][] cellmap = new boolean[GameConstants.gameWidth][GameConstants.gameHeight];
        //Set up the map with random values
        cellmap = initialiseMap(cellmap);
        //And now run the simulation for a set number of steps
        for(int i=0; i<GameConstants.numberOfSteps; i++){
            cellmap = doSimulationStep(cellmap);
        }
        cellmap = connectEmptySpaces(cellmap);
        return cellmap;
    }

    public void placeEntities(boolean[][] world, SpriteSheet ss, Handler handler, Game game, SpriteSheet playerSS, Camera camera, SpriteSheet enemySS){
        MapGenerator mg = new MapGenerator();
        //How hidden does a spot need to be for treasure?
        //I find 5 or 6 is good. 6 for very rare treasure.
        int treasureHiddenLimit = 5;
        boolean playerSpawned = false;
        boolean enemySpawned = false;
        for (int i=0; i < world.length; i++){
            for (int j=0; j < world[0].length; j++){
                if(!world[i][j]){
                    int nbs = mg.countAliveNeighbours(world, i, j);
                    if(nbs >= treasureHiddenLimit){
                        handler.addObject(new Crate(i*32, j*32, ID.Crate, ss, handler, game));
                    }
                }
                if (!world[i][j] && !playerSpawned) {
                    handler.addObject(new Player(i*32, j*32, ID.Player, handler, game, playerSS, camera));
                    playerSpawned = true;
                }
                if (!world[GameConstants.gameWidth-1-i][GameConstants.gameHeight-1-j] && !enemySpawned) {
                    handler.addObject(new Enemy((GameConstants.gameWidth-1-i)*32, (GameConstants.gameHeight-1-j)*32, ID.Enemy, handler, enemySS, game));
                    enemySpawned = true;
                }
            }
        }
    }

    private boolean[][] doSimulationStep(boolean[][] oldMap){
        boolean[][] newMap = new boolean[GameConstants.gameWidth][GameConstants.gameHeight];
        //Loop over each row and column of the map
        for(int x=0; x<oldMap.length; x++){
            for(int y=0; y<oldMap[0].length; y++){
                int nbs = countAliveNeighbours(oldMap, x, y);
                //The new value is based on our simulation rules
                //First, if a cell is alive but has too few neighbours, kill it.
                if(oldMap[x][y]){
                    if(nbs < GameConstants.deathLimit){
                        newMap[x][y] = false;
                    }
                    else{
                        newMap[x][y] = true;
                    }
                } //Otherwise, if the cell is dead now, check if it has the right number of neighbours to be 'born'
                else{
                    if(nbs > GameConstants.birthLimit){
                        newMap[x][y] = true;
                    }
                    else{
                        newMap[x][y] = false;
                    }
                }
            }
        }
        return newMap;
    }

    private boolean[][] connectEmptySpaces(boolean[][] map) {
        int width = map.length;
        int height = map[0].length;
        boolean[][] visited = new boolean[width][height];
        ArrayList<int[]> emptySpaces = new ArrayList<>();

        // Find the first empty space
        int startX = -1, startY = -1;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!map[x][y]) {
                    startX = x;
                    startY = y;
                    break;
                }
            }
            if (startX != -1) break;
        }

        // If no empty space found, return the original map
        if (startX == -1) return map;

        // Flood fill from the first empty space
        floodFill(map, visited, startX, startY, emptySpaces);

        // Connect isolated empty spaces
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!map[x][y] && !visited[x][y]) {
                    connectToMainSpace(map, x, y, emptySpaces);
                }
            }
        }

        // Widen corridors
        widenCorridors(map);

        return map;
    }

    private void floodFill(boolean[][] map, boolean[][] visited, int x, int y, ArrayList<int[]> emptySpaces) {
        if (x < 0 || x >= map.length || y < 0 || y >= map[0].length || visited[x][y] || map[x][y]) {
            return;
        }

        visited[x][y] = true;
        emptySpaces.add(new int[]{x, y});

        floodFill(map, visited, x + 1, y, emptySpaces);
        floodFill(map, visited, x - 1, y, emptySpaces);
        floodFill(map, visited, x, y + 1, emptySpaces);
        floodFill(map, visited, x, y - 1, emptySpaces);
    }

    private void connectToMainSpace(boolean[][] map, int x, int y, ArrayList<int[]> emptySpaces) {
        int[] nearest = findNearestEmptySpace(x, y, emptySpaces);
        int startX = x;
        int startY = y;
        int endX = nearest[0];
        int endY = nearest[1];

        // Create initial path
        while (startX != endX || startY != endY) {
            map[startX][startY] = false;

            if (startX != endX) {
                startX += Integer.compare(endX, startX);
            }
            if (startY != endY) {
                startY += Integer.compare(endY, startY);
            }
        }
    }

    private void widenCorridors(boolean[][] map) {
        int width = map.length;
        int height = map[0].length;
        boolean[][] newMap = new boolean[width][height];

        // Copy the original map
        for (int x = 0; x < width; x++) {
            System.arraycopy(map[x], 0, newMap[x], 0, height);
        }

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (!map[x][y]) {
                    widenIfNecessary(newMap, x, y);
                }
            }
        }

        // Copy the new map back to the original
        for (int x = 0; x < width; x++) {
            System.arraycopy(newMap[x], 0, map[x], 0, height);
        }
    }

    private void widenIfNecessary(boolean[][] map, int x, int y) {
        // Check horizontal corridor
        if (map[x][y-1] && map[x][y+1] && !map[x-1][y] && !map[x+1][y]) {
            map[x][y-1] = false;
            map[x][y+1] = false;
        }
        // Check vertical corridor
        else if (map[x-1][y] && map[x+1][y] && !map[x][y-1] && !map[x][y+1]) {
            map[x-1][y] = false;
            map[x+1][y] = false;
        }
        // Check diagonal corridors
        else if (map[x-1][y-1] && map[x+1][y+1] && !map[x-1][y+1] && !map[x+1][y-1]) {
            map[x-1][y] = false;
            map[x+1][y] = false;
            map[x][y-1] = false;
            map[x][y+1] = false;
        }
        else if (map[x-1][y+1] && map[x+1][y-1] && !map[x-1][y-1] && !map[x+1][y+1]) {
            map[x-1][y] = false;
            map[x+1][y] = false;
            map[x][y-1] = false;
            map[x][y+1] = false;
        }
    }

    private int countEmptyNeighbors(boolean[][] map, int x, int y) {
        int count = 0;
        int width = map.length;
        int height = map[0].length;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height && !map[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void createWidePassage(boolean[][] map, int x, int y) {
        int width = map.length;
        int height = map[0].length;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    map[nx][ny] = false;
                }
            }
        }
    }

    private int[] findNearestEmptySpace(int x, int y, ArrayList<int[]> emptySpaces) {
        if (emptySpaces.isEmpty()) {
            return new int[]{x, y}; // Return the original point if no empty spaces
        }

        int[] nearest = emptySpaces.get(0);
        int minDistance = Integer.MAX_VALUE;

        for (int[] space : emptySpaces) {
            int distance = Math.abs(space[0] - x) + Math.abs(space[1] - y);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = space;
            }
        }

        return nearest;
    }

    private boolean[][] initialiseMap(boolean[][] map){
        for(int x=0; x<GameConstants.gameWidth; x++){
            for(int y=0; y<GameConstants.gameHeight; y++){
                if(Math.random() < GameConstants.chanceToStartAlive){
                    map[x][y] = true;
                }
            }
        }
        return map;
    }

    private int countAliveNeighbours(boolean[][] map, int x, int y){
        int count = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++){
                int neighbour_x = x + i;
                int neighbour_y = y + j;

                //If we're looking at the middle point
                if (i == 0 && j == 0){
                    //Do nothing, we don't want to add ourselves in!
                }
                //In case the index we're looking at it off the edge of the map
                else if (neighbour_x < 0 || neighbour_y < 0 || neighbour_x >= map.length || neighbour_y >= map[0].length) {
                    count = count + 1;
                }
                //Otherwise, a normal check of the neighbour
                else if(map[neighbour_x][neighbour_y]){
                    count = count + 1;
                }
            }
        }
        return count;
    }
}
