package com.kaseyo23.smashtennis;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.kaseyo23.smashtennis.SceneManager.SceneType;

public class LoadingScene extends BaseScene {

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		
		attachChild(new Text(camera.getWidth()/2.f, camera.getHeight()/2.f, resourcesManager.font, "Loading...", vbom));
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		
	}

}
