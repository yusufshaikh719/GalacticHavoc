import javax.swing.JFrame;

public class Window {
    public Window(int width, int height, String title, Game game) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);

        frame.add(game);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}