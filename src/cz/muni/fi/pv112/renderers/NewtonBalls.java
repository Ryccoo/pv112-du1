package cz.muni.fi.pv112.renderers;

import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;

/**
 * Created by richard on 28. 3. 2015.
 */
public class NewtonBalls {

    private GL2 gl;
    private GLUT glut;

    private float timeModifier = 5;
    private float degree = 0;
    private boolean addDirection = true;
    private float maxDegree = 50;

    public NewtonBalls(GL2 gl, GLUT glut) {
        this.gl = gl;
        this.glut = glut;
    }


    public void render() {
        computeDegree();

        gl.glPushMatrix(); // render newton

        renderBase();

        gl.glPushMatrix(); // ball
        gl.glTranslatef(0, 5, 0);
        renderBall(0);
        gl.glPopMatrix();

        gl.glPushMatrix(); // ball
        gl.glTranslatef(3, 5, 0);
        renderBall(0);
        gl.glPopMatrix();

        gl.glPushMatrix(); // ball
        gl.glTranslatef(-3, 5, 0);
        renderBall(0);
        gl.glPopMatrix();

        gl.glPushMatrix(); // ball
        gl.glTranslatef(6, 5, 0);
        if(degree < 0) {
            renderBall(0);
        } else {
            renderBall(degree);
        }
        gl.glPopMatrix();

        gl.glPushMatrix(); // ball
        gl.glTranslatef(-6, 5, 0);
        if(degree > 0) {
            renderBall(0);
        } else {
            renderBall(degree);
        }
        gl.glPopMatrix();

        gl.glPopMatrix(); // end newton matrix
    }

    private void renderBase() {
        gl.glPushMatrix(); // base
        gl.glScalef(2.5f,0.1f,1); // dimensions of base 30 * 1 * 10
        glut.glutSolidCube(10);
        gl.glPopMatrix(); // end base matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(-12f, 0, 4.5f);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCylinder(0.5f,15,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(12f, 0, 4.5f);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCylinder(0.5f,15,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(-12f, 0, -4.5f);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCylinder(0.5f,15,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(12f, 0, -4.5f);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCylinder(0.5f,15,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(12.5f, 15, 4.5f);
        gl.glRotatef(-90, 0, 1, 0);
        glut.glutSolidCylinder(0.5f,25,5,5);
        gl.glPopMatrix(); // end frame matrix

        gl.glPushMatrix(); //frame
        gl.glTranslatef(12.5f, 15, -4.5f);
        gl.glRotatef(-90, 0, 1, 0);
        glut.glutSolidCylinder(0.5f,25,5,5);
        gl.glPopMatrix(); // end frame matrix
    }

    private void renderBall(float degree) {
        gl.glTranslatef(0,10,0);
        gl.glRotatef(degree,0,0,1);
        gl.glTranslatef(0,-10,0);

        glut.glutSolidSphere(1.5f, 25, 25);
        gl.glPushMatrix(); // rope one
        gl.glRotatef(-65, 1, 0, 0);
        glut.glutSolidCylinder(0.075f, 11, 7, 7);
        gl.glPopMatrix(); // end rope

        gl.glPushMatrix(); //rope two
        gl.glRotatef(-115, 1, 0, 0);
        glut.glutSolidCylinder(0.075f, 11, 7, 7);
        gl.glPopMatrix(); // end rope
    }

    private void computeDegree() {
//        set direction
        if (degree > maxDegree) {
            addDirection = false;
        }
        if (degree < -maxDegree) {
            addDirection = true;
        }

//        compute angle
        float modifier = 1 - (Math.max(20, (Math.abs(degree) - 2)) / maxDegree); // simulate gravity (logarithmic) move
        if (addDirection) {
            degree += timeModifier * modifier;
        } else {
            degree -= timeModifier * modifier;
        }
    }

    public void togglSlowMotion() {
        timeModifier = (timeModifier == 5.0f) ? 0.5f : 5.0f;
    }

}
