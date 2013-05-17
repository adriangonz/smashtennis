package com.kaseyo23.scene;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.kaseyo23.base.BaseScene;
import com.kaseyo23.manager.SceneManager.SceneType;

/**
 * @class SplashScene
 * Escena encargada de mostrar el "splash" al principio de
 * la aplicacion (solo se muestra una vez)
 */
public class SplashScene extends BaseScene {
	
	/**
	 * Sprite con la imagen del splash
	 */
	private Sprite splash;
	
	/**
	 * Metodo que crea la escena
	 */
	@Override
	public void createScene() {
		//Creamos el sprite y habilitamos el dithering (mejor rendimiento
		//y calidad)
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
	
	/**
	 * No hacemos nada al pulsar "atras" (dejamos que se cargue el juego)
	 */
	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Getter del tipo de escena (Splash)
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}
	
	/**
	 * Liberador de recursos de la escena
	 */
	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}

}
