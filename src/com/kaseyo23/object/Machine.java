package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.kaseyo23.manager.ResourcesManager;

public class Machine extends Actor {
	
	////CONSTRUCTOR
	
	public Machine(float pX, float pY, VertexBufferObjectManager vbo, Camera camera) {
		super(pX, pY, vbo, camera, (ITiledTextureRegion) ResourcesManager.getInstance().machine_region);
		this.setScale(2.5f);
		this.setRotation((float)Math.PI); //Rotamos el modelo para que mire hacia abajo
	}
}
