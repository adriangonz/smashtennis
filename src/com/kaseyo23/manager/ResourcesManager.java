package com.kaseyo23.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.kaseyo23.SmashTennis;

/**
 * @class ResourcesManager
 * Gestor que se encarga de manejar los recursos de cada una de las escenas
 * y de guardar los punteros a los elementos basicos del sistema (camara,
 * motor, activity y gestor de Vertex).
 * 
 * Esta implementado con el patron Singleton.
 */
public class ResourcesManager {
	/**
	 * Puntero a la unica instancia del gestor
	 */
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	/**
	 * Puntero al motor del juego
	 */
	public Engine engine;
	
	/**
	 * Puntero a la activity
	 */
	public SmashTennis activity;
	
	/**
	 * Puntero a la camera
	 */
	public BoundCamera camera;
	
	/**
	 * Puntero al gestor de Vertex
	 */
	public VertexBufferObjectManager vbom;
	
	/**
	 * Puntero a la fuente que usamos en el juego
	 */
	public Font font;
	
	/**
	 * Punteros a las texturas especificas del
	 * splash 
	 */
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	/**
	 * Punteros a las texturas especificas del menu
	 */
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	public ITextureRegion menu_background_region,
							play_region;
	
	/**
	 * Punteros a las texturas de la escena del juego
	 */
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	public ITextureRegion play_background_region,
							ball_region,
							machine_region,
							player_region;
	
	/**
	 * Metodo encargado de cargar los recursos del menu
	 */
	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuFonts();
	}

	/**
	 * Metodo encargado de cargar los recursos de la escena
	 * del juego
	 */
	public void loadGameResources() {
		loadGameGraphics();
	}
	
	/**
	 * Metodo que carga las texturas del menu
	 */
	public void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
		       
		try 
		{
		    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    this.menuTextureAtlas.load();
		} 
		catch (final TextureAtlasBuilderException e)
		{
		        Debug.e(e);
		}
	}
	
	/**
	 * Metodo que carga las fuentes del menu, y del juego
	 */
	public void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, android.graphics.Color.WHITE, 2, android.graphics.Color.BLACK);
		font.load();
	}
	
	/**
	 * Metodo que pseudolibera las texturas del menu
	 */
	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}
	
	/**
	 * Metodo que vuelve a cargar las texturas del menu tras
	 * haberlas "pseudoliberado" con el unload 
	 */
	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}

	/**
	 * Metodo encargado de cargar las texturas del juego
	 */
	public void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		
	    play_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "background.png");
	    ball_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "ball.png");
	    machine_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "machine.png", 9, 1);
	    player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 9, 1);
	  
	    try 
	    {
	        this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	        this.gameTextureAtlas.load();
	    } 
	    catch (final TextureAtlasBuilderException e)
	    {
	        Debug.e(e);
	    }
	}
	
	/**
	 * Metodo encargado de cargar los recursos del splash
	 */
	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 480, 800, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}
	
	/**
	 * Metodo que libera los recursos del splash
	 */
	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	/**
	 * Metodo estatico al que llamamos para inicializar los punteros del gestor
	 */
	public static void prepareManager(Engine engine, SmashTennis activity, Camera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().camera = (BoundCamera) camera;
		getInstance().activity = activity;
		getInstance().vbom = vbom;
	}
	
	/**
	 * Metodo que nos permite usar el patron singleton, y es el que
	 * usamos para obtener la instancia actual del gestor.
	 * @return Puntero al gestor de recursos.
	 */
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
}
