import java.awt.Toolkit;

public class GameConstants {
    public static final int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(); //1707
    public static final int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(); //1067
    public static final int playerSpeed = 2;
    public static final int bulletSpeed = 7;
    public static final int enemySpeed = 2;
    public static final int playerShootingRange = 12;
}
