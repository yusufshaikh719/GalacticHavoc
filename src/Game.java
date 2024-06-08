import java.awt.image.*;
import java.awt.*;

public class Game extends Canvas implements Runnable {
    private boolean isRunning = false;
    private Thread thr;
    private Handler handler;
    private Camera camera;
    private SpriteSheet ss;

    private BufferedImage scene_1 = null;
    private BufferedImage scene_1_scaled = null;
    private BufferedImage sprite_sheet = null;
    private BufferedImage floor = null;

    public double playerAmmo = 3;
    public double enemyAmmo = 3;
    public double playerHp = 100;

    public int enemyHp = 100;
    public int powercubes = 0;
    public double maxHp = 100;
    public int playerDmg = 10;

    public int[][] grid = new int[1152][2048];
    public int[] enemyLoc = new int[2];
    public int[] playerLoc = new int[2];
    private double fps;

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
        scene_1_scaled = loader.loadImage("/Assets/compsci_scene_1_scaled.png");
        sprite_sheet = loader.loadImage("/Assets/sprite-sheet.png");

        ss = new SpriteSheet(sprite_sheet);
        floor = ss.grabImage(4, 2, 32, 32);

        loadLevel(scene_1, scene_1_scaled);
    }

    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double ns = 1000000000 / 60.0;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        long frameTime = (long) (1000 / 60.0);

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                tick(); // Update game logic
                delta--;
            }

            render(); // Render the game frame
            frames++;

            // Sleep to maintain frame rate
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - (timer + frames * frameTime);
            long sleepTime = frameTime - elapsedTime;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (currentTime - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames); // Output the frame rate
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

        g.setColor(Color.white);

        g2.translate(camera.getX(), camera.getY());

        g.dispose();
        bs.show();
    }

    private void loadLevel(BufferedImage image, BufferedImage img2) {
        int w = image.getWidth();
        int h = image.getHeight();
        int w2 = img2.getWidth();
        int h2 = img2.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pixel = image.getRGB(i, j);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 255) {
                    handler.addObject(new Block(i*32, j*32, ID.Block, ss));
                }
                if (green == 255 && blue == 76) {
                    handler.addObject(new Enemy(i*32, j*32, ID.Enemy, handler, ss, this));
                    enemyLoc[0] = (j);
                    enemyLoc[1] = (i);
                }
                if (blue == 255 && green == 54) {
                    handler.addObject(new Player(i*32, j*32, ID.Player, handler, this, ss, camera));
                    playerLoc[0] = (j);
                    playerLoc[1] = (i);
                }
                if (blue == 255 && green == 255) {
                    handler.addObject(new Crate(i*32, j*32, ID.Crate, ss, handler, this));
                }
            }
        }

//        for (int i = 0; i < grid.length; i++) {
//            for (int j = 0; j < grid[0].length; j++) {
//                System.out.print(grid[i][j]);
//            }
//            System.out.println();
//        }
    }

    public static void main(String[] args) {
        new Game();
    }
}
