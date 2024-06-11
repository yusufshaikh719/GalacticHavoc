import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
    private Handler handler;

    public KeyInput (Handler handler) {
        this.handler = handler;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) handler.setUp(true);
        if (key == KeyEvent.VK_A) handler.setLeft(true);
        if (key == KeyEvent.VK_S) handler.setDown(true);
        if (key == KeyEvent.VK_D) handler.setRight(true);
        if (key == KeyEvent.VK_SPACE) handler.setSpace(true);
        if (key == KeyEvent.VK_Z) handler.setAlt(true);
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) handler.setUp(false);
        if (key == KeyEvent.VK_A) handler.setLeft(false);
        if (key == KeyEvent.VK_S) handler.setDown(false);
        if (key == KeyEvent.VK_D) handler.setRight(false);
        if (key == KeyEvent.VK_SPACE) handler.setSpace(false);
        if (key == KeyEvent.VK_Z) handler.setAlt(false);
    }
}
