package com.belen.laboratorio05;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

import java.io.IOException;

/**
 * Clase Renderiza (OpenGL 1.x)
 *
 * @author Jhonny Felipez
 * @version 1.0 02/04/2016
 *
 */
public class Renderiza extends GLSurfaceView implements Renderer {

	Objeto obj;
	Objeto obj2;
	Objeto obj3;
	Objeto obj4;
	Objeto obj5;
	Objeto flor;
	Objeto cubo2;

	/* Objetos */
	private Cubo cubo;
	private Piso piso;
	private Piramide piramide;
	private Piramide2 piramide2;

	private Terreno terreno;


	/* Texturas */
	Textura texturaBotonArr;
	Textura texturaBotonAba;

	/* Inicializa ubicaci�n de la vista del observador */
	private final float[] vectorEntrada = { 0, 0, -1, 1 };
	private static float posicion[] = { 0, 1, 3 };
	private final float[] direccion = new float[4];

	final float[] matriz = new float[16];

	/* Para la rotaci�n y traslaci�n */
	private float rotX, rotY;
	private	float antX, antY;

	/* Tama�o de la ventana en pixeles */
	private int alto;
	private int ancho;

	/*rotacion*/

	private  float tx;

	private  float inc=1f;

	/* Contexto */
	Context contexto;


	MD2 md2 = new MD2();
	boolean estaBienElModelo;
	MD2 md2c = new MD2();
	boolean estaBienElModeloc;
	MD2 md2c1 = new MD2();
	boolean estaBienElModeloc1;

	float angulo = 30;
	String nombreCuadro = "stand";
	String nombreCuadro2 = "run";
	String nombreCuadro21 = "salute";



	public Renderiza(Context contexto) {
		super(contexto);
		this.contexto = contexto;
		this.setRenderer(this);
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {

		obj = new Objeto(contexto, "arbol.obj");
		obj2 = new Objeto(contexto, "casita.obj");
		obj3 = new Objeto(contexto, "farmhouse_obj.obj");
		obj4 = new Objeto(contexto, "porsche.obj");
		obj5 = new Objeto(contexto, "f-16.obj");
		flor = new Objeto(contexto, "rose+vase.obj");
		cubo2 = new Objeto(contexto, "cubo2.obj");
		cubo = new Cubo();
		piso = new Piso();
		piramide = new Piramide();
		piramide2 = new Piramide2();


		/* Lee las texturas */
		texturaBotonArr = new Textura(gl, contexto, "arriba.png");
		texturaBotonArr.setVertices(-0.5f, -4f, 1, 1);

		texturaBotonAba = new Textura(gl, contexto, "abajo.png");
		texturaBotonAba.setVertices(-0.5f, -5.5f, 1, 1);

		antX = antY = -1;

		terreno = new Terreno(gl, contexto, "heightmap2_65.raw", "texture.png");


		try {
			estaBienElModelo = md2.leeArchivoMD2(contexto, gl, "Ogros.md2", "igdosh.png");
		} catch (IOException e) {
			System.err.println("Error parsing :");
			e.printStackTrace();
		}

		try {
			estaBienElModeloc = md2c.leeArchivoMD2(contexto, gl, "Squelette.md2", "Squelette.jpg");
		} catch (IOException e) {
			System.err.println("Error parsing :");
			e.printStackTrace();
		}

		try {
			estaBienElModeloc1 = md2c1.leeArchivoMD2(contexto, gl, "goblin.md2", "igdosh.png");
		} catch (IOException e) {
			System.err.println("Error parsing :");
			e.printStackTrace();
		}




		float [] luz_ambiente = { 0, 0, 0, 1}; // I
		float [] luz_difusa = { 1, 1, 1, 1 };
		float [] luz_especular = { 1, 1, 1, 1 };
		float [] luz_posicion = { 0, 0, 5, 0}; // L

		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, luz_ambiente, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, luz_difusa, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, luz_especular, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, luz_posicion, 0);

		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_NORMALIZE);
		/* Habilita la textura */
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glClearColor(153/255f, 217/255f, 234/256f, 0);




	}

	@Override
	public void onDrawFrame(GL10 gl) {

		/* Borra el buffer de la ventana y del z-buffer */
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_DEPTH_TEST);

		/* Matriz de Proyecci�n */
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 67, ancho / (float)alto, 1f, 50f);
		//		gl.glFrustumf(-4, 4, -6, 6, 1, 50);
		//		gl.glOrthof(-4, 4, -6, 6, 1, 50);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glRotatef(-rotX, 1, 0, 0);
		gl.glRotatef(-rotY, 0, 1, 0);
		gl.glTranslatef(-posicion[0], -posicion[1], -posicion[2]);

		gl.glTranslatef(0, 0, -5);
//		 Piso
		gl.glPushMatrix();
		gl.glTranslatef(-terreno.getAncho()/2, 0, -terreno.getAncho()/2);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, terreno.getCodigoTextura());
		terreno.dibuja(gl);
		gl.glPopMatrix();


		if (estaBienElModelo)
			md2.animacion(nombreCuadro);
		if (estaBienElModeloc)
			md2c.animacion(nombreCuadro2);
		if (estaBienElModeloc1)
			md2c1.animacion(nombreCuadro21);


		//casa
		gl.glPushMatrix();
		gl.glTranslatef(5.5f, 3.5f, -7f);
		gl.glRotatef(-15, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(30, 1f, 0f, 0f);
		gl.glScalef(6f, 7f, 7f);
		obj2.dibuja(gl);
		gl.glPopMatrix();


//arboles
		gl.glPushMatrix();
		gl.glTranslatef(-15f, 13f, -7f);
		gl.glScalef(9f, 9f, 9f);
		obj.dibuja(gl);
		gl.glPopMatrix();

		//gl.glPushMatrix();
		if (estaBienElModelo) {
			gl.glPushMatrix();
			gl.glTranslatef(2f, 2f, -4f);
			gl.glRotatef(-angulo, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
			gl.glScalef(0.1f, 0.1f, 0.1f);
			md2.dibuja(gl);
			gl.glPopMatrix();
		}

		if (estaBienElModeloc) {
			gl.glPushMatrix();
			gl.glTranslatef(5.5f, 3.5f, -5f);
			gl.glRotatef(-angulo, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
			gl.glScalef(0.1f, 0.1f, 0.1f);
			md2c.dibuja(gl);
			gl.glPopMatrix();
		}

		if (estaBienElModeloc1) {
			gl.glPushMatrix();
			gl.glTranslatef(5.5f, 3.5f, -3f);
			gl.glRotatef(-angulo, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
			gl.glScalef(0.1f, 0.1f, 0.1f);
			md2c1.dibuja(gl);
			gl.glPopMatrix();
		}
		//gl.glPopMatrix();
		gl.glPopMatrix();
		gl.glPopMatrix();

		angulo = angulo + 0.7f;
		if (angulo > 360) {
			angulo = angulo - 360;
		}

		// Avanza la animacion
		if (estaBienElModelo)
			md2.avanza(0.025f);

		if (estaBienElModeloc)
			md2c.avanza(0.025f);

		if (estaBienElModeloc1)
			md2c1.avanza(0.025f);


		//Bosque
//		gl.glPushMatrix();
//		gl.glTranslatef(-3,1,0);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-3,1,-2);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-3,1,2);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-3,1f,-4);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-3,1f,-6);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-3,1f,-8);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-3,1f,-10);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//
//		gl.glPushMatrix();
//		gl.glTranslatef(-6,1,1);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-6,1,-1);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-6,1,3);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-6,1f,-3);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-6,1f,-5);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-6,1f,-7);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-6,1f,-9);
//		gl.glScalef(2,2,2);
//		obj2.dibuja(gl);
//		gl.glPopMatrix();


		//flores

		gl.glPushMatrix();
		gl.glTranslatef(6,-0.7f,-5);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(6.5f,-0.7f,-5f);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(7,-0.7f,-5);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(7.5f,-0.7f,-5f);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(6,-0.7f,-4);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(6.5f,-0.7f,-4f);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(7f,-0.7f,-4);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(7.5f,-0.7f,-4f);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(6,-0.7f,-6);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(6.5f,-0.7f,-6f);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(7f,-0.7f,-6);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(7.5f,-0.7f,-6f);
		gl.glScalef(0.3f,0.3f,0.3f);
		flor.dibuja(gl);
		gl.glPopMatrix();


        //casas
//		gl.glPushMatrix();
//		gl.glTranslatef(8f,2,-11);
//		gl.glScalef(3f,3f,3f);
//		obj3.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(8f,2,0);
//		gl.glScalef(3f,3f,3f);
//		obj3.dibuja(gl);
//		gl.glPopMatrix();


		//autos
//		gl.glPushMatrix();
//		gl.glTranslatef(0.7f,1,-9);
//		gl.glScalef(2,2,2);
//		obj4.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(3,1,0);
//		gl.glScalef(2,2,2);
//		obj4.dibuja(gl);
//		gl.glPopMatrix();

		//avion
		gl.glPushMatrix();
		gl.glTranslatef(0,4,0);
		obj5.dibuja(gl);
		gl.glPopMatrix();


		//cubos
//		gl.glPushMatrix();
//		gl.glTranslatef(-8f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-7f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-6f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-5f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-4f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(-3f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef( 5f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(6f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(7f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(8f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//		gl.glPushMatrix();
//		gl.glTranslatef(9f,-0.7f,-15);
//		gl.glScalef(0.3f,0.3f,0.3f);
//		cubo2.dibuja(gl);
//		gl.glPopMatrix();
//
//
//		gl.glPopMatrix();








		/* Botones de las opciones */
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-4, 4, -6, 6, 1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);


		texturaBotonArr.muestra(gl);

		texturaBotonAba.muestra(gl);

		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glFlush();

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		ancho = w;
		alto = h;
		gl.glViewport(0, 0, w, h);
		GLU.gluLookAt(gl, 0, 0, 0, 0, 0, -1, 0, 1, 0);
	}

	/**
	 * Maneja los eventos del movimiento en la pantalla t�ctil.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {

		/* Obtiene la coordenada de la pantalla */
		float x = e.getX();
		float y = e.getY();

		/* Se considera cuando se levanta el dedo de la pantalla. */
		if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {

			/* En coordenadas del OpenGL */
			float posx = ((x / (float) ancho) * 8) - 4;
			float posy = ((1 - y / (float) alto) * 12) - 6;

			/* Verifica �rea elegida */
			if (puntoEstaDentroDelRectangulo(posx, posy, -0.5f, -4f, 1, 1)) { // Arr
				Matrix.setIdentityM(matriz, 0);
				Matrix.rotateM(matriz, 0, rotY, 0, 1, 0);
				Matrix.rotateM(matriz, 0, rotX, 1, 0, 0);
				Matrix.multiplyMV(direccion, 0, matriz, 0, vectorEntrada, 0);

				posicion[0] = posicion[0] + direccion[0] * 0.1f;
				posicion[1] = posicion[1] + direccion[1] * 0.1f;
				posicion[2] = posicion[2] + direccion[2] * 0.1f;

			} else if (puntoEstaDentroDelRectangulo(posx, posy, -0.5f, -5.5f, 1, 1)) { // Abj
				Matrix.setIdentityM(matriz, 0);
				Matrix.rotateM(matriz, 0, rotY, 0, 1, 0);
				Matrix.multiplyMV(direccion, 0, matriz, 0, vectorEntrada, 0);

				posicion[0] = posicion[0] - direccion[0] * 0.1f;
				posicion[1] = posicion[1] - direccion[1] * 0.1f;
				posicion[2] = posicion[2] - direccion[2] * 0.1f;
			} else {
				if(antX == -1) {
					antX = x;
					antY = y;
				} else {
					rotX = rotX + (y - antY) / 10;
					rotY = rotY + (x - antX) / 10;
					antX = x;
					antY = y;
				}
			}

			//requestRender();
		} else {
			antX = -1;
			antY = -1;
		}
		return true;
	}

	private boolean puntoEstaDentroDelRectangulo(float posx, float posy,
			float x, float y, float ancho, float alto) {
		return (x < posx && posx < x + ancho && y < posy && posy < y + alto);
	}

}
