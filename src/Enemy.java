import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class Enemy extends GameObject {
    private Handler handler;
    private BufferedImage[] enemyImage = new BufferedImage[4];
    private ImageLoader loader;
    private Animation anim;
    private Game game;
    private AStar aStar;
    private int gotoX;
    private int gotoY;
    private double magnitude;
    private long shootTime;
    private long hitTime;
    private boolean canFire;

    public Enemy(int x, int y, ID id, Handler handler, SpriteSheet ss, Game game) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        aStar = new AStar();
        loader = new ImageLoader();

        shootTime = System.currentTimeMillis();

        enemyImage[0] = ss.grabImage64(1, 1, 64, 64);
        enemyImage[1] = ss.grabImage64(2, 1, 64, 64);
        enemyImage[2] = ss.grabImage64(3, 1, 64, 64);
        enemyImage[3] = ss.grabImage64(4, 1, 64, 64);
        anim = new Animation(3, enemyImage[0], enemyImage[1], enemyImage[2], enemyImage[3]);
    }

    public void tick() {

        // Pathplanning
        AStar.Pair src = new AStar.Pair(game.enemyLoc[0], game.enemyLoc[1]);
        AStar.Pair dest = new AStar.Pair(game.playerLoc[0], game.playerLoc[1]);
        int randoX = 5;
        int randoY = 6;
        if (src.equals(dest)) {
            randoX = (int) (Math.random() * 64);
            randoY = (int) (Math.random() * 36);
            while (game.grid[randoY][randoX] == 0) {
                randoX = (int) (Math.random() * 64);
                randoY = (int) (Math.random() * 36);
            }
        }
        if (game.enemyHp < (game.enemyMaxHP / 2.5)) dest = new AStar.Pair(36 - game.playerLoc[0], 64 - game.playerLoc[1]);
        if (!game.playerIsVisible) {
            dest = new AStar.Pair(randoY, randoX);
        }

        String str = aStar.aStarSearch(game.grid, game.grid.length , game.grid[0].length, src, dest);
        if (str.contains("(")) {
            int commaPos = str.indexOf(',');
            int closeParanthesisPos = str.indexOf(')');
            gotoY = Integer.parseInt(str.substring(41, commaPos));
            gotoX = Integer.parseInt(str.substring(commaPos + 1, closeParanthesisPos));
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

        canFire = true;
        game.enemyIsVisible = true;
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);

            if (temp.getId() == ID.Grass) {
                if (getBounds().intersects(temp.getBounds())) game.enemyIsVisible = false;
            }

            if (temp.getId() == ID.Block) {

                // If too close to a wall
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

                // If walls are in the way of shooting
                if (getSightBounds().intersects(temp.getBounds())) canFire = false;
            }
        }

        // Shooting
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.Player) {
                double dist = Math.sqrt(Math.pow(temp.getX() - x, 2) +  Math.pow(temp.getY() - y, 2));
                if (System.currentTimeMillis() - shootTime >= 1500 && game.enemyAmmo >= 1 && canFire && dist <= 32 * GameConstants.playerShootingRange) {
                    shootTime = System.currentTimeMillis();
                    double angle = Math.atan2(temp.getY() - y, temp.getX() - x);
                    if (!game.playerIsVisible) angle += Math.random() * (0.3 + 0.3) -0.3;
                    for (int j = 0; j < 5; j++) {
                        handler.addObject(new EnemyBullet(x + 16, y + 24, ID.EnemyBullet, handler, ss, angle + (j / 10.0)));
                        handler.addObject(new EnemyBullet(x + 16, y + 24, ID.EnemyBullet, handler, ss, angle - (j / 10.0)));
                    }
                    game.enemyAmmo--;
                }
            }
        }

        // Reloading
        if (Math.abs(System.currentTimeMillis() - shootTime) >= 1 && game.enemyAmmo < 3) {
            game.enemyAmmo += 0.01;
        }

        // Healing
        if (Math.abs(System.currentTimeMillis() - hitTime) >= 4000 && Math.abs(System.currentTimeMillis() - shootTime) >= 4000) {
            if (game.enemyHp < game.enemyMaxHP) game.enemyHp += (0.001 * 100);
        }

        // Updating position
        x += Math.toIntExact(Math.round(velX));
        y += Math.toIntExact(Math.round(velY));
        game.enemyLoc[0] = y / 32;
        game.enemyLoc[1] = x / 32;

        if ((int) game.enemyHp <= 0) {
            handler.removeObject(this);
            game.endVictory = true;
        }
        anim.runAnimation();
    }

    public void render(Graphics g) {
        if (!game.enemyIsVisible) g.drawImage(loader.loadImage("/Assets/invisible.png"), x, y, null);
        else g.drawImage(enemyImage[0], x - 15, y - 22, null);
        //heath
        if (game.enemyIsVisible) {
            g.setColor(Color.gray);
            g.fillRect(x - 9, y - 10, 50, 10);
            g.setColor(Color.green);
            g.fillRect(x - 9, y - 10, (int) (game.enemyHp/(game.enemyMaxHP / 50)), 10);
            g.setColor(Color.black);
            g.drawRect(x - 9, y - 10, 50, 10);
            g.setFont(new Font("Courier", Font.BOLD, 10));
            g.drawString("" + (int) game.enemyHp, x + 7, y - 1);
        }

        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            // Taking damage
            if (temp.getId() == ID.PlayerBullet) {
                if (getBounds().intersects(temp.getBounds())) {
                    game.enemyHp -= game.playerDmg;
                    handler.removeObject(temp);
                    game.altCharge++;
                    hitTime = System.currentTimeMillis();
                    g.setColor(Color.yellow);
                    g.setFont(new Font("Courier", Font.BOLD, 30));
                    g.drawString("" + game.playerDmg, x, y - 30);
                }
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
}