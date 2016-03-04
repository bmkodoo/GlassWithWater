import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class App {

    private static float lightAmbient[] = {0.5f, 0.5f, 0.5f, 1.0f};
    private static float lightDiffuse[] = {1.0f, 1.0f, 1.0f, 0.0f};
    private static float lightPosition[] = {0.0f, 0.0f, 2.0f, 0.0f};

    private static Texture[] texture = new Texture[3];

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        initGL();
        //loadTextures();

        float rotate = 0;
        while (!Display.isCloseRequested()) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  // Clear The Screen And The Depth Buffer
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0f, 0.0f, -5.0f);
            GL11.glRotatef(rotate += 0.2, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(rotate, 0.0f, 1.0f, 0.0f);

            //texture[0].bind();
            GL11.glColor3f(1.0f, 1.0f, 1.0f);

            glDrawCube();
            DisplayManager.updateDisplay();
        }

        DisplayManager.closeDisplay();
    }

    private static void loadTextures() {
        try {
            texture[0] = TextureLoader.getTexture(
                    "BMP", ResourceLoader.getResourceAsStream("textures/Wall.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initGL() {                                     // All Setup For OpenGL Goes Here

        GL11.glEnable(GL11.GL_TEXTURE_2D);                            // Enable Texture Mapping
        GL11.glShadeModel(GL11.GL_SMOOTH);                            // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);               // Black Background
        GL11.glClearDepth(1.0f);                                 // Depth Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST);                            // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);                             // The Type Of Depth Testing To Do
        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(45.0f,
                (float) DisplayManager.getDisplayMode().getWidth() / (float) DisplayManager.getDisplayMode().getHeight(),
                0.1f, 100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

        // Really Nice Perspective Calculations
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        ByteBuffer temp = ByteBuffer.allocateDirect(16);
        temp.order(ByteOrder.nativeOrder());


        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer) temp.asFloatBuffer().put(lightAmbient).flip());              // Setup The Ambient Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer) temp.asFloatBuffer().put(lightDiffuse).flip());              // Setup The Diffuse Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, (FloatBuffer) temp.asFloatBuffer().put(lightPosition).flip());         // Position The Light

        FloatBuffer lModelAmbient = BufferUtils.createFloatBuffer(4);
        lModelAmbient.put(0.5f).put(0.5f).put(0.5f).put(1.0f).flip();

        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, lModelAmbient);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT1);                          // Enable Light One
    }

    private static void glDrawCube() {
        GL11.glBegin(GL11.GL_QUADS);
        // Front Face
        GL11.glNormal3f(0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
        // Back Face
        GL11.glNormal3f(0.0f, 0.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);
        // Top Face
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);
        // Bottom Face
        GL11.glNormal3f(0.0f, -1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
        // Right Face
        GL11.glNormal3f(1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(1.0f, -1.0f, 1.0f);
        // Left Face
        GL11.glNormal3f(-1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-1.0f, -1.0f, 1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-1.0f, 1.0f, -1.0f);
        GL11.glEnd();
    }
}