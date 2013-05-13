package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import com.kaseyo23.manager.ResourcesManager;
import com.kaseyo23.scene.GameScene;

//Clase encargada de manejar el marcador de la puntuacion (desde
//pintarlo a llevar la cuenta de "juegos" y "sets"
public class Marcador extends Rectangle {
	
	//// ATRIBUTOS
	
	//Contadores para los puntos del juego actual (0, 15, 30, 40, Adv)
	private int[] puntos = new int[]{0,0};
	
	//Contadores para los juegos (el primero en ganar 6, gana)
	private int[] juegos = new int[]{0,0};
	
	//Textos donde mostramos la puntuacion
	private Text scorePlayer, scoreMaquina;
	
	//// CONSTRUCTOR
	
	public Marcador(Camera camera, VertexBufferObjectManager vbom, Font font) {
		super(120.f, camera.getHeight() - 50.f, 350.f, 100.f, vbom);
					
		this.setColor(Color.WHITE);
		this.setAlpha(0.8f);
		
		Text namePlayer = new Text(60.f, 40.f, font, "Player", new TextOptions(HorizontalAlign.LEFT), vbom);
		namePlayer.setAnchorCenter(0.f, 0.f);
		namePlayer.setScale(0.75f);
		
		scorePlayer = new Text(210.f, 40.f, font, "| 0 | 0", new TextOptions(HorizontalAlign.LEFT), vbom);
		scorePlayer.setAnchorCenter(0.f, 0.f);
		scorePlayer.setScale(0.75f);
		
		Text nameMaquina = new Text(60.f, 10.f, font, "Maquina", new TextOptions(HorizontalAlign.LEFT), vbom);
		nameMaquina.setAnchorCenter(0.f, 0.f);
		nameMaquina.setScale(0.75f);
		
		scoreMaquina = new Text(210.f, 10.f, font, "| 0 | 0", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreMaquina.setAnchorCenter(0.f, 0.f);
		scoreMaquina.setScale(0.75f);
		
		this.attachChild(namePlayer);
		this.attachChild(scorePlayer);
		this.attachChild(nameMaquina);
		this.attachChild(scoreMaquina);
	}
	
	//// INTERFAZ
	
	//Metodo que comprueba si hay algun ganador
	public int getGanador() {
		if(juegos[GameScene.PLAYER] == 6)
			return GameScene.PLAYER;
		else if(juegos[GameScene.MAQUINA] == 6)
			return GameScene.MAQUINA;
		
		return 0;
	}
	
	//Metodo que anyade un punto al jugador que le indiquemos (0 o 1)
	public void addPunto(int jugador) {
		if(jugador != GameScene.PLAYER && jugador != GameScene.MAQUINA) return;
		
		puntos[jugador]++;
		
		if(puntos[jugador] == 3) {
			juegos[jugador]++;
			puntos[jugador] = 0;
		}
		updatePuntuacion();
	}
	
	//// METODOS PRIVADOS
	
	//Metodo que traduce el codigo de puntos (de 0 a 4) con el valor real de los puntos
	private String getStringPuntos(int player) {
		String sPuntos = "";
		
		switch(puntos[player]) {
		case 0:
			sPuntos = "0";
			break;
		case 1:
			sPuntos = "15";
			break;
		case 2:
			sPuntos = "30";
			break;
		case 3:
			sPuntos = "40";
			break;
		case 4:
			sPuntos = "Adv";
			break;
		default:
			sPuntos = "";
			break;
		}
		
		return "| " + juegos[player] + " | " + sPuntos;
	}
	
	//Metodo que actualiza los textos del marcador
	private void updatePuntuacion() {
		scorePlayer.setText(getStringPuntos(GameScene.PLAYER));
		scoreMaquina.setText(getStringPuntos(GameScene.MAQUINA));
	}
}
