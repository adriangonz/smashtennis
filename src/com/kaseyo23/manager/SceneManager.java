package com.kaseyo23.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.kaseyo23.base.BaseScene;
import com.kaseyo23.scene.GameScene;
import com.kaseyo23.scene.LoadingScene;
import com.kaseyo23.scene.MainMenuScene;
import com.kaseyo23.scene.SplashScene;

/**
 * @class SceneManager
 * Gestor encargado de manejar las cargas y transiciones
 * entre escenas. Al igual que el gestor de recursos, emplea
 * el patron singleton.
 * 
 * Tenemos cuatro tipos de escena:
 * 	- Splash. Que se carga al principio de la aplicacion.
 * 	- Menu. Menu con la opcion de "Jugar" que cargamos despues
 * 			del splash.
 * 	- Loading. Escena que se muestra en la transicion entre el
 * 			   menu y la escena del juego.
 *  - Game. Escena principal del juego donde transcurre toda
 *  		la jugabilidad.
 */
public class SceneManager {
	/**
	 * Punteros a los distintos tipos de escenas.
	 * Solo tenemos una instancia de cada una en todo
	 * el juego.
	 */
	private BaseScene splashScene,
						menuScene,
						gameScene,
						loadingScene;
	
	/**
	 * Puntero a la instancia global del gestor de escenas
	 */
	private static final SceneManager INSTANCE = new SceneManager();
	
	/**
	 * Tipo de la escena actual
	 */
	private SceneType currentSceneType = SceneType.SCENE_SPLASH;
	
	/**
	 * Puntero a la escena actual
	 */
	private BaseScene currentScene;
	
	/**
	 * Puntero al motor de juego (que cogemos del gestor de recursos)
	 */
	private Engine engine = ResourcesManager.getInstance().engine;
	
	/**
	 *	Enum con los distintos tipos de escena
	 */
	public enum SceneType {
		SCENE_SPLASH,
		SCENE_MENU,
		SCENE_GAME,
		SCENE_LOADING
	}
	
	/**
	 * Setter de la escena actual indicando la escena
	 * @param scene Escena que setteamos
	 */
	public void setScene(BaseScene scene) {
		//Le indicamos al motor cual es la escena actual
		engine.setScene(scene);
		//Nos la guardamos como la "actual"
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}
	
	/**
	 * Setter de la escena, indicando el tipo
	 * @param sceneType Tipo de la escena
	 */
	public void setScene(SceneType sceneType) {
		switch (sceneType) {
		case SCENE_SPLASH:
			setScene(splashScene);
			break;
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_GAME:
			setScene(gameScene);
			break;
		case SCENE_LOADING:
			setScene(loadingScene);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Devuelve un puntero a la instancia singleton
	 * del gestor.
	 * @return Puntero al gestor
	 */
	public static SceneManager getInstance() {
        return INSTANCE;
    }
    
	/**
	 * Devuelve el tipo de escena actual.
	 * @return Tipo de escena
	 */
    public SceneType getCurrentSceneType() {
        return currentSceneType;
    }
    
    /**
     * Devuelve la escena actual.
     * @return Escena actual
     */
    public BaseScene getCurrentScene() {
        return currentScene;
    }
    
    /**
     * Metodo encargado de crear la escena de Splash
     * @param pOnCreateSceneCallback Callback al que 
     * llamamos cuando terminamos de crear la escena
     */
    public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
    	//Cargamos los recursos del splash
        ResourcesManager.getInstance().loadSplashScreen();
        //Creamos la escena
        splashScene = new SplashScene();
        //La setteamos como actual
        currentScene = splashScene;
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }
    
    /**
     * Metodo que libera la escena del splash
     */
    private void disposeSplashScene() {
    	//Liberamos los recursos del splash
        ResourcesManager.getInstance().unloadSplashScreen();
        //Liberamos la escena
        splashScene.disposeScene();
        splashScene = null;
    }
    
    /**
     * Metodo que crea la escena del menu
     */
    public void createMenuScene()
    {
    	//Cargamos los recursos del menu
        ResourcesManager.getInstance().loadMenuResources();
        //Creamos la escena del menu
        menuScene = new MainMenuScene();
        loadingScene = new LoadingScene();
        //La setteamos como la actual y eliminamos el splash
        setScene(menuScene);
        disposeSplashScene();
    }
    
    /**
     * Metodo encargado de cargar la escena del juego
     * @param mEngine Motor del juego
     */
    public void loadGameScene(final Engine mEngine) {
    	//Setteamos la escena de cargando...
    	setScene(loadingScene);
    	//Liberamos los recursos del menu (no la escena)
    	ResourcesManager.getInstance().unloadMenuTextures();
    	//Creamos un temporizador que tras 0.1 segundos carga los recursos
    	//del juego y settea la escena. De esta forma, conseguimos hacerlo
    	//en otro hilo y dejar que siga la escena de cargando
    	mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadGameResources();
				gameScene = new GameScene();
				setScene(gameScene);
			}
		}));
    }
    
    /**
     * Metodo encargado de cargar la escena del menu tras haber
     * vuelto desde la del juego, es decir, la escena del menu
     * ya esta creada.
     * @param mEngine Motor del juego
     */
    public void loadMenuScene(final Engine mEngine) {
    	//Setteamos la escena de cargando
    	setScene(loadingScene);
    	//Eliminamos la escena entera (con recursos y con todo)
    	//del juego
    	gameScene.disposeScene();
    	//Al igual que en el metodo anterior, creamos un hilo nuevo que 
    	//cargue los recursos del menu mientras que sigue setteada la 
    	//escena de cargando
    	mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				setScene(menuScene);
			}
		}));
    }
}
