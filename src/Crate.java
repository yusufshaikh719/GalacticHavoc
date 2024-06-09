import java.awt.*;
import java.awt.image.BufferedImage;

public class Crate extends GameObject {
    private Handler handler;
    private Game game;
    private BufferedImage crateImage;
    private int hp;

    public Crate(int x, int y, ID id, SpriteSheet ss, Handler handler, Game game) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        hp = 50;

        crateImage = ss.grabImage(6, 2, 32, 32);
    }

    public void tick() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.PlayerBullet) {
                if (getBounds().intersects(temp.getBounds())) {
                    hp -= game.playerDmg;
                    handler.removeObject(temp);
                }
            }
        }
        if (hp <= 0) {
            game.powercubes++;
            handler.removeObject(this);
        }
    }

    public void render(Graphics g) {
        g.drawImage(crateImage, x, y, null);

        g.setColor(Color.gray);
        g.fillRect(x - 9, y - 20, 50, 10);
        g.setColor(Color.green);
        g.fillRect(x - 9, y - 20, hp, 10);
        g.setColor(Color.black);
        g.drawRect(x - 9, y - 20, 50, 10);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

}
