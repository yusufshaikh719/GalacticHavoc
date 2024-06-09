import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class EnemyBullet extends GameObject {

    private Handler handler;

    public EnemyBullet(int x, int y, ID id, Handler handler, SpriteSheet ss, double angle) {
        super(x, y, id, ss);
        this.handler = handler;

        velX = (int) (GameConstants.bulletSpeed * Math.cos(angle));
        velY = (int) (GameConstants.bulletSpeed * Math.sin(angle));
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
        g.setColor(Color.green);
        g.fillOval(x, y, 8, 8);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 8, 8);
    }

}
