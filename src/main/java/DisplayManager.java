import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

/**
 * Created by kodoo on 15.02.2016.
 */
public class DisplayManager {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int FPS_CAP = 60;

    private static DisplayMode displayMode;

    public static void createDisplay() {
        try {
            DisplayMode d[] = new DisplayMode[0];
            d = Display.getAvailableDisplayModes();
            //parcour tous les mode retourn? et selectionne celui ou
            //hauteur = 480, largeur = 640, et ou on a 32 bits par pixel
            for (int i = 0; i < d.length; i++) {
                if (d[i].getWidth() == WIDTH
                        && d[i].getHeight() == HEIGHT
                        && d[i].getBitsPerPixel() == 32) {
                    displayMode = d[i];
                    break;
                }
            }
            Display.setDisplayMode(displayMode);
            Display.setTitle("GLASS");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDisplay() {
        Display.sync(FPS_CAP);
        Display.update();
    }

    public static DisplayMode getDisplayMode() {
        return displayMode;
    }

    public static void closeDisplay() {
        Display.destroy();
    }
}
