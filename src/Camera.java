public class Camera {
    private float x, y;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void tick(GameObject obj) {
        x += ((obj.getX() - x) - GameConstants.screenWidth / 4) * 0.05f;
        y += ((obj.getY() - y) - GameConstants.screenHeight / 4) * 0.05f;

        if (x <= 0) x = 0;
        if (x >= (32*64) - (GameConstants.screenWidth) + 16) x = (32*64) - (GameConstants.screenWidth) + 16;
        if (y <= 0) y = 0;
        if (y >= (32*64) - (GameConstants.screenHeight) + 40) y = (32*64) - (GameConstants.screenHeight) + 40;
    }
    
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
