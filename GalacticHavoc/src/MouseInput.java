// import java.awt.event.KeyEvent;
// import java.awt.event.MouseAdapter;
// import java.awt.event.MouseEvent;

// public class MouseInput extends MouseAdapter {
//     private Handler handler;
//     private Camera camera;
//     private Game game;
//     private SpriteSheet ss;

//     public MouseInput(Handler handler, Camera camera, Game game, SpriteSheet ss) {
//         this.handler = handler;
//         this.camera = camera;
//         this.game = game;
//         this.ss = ss;
//     }

//     public void mouseMoved(MouseEvent e) {
//         int mx = (int) (e.getX() + camera.getX());
//         int my = (int) (e.getY() + camera.getY());
//         System.out.println("otuside");
//         for (int i = 0; i < handler.object.size(); i++) {
//             GameObject temp = handler.object.get(i);
//             if (temp.getId() == ID.Player && game.ammo >= 1 && handler.isSpace()) {
//                 System.out.println("within");
//                 handler.addObject(new Bullet(temp.getX() + 16, temp.getY() + 24, ID.Bullet, handler, mx, my, ss));
//                 game.ammo--;
//             }
//         }
//     }
// }
