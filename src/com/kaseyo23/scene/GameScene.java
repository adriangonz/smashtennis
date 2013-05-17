package com.kaseyo23.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.kaseyo23.base.BaseScene;
import com.kaseyo23.manager.SceneManager;
import com.kaseyo23.manager.SceneManager.SceneType;
import com.kaseyo23.object.Ball;
import com.kaseyo23.object.Machine;
import com.kaseyo23.object.Marcador;
import com.kaseyo23.object.Player;

/**
 * @class GameScene
 * 
 * Escena principal del juego. Hereda de BaseScene e implementa las
 * interfaces necesarias para poder atender los eventos de toque/tap
 * y de movimiento del sensor
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener, SensorEventListener {
	
	/**
	 * Constante que define la distancia entre los jugadores
	 */
	public static final float DIST_PLAYERS = 550.f;
	
	/**
	 * Atributo estatico que indica la posicion en Y del jugador
	 * (calculada a partir del tamanyo de la pantalla y la distancia entre
	 * los jugadores)
	 */
	public static float PLAYER_Y_INIT;
	
	//// ATRIBUTOS
	
	/**
	 * Puntero al HUD del juego (donde esta el marcador)
	 */
	private HUD gameHUD;
	
	/**
	 * Puntero al mundo fisico del juego
	 */
	private PhysicsWorld physicsWorld;
	
	/**
	 * Punteros a los objetos del juego
	 * (jugador, maquian, bola y marcador)
	 */
	private Player player;
	private Machine machine;
	private Ball ball;
	private Marcador marcador;
	
	/**
	 * Valor del desplazamiento en X del sensor y factor
	 * de aceleracion sobre este desplazamiento.
	 */
	private float accelSpeedX, factorAccel = 1.5f;
	
	/**
	 * Puntero al "gestor" interno de sensores de Android
	 */
	private SensorManager sensorManager;
	
	//// METODOS INTERNOS DEL JUEGO
	
	/**
	 * Handler de la tecla "atras"
	 */
	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}
	
	/**
	 * Getter del tipo de escena
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}
	
	//// METODOS CREADORES
	/**
	 * Metod creador de la escena
	 */
	@Override
	public void createScene() {
		//Iniciamos algunos parametros
		initParams();
		//Creamos el mundo fisico
		createPhysics();
		//Creamos el fondo
		createBackground();
		//Creamos el marcador
		createMarcador();
		//Creamos la bola
		createBall();
		//Creamos al jugador
		createPlayer();
		//Creamos a la maquina
		createMachine();
		//Setteamos como listener del evento "touch" a la propia escena
		setOnSceneTouchListener(this);
		//Obtenemos el gestor de sensores y setteamos como listener del acelerometro a la escena
		sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
		//Creamos algunos handlers encargados de actualizar logica
		createHandlers();
	}
	
	/**
	 * Calcula la posicion en Y del jugador
	 */
	private void initParams() {
		PLAYER_Y_INIT = (camera.getHeight() - DIST_PLAYERS) / 2.f;
	}
	
	/**
	 * Crea el fondo de la escena
	 */
	private void createBackground() {
		attachChild(new Sprite(camera.getWidth()/2.f, camera.getHeight()/2.f, resourcesManager.play_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}
	
	/**
	 * Crea el jugador
	 */
	private void createPlayer() {
		player = new Player(camera.getWidth()/2f, PLAYER_Y_INIT, vbom, camera);
		attachChild(player);
	}
	
	/**
	 * Crea la maquina
	 */
	private void createMachine() {
		machine = new Machine(camera.getWidth()/2f, PLAYER_Y_INIT + DIST_PLAYERS, vbom, camera, ball, physicsWorld);
		attachChild(machine);
	}
	
	/**
	 * Crea el marcador y lo anyade al HUD del juego
	 */
	private void createMarcador() {
		marcador = new Marcador(camera, vbom, resourcesManager.font);
		
		gameHUD = new HUD();
		gameHUD.attachChild(marcador);
		camera.setHUD(gameHUD);
	}
	
	/**
	 * Crea la bola
	 */
	private void createBall() {
		ball = new Ball(camera.getWidth()/2f, PLAYER_Y_INIT + 50.f, vbom, physicsWorld, camera, marcador);
		attachChild(ball);
	}
	
	/**
	 * Crea el mundo fisico (sin gravedad)
	 */
	private void createPhysics() {	
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false); 
		//physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	/**
	 * Creo el metodo encargado de actualizar la posicion
	 * del jugador en funcion de las variaciones del sensor
	 */
	private void createHandlers() {
		engine.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void onUpdate(float pSecondsElapsed) {				
				player.updatePosition(accelSpeedX, factorAccel);
			}

			@Override
			public void reset() {}
			
		});
	}
	
	///// METODOS DESTRUCTORES
	/**
	 * Metodo que elimina la escena y libera los recursos
	 */
	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(camera.getWidth()/2f, camera.getHeight()/2f);
	}
	
	///// METODOS ACTUALIZADORES
	
	/**
	 * Metodo encargado de actualizar la bola (intentar golpearla)
	 * cuando pulsamos la pantalla
	 */
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			ball.updateMovement(player);
		}
		
		return false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	/**
	 * Handler que modifica la aceleracion recibida desde
	 * el sensor
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
            switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                    accelSpeedX = (float) event.values[0];
                    break;
            }
		}
	}
}
