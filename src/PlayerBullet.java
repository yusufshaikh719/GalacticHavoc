import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class PlayerBullet extends GameObject {

    private Handler handler;

    public PlayerBullet(int x, int y, ID id, Handler handler, int mx, int my, int px, int py, SpriteSheet ss) {
        super(x, y, id, ss);
        this.handler = handler;

        double magnitude = Math.sqrt(Math.pow(mx - px, 2) + Math.pow(my - py, 2));
         velX = ((mx - px) / magnitude) * GameConstants.bulletSpeed;
         velY = ((my - py) / magnitude) * GameConstants.bulletSpeed;
    }

    @Override
    public void tick() {
        x += Math.toIntExact(Math.round(velX));
        y += Math.toIntExact(Math.round(velY));

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
        g.setColor(Color.blue);
        g.fillOval(x, y, 8, 8);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 8, 8);
    }

}
