package com.kaseyo23.scene;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.kaseyo23.base.BaseScene;
import com.kaseyo23.manager.SceneManager;
import com.kaseyo23.manager.SceneManager.SceneType;

/**
 * @class MainMenuScene
 * Escena que muestra el menu con las opciones a elegir.
 * Implementa la interfaz necesaria para atender los eventos de tipo
 * """click""" que llegan al menu.
 */
public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
	/**
	 * Puntero a la clase propia de AndEngine MenuScene
	 */
	private MenuScene menuChildScene;
	
	/**
	 * Tipo de boton "play" (por si anyadimos mas opciones)
	 */
	private final int MENU_PLAY = 0;
	
	/**
	 * Metodo creador de la escena. Crea la escena y el menu "hijo"
	 */
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}
	
	/**
	 * Si pulsamos "atras", salimos
	 */
	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}
	
	/**
	 * Getter del tipo Menu
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}
	
	/**
	 * Metodo que libera la escena (la escena del Menu nunca se libera, si
	 * lo hacen sus recursos).
	 */
	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Metodo encargado de crear el background
	 */
	private void createBackground()
	{
		//Pintamos el sprite del background con dithering (mayor calidad)
	    attachChild(new Sprite(camera.getWidth()/2.f, camera.getHeight()/2.f, resourcesManager.menu_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}
	
	/**
	 * Metodo que crea la subescena con la clase propia de menu de AndEngine
	 */
	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(0, 0);
	    
	    //Creamos y anyadimos la opcion "jugar"
	    final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
	    menuChildScene.addMenuItem(playMenuItem);
	    
	    //Anyadimos una animacion de "escala" al menu
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    playMenuItem.setPosition(playMenuItem.getX(), 400.f);
	    
	    //Setteamos como listener a la propia clase
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}

	/**
	 * Listener del evento "click" que se lanza cuando elegimos una opcion de la subescena "menu"
	 * de AndEngine
	 */
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
        {
        case MENU_PLAY:
        	//Si es de tipo PLAY, cargamos la escena del juego
        	SceneManager.getInstance().loadGameScene(engine);
            return true;
        default:
            return false;
        }
	}
}
