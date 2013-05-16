package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kaseyo23.manager.ResourcesManager;
import com.kaseyo23.scene.GameScene;

public class Ball extends Sprite {
	
	//// CONSTANTES
	private final float BALL_MAX_SCALE = 2.f;
	
	private final float BALL_MAX_VEL = 300.f;
	private final float BALL_MAX_ANGLE = 135.f;
	private final float BALL_MIN_ANGLE = 45.f;
	private final float MAX_DIFF_X = 50.f;
	private final float MAX_DIFF_Y = 100.f;
	
	//// ATRIBUTOS
	private Body body;
	
	private float angulo = 0; //Angulo en horizontal
	private float vel = 0; //Velocidad
	
	Marcador marcador; //Puntero al marcador
	Camera camera; //Puntero a la camara
	
	///// CONSTRUCTOR
	
	public Ball(float pX, float pY, VertexBufferObjectManager vbom, PhysicsWorld physicsWorld, Camera camera, Marcador marcador) {
		super(pX, pY, 10.f, 10.f, ResourcesManager.getInstance().ball_region, vbom);
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
	
	///// UPDATERS
	
	public void updateMovement(Actor actor) {
		//Obtenemos la diferencia en Y y en X entre el actor y la bola
		float diff_y = this.getY() - actor.getY();
		float diff_x = this.getX() - actor.getX();
		
		//Si es la maquina, invertimos de diff_y porque esta
		//del otro lado
		if(actor.getTipo() == Actor.MAQUINA)
			diff_y = -diff_y; 
		
		//Si esta muy lejos, o ha sobrepasado al actor, ignoramos el tap
		if(diff_y > MAX_DIFF_Y) return;
		if(diff_y < 0.f) return;
		if(Math.abs(diff_x) > MAX_DIFF_X) return;
		
		//Obtenemos la velocidad que le daremos en Y a la bola
		//(cuanto mas cerca, mas fuerte)
		float vel = BALL_MAX_VEL / (diff_y + 1.f);

		//Obtenemos el desplazamiento en horizontal de la bola
		//Cuanto mas "esquinado" mas en lateral ira
		diff_x = -diff_x;
		diff_x += MAX_DIFF_X;
		float max_angle = BALL_MAX_ANGLE - BALL_MIN_ANGLE;
		float factor = max_angle / (MAX_DIFF_X * 2.f);
		float angle = diff_x * factor;
		angle += BALL_MIN_ANGLE;
		
		//Si es la maquina, invertimos el angulo 180 grados
		if(actor.getTipo() == Actor.MAQUINA)
			angle += 180.f;
		
		this.setMovement((float)Math.toRadians(angle), vel);
	}
	
	///// SETTERS
	
	public void setMovement(float angulo, float vel) {
		this.angulo = angulo;
		this.vel = vel;
	}
	
	//Settea la posicion en el inicio
	public void setPosInit() {
		//Reset params
		this.angulo = 0.f;
		this.vel = 0.f;
		this.setScale(1.f);
		
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
			marcador.addPunto(Actor.PLAYER);
			setPosInit();
		} else if(y < 0) {
			marcador.addPunto(Actor.MAQUINA);
			setPosInit();
		} else if(y < y_max/2.f) {
			//Esta por debajo de la red
			if(x < 0 || x > x_max) {
				marcador.addPunto(Actor.MAQUINA);
				setPosInit();
			}
		} else {
			//Esta por encima de la red
			if(x < 0 || x > x_max) {
				marcador.addPunto(Actor.PLAYER);
				setPosInit();
			}
		}
	}
}
