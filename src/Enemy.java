import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
//import javafx.scene.shape.*;

public class Enemy extends GameObject {
    Handler handler;
    private BufferedImage[] enemyImage = new BufferedImage[3];
    Animation anim;
    Game game;
    private int ammo = 10;
    private long time;
    private long prevTime = 0;
    AStar aStar;
    private boolean canFire;
    public Enemy(int x, int y, ID id, Handler handler, SpriteSheet ss, Game game) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        aStar = new AStar();
//        canFire = new ArrayList<>(){{canFire.add(false);}};

        enemyImage[0] = ss.grabImage(4, 1, 32, 32);
        enemyImage[1] = ss.grabImage(5, 1, 32, 32);
        enemyImage[2] = ss.grabImage(6, 1, 32, 32);

//        intArray = game.blocks.stream().map(  u  ->  u.stream().mapToInt(i->i).toArray()  ).toArray(int[][]::new); //410624 blocks

        anim = new Animation(3, enemyImage[0], enemyImage[1], enemyImage[2]);

    }

    public void tick() {
        AStar.Pair src = new AStar.Pair(game.enemyLoc[0], game.enemyLoc[1]);
        AStar.Pair dest = new AStar.Pair(game.playerLoc[0], game.playerLoc[1]);

        String str = aStar.aStarSearch(game.grid, game.grid.length , game.grid[0].length, src, dest);
        // System.out.println("Pure string: " + str);
        // if (str.contains("(")) {
        //     int commaPos = str.indexOf(',');
        //     int closeParanthesisPos = str.indexOf(')');
        //     int newyPos = Integer.parseInt(str.substring(41, commaPos)); //736 / 32
        //     int newxPos = Integer.parseInt(str.substring(commaPos + 1, closeParanthesisPos)); //1376 / 32
        //     System.out.println("go to xpos: " + newxPos * 32 + "  curr xpos: " + x);
        //     System.out.println("go to ypos: " + newyPos * 32 + "  curr ypos: " + y);
        //     velX = (newxPos * 32 - x) / 16.0f; // (736 - 1408 / 200)
        //     velY = (newyPos * 32 - y) / 16.0f;
        //     System.out.println("velX: " + velX);
        //     System.out.println("vely: " + velY);
        // }
        x += Math.round(velX);
        y += Math.round(velY);

        game.enemyLoc[0] = y / 32;
        game.enemyLoc[1] = x / 32;

        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);

            if (temp.getId() == ID.PlayerBullet) {
                if (getBounds().intersects(temp.getBounds())) {
                    game.enemyHp -= game.playerDmg;
                    handler.removeObject(temp);
                }
            }

            if (temp.getId() == ID.Block) {
                if (getSightBounds().intersects(temp.getBounds()));
                canFire = false;
            }
            if (temp.getId() == ID.Player) {
                time = System.currentTimeMillis();
                if (time - prevTime >= 600 && canFire) {
                    prevTime = time;
                    time = System.currentTimeMillis();
                    handler.addObject(new EnemyBullet(x + 16, y + 24, ID.EnemyBullet, handler, ss, Math.atan2(temp.getY() - y, temp.getX() - x), game));
                    ammo--;
                }
            }
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
                if (getSightBounds().intersects(temp.getBounds())) {
                    g.fillRect(temp.getX(), temp.getY(), 32, 32);
//                    canFire = false;
                } else {
//                    canFire = true;
                }
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
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
