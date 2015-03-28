package cz.muni.fi.pv112;

import static javax.media.opengl.GL2.*;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import com.jogamp.opengl.util.gl2.GLUT;
import cz.muni.fi.pv112.loaders.ObjLoader;
import cz.muni.fi.pv112.loaders.ObjLoaderVertexWrapper;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;

import javax.media.opengl.glu.GLU;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author milos_000
 */
public class Scene implements GLEventListener, KeyListener, MouseMotionListener
{
    private GLU glu;
    private GLUT glut;
    private ObjLoader model;

    private MainWindow window;
    private Cursor invisibleCursor;
    private double[] mouseCenters = {300,300};

    private final float moveStep = 0.1f;
    private final float mouseStep = 0.15f;
    private final float personHeight = 5.0f;

    private float time;
    private double[] lookAt = {
            0.0f, personHeight, 15.0f,
            0f, 0f, 0f,
            0f, 1f, 0f};

    private Robot robot;
    private boolean trackingMouse = false;

    private boolean[] keysPressed = {false, false, false, false, false, false}; // W S A D
    private double[] lookDirection = {0,0};

    public Scene()
    {
        time = 0;
    }

    private ObjLoaderVertexWrapper vase = new ObjLoaderVertexWrapper("/resources/vase.obj");
    private ObjLoaderVertexWrapper table = new ObjLoaderVertexWrapper("/resources/table.obj");
    private ObjLoaderVertexWrapper m4a1 = new ObjLoaderVertexWrapper("/resources/m4a1.obj");

    private ObjLoaderVertexWrapper[] sceneObjects = {vase, table, m4a1};

    @Override
    public void init(GLAutoDrawable drawable)
    {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
//

//        load objects
        for(ObjLoaderVertexWrapper item : sceneObjects) {
            item.load();
        }

        robot.mouseMove(drawable.getSurfaceWidth()/2, drawable.getSurfaceHeight()/2);
        trackingMouse = true;


        GL2 gl = drawable.getGL().getGL2();

        glu = new GLU();
        glut = new GLUT();
        gl.glClearColor(.6f,.6f,.6f,1);

        // TODO LIGHTS
        gl.glClearDepth(1.0f);

        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_NORMALIZE);


        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[]{1,0,0}, 0);
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, new float[]{1,0,0}, 0);


        gl.glEnable(GL_LIGHT1);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, new float[]{1,1,1}, 0);
        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, new float[]{1,1,1}, 0);

        gl.glEnable(GL_DEPTH_TEST);

        float[] ambientMat = {0.1f, 0.1f, 0.1f};
        float[] specularMat = {1.0f, 1.0f, 1.0f};

        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, ambientMat, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, specularMat, 0);

        gl.glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 30);
        gl.glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 30);

        gl.glLightfv(GL_LIGHT1, GL_POSITION, new float[]{4, 4, 4, 0}, 0);

    }

    @Override
    public void dispose(GLAutoDrawable drawable)
    {
    }

    @Override
    public void display(GLAutoDrawable drawable)
    {

        trackMouse(drawable);

        GL2 gl = drawable.getGL().getGL2();
        time += 0.01;

        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{1,1,1}, 0);

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

//        glu.gluLookAt(10, 10, 10, 0, 0, 0, 0, 1, 0);
        setCamera();

        gl.glPushMatrix();
        gl.glRotatef(time * 20, 0, 1, 0);
        gl.glTranslatef(4,4,4);
        gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[]{0, 0, 0, 1}, 0);
        gl.glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, new float[]{0,-1,0}, 0);

        glut.glutSolidSphere(0.2f, 10, 10);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(4, 4, 4);
//        gl.glLightfv(GL_LIGHT1, GL_POSITION, new float[]{0, 0, 0, 1}, 0);
//        glut.glutSolidSphere(0.2f, 10, 10);
        gl.glPopMatrix();

        //  Draw obj
        gl.glPushMatrix(); // matrix scale to 0.05
        gl.glScalef(0.05f, 0.05f, 0.05f);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{0.9f,0.8f, 0.5f}, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 100);
//        render table
//        table.render(gl);
        table.render(gl);

//        render vase
        gl.glPushMatrix();
        gl.glTranslatef(20, 70, 20);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{0.2f,0.4f, 0.3f}, 0);
        vase.render(gl);
        gl.glPopMatrix();

//        render m4a1
        gl.glPushMatrix();
        gl.glRotatef(45,0,1,0);
        gl.glTranslatef(-20, 73, -20);
        gl.glRotatef(90,0,0,1);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{0.2f, 0.2f, 0.2f}, 0);
        gl.glScalef(0.5f,0.5f,0.5f);
        m4a1.render(gl);
        gl.glPopMatrix();


        gl.glPushMatrix(); // render newton
        gl.glTranslatef(0,80,0); // move it
        gl.glScalef(10,10,10); // tmp scale
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{0.75f, 0.75f, 0.75f}, 0);

        gl.glPushMatrix(); // base
        gl.glScalef(3,0.1f,1); // dimensions of base 30 * 1 * 10
        glut.glutSolidCube(10);
        gl.glPopMatrix(); // end base matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(-14.5f, 0, 4.5f);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCylinder(0.5f,15,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(14.5f, 0, 4.5f);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCylinder(0.5f,15,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(-14.5f, 0, -4.5f);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCylinder(0.5f,15,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(14.5f, 0, -4.5f);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCylinder(0.5f,15,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(15f, 15, 4.5f);
        gl.glRotatef(-90, 0, 1, 0);
        glut.glutSolidCylinder(0.5f,30,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(15f, 15, -4.5f);
        gl.glRotatef(-90, 0, 1, 0);
        glut.glutSolidCylinder(0.5f,30,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); // ball
        gl.glTranslatef(0, 5, 0);
        glut.glutSolidSphere(1.5f, 20, 20);
        gl.glPushMatrix(); // rope one
        gl.glRotatef(-65, 1, 0, 0);
        glut.glutSolidCylinder(0.075f, 11, 5, 5);
        gl.glPopMatrix(); // end rope

        gl.glPushMatrix(); //rope two
        gl.glRotatef(-115, 1, 0, 0);
        glut.glutSolidCylinder(0.075f, 11, 5, 5);
        gl.glPopMatrix(); // end rope
        gl.glPopMatrix(); // end ball matrix



        gl.glPopMatrix(); // end newton matrix








        gl.glPopMatrix(); // matrix scale to 0.5

        // Draw Floor
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{0.5f,0.25f,0.01f}, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 100);
        drawFloor(gl, -40, 40, -40, 40, 0.25f, 0.25f);


        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);


    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL2 gl = drawable.getGL().getGL2();

        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        //gl.glOrthof(-10, 10, -10, 10, 0.1f, 100);
        glu.gluPerspective(55, (float)width/height, 1, 100);

        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL_MODELVIEW);
    }

    private void drawFloor(GL2 gl, float x_from, float x_to, float z_from, float z_to, float x_step, float z_step) {
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        for (float z = z_from; z < z_to; z += z_step) {
            gl.glBegin(GL_QUAD_STRIP);
            for (float x = x_from; x <=  x_to; x += x_step) {
                gl.glVertex3f(x, 0, z);
                gl.glVertex3f(x, 0, z + z_step);
            }
            gl.glEnd();
        }
    }

    private void setCamera() {
        if(keysPressed[0]) {
            lookAt[2] += moveStep * (lookAt[5] - lookAt[2]);
            lookAt[0] += moveStep * (lookAt[3] - lookAt[0]);
        }
        if (keysPressed[1]) {
            lookAt[2] -= moveStep * (lookAt[5] - lookAt[2]);
            lookAt[0] -= moveStep * (lookAt[3] - lookAt[0]);
        }
        if (keysPressed[2]) {
            lookAt[2] -= moveStep * (lookAt[3] - lookAt[0]);
            lookAt[0] += moveStep * (lookAt[5] - lookAt[2]);
        }
        if (keysPressed[3]) {
            lookAt[2] += moveStep * (lookAt[3] - lookAt[0]);
            lookAt[0] -= moveStep * (lookAt[5] - lookAt[2]);
        }
        if (keysPressed[4]) {
            lookAt[1] += moveStep;
        }
        if (keysPressed[5]) {
            lookAt[1] -= moveStep;
        }

        setViewDirection();

        glu.gluLookAt(
            lookAt[0], lookAt[1], lookAt[2],
            lookAt[3], lookAt[4], lookAt[5],
            lookAt[6], lookAt[7], lookAt[8]
        );
    }

    public void setViewDirection() {
        if (trackingMouse) {
            double y_direction = lookDirection[1] * -1;
            double x_look = (double) Math.cos(Math.toRadians(lookDirection[0]));
            double y_look = (double) Math.sin(Math.toRadians(y_direction));
            double z_look = (double) Math.sin(Math.toRadians(lookDirection[0]));

            lookAt[3] = lookAt[0] + x_look;
            lookAt[4] = lookAt[1] + y_look;
            lookAt[5] = lookAt[2] + z_look;
        }
    }

    private void drawObj(GL2 gl, ObjLoader model) {
        gl.glShadeModel(GL_SMOOTH);

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            keysPressed[0] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            keysPressed[1] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            keysPressed[2] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            keysPressed[3] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            keysPressed[4] = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            keysPressed[5] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            keysPressed[0] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            keysPressed[1] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            keysPressed[2] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            keysPressed[3] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (trackingMouse) {
                window.setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                trackingMouse = false;
            } else {
                window.setCursor (invisibleCursor);
                trackingMouse = true;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            keysPressed[4] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            keysPressed[5] = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (trackingMouse) {
//            we have to account the fact that window can move on screen
            double diff_x = e.getX() - mouseCenters[0] + window.getCanvas().getLocationOnScreen().getX();
            double diff_y = e.getY() - mouseCenters[1] +  window.getCanvas().getLocationOnScreen().getY();

//        System.out.println("Is on (" +   e.getX() + ", " + e.getY() + ")");
//        System.out.println("Center on (" +  mouseCenters[0] + ", " + mouseCenters[1] + ")");
//        System.out.println("Moved (" +  diff_x + ", " + diff_y + ")");

            lookDirection[0] += diff_x * mouseStep;
            lookDirection[1] += diff_y * mouseStep;

            lookDirection[1] = lookDirection[1] > 90 ? 90 : lookDirection[1];
            lookDirection[1] = lookDirection[1] < -90 ? -90 : lookDirection[1];
            lookDirection[0] = lookDirection[0] % 360;
        }
    }

    void trackMouse(GLAutoDrawable drawable) {
        if (trackingMouse) {
            int width = drawable.getSurfaceWidth();
            int height = drawable.getSurfaceHeight();
//            we have to account the fact that window can move on screen
            double screen_x = window.getCanvas().getLocationOnScreen().getX();
            double screen_y = window.getCanvas().getLocationOnScreen().getY();

            mouseCenters[0] = screen_x + (width / 2);
            mouseCenters[1] = screen_y + (height / 2);

            robot.mouseMove((int)mouseCenters[0],(int)mouseCenters[1]);
        }
    }

    public void setWindow(MainWindow window) {
        this.window = window;
    }

    public void setInvisibleCursor(Cursor invisibleCursor) {
        this.invisibleCursor = invisibleCursor;
    }
}
