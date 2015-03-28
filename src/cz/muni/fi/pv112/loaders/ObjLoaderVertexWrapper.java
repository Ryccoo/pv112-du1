package cz.muni.fi.pv112.loaders;

import com.jogamp.common.nio.Buffers;

import javax.media.opengl.GL2;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static javax.media.opengl.GL2.*;

/**
 * Created by richard on 27. 3. 2015.
 */
public class ObjLoaderVertexWrapper {

    private ObjLoader model;

    private List<float[]> vertices;
    private List<float[]> normals;
    private List<int[]> vertexIndices;

    private FloatBuffer verticesBuffer;
    private FloatBuffer normalsBuffer;
    private IntBuffer vertexIndicesBuffer;

    public ObjLoaderVertexWrapper(String path) {
        this.model = new ObjLoader(path);

        vertices = new ArrayList<float[]>();
        normals = new ArrayList<float[]>();
        vertexIndices = new ArrayList<int[]>();
    }

    public void load() {
        model.load();

        List<float[]> objVertices = model.getVertices();
        List<float[]> objNormals = model.getNormals();
        List<int[]> objVertexIndices = model.getVertexIndices();
        List<int[]> objNormalIndices = model.getNormalIndices();

        int common_index = 0;

        for (int j = 0; j < objVertexIndices.size(); j++) {
            int[] vertexI = objVertexIndices.get(j);
            int[] normalI = objNormalIndices.get(j);

            int[] newVertexI = {0,0,0};

            for (int i = 0; i < 3; i++) {
                normals.add(objNormals.get(normalI[i]));
                vertices.add(objVertices.get(vertexI[i]));
                newVertexI[i] = common_index;
                common_index++;
            }

            vertexIndices.add(newVertexI);
//            for (int i = 0; i < 3; i++) {
//                gl.glNormal3fv(normals.get(normal[i]), 0);
//                gl.glVertex3fv(vertices.get(vertex[i]), 0);
//            }
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
    }

    public void render(GL2 gl) {
        // Vertex arrays
        /*
        *   Funguje spravne len ak mate rovnaky index pre normaly, vertexy, a textury
        *   Model Terrain.obj ich ma tak nastavene... ostatne .obj v studijnych mat. NIE
        */

//        Povolit vertex a normal arrays
        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_NORMAL_ARRAY);

        // Specifikovat kde su jednotlive vetexy a normaly ulozene
        // pozor pri pouzivani vertex arrays musia byt body ulozene vo FloatBuffer a indexy v IntBuffer
        // nemylit si getVertexIndicesBuffer() a getIndices()
        // rozdiel je len v tom v akom type sa vam data vratia
        // viz ObjLoader.class
        gl.glVertexPointer(3, GL_FLOAT, 0, verticesBuffer);
        gl.glNormalPointer(GL_FLOAT, 0, normalsBuffer);

//        gl.glScalef(0.1f,0.1f,0.1f);

        // Vykreslenie celeho modelu jednym prikazom
        gl.glDrawElements(GL_TRIANGLES, vertexIndicesBuffer.capacity(), GL_UNSIGNED_INT, vertexIndicesBuffer);

        // Zakazat vertex a normal arrays
        gl.glDisableClientState(GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL_NORMAL_ARRAY);

    }
}
