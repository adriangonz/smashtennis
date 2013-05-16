package com.kaseyo23.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.kaseyo23.manager.ResourcesManager;

public class Actor extends AnimatedSprite {
	////CONSTANTES
	protected final long[] ACTOR_ANIMATE = new long[]{100, 100, 100, 100};
	protected final int ACTOR_STAND_INDEX = 0;
	protected final int ACTOR_RIGHT_START_INDEX = 5;
	protected final int ACTOR_RIGHT_END_INDEX = 8;
	protected final int ACTOR_LEFT_START_INDEX = 1;
	protected final int ACTOR_LEFT_END_INDEX = 4;
	protected final float ACTOR_TOLERANCIA = 0.25f;
	
	//// ATRIBUTOS
	
	protected Camera camera;
	protected int sentidoAnterior;
	
	////CONSTRUCTOR
	
	public Actor(float pX, float pY, VertexBufferObjectManager vbo,
					Camera camera, ITiledTextureRegion region) {
		super(pX, pY, region, vbo);
		this.camera = camera;
		this.sentidoAnterior = 0;
		this.setScale(2.f);
	}
	
	////HELPERS
	
	protected void updateSprite(float diff) {
		System.out.println("Diferencia: " + diff);
		if(Math.abs(diff) <= ACTOR_TOLERANCIA) {
			//Si no nos movemos y antes nos moviamos
			//paramos el sprite
			if(sentidoAnterior != 0) {
				stopAnimation();
				setCurrentTileIndex(ACTOR_STAND_INDEX);
				sentidoAnterior = 0;
			}
		} else if(diff > 0) { //Nos movemos a la derecha
			//Si el sentido anterior es distinto
			//al actual...
			if(sentidoAnterior < 1){
				sentidoAnterior = 1;
				animate(ACTOR_ANIMATE, ACTOR_RIGHT_START_INDEX, ACTOR_RIGHT_END_INDEX, true);
			}
		} else if(diff < 0) { //Nos movemos a la izquierda
			if(sentidoAnterior > -1) {
				sentidoAnterior = -1;
				animate(ACTOR_ANIMATE, ACTOR_LEFT_START_INDEX, ACTOR_LEFT_END_INDEX, true);
			}
		}
	}
}
