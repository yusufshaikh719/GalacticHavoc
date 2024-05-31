import com.sun.nio.file.SensitivityWatchEventModifier;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class PlayerBullet extends GameObject {

    private Handler handler;
    Game game;

    public PlayerBullet(int x, int y, ID id, Handler handler, SpriteSheet ss, double angle, Game game) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;

        // velX = (mx - x) / 10;
        // velY = (my - y) / 10;
        velX = (int) (GameConstants.bulletSpeed * Math.cos(angle));
        velY = (int) (GameConstants.bulletSpeed * Math.sin(angle));
    }

    @Override
    public void tick() {
        x += velX;
        y += velY;

        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.Block) {
                if (getBounds().intersects(temp.getBounds())) {
                    handler.removeObject(this);
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.green);
        g.fillOval(x, y, 8, 8);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 8, 8);
    }

}
