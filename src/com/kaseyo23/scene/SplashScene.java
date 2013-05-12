package com.kaseyo23.scene;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.kaseyo23.base.BaseScene;
import com.kaseyo23.manager.SceneManager;
import com.kaseyo23.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene {
	
	private Sprite splash;
	
	@Override
	public void createScene() {
		splash = new Sprite(0, 0, resourcesManager.splash_region, vbom)
					{
						@Override
						protected void preDraw(GLState pGLState, Camera pCamera) {
							super.preDraw(pGLState, pCamera);
							pGLState.enableDither();
						}
					};
		splash.setPosition(camera.getWidth()/2.f, camera.getHeight()/2.f);
		attachChild(splash);
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}

}
