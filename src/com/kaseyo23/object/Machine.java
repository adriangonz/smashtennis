package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kaseyo23.manager.ResourcesManager;

/**
 * @class Machine
 * Clase que hereda de Actor y representa a la maquina
 * contra la que nos enfrentamos en el juego
 */
public class Machine extends Actor {
	
	//// CONSTANTES
	/**
	 * Constante que fija la diferencia maxima con la bola
	 * que puede tener la maquina para golpearla.
	 * Es menor al maximo, para que asi le de mas fuerte
	 */
	private final float MACHINE_MIN_DIFF = 50.f;
	
	//// ATRIBUTOS
	
	/**
	 * Puntero a la bola
	 */
	Ball ball;
	
	/**
	 * Puntero al cuerpo fisico de la maquina
	 */
	Body body;
	
	/**
	 * Posicion en X hacia la que se DIRIGE la maquina
	 */
	float _dirX;
	
	//// CONSTRUCTOR
	
	/**
	 * Constructor de la maquina
	 */
	public Machine(float pX, float pY, VertexBufferObjectManager vbo, 
			Camera camera, Ball ball, PhysicsWorld physicsWorld) {
		super(pX, pY, vbo, camera, (ITiledTextureRegion) ResourcesManager.getInstance().machine_region);
		this.ball = ball;
		this._dirX = 0.f;
		createPhysics(physicsWorld, camera);
	}
	
	/**
	 * Metodo que crea la fisica que se encargara de mover a la maquina
	 */
	private void createPhysics(PhysicsWorld physicsWorld, final Camera camera) {
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0.f, 0.f, 0.f));
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				updateMovement();
	        }
		});
	}
	
	//// INTERFAZ
	
	/**
	 * Metodo que devuelve el tipo de Actor,
	 * que en este caso es MAQUINA
	 */
	@Override
	public int getTipo() {
		return Actor.MAQUINA;
	}
	
	//// UPDATERS
	
	/**
	 * Metodo encargado de gestionar la "IA" de la maquina.
	 * En nuestro caso es muy simple. En cada iteracion buscamos
	 * la bola y nos vemos hacia ella. Si esta lo suficientemente
	 * cerca, intentamos golpearla
	 */
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		
		//Calculo la diferencia hacia la bola
		float diff = ball.getX() - this.getX();
		
		//Actualizo mi "velocidad" en X
		_dirX = diff;
		
		//Si la bola esta suficientemente cerca, intentamos golpearla
		float diff_y = ball.getY() - this.getY();
		if(-diff_y < MACHINE_MIN_DIFF)
			ball.updateMovement(this);
	}
	
	/**
	 * Metodo que actualiza el movimiento de la maquina variando
	 * la velocidad lineal de esta y el movimiento del sprite
	 */
	private void updateMovement() {
		//Actualizo mi velocidad lineal
		this.body.setLinearVelocity(_dirX, 0);
		
		//Actualizamos el sprite
		updateSprite(_dirX);
	}
	
}
