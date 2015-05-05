package cz.muni.fi.pv112.controlls;

import cz.muni.fi.pv112.Scene;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by richard on 28. 3. 2015.
 */
public class KeyboardController implements KeyListener {
    private boolean[] keysPressed = {false, false, false, false, false, false}; // W S A D
    private Scene scene;
    private final float moveStep = 0.1f;

    public void setScene(Scene scene) {
        this.scene = scene;
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
        if (e.getKeyCode() == KeyEvent.VK_F) {
            scene.toggleSlowMotion();
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            scene.toggleLights();
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
            scene.escapeAction();
        }
        if (e.getKeyCode() == KeyEvent.VK_Q) {
            keysPressed[4] = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_E) {
            keysPressed[5] = false;
        }

//        development environment
        if (e.getKeyCode() == KeyEvent.VK_J) {
            scene.preview_x -= scene.step;
            printDevelopmentValues();
        }
        if (e.getKeyCode() == KeyEvent.VK_L) {
            scene.preview_x += scene.step;
            printDevelopmentValues();
        }
        if (e.getKeyCode() == KeyEvent.VK_U) {
            scene.preview_y -= scene.step;
            printDevelopmentValues();
        }
        if (e.getKeyCode() == KeyEvent.VK_O) {
            scene.preview_y += scene.step;
            printDevelopmentValues();
        }
        if (e.getKeyCode() == KeyEvent.VK_I) {
            scene.preview_z -= scene.step;
            printDevelopmentValues();
        }
        if (e.getKeyCode() == KeyEvent.VK_K) {
            scene.preview_z += scene.step;
            printDevelopmentValues();
        }
        if (e.getKeyCode() == KeyEvent.VK_M) {
            scene.step -= scene.step_change;
            printDevelopmentValues();
        }
        if (e.getKeyCode() == KeyEvent.VK_N) {
            scene.step += scene.step_change;
            printDevelopmentValues();
        }
    }

    private void printDevelopmentValues() {
        System.out.println("X: " + scene.preview_x +
                "\nY: " + scene.preview_y +
                "\nZ: " + scene.preview_z +
                "\nStep: " + scene.step +
                "\n");
    }

    public double[] getMovement(double[] lookAt) {
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

        return lookAt;
    }
}
