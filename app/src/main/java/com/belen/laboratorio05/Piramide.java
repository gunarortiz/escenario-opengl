package com.belen.laboratorio05;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;
public class Piramide {

	private float vertices[] = new float[] {

			// Frente
			-1, -1,  1, // 4   0
			1, -1,  1, // 5   1
			0,  1,  0, // 6   2
			0,  1,  0, // 7   3

			// Atr�s
			0,  1, 0, // 3   4
			0,  1, 0, // 2   5
			1, -1, -1, // 1   6
			-1, -1, -1, // 0   7
			// Izquierda
			-1, -1, -1, // 0   8
			-1, -1,  1, // 4   9
			0,  1,  0, // 7  10 
			0,  1, 0, // 3  11
			// Derecha
			1, -1, 1,// 5  12
			1, -1, -1, // 1  13
			0,  1, 0, // 2  14
			0,  1,  0, // 6  15
			// Abajo
			-1, -1, -1, // 0  16
			1, -1, -1, // 1  17
			1, -1,  1, // 5  18
			-1, -1,  1, // 4  19
		/*// Arriba
			-1,  1,  1, // 7  20
			1,  1,  1, // 6  21
			0,  1, 0, // 2  22
			0,  1, 0  // 3  23*/
	};
	byte maxColor= (byte)255;
	private byte colores[] = new byte[] {
			// Frente-lila
			maxColor, 0, maxColor, maxColor, // 4   0
			maxColor, 0, maxColor, maxColor, // 5   1
			maxColor, 0, maxColor, maxColor, // 6   2
			maxColor, 0, maxColor, maxColor, // 7   3
			// Atr�s-amarillo
			maxColor, maxColor, 0, maxColor, // 3   4
			maxColor, maxColor, 0, maxColor, // 2   5
			maxColor, maxColor, 0, maxColor, // 1   6
			maxColor, maxColor, 0, maxColor, // 0   7*/
			// Izquierda-celeste
			
			0, maxColor,	maxColor, maxColor, // 0	8
			0, maxColor,	maxColor, maxColor, // 4	9
			0, maxColor,	maxColor, maxColor, // 7	10
			0, maxColor,	maxColor, maxColor, // 3	11 
			//Derecha-rojo
			maxColor, 0, 0, maxColor, // 5  12
			maxColor, 0, 0, maxColor, // 1  13
			maxColor, 0, 0, maxColor, // 2  14
			maxColor, 0, 0, maxColor, // 6  15
			// Abajo-azul
			0, 0, maxColor, maxColor, // 0  16
			0, 0, maxColor, maxColor, // 1  17
			0, 0, maxColor, maxColor, // 5  18
			0, 0, maxColor, maxColor, // 4  19
			// Arriba-verde
			/*0, maxColor, 0, maxColor, // 7  20
			0, maxColor, 0, maxColor, // 6  21
			0, maxColor, 0, maxColor, // 2  22
			0, maxColor, 0, maxColor// 3  23*/
	}; 
	private short indices[] = new short[] {
			0,  1,  2,  0,  2,  3, // Frente
			4,  5,  6,  4,  6,  7, // Atr�s
			8,  9, 10,  8, 10, 11, // Izquierda
			12, 13, 14, 12, 14, 15, // Derecha
			16, 17, 18, 16, 18, 19, // Abajo
			//20, 21, 22, 20, 22, 23  // Arriba

	};
	private FloatBuffer bufVertices;
	private ByteBuffer bufColores;
	private ShortBuffer bufIndices;
	public Piramide() {
		/* Lee losv�rtices*/
		ByteBuffer bufByte = ByteBuffer.allocateDirect(vertices.length* 4);
		bufByte.order(ByteOrder.nativeOrder()); // Utilizael ordendebyte nativo
		bufVertices= bufByte.asFloatBuffer(); // Conviertedebyte a float
		bufVertices.put(vertices);
		bufVertices.rewind(); // punteroalprincipiodelbuffer
		/* Lee loscolores*/
		bufColores= ByteBuffer.allocateDirect(colores.length);
		bufColores.put(colores);
		bufColores.position(0); // punteroalprincipiodelbuffer
		/* Lee losindices */
		bufByte = ByteBuffer.allocateDirect(indices.length* 2);
		bufByte.order(ByteOrder.nativeOrder()); // Utilizael ordendebyte nativo
		bufIndices= bufByte.asShortBuffer(); // Conviertedebyte a short
		bufIndices.put(indices);
		bufIndices.rewind(); // punteroalprincipiodelbuffer
	}
	public void dibuja(GL10 gl) {
		/* Seactivael arreglodev�rtices*/
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		/* Seactivael arreglodecolores*/
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		/* Seespecificalosdatosdelarreglodev�rtices*/
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufVertices);
		/* Seespecificalosdatosdelarreglodecolores*/
		gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, bufColores);
		/* Sedibujael cubo*/
		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,GL10.GL_UNSIGNED_SHORT, bufIndices);
		//gl.glDrawElements(GL10.GL_LINE_STRIP, indices.length,GL10.GL_UNSIGNED_SHORT, bufIndices);//para solo lineas
		/* Sedesactivael arreglodev�rtices*/
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		/* Sedesactivael arreglodecolores*/
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}

}
