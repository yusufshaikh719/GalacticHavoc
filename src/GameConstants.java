import java.awt.Toolkit;

public class GameConstants {
    public static final int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(); //1707
    public static final int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(); //1067
    public static final int gameWidth = 64;
    public static final int gameHeight = 64;
    public static final int playerSpeed = 3;
    public static final int bulletSpeed = 7;
    public static final int enemySpeed = 2;
    public static final int playerShootingRange = 12;
    public static final float chanceToStartAlive = 0.3f;
    public static final int deathLimit = 3;
    public static final int birthLimit = 3;
    public static final int numberOfSteps = 6;

}
