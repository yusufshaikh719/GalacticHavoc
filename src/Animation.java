import java.awt.*;
import java.awt.image.BufferedImage;

public class Animation {

    private int speed;
    private int frames;
    private int index = 0;
    private int count = 0;

    private BufferedImage img1;
    private BufferedImage img2;
    private BufferedImage img3;
    private BufferedImage img4;

    private BufferedImage currentImg;

    public Animation(int speed, BufferedImage img1, BufferedImage img2, BufferedImage img3, BufferedImage img4) {
        this.speed = speed;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        frames = 4;
    }

    public void runAnimation() {
        index++;
        if (index > speed) {
            index = 0;
            nextFrame();
        }
    }

    public void nextFrame() {

        switch (frames) {
            case 4:
                if(count == 0)
                    currentImg = img1;
                if(count == 1)
                    currentImg = img2;
                if(count == 2)
                    currentImg = img3;
                if(count == 4)
                    currentImg = img4;

                count++;

                if (count > frames)
                    count = 0;

                break;
        }
    }

    public void drawAnimation(Graphics g, double x, double y, int offset) {
        g.drawImage(currentImg, (int) x - offset, (int) y, null);
    }
}