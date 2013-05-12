package com.kaseyo23.scene;

import java.io.IOException;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
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
import com.kaseyo23.object.Player;

public class GameScene extends BaseScene implements IOnSceneTouchListener, SensorEventListener {
	
	//// CONSTANTES
	private final float MAX_BALL_VEL = 300.f;
	private final float MAX_BALL_ANGLE = 135.f;
	private final float MIN_BALL_ANGLE = 45.f;
	private final float MAX_DIFF_X = 50.f;
	private final float MAX_DIFF_Y = 100.f;
	
	//// ATRIBUTOS
	private HUD gameHUD;
	private Text scoreText;
	private PhysicsWorld physicsWorld;
	
	private Player player;
	private Ball ball;
	
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
		createPhysics();
		createBackground();
		createHUD();
		createPlayer();
		createBall();
		setOnSceneTouchListener(this);
		sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sensorManager.SENSOR_DELAY_GAME);
		
		createHandlers();
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
		player = new Player(camera.getWidth()/2f, 200, vbom, camera);
		attachChild(player);
	}
	
	private void createBall() {
		ball = new Ball(camera.getWidth()/2f, 250, vbom, physicsWorld, camera);
		attachChild(ball);
	}
	
	private void createPhysics()
	{
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false); 
		//physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void createHUD() {
		gameHUD = new HUD();
		camera.setHUD(gameHUD);
		
		scoreText = new Text(20, camera.getHeight() - 100, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreText.setAnchorCenter(0, 0);
		scoreText.setText("Score: 0");
		gameHUD.attachChild(scoreText);
		
		camera.setHUD(gameHUD);
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
			if(!firstTouch) {
				firstTouch = true;
				ball.startMoving();
			}
			
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
		
		System.out.println("Diff_Y: " + diff_y + " Diff_X: " + diff_x);
		System.out.println("Ola ke ase Box2D: (" + angle + "," + vel + ")");
		
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
		
		if(!firstTouch) //Si aun no ha sacado, movemos la bola con el jugador
			ball.setX(x_actual);
		
		player.setX(x_actual);
	}
}
