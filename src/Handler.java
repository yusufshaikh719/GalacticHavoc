import java.awt.Graphics;
import java.util.ArrayList;

public class Handler {
    public ArrayList<GameObject> object = new ArrayList<>();
    private boolean up = false, down = false, right = false, left = false, space = false, alt = false;

    public void tick() {
        for (int i = 0; i < object.size(); i++) {
            GameObject temp = object.get(i);

            temp.tick();
        }
    }

    public void render(Graphics g) {
        for (int i = 0; i < object.size(); i++) {
            GameObject temp = object.get(i);

            temp.render(g);
        }
    }

    public void addObject(GameObject temp) {
        object.add(temp);
    }

    public void removeObject(GameObject temp) {
        object.remove(temp);
    }

    public ArrayList<GameObject> getObject() {
        return object;
    }

    public void setObject(ArrayList<GameObject> object) {
        this.object = object;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isSpace() {
        return space;
    }

    public void setSpace(boolean space) {
        this.space = space;
    }

    public boolean isAlt() {
        return alt;
    }

    public void setAlt(boolean alt) {
        this.alt = alt;
    }
}
