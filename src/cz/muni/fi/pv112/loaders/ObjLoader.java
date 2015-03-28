package cz.muni.fi.pv112.loaders;

import com.jogamp.common.nio.Buffers;

import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjLoader {

    private String path;

    private List<float[]> vertices;
    private List<float[]> normals;
    private List<int[]> vertexIndices;
    private List<int[]> normalIndices;

    private FloatBuffer verticesBuffer;
    private FloatBuffer normalsBuffer;
    private IntBuffer vertexIndicesBuffer;
    private IntBuffer normalsIndicesBuffer;

    private BufferedReader inReader;

    public ObjLoader(String path) {
        this.path = path;
    }

    public void load() {
        /** Mesh containing the loaded object */
        vertices = new ArrayList<float[]>();
        normals = new ArrayList<float[]>();
        vertexIndices = new ArrayList<int[]>();
        normalIndices = new ArrayList<int[]>();

        String line;
        try {
            inReader = new BufferedReader(new InputStreamReader(
                    this.getClass().getResource(path).openStream()));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return;
        }
        try {
            while ((line = inReader.readLine()) != null) {

                if (line.startsWith("v ")) {

                    String[] vertStr = line.split("\\s+");
                    float[] vertex = new float[3];

                    vertex[0] = Float.parseFloat(vertStr[1]);
                    vertex[1] = Float.parseFloat(vertStr[2]);
                    vertex[2] = Float.parseFloat(vertStr[3]);
                    vertices.add(vertex);

                } else if (line.startsWith("vn ")) {

                    String[] normStr = line.split("\\s+");
                    float[] normal = new float[3];

                    normal[0] = Float.parseFloat(normStr[1]);
                    normal[1] = Float.parseFloat(normStr[2]);
                    normal[2] = Float.parseFloat(normStr[3]);
                    normals.add(normal);

                } else if (line.startsWith("vt ")) {

                    // TODO: texturove suradnice

                } else if (line.startsWith("f ")) {

                    String[] faceStr = line.split("\\s+");
                    int[] faceVert = new int[3];

                    faceVert[0] = Integer.parseInt(faceStr[1].split("/")[0]) - 1;
                    faceVert[1] = Integer.parseInt(faceStr[2].split("/")[0]) - 1;
                    faceVert[2] = Integer.parseInt(faceStr[3].split("/")[0]) - 1;
                    vertexIndices.add(faceVert);

                    // TODO: indexy texturovych suradnic (2. hodnota z trojice cisel)
                    if (faceStr[1].split("/").length >= 3) {
                        int[] faceNorm = new int[3];

                        faceNorm[0] = Integer.parseInt(faceStr[1].split("/")[2]) - 1;
                        faceNorm[1] = Integer.parseInt(faceStr[2].split("/")[2]) - 1;
                        faceNorm[2] = Integer.parseInt(faceStr[3].split("/")[2]) - 1;
                        normalIndices.add(faceNorm);
                    }
                }
            }


        verticesBuffer = Buffers.newDirectFloatBuffer(vertices.size() * 3);
            for (float[] vertex : vertices) {
                verticesBuffer.put(vertex[0]);
                verticesBuffer.put(vertex[1]);
                verticesBuffer.put(vertex[2]);
            }
        verticesBuffer.rewind();

        normalsBuffer = Buffers.newDirectFloatBuffer(normals.size() * 3);
        for (float[] normal : normals) {
                normalsBuffer.put(normal[0]);
                normalsBuffer.put(normal[1]);
                normalsBuffer.put(normal[2]);
            }
        normalsBuffer.rewind();

        vertexIndicesBuffer = Buffers.newDirectIntBuffer(vertexIndices.size() * 3);
        for (int[] vertexIndex : vertexIndices) {
                vertexIndicesBuffer.put(vertexIndex[0]);
                vertexIndicesBuffer.put(vertexIndex[1]);
                vertexIndicesBuffer.put(vertexIndex[2]);
            }
        vertexIndicesBuffer.rewind();

        normalsIndicesBuffer = Buffers.newDirectIntBuffer(normalIndices.size() * 3);
        for (int[] normalIndex : normalIndices) {
                normalsIndicesBuffer.put(normalIndex[0]);
                normalsIndicesBuffer.put(normalIndex[1]);
                normalsIndicesBuffer.put(normalIndex[2]);
            }
        normalsIndicesBuffer.rewind();

        } catch (IOException ex) {
            System.out.println("Unable to load " + path + " file: " + ex.getMessage());
        }
    }

    public List<float[]> getVertices() {
        return vertices;
    }

    public List<float[]> getNormals() {
        return normals;
    }

    public List<int[]> getVertexIndices() {
        return vertexIndices;
    }

    public List<int[]> getNormalIndices() {
        return normalIndices;
    }

    public void render(GL2 gl) {

        gl.glBegin(GL_TRIANGLES);
        for (int j = 0; j < vertexIndices.size(); j++) {
            int[] vertex = vertexIndices.get(j);
            int[] normal = normalIndices.get(j);

            for (int i = 0; i < 3; i++) {
                gl.glNormal3fv(normals.get(normal[i]), 0);
                gl.glVertex3fv(vertices.get(vertex[i]), 0);
            }
        }
        gl.glEnd();
    }

    public FloatBuffer getVerticesBuffer() {
        return verticesBuffer;
    }

    public FloatBuffer getNormalsBuffer() {
        return normalsBuffer;
    }

    public IntBuffer getVertexIndicesBuffer() {
        return vertexIndicesBuffer;
    }

    public IntBuffer getNormalIndicesBuffer() {
        return normalsIndicesBuffer;
    }
}
