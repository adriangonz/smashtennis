package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.vbo.ITiledSpriteVertexBufferObject;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.kaseyo23.manager.ResourcesManager;

public class Player extends Actor {
	//// CONSTRUCTOR
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo,
					Camera camera) {
		super(pX, pY, vbo, camera, (ITiledTextureRegion) ResourcesManager.getInstance().player_region);
		this.setScale(2.5f);
	}
	
	//// INTERFAZ
	
	public void updatePosition(float desp, float accel) {
		float x_actual = getX();
		float x_nueva = x_actual - desp * accel;
		
		if(x_nueva <= 0)
			x_nueva = 0;
		else if(x_nueva >= camera.getWidth())
			x_nueva = camera.getWidth();
		
		setX(x_nueva);
		updateSprite(x_nueva - x_actual);
	}
}
