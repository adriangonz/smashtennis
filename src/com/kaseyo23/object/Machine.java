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

public class Machine extends Actor {
	
	//// CONSTANTES
	private final float MACHINE_MIN_DIFF = 50.f;
	
	//// ATRIBUTOS
	
	Ball ball;
	Body body;
	
	float _dirX;
	
	//// CONSTRUCTOR
	
	public Machine(float pX, float pY, VertexBufferObjectManager vbo, 
			Camera camera, Ball ball, PhysicsWorld physicsWorld) {
		super(pX, pY, vbo, camera, (ITiledTextureRegion) ResourcesManager.getInstance().machine_region);
		this.ball = ball;
		this._dirX = 0.f;
		createPhysics(physicsWorld, camera);
	}
	
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
	
	@Override
	public int getTipo() {
		return Actor.MAQUINA;
	}
	
	//// UPDATERS
	
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
	
	private void updateMovement() {
		//Actualizo mi velocidad lineal
		this.body.setLinearVelocity(_dirX, 0);
		
		//Actualizamos el sprite
		updateSprite(_dirX);
	}
	
}
