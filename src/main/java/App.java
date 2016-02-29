
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.PartialDisk;
import org.lwjgl.util.glu.Sphere;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class App {
    private boolean done = false;
    private boolean fullscreen = false;
    private final String windowTitle = "NeHe's OpenGL Lesson 18 for LWJGL (Quadrics)";
    private boolean f1 = false;
    private DisplayMode displayMode;

    private boolean light;                 // Lighting ON/OFF
    private boolean lp;                    // L Pressed?
    private boolean fp;                    // F Pressed?
    private boolean sp;                    // Spacebar Pressed? ( NEW )

    private int part1;                     // Start Of Disc ( NEW )
    private int part2;                     // End Of Disc ( NEW )
    private int p1 = 0;                    // Increase 1 ( NEW )
    private int p2 = 1;                    // Increase 2 ( NEW )

    private float xrot;                    // X Rotation
    private float yrot;                    // Y Rotation
    private float xspeed;                  // X Rotation Speed
    private float yspeed;                  // Y Rotation Speed
    private float z = -5.0f;               // Depth Into The Screen


    private float lightAmbient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    private float lightDiffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    private float lightPosition[] = { 0.0f, 0.0f, 2.0f, 1.0f };

    private int filter;                    // Which Filter To Use
    private Texture[] texture = new Texture[3];    // Storage For 3 Textures
    private int object = 0;                // Which Object To Draw (NEW)

    /* There is a slight difference in the way that LWJGL does the 3D object generation.
     * The following 'C' line has been removed:
     *      GLUquadricObj *quadratic;   // Storage For Our Quadratic Objects ( NEW )
     *
     * Instead there is a class for each type of 3D object you want to generate and
     * they all are subclasses of a class called Quadric.  So instead of calling a method
     * on the static class GLU, you create an instance of the object you want, set up the settings
     * and then call it's you want and then call it's draw method.  This will be explained where
     * it happens.
     */
    private Cylinder cylinder = new Cylinder();
    private Disk disk = new Disk();
    private Sphere sphere = new Sphere();
    private Cylinder cone = new Cylinder();
    private PartialDisk partialDisk = new PartialDisk();

    public static void main(String args[]) {
        App t1 = new App();
        t1.run();
    }

    /**
     * Boucle principale
     */
    public void run() {
        try {
            init();
            while (!done) {
                readAndAct();
                render();
                Display.update();
                Thread.sleep(5);
            }
            Destroy();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Toute les updates sont fait ici, lecture de clavier,
     * lecture de souris, agissement IA, etc
     */
    private void readAndAct() {
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {       // Exit if Escape is pressed
            done = true;
        }
        if(Display.isCloseRequested()) {                     // Exit if window is closed
            done = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_F1) && !f1) {    // Is F1 Being Pressed?
            f1 = true;                                      // Tell Program F1 Is Being Held
            toggle();                                   // Toggle Fullscreen / Windowed Mode
        }
        if(!Keyboard.isKeyDown(Keyboard.KEY_F1)) {          // Is F1 Being Pressed?
            f1 = false;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_L) && !lp) {
            lp = true;
            light = !light;
            if(light) {
                GL11.glEnable(GL11.GL_LIGHTING);
            }
            else {
                GL11.glDisable(GL11.GL_LIGHTING);
            }
        }
        if(!Keyboard.isKeyDown(Keyboard.KEY_L)) {
            lp = false;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !sp) {
            sp = true;
            object++;
            if(object > 5) {
                object = 0;
            }
        }
        if(!Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            sp = false;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) {
            z -= 0.02f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_NEXT)) {
            z += 0.02f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            xspeed -= 0.01f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            xspeed += 0.01f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            yspeed += 0.01f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            yspeed -= 0.01f;
        }
    }

    private void toggle() {
        fullscreen = !fullscreen;
        try {
            Display.setFullscreen(fullscreen);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * Ici tous les dessin sont fait a l'ecran
     * le "rendering"
     * @return boolean si tous c'est bien passer
     */
    private boolean render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  // Clear The Screen And The Depth Buffer
        GL11.glLoadIdentity();                                  // Reset The View
        GL11.glTranslatef(0.0f,0.0f,z);

        GL11.glRotatef(xrot,1.0f,0.0f,0.0f);
        GL11.glRotatef(yrot,0.0f,1.0f,0.0f);

        texture[0].bind();
        GL11.glColor3f(1.0f, 1.0f, 1.0f);

        switch(object) {
            case 0:
                glDrawCube();
                break;
            case 1:
                GL11.glTranslatef(0.0f,0.0f,-1.5f);                  // Center The Cylinder
                /* Each draw routine replaces a similar line of 'C' code.  For example, here is the old
                 * cylinder 'C' line:
                 *      gluCylinder(quadratic,1.0f,1.0f,3.0f,32,32);    // A Cylinder With A Radius Of 0.5 And A Height Of 2
                 *
                 * As you can see, the quadratic is not needed as it was set within the 'cylinder' object.
                 * Then the draw() method call replaces the old glu*** call.
                 */
                cylinder.draw(1.0f, 1.0f, 3.0f, 32, 32);    // A Cylinder With A Radius Of 0.5 And A Height Of 2
                break;
            case 2:
                disk.draw(0.5f, 1.5f, 32, 32);             // Draw A Disc (CD Shape) With An Inner Radius Of 0.5, And An Outer Radius Of 2.  Plus A Lot Of Segments ;)
                break;
            case 3:
                sphere.draw(1.3f, 32, 32);                // Draw A Sphere With A Radius Of 1 And 16 Longitude And 16 Latitude Segments
                break;
            case 4:
                GL11.glTranslatef(0.0f,0.0f,-1.5f);                  // Center The Cone
                cone.draw(1.0f, 0.0f, 3.0f, 32, 32);    // A Cone With A Bottom Radius Of .5 And A Height Of 2
                break;
            case 5:
                part1 += p1;
                part2 += p2;

                if(part1 > 359) {                                   // 360 Degrees
                    p1 = 0;
                    part1 = 0;
                    p2 = 1;
                    part2 = 0;
                }
                if(part2 > 359) {                                   // 360 Degrees
                    p1 = 1;
                    p2 = 0;
                }
                partialDisk.draw(0.5f, 1.5f, 32, 32, part1, part2 - part1);    // A Disk Like The One Before
                break;
        }

        xrot += xspeed;
        yrot += yspeed;
        return true;
    }

    /**
     * Cr?e la fenetre
     * @throws Exception Throws Window.create() exception.
     */
    private void createWindow() throws Exception {
        //demande tous les mode d'affichage disponible
        DisplayMode d[] = Display.getAvailableDisplayModes();
        //parcour tous les mode retourn? et selectionne celui ou
        //hauteur = 480, largeur = 640, et ou on a 32 bits par pixel
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640
                    && d[i].getHeight() == 480
                    && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle(windowTitle);
        Display.create();
    }

    /**
     * Faire toute les initialisation ici, comme le clavier et openGL
     */
    private void init() throws Exception {

        createWindow();

        texture[0] =TextureLoader.getTexture("BMP", ResourceLoader.getResourceAsStream("textures/Wall.bmp"));

        initGL();

        /*
         * Create each GLU object and set it's normals and texture coordinate generation.
         * This replaces the following 'C' lines:
         *          quadratic=gluNewQuadric();                          // Create A Pointer To The Quadric Object (Return 0 If No Memory) (NEW)
         *          gluQuadricNormals(quadratic, GLU_SMOOTH);           // Create Smooth Normals (NEW)
         *          gluQuadricTexture(quadratic, GL_TRUE);              // Create Texture Coords (NEW)
         */
        cylinder = new Cylinder();
        cylinder.setNormals(GLU.GLU_SMOOTH);
        cylinder.setTextureFlag(true);

        disk = new Disk();
        disk.setNormals(GLU.GLU_SMOOTH);
        disk.setTextureFlag(true);

        sphere = new Sphere();
        sphere.setNormals(GLU.GLU_SMOOTH);
        sphere.setTextureFlag(true);

        cone = new Cylinder();
        cone.setNormals(GLU.GLU_SMOOTH);
        cone.setTextureFlag(true);

        partialDisk = new PartialDisk();
        partialDisk.setNormals(GLU.GLU_SMOOTH);
        partialDisk.setTextureFlag(true);
    }

    /**
     * Init OpenGL
     *
     */
    private void initGL() {                                     // All Setup For OpenGL Goes Here

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
                (float) displayMode.getWidth() / (float) displayMode.getHeight(),
                0.1f,100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

        // Really Nice Perspective Calculations
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        ByteBuffer temp = ByteBuffer.allocateDirect(16);
        temp.order(ByteOrder.nativeOrder());
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer)temp.asFloatBuffer().put(lightAmbient).flip());              // Setup The Ambient Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer)temp.asFloatBuffer().put(lightDiffuse).flip());              // Setup The Diffuse Light
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION,(FloatBuffer)temp.asFloatBuffer().put(lightPosition).flip());         // Position The Light
        GL11.glEnable(GL11.GL_LIGHT1);                          // Enable Light One
    }
    /**
     * nettoyage.
     */
    private void Destroy() {
        Display.destroy();
    }
    private void glDrawCube() {
        GL11.glBegin(GL11.GL_QUADS);
        // Front Face
        GL11.glNormal3f( 0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( 1.0f, -1.0f,  1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( 1.0f,  1.0f,  1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);
        // Back Face
        GL11.glNormal3f( 0.0f, 0.0f,-1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( 1.0f,  1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( 1.0f, -1.0f, -1.0f);
        // Top Face
        GL11.glNormal3f( 0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( 1.0f,  1.0f,  1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( 1.0f,  1.0f, -1.0f);
        // Bottom Face
        GL11.glNormal3f( 0.0f,-1.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( 1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( 1.0f, -1.0f,  1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);
        // Right Face
        GL11.glNormal3f( 1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( 1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( 1.0f,  1.0f, -1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( 1.0f,  1.0f,  1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( 1.0f, -1.0f,  1.0f);
        // Left Face
        GL11.glNormal3f(-1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f, -1.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-1.0f, -1.0f,  1.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f,  1.0f);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-1.0f,  1.0f, -1.0f);
        GL11.glEnd();
    }
}