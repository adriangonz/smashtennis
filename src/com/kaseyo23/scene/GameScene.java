package com.kaseyo23.scene;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.kaseyo23.base.BaseScene;
import com.kaseyo23.extras.LevelCompleteWindow.StarsCount;
import com.kaseyo23.manager.SceneManager;
import com.kaseyo23.manager.SceneManager.SceneType;
import com.kaseyo23.object.Ball;
import com.kaseyo23.object.Machine;
import com.kaseyo23.object.Marcador;
import com.kaseyo23.object.Player;

public class GameScene extends BaseScene implements IOnSceneTouchListener, SensorEventListener {
	
	public static final float DIST_PLAYERS = 550.f;
	public static float PLAYER_Y_INIT;
	
	public static final int PLAYER = 0, MAQUINA = 1;
	
	//// ATRIBUTOS
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	
	private Player player;
	private Machine machine;
	private Ball ball;
	private Marcador marcador;
		
	private float accelSpeedX, factorAccel = 1.5f;
	private SensorManager sensorManager;
	
	//// METODOS INTERNOS DEL JUEGO
	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}
	
	//// METODOS CREADORES
	@Override
	public void createScene() {
		initParams();
		createPhysics();
		createBackground();
		createMarcador();
		createPlayer();
		createMachine();
		createBall();
		setOnSceneTouchListener(this);
		sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sensorManager.SENSOR_DELAY_GAME);
		
		createHandlers();
	}
	
	private void initParams() {
		PLAYER_Y_INIT = (camera.getHeight() - DIST_PLAYERS) / 2.f;
	}
	
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
	
	private void createPlayer() {
		player = new Player(camera.getWidth()/2f, PLAYER_Y_INIT, vbom, camera);
		attachChild(player);
	}
	
	private void createMachine() {
		machine = new Machine(camera.getWidth()/2f, PLAYER_Y_INIT + DIST_PLAYERS, vbom, camera);
		attachChild(machine);
	}
	
	private void createMarcador() {
		marcador = new Marcador(camera, vbom, resourcesManager.font);
		
		gameHUD = new HUD();
		gameHUD.attachChild(marcador);
		camera.setHUD(gameHUD);
	}
	
	private void createBall() {
		ball = new Ball(camera.getWidth()/2f, PLAYER_Y_INIT + 50.f, vbom, physicsWorld, camera, marcador);
		attachChild(ball);
	}
	
	private void createPhysics() {	
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false); 
		//physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
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
	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(camera.getWidth()/2f, camera.getHeight()/2f);
	}
	
	///// METODOS ACTUALIZADORES
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			ball.updateMovimiento(player);
		}
		
		return false;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

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
