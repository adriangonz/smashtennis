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
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import com.kaseyo23.SmashTennis;

public class ResourcesManager {
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public SmashTennis activity;
	public BoundCamera camera;
	public VertexBufferObjectManager vbom;
	
	public Font font;
	
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	public ITextureRegion menu_background_region,
							play_region,
							options_region;
	
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	
	public ITextureRegion play_background_region,
							ball_region,
							machine_region,
							player_region;
	
	public ITextureRegion complete_window_region;
	public ITiledTextureRegion complete_stars_region;
	
	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}

	public void loadGameResources() {
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}
	
	public void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
		//options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
		       
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
	
	public void loadMenuAudio() {
		// TODO Auto-generated method stub
		
	}
	
	public void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, android.graphics.Color.WHITE, 2, android.graphics.Color.BLACK);
		font.load();
	}
	
	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}
	
	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}
	
	public void loadGameAudio() {
		// TODO Auto-generated method stub
		
	}

	public void loadGameFonts() {
		// TODO Auto-generated method stub
		
	}

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
	
	public void unloadGameTextures() {
		
	}
	
	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 480, 800, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}
	
	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	public static void prepareManager(Engine engine, SmashTennis activity, Camera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().camera = (BoundCamera) camera;
		getInstance().activity = activity;
		getInstance().vbom = vbom;
	}
	
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}
}
