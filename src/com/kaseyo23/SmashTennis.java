package com.kaseyo23;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import com.kaseyo23.manager.ResourcesManager;
import com.kaseyo23.manager.SceneManager;

import android.view.KeyEvent;

/**
 * @class SmashTennis
 * Clas que representa la Activity en la que se ejecuta el juego.
 */
public class SmashTennis extends BaseGameActivity {
	
	/**
	 * Puntero a la Camera de AndEngine
	 */
	private Camera camera;
	
	/**
	 * Ancho y alto con los que vamos a trabajar
	 */
	private int WIDTH=480, HEIGHT=800;

	/**
	 * Metodo al que llama AndEngine cuando acaba de crear
	 * la instancia del motor del juego. En el creamos la camera
	 * y setteamos algunas opciones.
	 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		// Creamos la camara
		camera = new BoundCamera(0, 0, WIDTH, HEIGHT);
		
		//Fijamos la resolucion a la que vamos a trabajar
		EngineOptions opts = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, 
				new RatioResolutionPolicy(WIDTH, HEIGHT), camera);
		
		//Setteamos el audio y la musica a True (por si se implementa)
		opts.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		
		//Si volvemos de reposo, activamos la pantalla del juego (y con brillo a tope)
		opts.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		
		return opts;
	}
	
	/**
	 * Metodo al que llama AndEngine para crear el motor del juego.
	 */
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		//Creamos un motor que se "autolimite" a 60 fps
		return new LimitedFPSEngine(pEngineOptions, 60);
	};

	/**
	 * Metodo al que llama AndEngine para crear los recursos.
	 */
	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws IOException {
		//Iniciamos el gestor de recursos con el puntero al motor, a la activity, a la camara
		//y al vertex buffer object manager
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		
		//Llamamos al callback para avisar de que hemos creado los recursos que necesitabamos
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	/**
	 * Este metodo se llama cuando se crea la primera "escena" del juego.
	 * Lo aprovechamos para crear la escena del Splash.
	 */
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}
	
	/**
	 * Metodo al que llama AndEngine cuando acaba de crear la primera
	 * escena. Es decir, el Splash ya esta cargado.
	 */
	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback)
			throws IOException {
		//Dejamos el splash 2 segundos, para que no desaparezca instantaneamente
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				//Cuando pasan los dos segundos, creamos la escena del menu
				//que cuando acabe, se autocargara
	            SceneManager.getInstance().createMenuScene();
			}
		}));
		
		//Llamamos al callback para avisar de que hemos terminado de cargar la escena
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	/**
	 * Metodo que destruye el juego
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
	    if (this.isGameLoaded()) {
	    	//Si el juego esta cargado, salimos
	        System.exit(0);    
	    }
	}
	
	/**
	 * Callback para manejar la tecla "retroceder"
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	    	//Si es retroceder, llamamos al onBackKey de la escena actual
	        SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}

}
