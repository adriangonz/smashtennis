package com.kaseyo23.smashtennis;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.kaseyo23.smashtennis.SceneManager.SceneType;

import android.app.Activity;

public abstract class BaseScene extends Scene {
	protected Engine engine;
	protected Activity activity;
	protected ResourcesManager resourcesManager;
	protected VertexBufferObjectManager vbom;
	protected BoundCamera camera;
	
	public BaseScene() 
	{
		this.resourcesManager = ResourcesManager.getInstance();
		this.activity = resourcesManager.activity;
		this.engine = resourcesManager.engine;
		this.camera = resourcesManager.camera;
		this.vbom = resourcesManager.vbom;
		createScene();
	}
	
	public abstract void createScene();
	public abstract void onBackKeyPressed();
	public abstract SceneType getSceneType();
	public abstract void disposeScene();
	
}
