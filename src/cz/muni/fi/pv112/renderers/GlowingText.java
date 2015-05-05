package cz.muni.fi.pv112.renderers;

import com.jogamp.opengl.util.gl2.GLUT;
import cz.muni.fi.pv112.Colors;
import cz.muni.fi.pv112.loaders.ObjLoaderVertexWrapper;

import javax.media.opengl.GL2;

import java.util.Random;

import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

/**
 * Created by richard on 5. 5. 2015.
 */
public class GlowingText {

    private GL2 gl;
    private GLUT glut;
    private int LIGHT = 0;
    boolean on = false;
    long time_barrier = 0;
    long min_on = 200;
    long max_on = 2000;
    long min_off = 10;
    long max_off = 200;

    public GlowingText(GL2 gl, GLUT glut, int light) {
        this.gl = gl;
        this.glut = glut;
        this.LIGHT = light;

        gl.glEnable(LIGHT);
        gl.glLightfv(LIGHT, GL_DIFFUSE, Colors.neon, 0);
        gl.glLightfv(LIGHT, GL_SPECULAR, Colors.neon, 0);
        gl.glLightf(LIGHT, GL_SPOT_CUTOFF, 70);
        gl.glLightf(LIGHT, GL_SPOT_EXPONENT, 3);
    }

    public void render() {
        if(System.currentTimeMillis() >= time_barrier) {
            on = !on;
            if(on) {
                Random rand = new Random();
                long stay_on = ((Math.abs(rand.nextLong()) % (max_on - min_on)) + min_on);
                time_barrier = System.currentTimeMillis() + stay_on;
            } else {
                Random rand = new Random();
                long stay_of = ((Math.abs(rand.nextLong()) % (max_off - min_off)) + min_off);
                time_barrier = System.currentTimeMillis() + stay_of;
            }
        }


        gl.glPushMatrix();
        if(on) {
            gl.glMaterialfv(GL_FRONT, GL_EMISSION, Colors.neon, 0);
            gl.glEnable(LIGHT);
        } else {
            gl.glDisable(LIGHT);
            gl.glMaterialfv(GL_FRONT, GL_EMISSION, Colors.black, 0);
        }

        gl.glPushMatrix();
        gl.glTranslatef(40, -29, 80);
        gl.glLightfv(LIGHT, GL_POSITION, new float[]{0,0,0,1}, 0);
        gl.glLightfv(LIGHT, GL_SPOT_DIRECTION, new float[]{0, 0f, -1f}, 0);
        gl.glPopMatrix();


//        T
        renderT();

//        H
        gl.glPushMatrix();
        gl.glTranslatef(20, -5, 0);
        gl.glScalef(0.8f, 0.8f, 0.8f);
        renderH();
        gl.glPopMatrix();

//        E
        gl.glPushMatrix();
        gl.glTranslatef(35, 0, 0);
//        gl.glScalef(0.8f, 0.8f, 0.8f);
        renderE();
        gl.glPopMatrix();

//        G
        gl.glPushMatrix();
        gl.glTranslatef(-10, -30, 0);
        renderG();
        gl.glPopMatrix();

//        A
        gl.glPushMatrix();
        gl.glTranslatef(10, -30, 0);
        renderA();
        gl.glPopMatrix();

//        M
        gl.glPushMatrix();
        gl.glTranslatef(30, -30, 0);
        renderM();
        gl.glPopMatrix();

//        E
        gl.glPushMatrix();
        gl.glTranslatef(55, -30, 0);
        renderE();
        gl.glPopMatrix();

        gl.glMaterialfv(GL_FRONT, GL_EMISSION, Colors.dark, 0);
        gl.glPopMatrix();
    }

    private void renderT() {
        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        glut.glutSolidCylinder(1f, 25, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(12.5f, 0,0);
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(1f, 25, 20, 20);
        gl.glPopMatrix();
    }

    private void renderH() {
        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslatef(0, -12.5f,0);
        glut.glutSolidCylinder(1f, 12.5f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(12.5f, 0,0);
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(1f, 25, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0, 0,0);
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(1f, 25, 20, 20);
        gl.glPopMatrix();
    }

    private void renderE() {
        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslatef(0, -0.5f, 0);
        glut.glutSolidCylinder(1f, 12.5f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslatef(0, -12.5f,0);
        glut.glutSolidCylinder(1f, 12.5f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslatef(0, -24.5f,0);
        glut.glutSolidCylinder(1f, 12.5f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0, 0,0);
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(1f, 25, 20, 20);
        gl.glPopMatrix();
    }

    private void renderG() {
        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslatef(0, -0.5f, 0);
        glut.glutSolidCylinder(1f, 12.5f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslatef(0, -13f,2.5f);
        glut.glutSolidCylinder(1f, 10f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslatef(0, -24.5f,0);
        glut.glutSolidCylinder(1f, 12.5f, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0, 0,0);
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(1f, 25, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(12f, -12.5f,0);
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(1f, 12.5f, 20, 20);
        gl.glPopMatrix();
    }

    private void renderA() {
        renderH();
        gl.glPushMatrix();
        gl.glRotatef(90, 0, 1, 0);
        gl.glTranslatef(0, -0.5f, 0);
        glut.glutSolidCylinder(1f, 12.5f, 20, 20);
        gl.glPopMatrix();
    }

    private void renderM() {
        gl.glPushMatrix();
        gl.glTranslatef(15f, 0,0);
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(1f, 25, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0, 0,0);
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(1f, 25, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0, -1f,0);
        gl.glRotatef(90, 1, 0, 0);
        gl.glRotatef(45, 0, 1, 0);
        glut.glutSolidCylinder(1f, 11, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(15f, -1f, 0);
        gl.glRotatef(90, 1, 0, 0);
        gl.glRotatef(-45, 0, 1, 0);
        glut.glutSolidCylinder(1f, 11, 20, 20);
        gl.glPopMatrix();
    }


}
