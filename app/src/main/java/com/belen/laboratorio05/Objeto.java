package com.belen.laboratorio05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.util.Log;
/**
 * Clase Objeto (OpenGL)
 *
 * Lee archivo .obj y .mtl
 *
 * @author J Felipez
 * @version 2.0 30/05/2015
 *
 */
public class Objeto {

    class Grupo {
        String nombre;					/* Nombre del grupo */
        ArrayList<Integer> triangulos;	/* Arreglo de �ndice de triangulos */
        int material;					/* Indice del material del grupo */

        public Grupo() {
            nombre = "si_falta";
            triangulos = new ArrayList<Integer>();
            material = 0;
        }

        public Grupo(String nombre) {
            this.nombre = nombre;
            triangulos = new ArrayList<Integer>();
            material = 0;
        }

        public String getNombre() {
            return nombre;
        }

        public void adiTriangulo(int t) {
            triangulos.add(t);
        }

        public int getTriangulo(int indice){
            return triangulos.get(indice);
        }

        public int getNumTriangulos(){
            return triangulos.size();
        }

        public void setMaterial(int material){
            this.material = material;
        }

        public int getMaterial() {
            return material;
        }

        @Override
        public String toString() {
            return nombre +
                    "\n triangulos: " + triangulos +
                    "\n material  : " + material;
        }
    }

    class Material {
        String nombre;					/* Nombre del material */
        float ambiente[];				/* Arreglo del color ambiente */
        float difuso[];					/* Arreglo del color difuso */
        float especular[];				/* Arreglo del color especular */
        float brillo;					/* Exponente del brillo */
        public Material() {
            ambiente = new float[4];
            difuso = new float[4];
            especular = new float[4];
            nombre = "si_falta";
            ambiente[0] = 0.2f;
            ambiente[1] = 0.2f;
            ambiente[2] = 0.2f;
            ambiente[3] = 1.0f;
            difuso[0] = 0.8f;
            difuso[1] = 0.8f;
            difuso[2] = 0.8f;
            difuso[3] = 1.0f;
            especular[0] = 0.0f;
            especular[1] = 0.0f;
            especular[2] = 0.0f;
            especular[3] = 1.0f;
        }
        public Material(String nombre,float ambiente[], float difuso[], float especular[],
                        float brillo) {
            this.nombre = nombre;
            this.ambiente = ambiente;
            this.difuso = difuso;
            this.especular = especular;
            this.brillo = brillo;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
        public void setAmbiente(float[] ambiente) {
            this.ambiente = ambiente;
        }
        public void setDifuso(float[] difuso) {
            this.difuso = difuso;
        }
        public void setEspecular(float[] especular) {
            this.especular = especular;
        }
        public void setBrillo(float brillo) {
            this.brillo = brillo;
        }
        public String getNombre() {
            return nombre;
        }
        public float[] getAmbiente() {
            return ambiente;
        }
        public float[] getDifuso() {
            return difuso;
        }
        public float[] getEspecular() {
            return especular;
        }
        public float getBrillo() {
            return brillo;
        }
        @Override
        public String toString() {
            return nombre +
                    "\n Ka: " + Arrays.toString(ambiente) +
                    "\n Kd: " + Arrays.toString(difuso) +
                    "\n Ks: " + Arrays.toString(especular) +
                    "\n Ns: " + brillo;
        }
    }

    ArrayList<Material> materiales = new ArrayList<Material>();
    ArrayList<Grupo> grupos = new ArrayList<Grupo>();
    ArrayList<Short> aristas = new ArrayList<Short>();

    /* N�mero de V�rtices */
    private int numVertices;

    /* N�mero de Normales */
    private int numNormales;

    /* N�mero de Triangulos */
    private int numTriangulos;

    private FloatBuffer bufVertices;
    private FloatBuffer bufNormales;
    private ShortBuffer bufIndices;

    public Objeto(Context contexto, String nombreArchivo){

        lee_archivo_obj(contexto, nombreArchivo);

    }

    public void dibuja(GL10 gl) {

        Iterator<Grupo> iterador = grupos.iterator();

        while (iterador.hasNext()) {

            /* Lee un grupo  */
            Grupo grupo = iterador.next();

            /* Obtiene el n�mero de tri�ngulos del grupo */
            int numTriangulos = grupo.getNumTriangulos();

            if (numTriangulos == 0)
                continue;

            /* Obtiene el color del material */
            Material material = materiales.get(grupo.getMaterial());

            /* Definici�n del material */
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, material.getAmbiente(), 0);
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, material.getDifuso(), 0);
            gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, material.getEspecular(), 0);
            gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, material.getBrillo());

            /* Lee las caras */
            ByteBuffer bufByte = ByteBuffer.allocateDirect(numTriangulos * 2 * 3 * 2);
            bufByte.order(ByteOrder.nativeOrder()); // Utiliza el orden de byte nativo
            bufIndices = bufByte.asShortBuffer(); // Convierte de byte a short

            /* Lee los indices */
            for (int j = 0; j < numTriangulos; j++) {
                int indice = grupo.getTriangulo(j);
                bufIndices.put(aristas.get(indice * 3 + 0));
                bufIndices.put(aristas.get(indice * 3 + 1));
                bufIndices.put(aristas.get(indice * 3 + 2));
            }

            bufIndices.rewind(); // puntero al principio del buffer

            /* Se habilita el acceso al arreglo de v�rtices */
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            /* Se habilita el acceso al arreglo de las normales */
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

            /* Se especifica los datos del arreglo de v�rtices */
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufVertices);

            /* Se especifica los datos del arreglo de las normales */
            gl.glNormalPointer(GL10.GL_FLOAT, 0, bufNormales);

            /* Renderiza las primitivas desde los datos de los arreglos (vertices,
             * normales e indices) */
            gl.glDrawElements(GL10.GL_TRIANGLES, numTriangulos * 3, GL10.GL_UNSIGNED_SHORT, bufIndices);

            /* Se deshabilita el acceso al arreglo de v�rtices */
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

            /* Se deshabilita el acceso al arreglo de las normales */
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        }
    }

    /**
     * Lee archivo .obj
     */
    public void lee_archivo_obj(Context contexto, String nombreArchivo) {
        int i;

        try {

            /* Obtiene la textura del directorio de assets Android */
            InputStream is = contexto.getAssets().open(nombreArchivo);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));

            /* LEE EL ARCHIVO */

            String linea;

            StringTokenizer st;
            float x, y, z;

            float minX = Float.MAX_VALUE, maxX = Float.MIN_VALUE;
            float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
            float minZ = Float.MAX_VALUE, maxZ = Float.MIN_VALUE;

            ArrayList<Float> vertices = new ArrayList<Float>();
            ArrayList<Vector3> normales = new ArrayList<Vector3>();
            grupos.add(new Grupo());
            int indiceDeGrupo = 0;

            while ((linea = buffer.readLine()) != null){
                linea = linea.trim();
                if (linea.length() > 0) {
                    if (linea.startsWith("mtllib ")) {			/* nombre del arch. de materiales */
                        st = new StringTokenizer(linea.substring(7), " ");
                        lee_archivo_mtl(contexto, st.nextToken());
                    } else if (linea.startsWith("v ")) {		/* v�rtice */
                        numVertices++;
                        st = new StringTokenizer(linea.substring(2), " ");
                        x = Float.parseFloat(st.nextToken());
                        y = Float.parseFloat(st.nextToken());
                        z = Float.parseFloat(st.nextToken());
                        vertices.add(x);
                        vertices.add(y);
                        vertices.add(z);

                        minX = Math.min(minX, x); maxX = Math.max(maxX, x);
                        minY = Math.min(minY, y); maxY = Math.max(maxY, y);
                        minZ = Math.min(minZ, z); maxZ = Math.max(maxZ, z);

                        /* Inicializa la normal de cada v�rtice */
                        numNormales++;
                        normales.add(new Vector3());

                    } else if (linea.startsWith("f ")) {		/* cara */
                        st = new StringTokenizer(linea.substring(2), " ");
                        int numTokens = st.countTokens();	/* Numero de tokens v/vt/vn */

                        st = new StringTokenizer(linea.substring(2));
                        String token1[] = st.nextToken().split("/");
                        aristas.add((short) (Short.parseShort(token1[0])-1));  		// v0

                        String token2[] = st.nextToken().split("/");
                        aristas.add((short) (Short.parseShort(token2[0])-1));  		// v1

                        String token3[] = st.nextToken().split("/");
                        aristas.add((short) (Short.parseShort(token3[0])-1));  		// v2

                        grupos.get(indiceDeGrupo).adiTriangulo(numTriangulos);
                        numTriangulos++;
                        for (i = 3; i < numTokens; i++) {
                            int k = aristas.size();
                            aristas.add(aristas.get(k - 3));						// v0
                            aristas.add(aristas.get(k - 1));						// v2
                            String token4[] = st.nextToken().split("/");
                            aristas.add((short) (Short.parseShort(token4[0])-1)); 	// v3
                            grupos.get(indiceDeGrupo).adiTriangulo(numTriangulos);
                            numTriangulos++;
                        }
                    } else if (linea.startsWith("g")) {			/* nombre de grupo */
                        st = new StringTokenizer(linea, " ");
                        int numTokens = st.countTokens();
                        st.nextToken();
                        if (numTokens > 1) {
                            String nombre = st.nextToken();
                            indiceDeGrupo = buscaGrupo(nombre); 	// �ndice de grupo actual
                            if (indiceDeGrupo == -1) {
                                grupos.add(new Grupo(nombre));
                                indiceDeGrupo = grupos.size() - 1; 	// �ndice de grupo actual
                            }
                        }

                    } else if (linea.startsWith("usemtl ")) {		/* nombre del material */
                        st = new StringTokenizer(linea.substring(7), " ");
                        String nombre = st.nextToken();
                        int indiceDeMaterial = buscaMaterial(nombre);
                        grupos.get(indiceDeGrupo).setMaterial(indiceDeMaterial);
                    } else if (linea.charAt(0) == '#')			/* comentario */
                        continue;
                }
            }

            /* Reescala las coordenadas entre [-1,1] */
            float tam_max = 0, escala;
            tam_max = Math.max(tam_max, maxX-minX);
            tam_max = Math.max(tam_max, maxY-minY);
            tam_max = Math.max(tam_max, maxZ-minZ);
            escala = 2.0f / tam_max;

            /* Actualiza los v�rtices */
            for (i = 0; i < numVertices * 3; i += 3) {
                vertices.set(i  , escala * (vertices.get(i  ) - minX) - 1.0f);
                vertices.set(i+1, escala * (vertices.get(i+1) - minY) - 1.0f);
                vertices.set(i+2, escala * (vertices.get(i+2) - minZ) - 1.0f);
            }

            /* Lee los v�rtices */
            ByteBuffer bufByte = ByteBuffer.allocateDirect(numVertices * 4 * 3);
            bufByte.order(ByteOrder.nativeOrder()); // Utiliza el orden de byte nativo
            bufVertices = bufByte.asFloatBuffer(); // Convierte de byte a float

            for (i = 0; i < numVertices * 3; i+=3) {
                bufVertices.put(vertices.get(i));
                bufVertices.put(vertices.get(i+1));
                bufVertices.put(vertices.get(i+2));
            }

            Vector3 v1 = new Vector3(); // v1
            Vector3 v2 = new Vector3(); // v2
            Vector3 v3 = new Vector3(); // v3
            Vector3 normal = new Vector3();  // normal
            int a, b, c, a1, b1, c1;

            /* Lee las caras y obtiene las normales de los v�rtices */
            for (i = 0; i < numTriangulos * 3; i+=3){
                a = aristas.get(i);
                b = aristas.get(i+1);
                c = aristas.get(i+2);

                a1 = a * 3; // Obtiene la posici�n del primer v�rtice
                v1.x = bufVertices.get(a1 + 0); // v1
                v1.y = bufVertices.get(a1 + 1);
                v1.z = bufVertices.get(a1 + 2);
                b1 = b * 3; // Obtiene la posici�n del segundo v�rtice
                v2.x = bufVertices.get(b1 + 0); // v2
                v2.y = bufVertices.get(b1 + 1);
                v2.z = bufVertices.get(b1 + 2);
                c1 = c * 3; // Obtiene la posici�n del tercer v�rtice
                v3.x = bufVertices.get(c1 + 0); // v3
                v3.y = bufVertices.get(c1 + 1);
                v3.z = bufVertices.get(c1 + 2);

                /* Obtiene la normal de la cara */
                normal = Vector3.normal(v1, v2, v3);

                /* Suma la normal de la cara, a la normal de cada v�rtice */
                normales.set(a, normal.mas(normales.get(a))); // normal 1
                normales.set(b, normal.mas(normales.get(b))); // normal 2
                normales.set(c, normal.mas(normales.get(c))); // normal 3
            }

            /* Lee las normales de los v�rtices */
            bufByte = ByteBuffer.allocateDirect(numNormales * 4 * 3);
            bufByte.order(ByteOrder.nativeOrder()); // Utiliza el orden de byte nativo
            bufNormales = bufByte.asFloatBuffer(); // Convierte de byte a float
            for (i = 0; i < numNormales; i++) {

                /* Normaliza la normal de cada v�rtice */
                normales.get(i).normaliza();

                bufNormales.put((float)normales.get(i).x);
                bufNormales.put((float)normales.get(i).y);
                bufNormales.put((float)normales.get(i).z);
            }

            bufVertices.rewind(); // puntero al principio del buffer
            bufNormales.rewind(); // puntero al principio del buffer

            /* Cierra el archivo */
            buffer.close();
            buffer = null;

        } catch (IOException e) {
            Log.d("Rectangulo", "No puede cargar " + nombreArchivo);
            throw new RuntimeException("No puede cargar " + nombreArchivo);
        }
    }

    /* Lee un archivo .MTL (archivo de los colores de los materiales) */
    public void lee_archivo_mtl(Context contexto, String nombreArchivo) throws IOException {

        try {
            /* Obtiene la textura del directorio de assets Android */
            InputStream is = contexto.getAssets().open(nombreArchivo);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));

            /* LEE EL ARCHIVO */

            String linea;
            StringTokenizer st;
            materiales.clear();
            while ((linea = buffer.readLine()) != null){
                linea = linea.trim();
                if (linea.length() > 0) {
                    if (linea.startsWith("newmtl ")) {				/* nombre del material */
                        st = new StringTokenizer(linea.substring(7), " ");
                        materiales.add(new Material());
                        materiales.get(materiales.size()-1).setNombre(st.nextToken());
                    } else if (linea.startsWith("Ka ")) {			/* ambiente */
                        st = new StringTokenizer(linea.substring(3), " ");
                        float ambiente[] = new float[4];
                        ambiente[0] = Float.parseFloat(st.nextToken());
                        ambiente[1] = Float.parseFloat(st.nextToken());
                        ambiente[2] = Float.parseFloat(st.nextToken());
                        ambiente[3] = 1.0f;
                        materiales.get(materiales.size()-1).setAmbiente(ambiente);
                    } else if (linea.startsWith("Kd ")) {			/* difuso */
                        st = new StringTokenizer(linea.substring(3), " ");
                        float difuso[] = new float[4];
                        difuso[0] = Float.parseFloat(st.nextToken());
                        difuso[1] = Float.parseFloat(st.nextToken());
                        difuso[2] = Float.parseFloat(st.nextToken());
                        difuso[3] = 1.0f;
                        materiales.get(materiales.size()-1).setDifuso(difuso);
                    } else if (linea.startsWith("Ks ")) {			/* especular */
                        st = new StringTokenizer(linea.substring(3), " ");
                        float especular[] = new float[4];
                        especular[0] = Float.parseFloat(st.nextToken());
                        especular[1] = Float.parseFloat(st.nextToken());
                        especular[2] = Float.parseFloat(st.nextToken());
                        especular[3] = 1.0f;
                        materiales.get(materiales.size()-1).setEspecular(especular);
                    } else if (linea.startsWith("Ns ")) {			/* brillo */
                        st = new StringTokenizer(linea.substring(3), " ");
                        float brillo = Float.parseFloat(st.nextToken());
                        /* el brillo ser� de [0..1000] */
                        brillo = (brillo / 1000) * 128;
                        materiales.get(materiales.size()-1).setBrillo(brillo);
                    }
                }
            }

            /* Cierra el archivo */
            buffer.close();

            buffer = null;

        } catch (IOException e) {
            Log.d("Rectangulo", "No puede cargar " + nombreArchivo);
            throw new RuntimeException("No puede cargar " + nombreArchivo);
        }
    }

    /* Busca el grupo */
    public int buscaGrupo(String nombre) {
        for (int i = 0; i < grupos.size(); i++)
            if (nombre.equals(grupos.get(i).getNombre()))
                return i;
        return -1;
    }

    /* Busca el material */
    public int buscaMaterial(String nombre) {
        for (int i = 0; i < materiales.size(); i++)
            if (nombre.equals(materiales.get(i).getNombre()))
                return i;
        return 0;
    }

}

