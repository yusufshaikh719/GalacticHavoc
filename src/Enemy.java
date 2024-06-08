import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
//import javafx.scene.shape.*;

public class Enemy extends GameObject {
    Handler handler;
    private BufferedImage[] enemyImage = new BufferedImage[3];
    Animation anim;
    Game game;
    private int ammo = 10;
    private long prevTime = 0;
    AStar aStar;
    private boolean canFire;
    int gotoX;
    int gotoY;
    double magnitude;
    private long shootTime;
    private long prevShootTime = 0;
    private long hitTime;

    public Enemy(int x, int y, ID id, Handler handler, SpriteSheet ss, Game game) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        aStar = new AStar();

        enemyImage[0] = ss.grabImage(4, 1, 32, 32);
        enemyImage[1] = ss.grabImage(5, 1, 32, 32);
        enemyImage[2] = ss.grabImage(6, 1, 32, 32);

        anim = new Animation(3, enemyImage[0], enemyImage[1], enemyImage[2]);

    }

    public void tick() {
        AStar.Pair src = new AStar.Pair(game.enemyLoc[0], game.enemyLoc[1]); //24, 44
        AStar.Pair dest = new AStar.Pair(game.playerLoc[0], game.playerLoc[1]); //5, 6

        String str = aStar.aStarSearch(game.grid, game.grid.length , game.grid[0].length, src, dest);
        if (str.contains("(")) {
            int commaPos = str.indexOf(',');
            int closeParanthesisPos = str.indexOf(')');
            gotoY = Integer.parseInt(str.substring(41, commaPos)); //736 / 32
            gotoX = Integer.parseInt(str.substring(commaPos + 1, closeParanthesisPos)); //1376 / 32
            gotoX = (gotoX * 32);
            gotoY = (gotoY * 32);
            magnitude = Math.sqrt(Math.pow(gotoX - x, 2) + Math.pow(gotoY - y, 2));
            velX = ((gotoX - x) / magnitude) * GameConstants.enemySpeed;
            velY = ((gotoY - y) / magnitude) * GameConstants.enemySpeed;
            if (velX > 0.01 && velX <= 1) velX = 1;
            else if (velX < -0.01 && velX >= -1) velX = -1;
            if (velY > 0.01 && velY <= 1) velY = 1;
            else if (velY < -0.01 && velY >= -1) velY = -1;
        }

        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);

            if (temp.getId() == ID.PlayerBullet) {
                if (getBounds().intersects(temp.getBounds())) {
                    game.enemyHp -= game.playerDmg;
                    handler.removeObject(temp);
                }
            }

            if (temp.getId() == ID.Block) {
                if (getBiggerBounds().intersects(temp.getBounds())) {
                    double bxPos = temp.getBounds().getX();
                    double byPos = temp.getBounds().getY();
                    if (bxPos < x && Math.abs(bxPos - x) > Math.abs(byPos - y)) {
                        if (velX < 0) velX = 0;
                    } else if (bxPos > x && Math.abs(bxPos - x) > Math.abs(byPos - y)) {
                        if (velX > 0) velX = 0;
                    } else if (byPos < y && Math.abs(bxPos - x) < Math.abs(byPos - y)) {
                        if (velY < 0) velY = 0;
                    } else if (byPos > y && Math.abs(bxPos - x) < Math.abs(byPos - y)) {
                        if (velY > 0) velY = 0;
                    }
                    if (velX == 0 && velY == 0) {
                        velX = ((gotoX - x) / magnitude);
                        velY = ((gotoY - y) / magnitude);
                    }
                }
            }
            if (temp.getId() == ID.Player) {
                shootTime = System.currentTimeMillis();
                if (shootTime - prevShootTime >= 400 && game.enemyAmmo >= 1) {
                    prevShootTime = shootTime;
                    shootTime = System.currentTimeMillis();
                    handler.addObject(new EnemyBullet(x + 16, y + 24, ID.EnemyBullet, handler, ss, Math.atan2(temp.getY() - y, temp.getX() - x), game));
                    game.enemyAmmo--;
                }
            }
        }

        x += Math.toIntExact(Math.round(velX));
        y += Math.toIntExact(Math.round(velY));

        game.enemyLoc[0] = y / 32;
        game.enemyLoc[1] = x / 32;

        if (System.currentTimeMillis() - shootTime >= 1 && game.enemyAmmo < 3) {
            game.enemyAmmo += 0.01;
        }

        if (game.enemyHp <= 0) handler.removeObject(this);
        anim.runAnimation();
    }

    public void render(Graphics g) {
//        anim.drawAnimation(g, x, y, 0);
        g.setColor(Color.yellow);
        g.fillRect(x, y, 32, 32);
        //health bar
        g.setColor(Color.gray);
        g.fillRect(x - 9, y - 10, 50, 10);
        g.setColor(Color.green);
        g.fillRect(x - 9, y - 10, game.enemyHp/2, 10);
        g.setColor(Color.black);
        g.drawRect(x - 9, y - 10, 50, 10);
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.Player) {
                g.drawLine(x + 18, y + 18, temp.getX() + 16, temp.getY() + 24);
            }
            if (temp.getId() == ID.Block) {
                if (getSightBounds().intersects(temp.getBounds())) g.fillRect(temp.getX(), temp.getY(), 32, 32);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    public Rectangle getBiggerBounds() {
        return new Rectangle(x - 16, y - 16, 64, 64);
    }

    public Line2D.Double getSightBounds() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.Player) {
                return new Line2D.Double(x + 18, y + 18, temp.getX() + 16, temp.getY() + 24);
            }
        }
        return null;
    }

    public Rectangle getBoundsBig() {
        return new Rectangle(x - 16, y - 16, 64, 64);
    }
}