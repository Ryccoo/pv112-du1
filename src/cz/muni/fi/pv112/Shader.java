package cz.muni.fi.pv112;

import javax.media.opengl.GL2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.media.opengl.GL.GL_FALSE;
import static javax.media.opengl.GL2ES2.GL_COMPILE_STATUS;
import static javax.media.opengl.GL2ES2.GL_INFO_LOG_LENGTH;

/**
 *
 * @author Milos Seleceni
 */
public class Shader
{   
    private String path;
    private String source;
    private int shaderId;
    
    public Shader(String path)
    {
        this.path = path;
        
        try {        
            source = readShaderFromFile(path);
        } catch (IOException ex) {
            Logger.getLogger(Shader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Nacita shader zo suboru a vrati ho ako jeden retazec
     * 
     * @param path - cesta k shader-u
     * @return shader nacitany v jednom string-u
     * @throws java.io.IOException
     */
    private String readShaderFromFile(String path) throws IOException {
        
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Shader.class.getResourceAsStream(path)
                )
        );
        
        StringBuilder source = new StringBuilder();

        String ls = System.getProperty("line.separator");
        String line;

        while ((line = reader.readLine()) != null) {
            source.append(line);
            source.append(ls);
        }

        return source.toString();
    }

    /**
     * Nainicializuje shader
     * 
     * @param gl
     * @param shaderType - typ shader-u, (vertex, tesselation, geometry, fragment, compute) 
     * @return 
     */
    public int initShader(GL2 gl, int shaderType) {

        // Vytvori Id pre shader
        int shaderId = gl.glCreateShader(shaderType);

        this.shaderId = shaderId;
        
        // priradi k ID shader-u jeho zdrojovy kod
        gl.glShaderSource(shaderId, 1, new String[]{source}, new int[]{source.length()}, 0);

        // zkompiluje shader
        gl.glCompileShader(shaderId);
        
        // kontrola compilacie shader-u
        checkShaderStatus(gl, 2);
        
        return shaderId;
    }
    
    /**
     * Zisti pripadne chyby pri kompilacii shader-u a vypise ich na konzolu
     * @param gl
     * @param errorCode 
     */
    private void checkShaderStatus(GL2 gl, int errorCode) {

        int[] compileStatus = new int[1];
        gl.glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);

        if (GL_FALSE == compileStatus[0]) {
            int[] infoLogLength = new int[1];
            gl.glGetShaderiv(shaderId, GL_INFO_LOG_LENGTH, infoLogLength, 0);

            byte[] infoLogBytes = new byte[infoLogLength[0]];
            gl.glGetShaderInfoLog(shaderId, infoLogLength[0], infoLogLength, 0, infoLogBytes, 0);

            String error = new String(infoLogBytes, 0, infoLogLength[0]);

            System.err.println(error);
            System.exit(errorCode);
        }
    }
}
