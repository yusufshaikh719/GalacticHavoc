import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class Grass extends GameObject {
    private BufferedImage grassImage;
    private Game game;
    private Handler handler;

    public Grass(int x, int y, ID id, SpriteSheet ss, Game game, Handler handler) {
        super(x, y, id, ss);
        ImageLoader loader = new ImageLoader();
        this.handler = handler;
        this.game = game;

        grassImage = loader.loadImage("/Assets/grass.png");
    }

    public void tick() {
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(grassImage, x, y, null);
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject temp = handler.object.get(i);
            if (temp.getId() == ID.Player) {
                if (getBounds().intersects(temp.getBounds())) {
                    int w = grassImage.getWidth(null);
                    int h = grassImage.getHeight(null);
                    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

                    float[] scales = { 1f, 1f, 1f, 0f };
                    float[] offsets = {0f, 0f, 0f, 120f};
                    RescaleOp rop = new RescaleOp(scales, offsets, null);
                    ((Graphics2D) g).drawImage(bi, rop, x, y);
                } else g.drawImage(grassImage, x, y, null);
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

}
