package com.kaseyo23.smashtennis;

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
import com.kaseyo23.smashtennis.LevelCompleteWindow.StarsCount;
import com.kaseyo23.smashtennis.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnSceneTouchListener, SensorEventListener {
	
	private HUD gameHUD;
	private Text scoreText;
	
	private Player player;
	
	private float accelSpeedX, factorAccel = 1.5f;
	private SensorManager sensorManager;
	
	@Override
	public void createScene() {
		createBackground();
		createHUD();
		createPlayer();
		setOnSceneTouchListener(this);
		sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sensorManager.SENSOR_DELAY_GAME);
		
		createHandlers();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(camera.getWidth()/2f, camera.getHeight()/2f);
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
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {
			
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
