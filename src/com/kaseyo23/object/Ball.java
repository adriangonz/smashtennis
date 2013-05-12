package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Ball extends Rectangle {
	
	Body body;
	boolean canMove = false;
	
	float angulo = 0; //Angulo en horizontal
	float vel = 0; //Velocidad
	
	public Ball(float pX, float pY, VertexBufferObjectManager vbom, PhysicsWorld physicsWorld, Camera camera) {
		super(pX, pY, 10.f, 10.f, vbom);
		setColor(Color.RED);
		
		createPhysics(physicsWorld, camera);
	}
	
	private void createPhysics(PhysicsWorld physicsWorld, final Camera camera) {
		 body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1.f, 0.8f, 0.75f));
		 
		 physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			 @Override
		        public void onUpdate(float pSecondsElapsed)
		        {
					super.onUpdate(pSecondsElapsed);
					camera.onUpdate(0.1f);
					
					if (canMove)
					{	
						body.setLinearVelocity(new Vector2((float)Math.cos(angulo) * vel, (float)Math.sin(angulo) * vel)); 
					}
		        }
		 });
	}
	
	public void startMoving() {
		canMove = true;
	}
	
	public void setMoviento(float angulo, float vel) {
		this.angulo = angulo;
		this.vel = vel;
	}
}
