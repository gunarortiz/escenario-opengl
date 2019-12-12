package com.belen.laboratorio05;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Clase Textura (OpenGL 1.x)
 * 
 * @author Jhonny Felipez
 * @version 4.0 25/09/2017
 * 
 */
public class Textura2 {
	
	/* Contexto */
	private Context contexto;
	
	/* Nombre del archivo de textura */
	private String nombreDeArchivo;
	
	/* Ancho y alto del archivo de textura */
	private int ancho;
	private int alto;
	
	/* C�digo o handle de la textura */
	int codigo[] = new int[1];
	
	/* Coordenadas cartesianas (x, y) */
	private float vertices[] = new float [8];
	
	/* Coordenadas de textura (u, v) */	
	private float textura[] = new float [8];
	
	/* Indices */
	private short indices[] = new short [] { 
		 0,  1,  2,  0,  2,  3, 
 	};
	
	private FloatBuffer bufVertices;
	private FloatBuffer bufTextura;
	private ShortBuffer bufIndices;
	private ByteBuffer bufByte;
	
	public Textura2(GL10 gl, Context contexto, String nombreDeArchivo) {
		
		this.contexto = contexto;
		this.nombreDeArchivo = nombreDeArchivo;
		leeTextura(gl);
		
		/* Por omisi�n */
		setCoord_Textura(0, 0, 1, 1);
		
		/* Lee los indices */
		bufByte = ByteBuffer.allocateDirect(indices.length * 2);
		bufByte.order(ByteOrder.nativeOrder()); // Utiliza el orden de byte nativo
		bufIndices = bufByte.asShortBuffer(); // Convierte de byte a short
		bufIndices.put(indices).rewind(); // puntero al principio del buffer
		
	}
	
	/* Retorna el ancho del archivo de textura */
	public int getAncho() {
		return ancho;
	}

	/* Retorna el alto del archivo de textura */
	public int getAlto() {
		return alto;
	}

	/* Retorna el handle del archivo de textura */
	public int getCodigoTextura() {
		return codigo[0];
	}
	
	/**
	 *       V�rtices              Textura
	 *    3 ---------- 2        0 ---------- 1
	 *     |        / |          |        / | 
	 *     |      /   |   /--    |      /   | 
	 *     |    /     |   \--    |    /     |
	 *     | /        |          | /        |
	 *    0 ---------- 1        3 ---------- 2  
	 */
	
	/* Lee las coordenadas cartesianas (x,y) */
	public void setVertices(float x1, float y1, float ancho, float alto) {
		
		float x2 = x1 + ancho;
		float y2 = y1 + alto;
		vertices[0] = x1; vertices[1] = y1;
		vertices[2] = x2; vertices[3] = y1;
		vertices[4] = x2; vertices[5] = y2;
		vertices[6] = x1; vertices[7] = y2;
        
		/* Lee los v�rtices */
		bufByte = ByteBuffer.allocateDirect(vertices.length * 4);
		bufByte.order(ByteOrder.nativeOrder()); // Utiliza el orden de byte nativo
		bufVertices = bufByte.asFloatBuffer(); // Convierte de byte a float
		bufVertices.put(vertices).rewind(); // puntero al principio del buffer	
		
	}
	
	/* Lee las coordenadas de textura (u,v) */
	public void setCoord_Textura(float u1, float v1, float u2, float v2) {
        
		textura[0] = u1; textura[1] = v2;
		textura[2] = u2; textura[3] = v2;
		textura[4] = u2; textura[5] = v1;
		textura[6] = u1; textura[7] = v1;
        
		/* Lee las coordenadas de textura */
		bufByte = ByteBuffer.allocateDirect(textura.length * 4);
		bufByte.order(ByteOrder.nativeOrder()); // Utiliza el orden de byte nativo
		bufTextura = bufByte.asFloatBuffer(); // Convierte de byte a float
		bufTextura.put(textura).position(0); // puntero al principio del buffer
		
	}	
	
	public void muestra(GL10 gl) {
		
		/* Se selecciona una textura */
		gl.glBindTexture(GL10.GL_TEXTURE_2D, codigo[0]);
		
		/* Se activa el arreglo de v�rtices */
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		/* Se activa el arreglo de colores */
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		/* Se especifica los datos del arreglo de v�rtices */
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufVertices);

		/* Se especifica los datos del arreglo de las coordenadas de textura */
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, bufTextura);

		/* Renderiza las primitivas desde los datos de los arreglos (v�rtices,
		 * coord. de textura e indices) */
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
				GL10.GL_UNSIGNED_SHORT, bufIndices);

		/* Se desactiva el arreglo de v�rtices */
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		/* Se desactiva el arreglo de colores */
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
	}
	
	/**
	 * Lee la textura
	 * 
	 * @param gl - El contexto GL
	 * @param contexto - El contexto de la Actividad
	 */
	public void leeTextura(GL10 gl) {

		try {
			/* Obtiene la textura del directorio de assets Android */
			InputStream is = contexto.getAssets().open(nombreDeArchivo);

			/* Decodifica un flujo de entrada en un mapa de bits. */
			Bitmap textura = BitmapFactory.decodeStream(is);
			
			ancho = textura.getWidth();
			alto = textura.getHeight();

			/* Genera un nombre (c�digo) para la textura */
			gl.glGenTextures(1, codigo, 0);

			/* Se asigna un nombre (c�digo) a la textura */
			gl.glBindTexture(GL10.GL_TEXTURE_2D, codigo[0]);

			/* Para que el patr�n de textura se agrande y se acomode a una �rea
			 * grande */
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_NEAREST);

			/* Para que el patr�n de textura se reduzca y se acomode a una �rea
			 * peque�a */
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_NEAREST);

			/* Para repetir la textura tanto en s y t fuera del rango del 0 al 1
			 * POR DEFECTO! */
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_REPEAT);

			/* Para no repetir la textura tanto de s y t dentro del rango del 0 al 1 */
//			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
//					GL10.GL_CLAMP_TO_EDGE);
//			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
//					GL10.GL_CLAMP_TO_EDGE);

			/* Determina la formato y el tipo de la textura. Carga la textura en
			 * el buffer de textura */
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textura, 0);

			/* Asignaci�n de textura a cero */
			gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

			/* Recicla la textura, porque los datos ya fueron cargados al OpenGL */
			textura.recycle();

			/* Cierra el archivo */
			is.close();

			is = null;

		} catch (IOException e) {
			Log.d("La textura", "No puede cargar " + nombreDeArchivo);
			throw new RuntimeException("No puede cargar " + nombreDeArchivo);
		}

	}
}
