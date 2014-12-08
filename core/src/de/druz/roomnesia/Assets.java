package de.druz.roomnesia;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {

	public static final String FUSEBOX = "fusebox";
	public static final String DOOR_BOTTOM = "doorBottom";
	public static final String DOOR_RIGHT = "doorRight";
	public static final String DOOR_LEFT = "doorLeft";
	public static final String DOOR_TOP = "doorTop";
	public static final String BOTTOM = "bottom";
	public static final String BOTTOM_LEFT = "bottomLeft";
	public static final String BOTTOM_RIGHT = "bottomRight";
	public static final String CENTER = "center";
	public static final String RIGHT = "right";
	public static final String LEFT = "left";
	public static final String TOP_RIGHT = "topRight";
	public static final String TOP = "top";
	public static final String TOP_LEFT = "topLeft";
	public static final String CORNER_TOP_LEFT = "cornerTopLeft";
	public static final String CORNER_BOTTOM_LEFT = "cornerBottomLeft";
	public static final String CORNER_TOP_RIGHT = "cornerTopRight";
	public static final String CORNER_BOTTOM_RIGHT = "cornerBottomRight";

	public static boolean soundOn = true;

	public static Music music;

	public static long stepSoundId;
	public static Sound stepSound;
	public static Sound matchSound;

	public static Texture tiles;

	/**
	 * Room tiles
	 */
	public static Map<String, TextureRegion> tileMap = new HashMap<String, TextureRegion>();
	public static TextureRegion playerTex;
	public static TextureRegion monsterTex;
	public static TextureRegion fuseboxTex;
	public static TextureRegion woodTex;
	public static TextureRegion litwoodTex;
	public static Texture title;

	public static Texture loadTexture(String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public static void load() {
		Gdx.app.log("init", "loading Assets");
		tiles = loadTexture("tileset.png");
		title = loadTexture("title.png");

		playerTex = new TextureRegion(tiles, 0, 0,  32, 32);

		monsterTex = new TextureRegion(tiles, 0, 32,  32, 32);
		fuseboxTex = new TextureRegion(tiles, 96, 96,  32, 32);
		woodTex = new TextureRegion(tiles, 128, 96,  32, 32);
		litwoodTex = new TextureRegion(tiles, 160, 96,  32, 32);
		
		tileMap.put(TOP_LEFT, new TextureRegion(tiles, 		     32, 0,  32, 32));
		tileMap.put(TOP, new TextureRegion(tiles, 			     64, 0,  32, 32));
		tileMap.put(TOP_RIGHT, new TextureRegion(tiles, 	     96, 0,  32, 32));
		tileMap.put(LEFT, new TextureRegion(tiles, 		         32, 32, 32, 32));
		tileMap.put(CENTER, new TextureRegion(tiles,		     64, 32, 32, 32));
		tileMap.put(RIGHT, new TextureRegion(tiles, 		     96, 32, 32, 32));
		tileMap.put(BOTTOM_LEFT, new TextureRegion(tiles, 	     32, 64, 32, 32));
		tileMap.put(BOTTOM, new TextureRegion(tiles, 		     64, 64, 32, 32));
		tileMap.put(BOTTOM_RIGHT, new TextureRegion(tiles, 	     96, 64, 32, 32));

		tileMap.put(CORNER_BOTTOM_RIGHT, new TextureRegion(tiles, 	32, 96,  32, 32));
		tileMap.put(CORNER_TOP_RIGHT, new TextureRegion(tiles, 		32, 128, 32, 32));//
		tileMap.put(CORNER_BOTTOM_LEFT, new TextureRegion(tiles, 	64, 96,  32, 32));
		tileMap.put(CORNER_TOP_LEFT, new TextureRegion(tiles, 		64, 128, 32, 32));

		tileMap.put(DOOR_TOP, new TextureRegion(tiles, 		    160, 0,  32, 32));
		tileMap.put(DOOR_LEFT, new TextureRegion(tiles, 	    128, 32, 32, 32));
		tileMap.put(DOOR_RIGHT, new TextureRegion(tiles, 	    192, 32, 32, 32));
		tileMap.put(DOOR_BOTTOM, new TextureRegion(tiles, 	    160, 64, 32, 32));
		
		tileMap.put(FUSEBOX, fuseboxTex);
		
//		music = Gdx.audio.newMusic(Gdx.files.internal("music2.ogg"));
//		music.setVolume(0.4f);
//		music.setLooping(true);
//		music.play();
		
		stepSound = Gdx.audio.newSound(Gdx.files.internal("step1.wav"));
		stepSoundId = stepSound.loop(0.0f);
		matchSound = Gdx.audio.newSound(Gdx.files.internal("match.wav"));
//		matchSound.loop();
	}

	public static void playSound(Sound sound) {
		if (soundOn) {
			sound.play(0.5f);
		}
	}
}
