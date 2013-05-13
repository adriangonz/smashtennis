package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kaseyo23.scene.GameScene;

public class Ball extends Rectangle {
	
	//// CONSTANTES
	final float BALL_MAX_SCALE = 2.f;
	
	//// ATRIBUTOS
	private Body body;
	
	private float angulo = 0; //Angulo en horizontal
	private float vel = 0; //Velocidad
	
	Marcador marcador; //Puntero al marcador
	Camera camera; //Puntero a la camara
	
	///// CONSTRUCTOR
	
	public Ball(float pX, float pY, VertexBufferObjectManager vbom, PhysicsWorld physicsWorld, Camera camera, Marcador marcador) {
		super(pX, pY, 10.f, 10.f, vbom);
		setColor(Color.RED);
		createPhysics(physicsWorld, camera);
		this.marcador = marcador;
		this.camera = camera;
	}
	
	private void createPhysics(PhysicsWorld physicsWorld, final Camera camera) {
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1.f, 0.8f, 0.75f));
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				updateBall(); 
	        }
		});
	}
	
	///// Setters
	public void setMoviento(float angulo, float vel) {
		this.angulo = angulo;
		this.vel = vel;
	}
	
	//Settea la posicion en el inicio
	public void setPosInit() {
		//Reset params
		this.angulo = 0.f;
		this.vel = 0.f;
		
		//Move Box2D body
		float x = camera.getWidth() / 2.f;
		float y = GameScene.PLAYER_Y_INIT + 50.f;
		float widthD2 = this.getWidth() / 2;
		float heightD2 = this.getHeight() / 2;
		float angle = body.getAngle(); // keeps the body angle
		Vector2 v2 = new Vector2((x + widthD2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (y + heightD2) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
		body.setTransform(v2, angle);
	}
	
	///// HELPERS
	
	//Metodo que actualiza la posicion de la bola ademas de otras cosas
	private void updateBall() {
		this.body.setLinearVelocity(new Vector2((float)Math.cos(angulo) * vel, (float)Math.sin(angulo) * vel));
		setScale(getScale(getY()));
		checkBounds();
	}
	
	//Metodo que devuelve la escala a la que debe estar
	private float getScale(float pos_y) {
		//Lo desplazamos a la escala de 0..DIST_PLAYERS
		//y normalizamos
		float x = pos_y - GameScene.PLAYER_Y_INIT - 50.f;
		x /= (GameScene.DIST_PLAYERS - 100.f);
				
		//Calculamos el factor A y B de la parabola
		float B = (BALL_MAX_SCALE - 1.f) / 0.25f;
		float A = (1.f - BALL_MAX_SCALE) / 0.25f;
		float C = 1;
		
		return A*x*x + B*x + C;
	}
	
	//Metodo que comprueba los limites de la pantalla
	//para ver si algun jugador ha perdido
	private void checkBounds() {
		float x = this.getX(), y = this.getY(),
				x_max = this.camera.getWidth(), y_max = this.camera.getHeight();
		
		if(y > y_max) {
			marcador.addPunto(GameScene.PLAYER);
			setPosInit();
		} else if(y < 0) {
			marcador.addPunto(GameScene.MAQUINA);
			setPosInit();
		} else if(y < y_max/2.f) {
			//Esta por debajo de la red
			if(x < 0 || x > x_max) {
				marcador.addPunto(GameScene.MAQUINA);
				setPosInit();
			}
		} else {
			//Esta por encima de la red
			if(x < 0 || x > x_max) {
				marcador.addPunto(GameScene.PLAYER);
				setPosInit();
			}
		}
	}
}
