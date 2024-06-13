import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class PlayerBullet extends GameObject {

    private Handler handler;
    private Game game;
    public boolean breakThrough;
    private int startX;
    private int startY;

    public PlayerBullet(int x, int y, ID id, Handler handler, double vx, double vy, SpriteSheet ss, Game game, boolean breakThrough) {
        super(x, y, id, ss);
        startX = x;
        startY = y;
        this.handler = handler;
        this.game = game;
        this.breakThrough = breakThrough;

         velX = vx;
         velY = vy;
    }

    @Override
    public void tick() {
        x += Math.toIntExact(Math.round(velX));
        y += Math.toIntExact(Math.round(velY));

        double distance = Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2));
        if (distance > (32 * GameConstants.playerShootingRange)) handler.removeObject(this);

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
