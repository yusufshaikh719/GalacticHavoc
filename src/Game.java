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
        scene_1 = loader.loadImage("/Assets/compsci_scene_1.png");
        sprite_sheet = loader.loadImage("/Assets/sprite-sheet.png");
        fox_sprite_sheet = loader.loadImage("/Assets/link-spritesheet_scaled.png");
        bird_sprite_sheet = loader.loadImage("/Assets/BIRDSPRITESHEET_scaled.png");
        brawlStarsIcon = loader.loadImage("/Assets/brawl-stars-icon.png");

        font = new Font("SansSerif", Font.BOLD, 100);
        ss = new SpriteSheet(sprite_sheet);
        playerSS = new SpriteSheet(fox_sprite_sheet);
        enemySS = new SpriteSheet(bird_sprite_sheet);
        floor = loader.loadImage("/Assets/floor.png");

        loadLevel(scene_1);
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

    private void loadLevel(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pixel = image.getRGB(i, j);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 255 && green == 0 && blue == 48) {
                    handler.addObject(new Block(i*32, j*32, ID.Block, ss));
                    grid[j][i] = 0;
                }
                if (red == 238 && green == 255 && blue == 0) {
                    handler.addObject(new Grass(i*32, j*32, ID.Grass, ss, this, handler));
                }
                if (red == 47 && green == 54 && blue == 255) {
                    handler.addObject(new Player(i*32, j*32, ID.Player, handler, this, playerSS, camera));
                    playerLoc[0] = (j);
                    playerLoc[1] = (i);
                }
                if (red == 34 && green == 255 && blue == 76) {
                    handler.addObject(new Enemy(i*32, j*32, ID.Enemy, handler, enemySS, this));
                    enemyLoc[0] = (j);
                    enemyLoc[1] = (i);
                }
                if (red == 0 && green == 255 && blue == 255) {
                    handler.addObject(new Crate(i*32, j*32, ID.Crate, ss, handler, this));
                }
            }
        }
    }

    public static void main(String[] args) {
        new Game();
    }
}