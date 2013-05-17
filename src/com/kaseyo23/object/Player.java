package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.kaseyo23.manager.ResourcesManager;

/**
 * @class Player
 * Jugador que maneja el usuario en la partida.
 * Hereda de Actor porque es un Sprite animado (con tiles).
 * Tiene los metodos necesarios para mover al jugador
 * cuando el usuario mueve el dispositivo.
 */
public class Player extends Actor {
	//// CONSTRUCTOR
	
	/**
	 * Constructor del player que construye al Actor
	 */
	public Player(float pX, float pY, VertexBufferObjectManager vbo,
					Camera camera) {
		super(pX, pY, vbo, camera, (ITiledTextureRegion) ResourcesManager.getInstance().player_region);
	}
	
	//// INTERFAZ
	/**
	 * Metodo encargado de actualizar la posicion del player
	 * cuando el usuario mueve el dispositivo.
	 * @param desp Desplazamiento en X detectado por el sensor.
	 * @param accel Factor de aceleracion que empleamos para ajustar el movimiento.
	 */
	public void updatePosition(float desp, float accel) {
		float x_actual = getX();
		float x_nueva = x_actual - desp * accel;
		
		if(x_nueva <= 0)
			x_nueva = 0;
		else if(x_nueva >= camera.getWidth())
			x_nueva = camera.getWidth();
		
		setX(x_nueva);
		updateSprite(x_nueva - x_actual);
	}
	
	/**
	 * Metodo que devuelve el tipo de jugador actual
	 */
	@Override
	public int getTipo() {
		return Actor.PLAYER;
	}
}
