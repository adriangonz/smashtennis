package com.kaseyo23.base;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.kaseyo23.manager.ResourcesManager;
import com.kaseyo23.manager.SceneManager.SceneType;

import android.app.Activity;

/**
 * @class BaseScene
 * Clase base para las escenas. Hace de interfaz, con los
 * metodos y atributos comunes, para el resto de escenas. 
 */
public abstract class BaseScene extends Scene {
	/**
	 * Puntero al motor del juego
	 */
	protected Engine engine;
	
	/**
	 * Puntero a la activity
	 */
	protected Activity activity;
	
	/**
	 * Puntero al gestor de recursos
	 */
	protected ResourcesManager resourcesManager;
	
	/**
	 * Puntero al VertexBuffer
	 */
	protected VertexBufferObjectManager vbom;
	
	/**
	 * Puntero a la camara (que es limitada, con bordes)
	 */
	protected BoundCamera camera;
	
	/**
	 * Constructor de la escena base. Donde nos guardamos todos
	 * los punteros (cogiendolos del gestor de recursos). Y 
	 * llamamos al "callback" de createScene.
	 */
	public BaseScene() 
	{
		this.resourcesManager = ResourcesManager.getInstance();
		this.activity = resourcesManager.activity;
		this.engine = resourcesManager.engine;
		this.camera = resourcesManager.camera;
		this.vbom = resourcesManager.vbom;
		createScene();
	}
	
	/**
	 * Metodo abstracto que actua de callback cuando la escena
	 * se acaba de crear.
	 */
	public abstract void createScene();
	
	/**
	 * Metodo abstracto al que llama la activity cuando pulsamos
	 * la tecla "retroceder"
	 */
	public abstract void onBackKeyPressed();
	
	/**
	 * Metodo abstracto que devuelve el tipo de escena actual.
	 * @return Tipo de escena (de los guardados en el gestor de escenas)
	 */
	public abstract SceneType getSceneType();
	
	/**
	 * Metodo abstracto que libera los recursos de la escena
	 */
	public abstract void disposeScene();
	
}
