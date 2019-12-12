package com.belen.laboratorio05;

import javax.microedition.khronos.opengles.GL10;
/**
 * Clase Proyeccion (OpenGL 1.x)
 * 
 * @author Jhonny Felipez
 * @version 1.0 02/04/2014
 *
 */
public class Proyeccion {
	
	private float m[] = new float[16];
	
	/* Proyecci�n Paralela */
	public void ortho(GL10 gl, float izq, float der, float abj, float arr, float cerca, float lejos){
	    m[0] = 2/(der - izq); m[4] =             0; m[8] =                   0; m[12] =         -(der + izq)/(der - izq);
	    m[1] =             0; m[5] = 2/(arr - abj); m[9] =                   0; m[13] =         -(arr + abj)/(arr - abj);
	    m[2] =             0; m[6] =             0; m[10] = -2/(lejos - cerca); m[14] = -(lejos + cerca)/(lejos - cerca);
	    m[3] =             0; m[7] =             0; m[11] =                  0; m[15] =                                1;
	    gl.glMultMatrixf(m, 0);
	}

	/* Proyecci�n Perspectiva */
	public void frustum(GL10 gl, float izq, float der, float abj, float arr, float cerca, float lejos){
	    m[0] = 2*cerca/(der-izq); m[4] =                 0; m[8 ] =          (der+izq)/(der-izq); m[12] =                            0;
	    m[1] =                 0; m[5] = 2*cerca/(arr-abj); m[9 ] =          (arr+abj)/(arr-abj); m[13] =                            0;
	    m[2] =                 0; m[6] =                 0; m[10] = -(lejos+cerca)/(lejos-cerca); m[14] = -2*lejos*cerca/(lejos-cerca);
	    m[3] =                 0; m[7] =                 0; m[11] =                           -1; m[15] =                            0;
	    gl.glMultMatrixf(m, 0);
	}

	/* Proyecci�n Perspectiva */
	public void perspective(GL10 gl, float fovy, float aspecto, float cerca, float lejos){
		float ang = (float)Math.toRadians(fovy * 0.5);
		float f = (float) (Math.abs(Math.sin(ang)) < 1e-8 ? 0 : 1 / Math.tan(ang));
	    m[0] = f/aspecto; m[4] = 0; m[8]  =                                  0; m[12] =                                       0;
	    m[1] =         0; m[5] = f; m[9]  =                                  0; m[13] =                                       0;
	    m[2] =         0; m[6] = 0; m[10] = -(lejos + cerca) / (lejos - cerca); m[14] = -2.0f * lejos * cerca / (lejos - cerca);
	    m[3] =         0; m[7] = 0; m[11] =                              -1.0f; m[15] =                                       0;
	    gl.glMultMatrixf(m, 0);
	}
}
