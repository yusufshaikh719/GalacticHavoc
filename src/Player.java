import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Player extends GameObject {

    Handler handler;
    Game game;
    private BufferedImage[] playerimage = new BufferedImage[3];
    private BufferedImage pcImage;
    Camera camera;
    private long shootTime;
    private long prevShootTime = 0;
    Animation anim;
    private long hitTime;

    public Player(int x, int y, ID id, Handler handler, Game game, SpriteSheet ss, Camera camera) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        this.camera = camera;
        ImageLoader loader = new ImageLoader();

        playerimage[0] = ss.grabImage(1, 1, 32, 48);
        playerimage[1] = ss.grabImage(2, 1, 32, 48);
        playerimage[2] = ss.grabImage(3, 1, 32, 48);

        anim = new Animation(3, playerimage[0], playerimage[1], playerimage[2]);
        pcImage = loader.loadImage("/Assets/powercube.png");
    }

    @Override
    public void tick() {
        x += Math.toIntExact(Math.round(velX));
        y += Math.toIntExact(Math.round(velY));

        game.playerLoc[0] = y;
        game.playerLoc[1] = x;

        collision();

        if (handler.isUp()) velY = -GameConstants.playerSpeed;
        else if (!handler.isDown()) velY = 0;

        if (handler.isDown()) velY = GameConstants.playerSpeed;
        else if (!handler.isUp()) velY = 0;

        if (handler.isLeft()) velX = -GameConstants.playerSpeed;
        else if (!handler.isRight()) velX = 0;

        if (handler.isRight()) velX = GameConstants.playerSpeed;
        else if (!handler.isLeft()) velX = 0;

        if (handler.isSpace()) {
            shootTime = System.currentTimeMillis();
            if (shootTime - prevShootTime >= 400 && game.ammo >= 1) {
                prevShootTime = shootTime;
                shootTime = System.currentTimeMillis();
                handler.addObject(new PlayerBullet(x + 16, y + 24, ID.PlayerBullet, handler, Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getX() + camera.getX() - 7)), Math.toIntExact(Math.round(MouseInfo.getPointerInfo().getLocation().getY() + camera.getY() - 30)), x, y, ss));
                game.ammo--;
            }
        }
        if (System.currentTimeMillis() - shootTime >= 1 && game.ammo < 3) {
            game.ammo += 0.01;
        }

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

        if (Math.abs(System.currentTimeMillis() - hitTime) >= 2000 && Math.abs(System.currentTimeMillis() - shootTime) >= 2000) {
            if (game.playerHp < game.maxHp) game.playerHp += (0.001 * game.maxHp);
        }

        anim.runAnimation();
    }

    @Override
    public void render(Graphics g) {
//        if (velX == 0 && velY == 0) g.drawImage(playerimage[0], x, y, null);
//        else anim.drawAnimation(g, x, y, 0);
        g.setColor(Color.green);
        g.fillRect(x, y, 32, 48);

        //heath
        g.setColor(Color.gray);
        g.fillRect(x - 9, y - 20, 50, 10);
        g.setColor(Color.green);
        g.fillRect(x - 9, y - 20, (int) (game.playerHp/(game.maxHp/50)), 10);
        g.setColor(Color.black);
        g.drawRect(x - 9, y - 20, 50, 10);

        //ammo
        g.setColor(Color.orange);
        g.fillRect(x - 11, y - 8, (int) (game.ammo * 18), 5);
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
        return new Rectangle(x, y, 32, 48);
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
