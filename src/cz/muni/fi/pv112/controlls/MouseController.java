package cz.muni.fi.pv112.controlls;

import cz.muni.fi.pv112.MainWindow;
import cz.muni.fi.pv112.Scene;

import javax.media.opengl.GLAutoDrawable;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by richard on 28. 3. 2015.
 */
public class MouseController implements MouseMotionListener {
    private Scene scene;
    private Robot robot;

    public MouseController() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private double[] lookDirection = {0,0};
    private double[] mouseCenters = {300,300};
    private final float mouseStep = 0.15f;

    public void setWindow(MainWindow window) {
        this.window = window;
    }

    private MainWindow window;

    public double[] getMouseCenters() {
        return mouseCenters;
    }

    public void setMouseCenters(double[] mouseCenters) {
        this.mouseCenters = mouseCenters;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
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

    public void trackMouse(GLAutoDrawable drawable) {
        int width = drawable.getSurfaceWidth();
        int height = drawable.getSurfaceHeight();
//            we have to account the fact that window can move on screen
        double screen_x = window.getCanvas().getLocationOnScreen().getX();
        double screen_y = window.getCanvas().getLocationOnScreen().getY();

        mouseCenters[0] = screen_x + (width / 2);
        mouseCenters[1] = screen_y + (height / 2);

        robot.mouseMove((int)mouseCenters[0],(int)mouseCenters[1]);
    }

    public double[] setViewDirection(double[] lookAt) {
        double y_direction = lookDirection[1] * -1;
        double x_look = (double) Math.cos(Math.toRadians(lookDirection[0]));
        double y_look = (double) Math.sin(Math.toRadians(y_direction));
        double z_look = (double) Math.sin(Math.toRadians(lookDirection[0]));

        lookAt[3] = lookAt[0] + x_look;
        lookAt[4] = lookAt[1] + y_look;
        lookAt[5] = lookAt[2] + z_look;

        return lookAt;
    }
}
