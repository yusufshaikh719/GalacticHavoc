import java.awt.image.*;
import java.awt.*;

public class Game extends Canvas implements Runnable {
    private boolean isRunning = false;
    private Thread thr;
    private Handler handler;
    private Camera camera;
    private SpriteSheet ss;
    private SpriteSheet playerSS;
    private SpriteSheet enemySS;
    private BufferedImage scene_1 = null, fox_sprite_sheet = null, bird_sprite_sheet = null, sprite_sheet = null, floor = null, brawlStarsIcon = null;
    public int powercubes = 0;
    public double playerAmmo = 3;
    public double playerHp = 100;
    public double playerMaxHP = 100;
    public boolean playerIsVisible = true;
    public int playerDmg = 10;
    public int altCharge = 0;
    public int gadgetTimes = 3;

    public double enemyAmmo = 3;
    public double enemyHp = 300;
    public double enemyMaxHP = 300;
    public boolean enemyIsVisible = true;

    public int[][] grid = new int[36][64];
    public int[] enemyLoc = new int[2];
    public int[] playerLoc = new int[2];
    public boolean endDefeat;
    public boolean endVictory;
    private Font font;

    public Game() {
        new Window(GameConstants.screenWidth, GameConstants.screenHeight, "Galactic Havoc", this);
        start();
        camera = new Camera(0, 0);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = 1;
            }
        }
        handler = new Handler();

        this.addKeyListener(new KeyInput(handler));
        ImageLoader loader = new ImageLoader();

        fox_sprite_sheet = loader.loadImage("/Assets/link-spritesheet_scaled.png");
        bird_sprite_sheet = loader.loadImage("/Assets/BIRDSPRITESHEET_scaled.png");
        playerSS = new SpriteSheet(fox_sprite_sheet);
        enemySS = new SpriteSheet(bird_sprite_sheet);
        floor = loader.loadImage("/Assets/floor.png");
        ss = new SpriteSheet(sprite_sheet);
        sprite_sheet = loader.loadImage("/Assets/sprite-sheet.png");
        brawlStarsIcon = loader.loadImage("/Assets/brawl-stars-icon.png");
        font = new Font("SansSerif", Font.BOLD, 100);

        loadLevel();
    }

    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double ns = 1000000000 / 60.0;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }
        stop();
    }

    private void start() {
        isRunning = true;
        thr = new Thread(this);
        thr.start();
    }

    private void stop() {
        isRunning = false;
        try {
            thr.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        for (int i = 0; i < handler.object.size(); i++) {
            if (handler.object.get(i).getId() == ID.Player) {
                camera.tick(handler.object.get(i));
            }
        }

        handler.tick();
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2 = (Graphics2D) g;

        g2.translate(-camera.getX(), -camera.getY());

        for (int i = 0; i < 72*30; i+=32) {
            for (int j = 0; j < 72*30; j+=32) {
                g.drawImage(floor, i, j, null);
            }
        }

        handler.render(g);

        // Gadget icon
        g.setColor(Color.green);
        g.fillOval((int) (GameConstants.screenWidth + camera.getX()) - 250, (int) (GameConstants.screenHeight + camera.getY()) - 135, 70, 70);
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("" + gadgetTimes, (int) (GameConstants.screenWidth + camera.getX()) - 220, (int) (GameConstants.screenHeight + camera.getY()) - 95);

        // Alt icon
        if (altCharge >= 10) g.setColor(Color.yellow);
        g.fillOval((int) (GameConstants.screenWidth + camera.getX()) - 150, (int) (GameConstants.screenHeight + camera.getY()) - 150, 100, 100);
        g.setColor(Color.yellow);
        ((Graphics2D) g).setStroke(new BasicStroke(4));
        g.drawArc((int) (GameConstants.screenWidth + camera.getX()) - 150, (int) (GameConstants.screenHeight + camera.getY()) - 150, 100, 100, 0, (int) (altCharge/(10.0 / 360)));
        g.drawImage(brawlStarsIcon, (int) (GameConstants.screenWidth + camera.getX()) - 123, (int) (GameConstants.screenHeight + camera.getY()) - 124, null);

        ((Graphics2D) g).setStroke(new BasicStroke(1));
        if (endDefeat) {
            g.setColor(Color.red);
            g.fillRect(0, 0, (int) (GameConstants.screenWidth + camera.getX()), (int) (GameConstants.screenHeight + camera.getY()));
            g.setColor(Color.black);
            g.setFont(font);
            g.drawString("DEFEAT", (int) (GameConstants.screenWidth + camera.getX()) / 2, (int) (GameConstants.screenHeight + camera.getY()) / 2);
        }
        if (endVictory) {
            g.setColor(Color.green);
            g.fillRect(0, 0, (int) (GameConstants.screenWidth + camera.getX()), (int) (GameConstants.screenHeight + camera.getY()));
            g.setColor(Color.black);
            g.setFont(font);
            g.drawString("VICTORY", (int) (GameConstants.screenWidth + camera.getX()) / 2, (int) (GameConstants.screenHeight + camera.getY()) / 2);
        }

        g2.translate(camera.getX(), camera.getY());

        g.dispose();
        bs.show();
    }

    private void loadLevel() {
        boolean[][] cellMap = generateMap();
        boolean playerSpawned = false;
        boolean enemySpawned = false;
        for (int i = 0; i < cellMap.length; i++) {
            for (int j = 0; j < cellMap[0].length; j++) {
                if (!cellMap[i][j] && !playerSpawned) {
                    handler.addObject(new Player(i*32, j*32, ID.Player, handler, this, playerSS, camera));
                    playerSpawned = true;
                }
                if (!cellMap[63-i][35-j] && !enemySpawned) {
                    handler.addObject(new Enemy((63-i)*32, (35-j)*32, ID.Enemy, handler, enemySS, this));
                    enemySpawned = true;
                }
                if (cellMap[i][j]) {
                    handler.addObject(new Block(i*32, j*32, ID.Block, ss));
                    grid[j][i] = 0;
                }
            }
        }

//        placeTreasure(cellMap);
    }

    public boolean[][] doSimulationStep(boolean[][] oldMap){
        boolean[][] newMap = new boolean[64][36];
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

    public void placeTreasure(boolean[][] world){
        //How hidden does a spot need to be for treasure?
        //I find 5 or 6 is good. 6 for very rare treasure.
        int treasureHiddenLimit = 5;
        for (int x=0; x < 64; x++){
            for (int y=0; y < 36; y++){
                if(!world[x][y]){
                    int nbs = countAliveNeighbours(world, x, y);
                    if(nbs >= treasureHiddenLimit){
                        handler.addObject(new Crate(x*32, y*32, ID.Crate, ss, handler, this));
                    }
                }
            }
        }
    }

    public boolean[][] generateMap(){
        //Create a new map
        boolean[][] cellmap = new boolean[64][36];
        //Set up the map with random values
        cellmap = initialiseMap(cellmap);
        //And now run the simulation for a set number of steps
        for(int i=0; i<GameConstants.numberOfSteps; i++){
            cellmap = doSimulationStep(cellmap);
        }
        return cellmap;
    }

    public boolean[][] initialiseMap(boolean[][] map){
        for(int x=0; x<64; x++){
            for(int y=0; y<36; y++){
                if(Math.random() < GameConstants.chanceToStartAlive){
                    map[x][y] = true;
                }
            }
        }
        return map;
    }

    public int countAliveNeighbours(boolean[][] map, int x, int y){
        int count = 0;
        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                int neighbour_x = x+i;
                int neighbour_y = y+j;
                //If we're looking at the middle point
                if(i == 0 && j == 0){
                    //Do nothing, we don't want to add ourselves in!
                }
                //In case the index we're looking at it off the edge of the map
                else if(neighbour_x < 0 || neighbour_y < 0 || neighbour_x >= map.length || neighbour_y >= map[0].length){
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

    public static void main(String[] args) {
        new Game();
    }
}