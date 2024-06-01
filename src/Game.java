import java.awt.image.*;
import java.util.ArrayList;
import java.awt.*;

public class Game extends Canvas implements Runnable {
    private boolean isRunning = false;
    private Thread thr;
    private Handler handler;
    private Camera camera;
    private SpriteSheet ss;

    private BufferedImage scene_1 = null;
    private BufferedImage sprite_sheet = null;
    private BufferedImage floor = null;
    private BufferedImage pcImage = null;

    public double ammo = 3;
    public double playerHp = 100;

    public int enemyHp = 100;
    public int powercubes = 0;
    public double maxHp = 100;
    public int playerDmg = 10;

    public int[][] grid = new int[1152][2048];
    public int[] enemyLoc = new int[2];
    public int[] playerLoc = new int[2];

    public Game() {
        new Window(GameConstants.screenWidth, GameConstants.screenHeight, "Galactic Havoc", this);
        start();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = 1;
            }
        }
        handler = new Handler();
        camera = new Camera(0, 0);
        this.addKeyListener(new KeyInput(handler));

        ImageLoader loader = new ImageLoader();
        scene_1 = loader.loadImage("/Assets/compsci_scene_1.png");
        sprite_sheet = loader.loadImage("/Assets/sprite-sheet.png");

        ss = new SpriteSheet(sprite_sheet);
        floor = ss.grabImage(4, 2, 32, 32);

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
        if (playerHp > maxHp) playerHp = maxHp;
        maxHp = (50 * powercubes) + 100;
        playerDmg = (5 * powercubes) + 10;

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

                if (red == 255) {
                    handler.addObject(new Block(i*32, j*32, ID.Block, ss));
                    for (int p = 0; p < 32; p++) {
                        for (int l = 0; l < 32; l++) {
                            grid[j*32 + p][i*32 + l] = 0;
                        }
                    }
                }
                if (green == 255 && blue == 76) {
                    handler.addObject(new Enemy(i*32, j*32, ID.Enemy, handler, ss, this));
                    enemyLoc[0] = j;
                    enemyLoc[1] = i;
                }
                if (blue == 255 && green == 54) {
                    handler.addObject(new Player(i*32, j*32, ID.Player, handler, this, ss, camera));
                    playerLoc[0] = j;
                    playerLoc[1] = i;
                }
                if (blue == 255 && green == 255) {
                    handler.addObject(new Crate(i*32, j*32, ID.Crate, ss, handler, this));
                }
            }
        }
        for (int p = 0; p < grid.length; p++) {
            for (int l = 0; l < grid[0].length; l++) {
                System.out.print(grid[p][l]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new Game();
    }
}
