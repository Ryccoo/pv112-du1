package cz.muni.fi.pv112.renderers;

import com.jogamp.opengl.util.gl2.GLUT;
import cz.muni.fi.pv112.Colors;
import cz.muni.fi.pv112.loaders.ObjLoaderVertexWrapper;

import javax.media.opengl.GL2;

import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

/**
 * Created by richard on 5. 5. 2015.
 */
public class RoofLight {


    private GL2 gl;
    private GLUT glut;
    private int LIGHT = 0;
    boolean on = true;

    private float timeModifier = 3;
    private float degree = 0;
    private boolean addDirection = true;
    private float maxDegree = 65;
    private ObjLoaderVertexWrapper bulb = new ObjLoaderVertexWrapper("/resources/bulb.obj");

    public RoofLight(GL2 gl, GLUT glut, int light) {
        this.gl = gl;
        this.glut = glut;
        this.LIGHT = light;
        bulb.load();
    }

    public void render() {
        computeDegree();

        if(on) {
            gl.glEnable(LIGHT);
        } else {
            gl.glDisable(LIGHT);
        }

        renderBall(degree);
    }

    private void renderBall(float degree) {
        gl.glPushMatrix();
//        gl.glTranslatef(0,10,0);
        gl.glRotatef(degree,0.5f,0,1);
//        gl.glTranslatef(0,-10,0);

        gl.glPushMatrix();
        gl.glRotatef(90, 1, 0, 0);
        glut.glutSolidCylinder(0.25f, 100, 20, 20);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslatef(0, -100, 0);
        gl.glScalef(0.3f, 0.3f, 0.3f);
        if(on) {
            gl.glMaterialfv(GL_FRONT, GL_EMISSION, Colors.bulb, 0);
        } else {
            gl.glMaterialfv(GL_FRONT, GL_EMISSION, Colors.black, 0);
        }
        bulb.render(gl);
        gl.glMaterialfv(GL_FRONT, GL_EMISSION, Colors.black, 0);
        gl.glLightfv(LIGHT, GL_POSITION, new float[]{0,0,0,1}, 0);
        gl.glLightfv(LIGHT, GL_SPOT_DIRECTION, new float[]{0, -1f, 0}, 0);
        gl.glPopMatrix();

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
        timeModifier = (timeModifier == 3.0f) ? 0.3f : 3.0f;
    }

    public void toggleLight() {
        on = !on;
    }

    public boolean isOn() {
        return on;
    }

}
