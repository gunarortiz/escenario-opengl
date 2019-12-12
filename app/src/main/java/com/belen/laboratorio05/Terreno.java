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
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Clase Terreno (OpenGL 1.x)
 * 
 * @author Jhonny Felipez
 * @version 1.0 02/04/2016
 * 
 */
public class Terreno {
	
	/* C�digo o handle de la textura */
	int codigoTextura[] = new int[1];
	
	/* Ancho y alto del archivo RAW */
	private int anchoRAW;
	private int altoRAW;
	
	private FloatBuffer bufVertices;
	private FloatBuffer bufNormales;
	private FloatBuffer bufTextura;
	private ByteBuffer bufByte;

	public Terreno(GL10 gl, Context contexto, String nombreDeArchivoRAW, String nombreDeArchivoDeTextura) {
		
		/* Lee el archivo de la Textura */
		
		leeTextura(gl, contexto, nombreDeArchivoDeTextura);
		
		/* Lee el archivo .RAW */

		ArrayList<Integer> datoByte = new ArrayList<Integer>();
		
		int tam = leeArchivoRAW(contexto, nombreDeArchivoRAW, datoByte);
			
		anchoRAW = (int) Math.sqrt(tam);
		altoRAW = (int) Math.sqrt(tam);
		
		/* Lee las altitudes */

		float altitud[][] = new float[altoRAW][anchoRAW];
		Vector3 normales2[][] = new Vector3[altoRAW][anchoRAW];
		Vector3 normales1[][] = new Vector3[altoRAW][anchoRAW];

		for (int z = 0; z < altoRAW; z++) {
			for (int x = 0; x < anchoRAW; x++) {
				int color = datoByte.get(z * altoRAW + x);
				float a = 20 * ((color / 255.0f) - 0.5f);
				altitud[z][x] = a;
			}
		}
		
		/* Obtiene la normal de cada v�rtice */
			
		Vector3 suma; 					// vector suma
		Vector3 norte = new Vector3(); 	// vector norte
		Vector3 sur = new Vector3(); 	// vector sur
		Vector3 este = new Vector3(); 	// vector este
		Vector3 oeste = new Vector3(); 	// vector oeste
		
		/**
		 *   Direcci�n de los vectores
		 *
		 *          ^ N    
		 *          |      
		 *     <----+---->
		 *     O    |    E
		 *          v S
		 *             
		 */
		
		for (int z = 0; z < altoRAW; z++) {
			for (int x = 0; x < anchoRAW; x++) {
				suma = new Vector3();
				// Obtiene el vector norte
				if (0 < z) {
					norte = new Vector3(0.0f,
							altitud[z - 1][x] - altitud[z][x], -1.0f);
				}
				// Obtiene el vector sur
				if (z < altoRAW - 1) {
					sur = new Vector3(0.0f, altitud[z + 1][x] - altitud[z][x],
							1.0f);
				}
				// Obtiene el vector este
				if (x < anchoRAW - 1) {
					este = new Vector3(1.0f, altitud[z][x + 1] - altitud[z][x],
							0.0f);
				}
				// Obtiene el vector oeste
				if (0 < x) {
					oeste = new Vector3(-1.0f, altitud[z][x - 1]
							- altitud[z][x], 0.0f);
				}
				// Suma la normal entre el vector norte y oeste
				if (0 < x && 0 < z) {
					suma = suma
							.mas(norte.producto_vectorial(oeste).normaliza());
				}
				// Suma la normal entre el vector oeste y sur
				if (0 < x && z < altoRAW - 1) {
					suma = suma.mas(oeste.producto_vectorial(sur).normaliza());
				}
				// Suma la normal entre el vector sur y este
				if (x < anchoRAW - 1 && z < altoRAW - 1) {
					suma = suma.mas(sur.producto_vectorial(este).normaliza());
				}
				// Suma la normal entre el vector este y norte
				if (x < anchoRAW - 1 && 0 < z) {
					suma = suma.mas(este.producto_vectorial(norte).normaliza());
				}
				// Finalmente se normaliza
				normales2[z][x] = suma.normaliza();
			}
		}
		
		/*
		 * Suaviza las normales.
		 * Sumando las normales de los cuatro v�rtices adyacentes.
		 */
			
		/**
		 *     N = Normal de f
		 *     
		 *           c
		 *           | 
		 *           | 
		 *     a-----f-----b
		 *           |
		 *           |
		 *           d
		 *           
		 *     N = a + b + c + d
		 *     del v�rtice f  
		 */

		for(int z = 0; z < altoRAW; z++) {
			for(int x = 0; x < anchoRAW; x++) {
				
				suma = normales2[z][x];
				
				// Suma la normal del vertice a
				if (0 < x) {
					suma = suma.mas(normales2[z][x - 1]);
				}
				
				// Suma la normal del vertice b
				if (x < anchoRAW - 1) {
					suma = suma.mas(normales2[z][x + 1]);
				}
				
				// Suma la normal del vertice c
				if (0 < z) {
					suma = suma.mas(normales2[z - 1][x]);
				}
				
				// Suma la normal del vertice d
				if (z < altoRAW - 1) {
					suma = suma.mas(normales2[z + 1][x]);
				}
				
				// Finalmente se vuelve a normalizar
				// para encontrar la normal del v�rtice f
				if (suma.longitud() == 0)
					normales1[z][x] = new Vector3(0.0f, 1.0f, 0.0f);
				else {
					normales1[z][x] = suma.normaliza(); 
				}
			}
		}
		
		/* Lee los v�rtices y las normales para renderizar */

		/**
		 *    V0    V2
		 *     |    /|       
		 *     |  /  |  ...
		 *     |/    |
		 *    V1    V3
		 */
		
		bufByte = ByteBuffer.allocateDirect(tam * 6 * 4);
		bufByte.order(ByteOrder.nativeOrder());
		bufVertices = bufByte.asFloatBuffer();
		
		bufByte = ByteBuffer.allocateDirect(tam * 6 * 4);
		bufByte.order(ByteOrder.nativeOrder());
		bufNormales = bufByte.asFloatBuffer();
		
		bufByte = ByteBuffer.allocateDirect(tam * 4 * 4);
		bufByte.order(ByteOrder.nativeOrder());
		bufTextura = bufByte.asFloatBuffer();
		
		for (int z = 0; z < altoRAW - 1; z++) {
			for (int x = 0; x < anchoRAW; x++) {
				bufNormales.put(normales1[z][x].x);
				bufNormales.put(normales1[z][x].y);
				bufNormales.put(normales1[z][x].z);
				bufVertices.put((float) x);
				bufVertices.put(altitud[z][x]);
				bufVertices.put((float) z);
				bufTextura.put((float) x / (anchoRAW - 1));
				bufTextura.put((float) z / (altoRAW - 1));

				bufNormales.put(normales1[z + 1][x].x);
				bufNormales.put(normales1[z + 1][x].y);
				bufNormales.put(normales1[z + 1][x].z);
				bufVertices.put((float) x);
				bufVertices.put(altitud[z + 1][x]);
				bufVertices.put((float) (z + 1));
				bufTextura.put((float) x / (anchoRAW - 1));
				bufTextura.put((float) (z+1) / (altoRAW - 1));
			}
		}
		bufVertices.rewind();
		bufNormales.rewind();
		bufTextura.rewind();
	}
	
	/* Retorna el ancho del archivo */
	public int getAncho() {
		return anchoRAW;
	}

	/* Retorna el alto del archivo */
	public int getAlto() {
		return altoRAW;
	}
	
	public void dibuja(GL10 gl) {

		/* Se habilita el acceso al arreglo de v�rtices */
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		/* Se habilita el acceso al arreglo de las normales */
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		
		/* Se habilita el acceso al arreglo de las coordenadas de textura */
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		/* Se especifica los datos del arreglo de v�rtices */
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufVertices);
		
		/* Se especifica los datos del arreglo de las normales */
		gl.glNormalPointer(GL10.GL_FLOAT, 0, bufNormales);
		
		/* Se especifica los datos del arreglo de las coordenadas de textura */
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, bufTextura);

		/* Renderiza por cada fila en 'x' */
		int x = 0;
		for (int z = 0; z < altoRAW - 1; z++) {
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, x, 2 * anchoRAW);
			x = x + 2 * anchoRAW;
		}

		/* Se deshabilita el acceso a los arreglos */
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	public int leeArchivoRAW(Context contexto, String nombreDeArchivo, ArrayList<Integer> datoByte) {
	
		int tam = 0;
		try {
			byte[] buffer = new byte[1000];

			// Abre el archivo
			InputStream in = contexto.getAssets().open(nombreDeArchivo);

			// Lee los datos
			int numeroDeBytes = 0;
			while ((numeroDeBytes = in.read(buffer)) != -1) {
				for (int i = 0; i < numeroDeBytes; i++) {
					datoByte.add((int) (buffer[i] & 0xFF));
					tam++;
				}
			}

			// Cierra el archivo
			in.close();

			in = null;

		} catch (IOException e) {
			Log.d("El archivo RAW", "No se puede cargar " + nombreDeArchivo);
			throw new RuntimeException("No se puede cargar " + nombreDeArchivo);
		}
		return tam;
	}
	
	/* Retorna el handle del archivo de textura */
	public int getCodigoTextura() {
		return codigoTextura[0];
	}

	
	/**
	 * Lee la textura
	 * 
	 * @param gl - El contexto GL
	 * @param contexto - El contexto de la Actividad
	 */
	public void leeTextura(GL10 gl, Context contexto, String nombreDeArchivo) {

		try {
			/* Obtiene la textura del directorio de assets Android */
			InputStream is = contexto.getAssets().open(nombreDeArchivo);

			/* Decodifica un flujo de entrada en un mapa de bits. */
			Bitmap textura = BitmapFactory.decodeStream(is);

			/* Genera un nombre (c�digo) para la textura */
			gl.glGenTextures(1, codigoTextura, 0);

			/* Se asigna un nombre (c�digo) a la textura */
			gl.glBindTexture(GL10.GL_TEXTURE_2D, codigoTextura[0]);

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

			/* Para limitar la textura tanto de s y t dentro del rango del 0 al 1 */
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
