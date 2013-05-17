package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kaseyo23.manager.ResourcesManager;
import com.kaseyo23.scene.GameScene;

/**
 * @class Ball
 * Bola de tenis en el juego
 *
 */
public class Ball extends Sprite {
	
	//// CONSTANTES
	/**
	 * Valor maximo al que escalamos la bola para 
	 * simular el movimiento parabolico
	 */
	private final float BALL_MAX_SCALE = 2.f;
	
	/**
	 * Constantes que limitan la velocidad maxima de
	 * la bola y el angulo maximo que puede tomar
	 * desde donde se golpea
	 */
	private final float BALL_MAX_VEL = 300.f;
	private final float BALL_MAX_ANGLE = 135.f;
	private final float BALL_MIN_ANGLE = 45.f;
	
	/**
	 * Constantes sobre el maximo de diferencia
	 * que puede haber entre la bola y un actor 
	 * para que este la golpee
	 */
	private final float MAX_DIFF_X = 50.f;
	private final float MAX_DIFF_Y = 100.f;
	
	//// ATRIBUTOS
	
	/**
	 * Puntero al cuerpo fisico de la bola
	 */
	private Body body;
	
	/**
	 * Angulo de la bola (90 es hacia arriba
	 * y 270 hacia abajo)
	 */
	private float angulo = 0;
	
	/**
	 * Velocidad de la bola
	 */
	private float vel = 0;
	
	/**
	 * Puntero al marcador (necesario para actualizarlo
	 * cuando la bola sale fuera)
	 */
	Marcador marcador;
	
	/**
	 * Puntero a la camara para obtener los limites del campo
	 */
	Camera camera;
	
	///// CONSTRUCTOR
	
	/**
	 * Constructor de la bola 
	 * @param pX Posicion en X
	 * @param pY Posicion en Y
	 * @param vbom Puntero al VertexBuffer
	 * @param physicsWorld Puntero al "mundo fisico" del juego
	 * @param camera Puntero a la camara
	 * @param marcador Puntero al marcador
	 */
	public Ball(float pX, float pY, VertexBufferObjectManager vbom, PhysicsWorld physicsWorld, Camera camera, Marcador marcador) {
		super(pX, pY, 10.f, 10.f, ResourcesManager.getInstance().ball_region, vbom);
		//Creamos la fisica
		createPhysics(physicsWorld, camera);
		this.marcador = marcador;
		this.camera = camera;
	}
	
	/**
	 * Metodo interno que crea el objeto fisico en Box2D
	 * @param physicsWorld Puntero al mundo fisico del juego
	 * @param camera Puntero a la camara
	 */
	private void createPhysics(PhysicsWorld physicsWorld, final Camera camera) {
		//Creamos el cuerpo fisico
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1.f, 0.8f, 0.75f));
		//Registramos un handler que se encargara de actualizar la posicion (y la fisica) de la bola
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
	
	/**
	 * Metodo que actualiza el movimiento de la bola
	 * cuanod un actor la golpea
	 * @param actor Puntero al actor que golpea la bola
	 */
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
	
	/**
	 * Setter del movimiento de la bola. Especificamos el 
	 * movimiento con un angulo y una velocidad (mas simple
	 * para que siempre se mueva igual y simplifica algunos
	 * calculos).
	 * @param angulo Angulo del movimiento
	 * @param vel Velocidad de la bola
	 */
	public void setMovement(float angulo, float vel) {
		this.angulo = angulo;
		this.vel = vel;
	}
	
	/**
	 * Settea la posicion de la bola en el punto de saque.
	 */
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
	
	/**
	 * Metodo que actualiza la posicion de la bola, ademas de indicar la escala
	 * actual (para simular el movimiento en alto) y comprobar si hemos sobrepasado los limites del campo.
	 */
	private void updateBall() {
		this.body.setLinearVelocity(new Vector2((float)Math.cos(angulo) * vel, (float)Math.sin(angulo) * vel));
		setScale(getScale(getY()));
		checkBounds();
	}
	
	/**
	 * Metodo que calcula la escala que debe tener la bola para poder
	 * simular correctamente su movimiento parabolico por encima de la red.
	 * @param pos_y Posicion actual (que debe estar entre los dos jugadores)
	 * @return Valor actual de la escala, que va de 1 a BALL_MAX_SCALE
	 */
	private float getScale(float pos_y) {
		//Lo desplazamos a la escala de 0..DIST_PLAYERS
		//y normalizamos
		float x = pos_y - GameScene.PLAYER_Y_INIT - 50.f;
		x /= (GameScene.DIST_PLAYERS - 100.f);
				
		//Calculamos el factor A y B de la parabola
		float B = (BALL_MAX_SCALE - 1.f) / 0.25f;
		float A = (1.f - BALL_MAX_SCALE) / 0.25f;
		float C = 1;
		
		//Calculamos el valor segun la funcion de la parabola
		return A*x*x + B*x + C;
	}
	
	/**
	 * Metodo que comprueba los limites del campo para ver
	 * si algun jugador ha perdido
	 */
	private void checkBounds() {
		//Obtenemos los valores actuales de la bola
		//y los maximos
		float x = this.getX(), y = this.getY(),
				x_max = this.camera.getWidth(), y_max = this.camera.getHeight();
		
		if(y > y_max) {
			//Si se ha salido por la parte de arriba, punto para el jugador
			marcador.addPunto(Actor.PLAYER);
			setPosInit();
		} else if(y < 0) {
			//Si se ha salido por debajo, punto para la maquina
			marcador.addPunto(Actor.MAQUINA);
			setPosInit();
		} else if(y < y_max/2.f) {
			//Si esta por debajo de la red
			//y se sale por un lateral, punto para la maquina
			if(x < 0 || x > x_max) {
				marcador.addPunto(Actor.MAQUINA);
				setPosInit();
			}
		} else {
			//Si esta por encima de la red y se sale por un lateral
			//punto para el jugador
			if(x < 0 || x > x_max) {
				marcador.addPunto(Actor.PLAYER);
				setPosInit();
			}
		}
	}
}
