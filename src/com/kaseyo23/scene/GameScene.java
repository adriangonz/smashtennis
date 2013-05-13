package com.kaseyo23.scene;

import java.io.IOException;

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
import com.kaseyo23.object.Marcador;
import com.kaseyo23.object.Player;

public class GameScene extends BaseScene implements IOnSceneTouchListener, SensorEventListener {
	
	//// CONSTANTES
	private final float MAX_BALL_VEL = 300.f;
	private final float MAX_BALL_ANGLE = 135.f;
	private final float MIN_BALL_ANGLE = 45.f;
	private final float MAX_DIFF_X = 50.f;
	private final float MAX_DIFF_Y = 100.f;
	
	public static final float DIST_PLAYERS = 550.f;
	public static float PLAYER_Y_INIT;
	
	public static final int PLAYER = 0, MAQUINA = 1;
	
	//// ATRIBUTOS
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	
	private Player player;
	private Ball ball;
	private Marcador marcador;
	
	private boolean firstTouch = false;
	
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
		setBackground(new Background(Color.GREEN));
		createTennisNet();
	}
	
	private void createTennisNet() {
		Rectangle tennis_net = new Rectangle(camera.getWidth()/2f, camera.getHeight()/2f, camera.getWidth(), 50, vbom);
		tennis_net.setColor(Color.WHITE);
		attachChild(tennis_net);
	}
	
	private void createPlayer() {
		player = new Player(camera.getWidth()/2f, PLAYER_Y_INIT, vbom, camera);
		attachChild(player);
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
	
	private void createPhysics()
	{	
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false); 
		//physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void createHandlers() {
		engine.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void onUpdate(float pSecondsElapsed) {				
				updatePlayerPosition();
			}

			@Override
			public void reset() {
				// TODO Auto-gefactorAccelnerated method stub
				
			}
			
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
			updateBallMovement();
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
	
	private void updateBallMovement() {
		//Obtenemos la diferencia en Y y en X entre el jugador y la bola
		float diff_y = ball.getY() - player.getY();
		float diff_x = ball.getX() - player.getX();
		
		//Si esta muy lejos, o ha sobrepasado al jugador, ignoramos el tap
		if(diff_y > MAX_DIFF_Y) return;
		if(diff_y < 0.f) return;
		if(Math.abs(diff_x) > MAX_DIFF_X) return;
		
		//Obtenemos la velocidad que le daremos en Y a la bola
		//(cuanto mas cerca, mas fuerte)
		float vel = MAX_BALL_VEL / (diff_y + 1.f);
		
		//Obtenemos el desplazamiento en horizontal de la bola
		//Cuanto mas "esquinado" mas en lateral ira
		diff_x = -diff_x;
		diff_x += MAX_DIFF_X;
		float max_angle = MAX_BALL_ANGLE - MIN_BALL_ANGLE;
		float factor = max_angle / (MAX_DIFF_X * 2.f);
		float angle = diff_x * factor;
		angle += MIN_BALL_ANGLE;
		
		ball.setMoviento((float)Math.toRadians(angle), vel);
	}
	
	private void updatePlayerPosition() {
		if(accelSpeedX == 0) return;
		
		float x_actual = player.getX();
		
		x_actual -= accelSpeedX * factorAccel;
		
		if(x_actual <= 0)
			x_actual = 0;
		else if(x_actual >= camera.getWidth())
			x_actual = camera.getWidth();
		
		player.setX(x_actual);
	}
}
