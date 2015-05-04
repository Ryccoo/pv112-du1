package cz.muni.fi.pv112;

import static javax.media.opengl.GL2.*;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private boolean trackingMouse = false;

    private ObjLoaderVertexWrapper vase = new ObjLoaderVertexWrapper("/resources/vase.obj");
    private ObjLoaderVertexWrapper table = new ObjLoaderVertexWrapper("/resources/table.obj");
    private ObjLoaderVertexWrapper m4a1 = new ObjLoaderVertexWrapper("/resources/m4a1.obj");
    private ObjLoaderVertexWrapper chair = new ObjLoaderVertexWrapper("/resources/chair2.obj");
    private ObjLoaderVertexWrapper rose = new ObjLoaderVertexWrapper("/resources/rose.obj");
    private NewtonBalls newton;

    private ObjLoaderVertexWrapper[] sceneObjects = {vase, table, m4a1, chair, rose};

//    controlls
    private KeyboardController keyboard;
    private MouseController mouse;

    Shader vs;
    Shader fs;
    Texture floorTex;
    Texture tableTex;
    Texture chairTex;
    Texture vaseTex;

    private int programId;

    public Scene()
    {
        time = 0;

        glu = new GLU();
        glut = new GLUT();

        vs = new Shader("shaders/VertexShader.vs");
        fs = new Shader("shaders/FragmentShader.fs");

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

//        load objects
        for(ObjLoaderVertexWrapper item : sceneObjects) {
            item.load();
        }

        trackingMouse = true;
        escapeAction();


        GL2 gl = drawable.getGL().getGL2();

//        init custom objects
        newton = new NewtonBalls(gl, glut);

        // TODO LIGHTS
        gl.glClearDepth(1.0f);
        gl.glClearColor(0f,.5f,1f,1);

        gl.glEnable(GL_DEPTH_TEST);

        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_NORMALIZE);

        gl.glDisable(GL_LIGHT0);

        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, Colors.white, 0);
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, Colors.white, 0);


//        gl.glEnable(GL_LIGHT1);
//        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, Colors.white, 0);
//        gl.glLightfv(GL_LIGHT1, GL_SPECULAR, Colors.white, 0);

//        gl.glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 30);
//        gl.glLightf(GL_LIGHT1, GL_SPOT_EXPONENT, 30);

        gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[]{4, 4, 4, 0}, 0);

        //   Material
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, Colors.white, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, Colors.green, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 100);


        // Exercise 5 Shaders //
        // Load Shaders
        int vsId = vs.initShader(gl, GL_VERTEX_SHADER);
        int fsId = fs.initShader(gl, GL_FRAGMENT_SHADER);

        // Create program
        programId = gl.glCreateProgram();

        // Attach Shaders
        gl.glAttachShader(programId, vsId);
        gl.glAttachShader(programId, fsId);

        gl.glLinkProgram(programId);

        checkProgramStatus(gl, programId);

        try {
            floorTex = loadTexture(gl, "/resources/textures/floor.jpg", TextureIO.JPG);
            tableTex = loadTexture(gl, "/resources/textures/table.jpg", TextureIO.JPG);
            chairTex = loadTexture(gl, "/resources/textures/chair.jpg", TextureIO.JPG);
            vaseTex = loadTexture(gl, "/resources/textures/vase.jpg", TextureIO.JPG);
        } catch (IOException ex) {
            Logger.getLogger(Scene.class.getName()).log(Level.SEVERE, null, ex);
        }
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


        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

//        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{1,1,1}, 0);

//        glu.gluLookAt(10, 10, 10, 0, 0, 0, 0, 1, 0);
        setCamera();

        //  Draw obj
        gl.glPushMatrix(); // matrix scale to 0.05
        gl.glScalef(0.05f, 0.05f, 0.05f);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, Colors.white, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 100);
//        render table
        tableTex.enable(gl);
        tableTex.bind(gl);
        table.render(gl);
        tableTex.disable(gl);

//        render vase
        gl.glPushMatrix();
        gl.glTranslatef(21, 70, 20);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, Colors.white, 0);
//        gl.glUseProgram(programId);
        vaseTex.enable(gl);
        vaseTex.bind(gl);
        vase.render(gl);
        vaseTex.disable(gl);
//        gl.glUseProgram(0);
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

        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, Colors.white, 0);

        chairTex.enable(gl);
        chairTex.bind(gl);
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, -50);
        gl.glScalef(65,65f,65f);
        chair.render(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 50);
        gl.glRotatef(180,0,1,0);
        gl.glScalef(65,65f,65f);
        chair.render(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(60, 0, 0);
        gl.glRotatef(-90,0,1,0);
        gl.glScalef(65,65f,65f);
        chair.render(gl);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(-60, 0, 0);
        gl.glRotatef(90,0,1,0);
        gl.glScalef(65,65f,65f);
        chair.render(gl);
        gl.glPopMatrix();

        chairTex.disable(gl);

        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, new float[]{0.8f,0.001f, 0.0f}, 0);
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, new float[]{0.0f,0.0f, 0.0f}, 0);
        gl.glPushMatrix();
        gl.glTranslatef(17, 100, 24);
        gl.glRotatef(20, 1, 0, 1);
        gl.glScalef(0.1f,0.1f,0.1f);
        rose.render(gl);
        gl.glPopMatrix();

        gl.glPopMatrix(); // matrix scale to 0.5


        // Draw Floor
        gl.glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, Colors.white, 0);
        gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 100);

        floorTex.enable(gl);
        floorTex.bind(gl);
//
//
        float floor_step = 5;
        for(float posx = -25; posx < 25; posx += floor_step) {
            for(float posy = -25; posy < 25; posy += floor_step) {
                drawFloor(gl, posx, posx + floor_step, posy, posy + floor_step, 0.25f, 0.25f);
            }
        }

        floorTex.disable(gl);


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
            for (float x = x_from; x <= x_to; x += x_step) {
                // podlaha pouziva 2D zatial co modely pouzivaju 3D
                gl.glTexCoord2f((x - x_from) / (x_to - x_from), 1 - (z - z_from) / (z_to - z_from));
                gl.glVertex3f(x, 0, z);
                gl.glTexCoord2f((x - x_from) / (x_to - x_from), 1 - (z + z_step - z_from) / (z_to - z_from));
                gl.glVertex3f(x, 0, z + z_step);
            }
            gl.glEnd();
        }
    }

    private void setCamera() {

        if(trackingMouse) {
            lookAt = keyboard.getMovement(lookAt);
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

    private Texture loadTexture(GL2 gl, String filename, String suffix) throws IOException {
        try (InputStream is = Scene.class.getResourceAsStream(filename)) {
            Texture tex =  TextureIO.newTexture(is, true, suffix);
            // Set texture filters
            tex.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            tex.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            // Set texture coordinates wrap mode
            tex.setTexParameteri(gl, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
            tex.setTexParameteri(gl, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
            // Unbind texture
            gl.glBindTexture(GL_TEXTURE_2D, 0);
            return tex;
        }
    }

    private void checkProgramStatus(GL2 gl, int programId) {

        int[] linkStatus = new int[1];
        gl.glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);

        if (GL_FALSE == linkStatus[0]) {
            int[] infoLogLength = new int[1];
            gl.glGetProgramiv(programId, GL_INFO_LOG_LENGTH, infoLogLength, 0);

            byte[] infoLogBytes = new byte[infoLogLength[0]];
            gl.glGetProgramInfoLog(programId, infoLogLength[0], infoLogLength, 0, infoLogBytes, 0);


            String error = new String(infoLogBytes, 0, infoLogLength[0]);

            System.err.println(error);
            System.exit(3);
        }
    }
}
