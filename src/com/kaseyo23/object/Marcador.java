package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

/**
 * @class Marcador
 * Clase encargada de manejar el marcador de la puntuacion (desde
 * pintarlo a llevar la cuenta de "juegos" y "sets").
 * Hereda de Rectangle (sprite primitivo que dibuja un cuadrado).
 */
public class Marcador extends Rectangle {
	
	//// ATRIBUTOS
	
	/**
	 * Contadores para los puntos del juego actual (0, 15, 30, 40, Adv)
	 */
	private int[] puntos = new int[]{0,0};
	
	/**
	 * Contadores para los juegos (el primero en ganar 6, gana)
	 */
	private int[] juegos = new int[]{0,0};
	
	/**
	 * Textos donde mostramos la puntuacion
	 */
	private Text scorePlayer, scoreMaquina;
	
	//// CONSTRUCTOR
	
	/**
	 * Constructor del marcador.
	 * @param camera Puntero a la camara
	 * @param vbom Puntero al vbom
	 * @param font Puntero a la fuente del juego
	 */
	public Marcador(Camera camera, VertexBufferObjectManager vbom, Font font) {
		//Creo el rectangulo "padre"
		super(120.f, camera.getHeight() - 50.f, 350.f, 100.f, vbom);
		
		//Lo pongo de color blanco y con transparencia
		this.setColor(Color.WHITE);
		this.setAlpha(0.8f);
		
		//Creo el texto de "Player" y donde ira su score
		Text namePlayer = new Text(60.f, 40.f, font, "Player", new TextOptions(HorizontalAlign.LEFT), vbom);
		namePlayer.setAnchorCenter(0.f, 0.f);
		namePlayer.setScale(0.75f);
		
		scorePlayer = new Text(210.f, 40.f, font, "| 0 | 0", new TextOptions(HorizontalAlign.LEFT), vbom);
		scorePlayer.setAnchorCenter(0.f, 0.f);
		scorePlayer.setScale(0.75f);
		
		//Creo el texto de "Maquina" y donde ira su score
		Text nameMaquina = new Text(60.f, 10.f, font, "Maquina", new TextOptions(HorizontalAlign.LEFT), vbom);
		nameMaquina.setAnchorCenter(0.f, 0.f);
		nameMaquina.setScale(0.75f);
		
		scoreMaquina = new Text(210.f, 10.f, font, "| 0 | 0", new TextOptions(HorizontalAlign.LEFT), vbom);
		scoreMaquina.setAnchorCenter(0.f, 0.f);
		scoreMaquina.setScale(0.75f);
		
		//Anyado todo al rectangulo
		this.attachChild(namePlayer);
		this.attachChild(scorePlayer);
		this.attachChild(nameMaquina);
		this.attachChild(scoreMaquina);
	}
	
	//// INTERFAZ
	
	/**
	 * Metodo que comprueba si hay algun ganador (si alguien
	 * ha llegado a 6 sets)
	 * @return PLAYER si ha ganado ya el player,
	 * MAQUINA si ha ganado la maquina, o -1
	 */
	public int getGanador() {
		if(juegos[Actor.PLAYER] == 6)
			return Actor.PLAYER;
		else if(juegos[Actor.MAQUINA] == 6)
			return Actor.MAQUINA;
		
		return -1;
	}
	
	/**
	 * Metodo que anyade un punto al jugador que le indiquemos (ACTOR o MAQUINA)
	 * @param jugador Tipo de jugador
	 */
	public void addPunto(int jugador) {
		if(jugador != Actor.PLAYER && jugador != Actor.MAQUINA) return;
		
		puntos[jugador]++;
		
		if(puntos[jugador] == 3) {
			juegos[jugador]++;
			puntos[jugador] = 0;
		}
		
		//Actualizamos los textos del marcador
		updatePuntuacion();
	}
	
	//// METODOS PRIVADOS
	
	/**
	 * Metodo que traduce el codigo de puntos (de 0 a 4) con el valor real de los puntos
	 * @param player Codigo del jugador
	 * @return String con la cadena de los puntos
	 */
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
	
	/**
	 * Metodo que actualiza los textos del marcador cuando
	 * se añade algun punto.
	 */
	private void updatePuntuacion() {
		scorePlayer.setText(getStringPuntos(Actor.PLAYER));
		scoreMaquina.setText(getStringPuntos(Actor.MAQUINA));
	}
}
