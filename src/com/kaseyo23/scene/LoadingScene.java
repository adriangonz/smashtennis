package com.kaseyo23.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.kaseyo23.base.BaseScene;
import com.kaseyo23.manager.SceneManager.SceneType;

/**
 * @class LoadingScene
 * Escena que muestra el texto "Cargando..." en las 
 * transiciones entre el juego y el menu
 */
public class LoadingScene extends BaseScene {

	/**
	 * Crea la escena y pinta el texto sobre el fondo blanco
	 */
	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));

		attachChild(new Text(camera.getWidth()/2.f, camera.getHeight()/2.f, resourcesManager.font, "Cargando...", vbom));
	}
	
	/**
	 * Si pulsamos la tecla de "atras" no hacemos nada
	 */
	@Override
	public void onBackKeyPressed() {
		return;
	}

	/**
	 * Devuelve el tipo de escena
	 */
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}
	
	/**
	 * "Liberador" de recursos, que no libera nada porque no tiene nada asignado
	 */
	@Override
	public void disposeScene() {
		
	}

}
