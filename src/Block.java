import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Block extends GameObject {
    private BufferedImage blockImage;

    public Block(int x, int y, ID id, SpriteSheet ss) {
        super(x, y, id, ss);
        ImageLoader loader = new ImageLoader();

        blockImage = loader.loadImage("/Assets/wall.png");
    }

    public void tick() {
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(blockImage, x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

}
