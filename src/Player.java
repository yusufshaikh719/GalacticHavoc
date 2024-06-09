import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Player extends GameObject {

    Handler handler;
    Game game;
    private BufferedImage[] playerimage = new BufferedImage[16];
    private BufferedImage pcImage;
    Camera camera;
    Animation animDown;
    Animation animLeft;
    Animation animUp;
    Animation animRight;
    private long shootTime;
    private long prevShootTime = 0;
    private long hitTime;
    private String lastPressed = "up";

    public Player(int x, int y, ID id, Handler handler, Game game, SpriteSheet ss, Camera camera) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        this.camera = camera;
        ImageLoader loader = new ImageLoader();

        playerimage[0] = ss.grabImage32(1, 5, 32, 32);
        playerimage[1] = ss.grabImage32(2, 5, 32, 32);
        playerimage[2] = ss.grabImage32(3, 5, 32, 32);
        playerimage[3] = ss.grabImage32(4, 5, 32, 32);
        playerimage[4] = ss.grabImage32(1, 6, 32, 32);
        playerimage[5] = ss.grabImage32(2, 6, 32, 32);
        playerimage[6] = ss.grabImage32(3, 6, 32, 32);
        playerimage[7] = ss.grabImage32(4, 6, 32, 32);
        playerimage[8] = ss.grabImage32(1, 7, 32, 32);
        playerimage[9] = ss.grabImage32(2, 7, 32, 32);
        playerimage[10] = ss.grabImage32(3, 7, 32, 32);
        playerimage[11] = ss.grabImage32(4, 7, 32, 32);
        playerimage[12] = ss.grabImage32(1, 8, 32, 32);
        playerimage[13] = ss.grabImage32(2, 8, 32, 32);
        playerimage[14] = ss.grabImage32(3, 8, 32, 32);
        playerimage[15] = ss.grabImage32(4, 8, 32, 32);

        animDown = new Animation(3, playerimage[0], playerimage[1], playerimage[2], playerimage[3]);
        animLeft = new Animation(3, playerimage[4], playerimage[5], playerimage[6], playerimage[7]);
        animUp = new Animation(3, playerimage[8], playerimage[9], playerimage[10], playerimage[11]);
        animRight = new Animation(3, playerimage[12], playerimage[13], playerimage[14], playerimage[15]);
        pcImage = loader.loadImage("/Assets/powercube.png");
    }

    @Override
    public void tick() {
        collision();

        // Key input
        if (handler.isUp()) {
            velY = -GameConstants.playerSpeed;
            lastPressed = "up";
        } else if (!handler.isDown()) velY = 0;

        if (handler.isDown()) {
            velY = GameConstants.playerSpeed;
            lastPressed = "down";
        } else if (!handler.isUp()) velY = 0;

        if (handler.isLeft()) {
            velX = -GameConstants.playerSpeed;
            lastPressed = "left";
        } else if (!handler.isRight()) velX = 0;

        if (handler.isRight()) {
            velX = GameConstants.playerSpeed;
            lastPressed = "right";
        } else if (!handler.isLeft()) velX = 0;

        // Shooting
        if (handler.isSpace()) {
            shootTime = System.currentTimeMillis();
            if (shootTime - prevShootTime >= 400 && game.playerAmmo >= 1) {
                prevShootTime = shootTime;
                shootTime = System.currentTimeMillis();
                handler.addObject(new PlayerBullet(x + 16, y + 24, ID.PlayerBullet, handler, Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getX() + camera.getX() - 7)), Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getY() + camera.getY() - 30)), x, y, ss));
                game.playerAmmo--;
            }
        }

        // Reloading
        if (Math.abs(System.currentTimeMillis() - shootTime) >= 1 && game.playerAmmo < 3) {
            game.playerAmmo += 0.01;
        }

        // Getting hit
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.EnemyBullet) {
                if (getBounds().intersects(temp.getBounds())) {
                    game.playerHp -= 10;
                    handler.removeObject(temp);
                    hitTime = System.currentTimeMillis();
                }
            }
        }

        // Healing
        if (Math.abs(System.currentTimeMillis() - hitTime) >= 2000 && Math.abs(System.currentTimeMillis() - shootTime) >= 2000) {
            if (game.playerHp < game.playerMaxHP) game.playerHp += (0.001 * game.playerMaxHP);
        }

        // Updating position
        x += Math.toIntExact(Math.round(velX));
        y += Math.toIntExact(Math.round(velY));

        game.playerLoc[0] = y / 32;
        game.playerLoc[1] = x / 32;

        if (game.playerHp <= 0) game.end = true;

        animDown.runAnimation();
        animLeft.runAnimation();
        animUp.runAnimation();
        animRight.runAnimation();
    }

    @Override
    public void render(Graphics g) {
        if (velX == 0 && velY == 0) g.drawImage(playerimage[0], x, y, null);
        else if (lastPressed.equals("down")) animDown.drawAnimation(g, x, y, 0);
        else if (lastPressed.equals("left")) animLeft.drawAnimation(g, x, y, 0);
        else if (lastPressed.equals("up")) animUp.drawAnimation(g, x, y, 0);
        else if (lastPressed.equals("right")) animRight.drawAnimation(g, x, y, 0);

        //heath
        g.setColor(Color.gray);
        g.fillRect(x - 9, y - 20, 50, 10);
        g.setColor(Color.green);
        g.fillRect(x - 9, y - 20, (int) (game.playerHp/(game.playerMaxHP / 50)), 10);
        g.setColor(Color.black);
        g.drawRect(x - 9, y - 20, 50, 10);

        //ammo
        g.setColor(Color.orange);
        g.fillRect(x - 11, y - 8, (int) (game.playerAmmo * 18), 5);
        g.setColor(Color.black);
        g.drawRect(x - 11, y - 8, 18, 5);
        g.drawRect(x + 7, y - 8, 18, 5);
        g.drawRect(x + 25, y - 8, 18, 5);

        //power cubes
        if (game.powercubes != 0) {
            g.setColor(Color.green);
            g.drawString("" + game.powercubes, x + 21, y - 26);
            g.drawImage(pcImage, x + 5, y - 35, null);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    private void collision() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.Block) {
                if (getBounds().intersects(temp.getBounds())) {
                    x += Math.toIntExact(Math.round(velX * -1));
                    y += Math.toIntExact(Math.round(velY * -1));
                }
            }

            if (temp.getId() == ID.Enemy) {
                if (getBounds().intersects(temp.getBounds())) {
                    game.playerHp--;
                }
            }
        }
    }

}