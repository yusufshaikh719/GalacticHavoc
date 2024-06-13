import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class PlayerBullet extends GameObject {

    private Handler handler;
    private Game game;
    public boolean breakThrough;

    public PlayerBullet(int x, int y, ID id, Handler handler, int mx, int my, SpriteSheet ss, Game game, boolean breakThrough) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        this.breakThrough = breakThrough;

        double magnitude = Math.sqrt(Math.pow(mx - x, 2) + Math.pow(my - y, 2));
         velX = ((mx - x) / magnitude) * GameConstants.bulletSpeed;
         velY = ((my - y) / magnitude) * GameConstants.bulletSpeed;
    }

    @Override
    public void tick() {
        x += Math.toIntExact(Math.round(velX));
        y += Math.toIntExact(Math.round(velY));

        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.Block) {
                if (getBounds().intersects(temp.getBounds())) {
                    if (breakThrough) {
                        game.grid[temp.getY() / 32][temp.getX() / 32] = 1;
                        handler.removeObject(temp);
                    }
                    else handler.removeObject(this);
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (breakThrough) {
            g.setColor(Color.red);
            g.fillOval(x, y, 16, 16);
        } else {
            g.setColor(Color.blue);
            g.fillOval(x, y, 8, 8);
        }
    }

    @Override
    public Rectangle getBounds() {
        if (breakThrough) return new Rectangle(x, y, 16, 16);
        else return new Rectangle(x, y, 8, 8);
    }
}
