import java.awt.*;
import java.awt.image.BufferedImage;

public class Crate extends GameObject {
    private Handler handler;
    private Game game;
    private BufferedImage crateImage;
    private BufferedImage pcImage;
    private int hp;

    public Crate(int x, int y, ID id, SpriteSheet ss, Handler handler, Game game) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        ImageLoader loader = new ImageLoader();
        hp = 50;

        crateImage = ss.grabImage32(6, 2, 32, 32);
        pcImage = loader.loadImage("/Assets/powercube_big.png");
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
            if (temp.getId() == ID.Player) {
                if (hp <= 0 && getBounds().intersects(temp.getBounds())) {
                    game.powercubes++;
                    game.playerMaxHP += 50;
                    game.playerDmg += 5;
                    game.playerHp = game.playerMaxHP;
                    handler.removeObject(this);
                }
            }
        }
    }

    public void render(Graphics g) {
        if (hp > 0) {
            g.drawImage(crateImage, x, y, null);

            // Health
            g.setColor(Color.gray);
            g.fillRect(x - 9, y - 20, 50, 10);
            g.setColor(Color.green);
            g.fillRect(x - 9, y - 20, hp, 10);
            g.setColor(Color.black);
            g.drawRect(x - 9, y - 20, 50, 10);
        } else {
            g.drawImage(pcImage, x + 8, y + 8, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

}
