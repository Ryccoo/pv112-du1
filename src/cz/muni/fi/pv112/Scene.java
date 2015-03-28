package cz.muni.fi.pv112;

import static javax.media.opengl.GL2.*;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import com.jogamp.opengl.util.gl2.GLUT;
import cz.muni.fi.pv112.controlls.KeyboardController;
import cz.muni.fi.pv112.controlls.MouseController;
import cz.muni.fi.pv112.loaders.ObjLoader;
import cz.muni.fi.pv112.loaders.ObjLoaderVertexWrapper;
import cz.muni.fi.pv112.renderers.NewtonBalls;

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
public class Scene implements GLEventListener
{
    private GLU glu;
    private GLUT glut;

    private MainWindow window;
    private Cursor invisibleCursor;

    private final float personHeight = 5.0f;

    private float time;
    private double[] lookAt = {
            0.0f, personHeight, 15.0f,
            0f, 0f, 0f,
            0f, 1f, 0f};

    private Robot robot;
    private boolean trackingMouse = false;

    private ObjLoaderVertexWrapper vase = new ObjLoaderVertexWrapper("/resources/vase.obj");
    private ObjLoaderVertexWrapper table = new ObjLoaderVertexWrapper("/resources/table.obj");
    private ObjLoaderVertexWrapper m4a1 = new ObjLoaderVertexWrapper("/resources/m4a1.obj");
    private NewtonBalls newton;

    private ObjLoaderVertexWrapper[] sceneObjects = {vase, table, m4a1};

//    controlls
    private KeyboardController keyboard;
    private MouseController mouse;

    public Scene()
    {
        time = 0;
    }

    public void setMouse(MouseController mouse) {
        this.mouse = mouse;
    }

    public void setKeyboard(KeyboardController keyboard) {
        this.keyboard = keyboard;
    }

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

//        init custom objects
        newton = new NewtonBalls(gl, glut);

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

        if(trackingMouse) {
            mouse.trackMouse(drawable);
        }

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
        gl.glTranslatef(-20, 72, -20);
        gl.glRotatef(90,0,0,1);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{0.2f, 0.2f, 0.2f}, 0);
        gl.glScalef(0.5f,0.5f,0.5f);
        m4a1.render(gl);
        gl.glPopMatrix();


        gl.glPushMatrix();
        gl.glRotatef(-70, 0, 1, 0);
        gl.glTranslatef(-13, 70.5f, -40); // move it
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{0.5f, 0.5f, 0.5f}, 0);
        newton.render();
        gl.glPopMatrix();

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

        lookAt = keyboard.getMovement(lookAt);

        if(trackingMouse) {
            lookAt = mouse.setViewDirection(lookAt);
        }

        glu.gluLookAt(
            lookAt[0], lookAt[1], lookAt[2],
            lookAt[3], lookAt[4], lookAt[5],
            lookAt[6], lookAt[7], lookAt[8]
        );
    }

    public void setWindow(MainWindow window) {
        this.window = window;
    }

    public void setInvisibleCursor(Cursor invisibleCursor) {
        this.invisibleCursor = invisibleCursor;
    }

    public void toggleSlowMotion() {
        newton.togglSlowMotion();
    }

    public void escapeAction() {
        if (trackingMouse) {
            window.setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            trackingMouse = false;
        } else {
            window.setCursor (invisibleCursor);
            trackingMouse = true;
        }
    }
}
