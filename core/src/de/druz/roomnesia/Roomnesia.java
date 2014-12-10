package de.druz.roomnesia;

import java.util.ArrayList;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Roomnesia extends InputAdapter implements ApplicationListener {
	static final int RAYS_NUM = 256;
	static final float LIGHT_DISTANCE = 6f;
	static final float PLAYER_RADIUS = 0.35f;

	private final static int MAX_FPS = 60;
	private final static int MIN_FPS = 15;
	final static float TIME_STEP = 1f / MAX_FPS;
	private final static float MAX_STEPS = 1f + MAX_FPS / MIN_FPS;
	private final static float MAX_TIME_PER_FRAME = TIME_STEP * MAX_STEPS;
	final static int VELOCITY_ITERS = 6;
	final static int POSITION_ITERS = 2;
	
	public static final short GROUP_INDEX_PLAYER = 1;
	public static final short GROUP_INDEX_DOOR = 2;

	public static final int TILE_WIDTH = 1;
	public static final int TILE_HEIGHT = TILE_WIDTH;
	public static final int TILES_NUM_WIDTH = 25;
	public static final int TILES_NUM_HEIGHT = 15;

	SpriteBatch batch;
	private float viewportWidth = TILES_NUM_WIDTH * TILE_WIDTH;
	private float viewportHeight = TILES_NUM_HEIGHT * TILE_HEIGHT;
	private float startX = viewportWidth / 2;
	private float startY = viewportHeight / 2;
	private Body playerBod;
	private boolean controllsLocked = false;
	private boolean makeNewRoom = false;
	private Vector2 moveNormal;
	private Vector2 moveTarget;
	
	public static RoomMap map;
	public static Room currentRoom;
	private Room lastRoom;
	
	/** our ground box **/
	private ArrayList<Light> lights = new ArrayList<Light>();
	private Body hitBody;
	private float physicsTimeLeft;

	static RayHandler rayHandler;
	static World world;
	static OrthographicCamera camera;

	static Box2DDebugRenderer debugRenderer;
	boolean debugEnabled = false;
	ExtendViewport viewport;

	/** pixel perfect projection for font rendering */
	Matrix4 normalProjection = new Matrix4();
	BitmapFont font;
	private String onscreenText = "The lights went out,\n I have to find the fuse box.";
	private float onscreenTextDuration = 5f;
	public static int roomVisitCount = 0;
	public static Vector2 fusebox;
	private PointLight matchLight;
	private float matchLightDuration = -1f;
	private PointLight playerLight;
	
	private boolean showTitle = true;
	public static boolean isAllRoomsLit = false;
	
	@Override
	public void create() {
		Assets.load();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		debugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera(viewportWidth, viewportHeight);
		camera.position.set(viewportWidth/2, viewportHeight/2, 0);
		camera.update();

		viewport = new ExtendViewport(viewportWidth, viewportHeight, viewportWidth, viewportHeight, camera);
		createPhysicsWorld();

		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler = new RayHandler(world);
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);

		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0f, 0f, 0f, 0.5f);
		rayHandler.setBlurNum(1);
		rayHandler.diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
//		rayHandler.setBlur(false);
		/** BOX2D LIGHT STUFF END */

		createPlayer();
		createMap();
		// new PointLight(rayHandler, RAYS_NUM, new Color(1, 1, 1, 1), LIGHT_DISTANCE, 0, 0);
	}

	private void createMap() {
		map = new RoomMap();
		int x = MathUtils.floor(playerBod.getPosition().x);
		int y = MathUtils.floor(playerBod.getPosition().y);
		currentRoom = RoomBuilder.addStartRoom(world, map, x, y);
		RoomBuilder.addTorchRoom(world, map, x, y+2);
	}

	private void createPhysicsWorld() {
		world = new World(new Vector2(0, 0), true);
		ContactListener contactListener = new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				
			}
			
			@Override
			public void endContact(Contact contact) {
				
			}
			
			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureA().getFilterData() != null  
						&& contact.getFixtureA().getFilterData().groupIndex == Roomnesia.GROUP_INDEX_DOOR
						&& contact.getFixtureB().getFilterData() != null  
						&& contact.getFixtureB().getFilterData().groupIndex == Roomnesia.GROUP_INDEX_PLAYER) {
					Gdx.app.log("debug", "CHANGE ROOM!");
					Vector2 moveBy = ((MyUserData)contact.getFixtureA().getUserData()).moveVec.cpy();
					changeRoom(contact.getFixtureA().getBody().getPosition().cpy(), moveBy);
				}
//				int x = MathUtils.round(playerBod.getPosition().x);
//				int y = MathUtils.round(playerBod.getPosition().y);
//				RoomBuilder.buildRoom(world, map, x, y);
			}
		};
		world.setContactListener(contactListener);
		normalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//		float halfWidth = viewportWidth / 2f;
//		ChainShape chainShape = new ChainShape();
//		chainShape.createLoop(new Vector2[] { new Vector2(-halfWidth, 0f), new Vector2(halfWidth, 0f),
//				new Vector2(halfWidth, viewportHeight), new Vector2(-halfWidth, viewportHeight) });
//		BodyDef chainBodyDef = new BodyDef();
//		chainBodyDef.type = BodyType.StaticBody;
//		groundBody = world.createBody(chainBodyDef);
//		groundBody.createFixture(chainShape, 0);
//		chainShape.dispose();
	}

	protected void changeRoom(Vector2 position, Vector2 moveBy) {
		roomVisitCount++;
		handleText();
		lastRoom = currentRoom;
		int x = MathUtils.round(position.x + moveBy.x);
		int y = MathUtils.round(position.y + moveBy.y);
		if (map.map[x][y] == null) {
			makeNewRoom = true;
		} else {
			Gdx.app.log("debug", "There is a room at " + x + ", " + y + ": " + map.map[x][y].tiles[x][y]);
		}
		controllsLocked = true;
		moveNormal = moveBy.cpy();
		moveTarget = playerBod.getPosition().cpy().add(moveBy.scl(1.5f));
		Gdx.app.log("debug", String.format("Moveing Player from %s to %s", playerBod.getPosition(), moveTarget));
		Gdx.app.log("debug", String.format("Number of unexplored Doors: %d", map.unexpoloredDoorCount()));
	}

	private void handleText() {
		if (debugEnabled) {
			showText(roomVisitCount+ "," + map.unexpoloredDoorCount(), 5f);
		} else if (roomVisitCount == 1) {
			showText("I have a sever case of Amnesia. \nSometimes I forget things that happend \njust seconds ago ", 5f);
		} else if (roomVisitCount == 2) {
			showText("I have a sever case of Amnesia. \nSometimes I forget things that happend \njust seconds ago. Rooms, too.", 5f);
		} else if (roomVisitCount == 3) {
			showText("I've found matches in my pocket.\n I can use them with [SPACE]", 5f);
		} else if (roomVisitCount == 4) {
			showText("I have trouble remembering those rooms.", 5f);
		} else if (roomVisitCount == 5) {
			showText("Maybe I will be able to\n remeber the rooms,\n if I light them up.", 5f);
		} else if (roomVisitCount == 6) {
			showText("I wonder what happend\n to those giant spiders, \n that I was keeping here...", 5f);
		} else if (roomVisitCount == 7) {
			showText("It sure is dark in here.", 5f);
		} else if (roomVisitCount == 8) {
			showText("Did you hear that?!", 5f);
		} else if (roomVisitCount == 9) {
			showText("I think that was the sounds \nof someone running out of time, \njust before implementing \ngiant spiders in the basement!", 5f);
		} else if (roomVisitCount == 13) {
			showText("There may or may not be \n" + map.unexpoloredDoorCount() + " more doors for me to go through...", 5f);
		} else if (roomVisitCount >= 15) {
			showText("I wish I would remember \nhow many doors were left...\n Maybe " + map.unexpoloredDoorCount(), 5f);
		} 		
	}

	private void showText(String string, float time) {
		onscreenText = string;
		onscreenTextDuration = time;
	}

	private void createPlayer() {
		CircleShape shape = new CircleShape();
		shape.setRadius(PLAYER_RADIUS);

		FixtureDef def = new FixtureDef();
		def.restitution = 0.03f;
		def.friction = 1f;
		def.shape = shape;
		def.density = 1f;
		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyType.DynamicBody;
//		playerBodyDef.linearDamping = 0.5f;

//		playerBodyDef.position.x = startX;
//		playerBodyDef.position.y = startY;
		playerBod = world.createBody(playerBodyDef);
		playerBod.setTransform(startX, startY, 0);
		playerBod.setFixedRotation(true);
		Fixture fix = playerBod.createFixture(def);
		fix.setFilterData(new MyFilter(GROUP_INDEX_PLAYER));
		shape.dispose();

		// PointLight
		playerLight = new PointLight(rayHandler, RAYS_NUM, null, LIGHT_DISTANCE/2, startX, startY);
		playerLight.attachToBody(playerBod, PLAYER_RADIUS / 2, PLAYER_RADIUS / 2);
		playerLight.setColor(0.8f, 0.8f, 1f, 0.5f);
		playerLight.setSoftnessLength(0.5f);
		lights.add(playerLight);
//		PointLight light2 = new PointLight(rayHandler, RAYS_NUM, null, LIGHT_DISTANCE/2, startX, startY);
//		light2.attachToBody(playerBod, PLAYER_RADIUS / 1.5f, PLAYER_RADIUS*0.9f);
//		light2.setSoftnessLength(0.5f);
//		light2.setColor(1f, 0.8f, 0.3f, 1f);
//		lights.add(light2);
//		// Conelight
		ConeLight light2 = new ConeLight(rayHandler, RAYS_NUM, null, LIGHT_DISTANCE, 0, 0, 0f, 90);//MathUtils.random(30f, 50f));
		light2.attachToBody(playerBod, PLAYER_RADIUS / 2f, PLAYER_RADIUS*0.9f, 90);
		light2.setSoftnessLength(0.5f);
		light2.setColor(1f, 0.9f, 0.7f, 1f);
		lights.add(light2);
		
		Gdx.app.log("debug", "Player Pos " + playerBod.getPosition().toString());
	}

	@Override
	public void render() {
//		isAllRoomsLit  = map.isAllRoomsLit();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		if (showTitle) {
//			batch.setProjectionMatrix(normalProjection);
			batch.begin();
			batch.draw(Assets.title, 0, 0, viewportWidth, viewportHeight);
			batch.end();
		} else {

			playSounds();

			if (fusebox != null && fusebox.cpy().sub(playerBod.getPosition()).len() < 1) {
				showText("Congrotulations! You Won!", 100);
			}
			
			batch.setProjectionMatrix(camera.combined);

			batch.begin();
			drawMap(batch);
			drawPlayer(batch);
			drawTorches(batch);
			batch.end();
			
			if (matchLight != null) {
				matchLight.setColor(1f, 0.3f, 0.01f, matchLightDuration);
				matchLightDuration -= Gdx.graphics.getDeltaTime();
			}
			if (makeNewRoom) {
				RoomBuilder.buildRoom(world, map, MathUtils.floor(moveTarget.x), MathUtils.floor(moveTarget.y), moveNormal.cpy());
				makeNewRoom = false;
			}
			if (controllsLocked) {
				rayHandler.update();
				if (moveTarget.cpy().sub(playerBod.getPosition()).scl(moveNormal).len() < 0.1f) {
					moveTarget = null;
					moveNormal = null;
					controllsLocked = false;
					currentRoom = map.getRoomForPos(playerBod.getPosition());
					RoomBuilder.destroyRoom(world, map, lastRoom);
				} else {
					playerBod.setTransform(playerBod.getPosition().add(moveNormal.cpy().scl(Gdx.graphics.getDeltaTime())), playerBod.getAngle());
				}
			} else {
				boolean stepped = fixedStep(Gdx.graphics.getDeltaTime());
				rayHandler.setCombinedMatrix(camera.combined);
				if (stepped) {
					rayHandler.update();
				}
			}

			rayHandler.render();
			// rayHandler.updateAndRender();

			if (debugEnabled) {
				debugRenderer.render(world, camera.combined);
			}

			if (onscreenText != null && onscreenTextDuration > 0) {
				onscreenTextDuration -= Gdx.graphics.getDeltaTime();
				
				batch.setProjectionMatrix(normalProjection);
				batch.begin();
				Vector3 pos = new Vector3(playerBod.getPosition().x+1, playerBod.getPosition().y, 0);
				camera.project(pos);
				font.drawMultiLine(batch, onscreenText, Math.min(pos.x, Gdx.graphics.getWidth()*0.75f), pos.y);
				batch.end();
			}
		}
		
	}

	private void drawTorches(SpriteBatch batch2) {
		for (Room room : map.rooms) {
			for (Torch light : room.lights) {
				light.render(batch);
			}
		}
	}

	private void playSounds() {
		float volume = playerBod.getLinearVelocity().cpy().len()/3f;
//		Gdx.app.log("debug", String.format("volume %s", volume));
		Assets.stepSound.setVolume(Assets.stepSoundId, volume);
//		Assets.playSound(Assets.stepSound);
	}

	private void drawPlayer(SpriteBatch batch2) {
//		batch.draw(Assets.playerTex, playerBod.getPosition().x-PLAYER_RADIUS, playerBod.getPosition().y-PLAYER_RADIUS, PLAYER_RADIUS*2, PLAYER_RADIUS*2);
		batch.draw(Assets.playerTex, playerBod.getPosition().x-PLAYER_RADIUS, playerBod.getPosition().y-PLAYER_RADIUS, 
				PLAYER_RADIUS, PLAYER_RADIUS, PLAYER_RADIUS*2, PLAYER_RADIUS*2, 1, 1, MathUtils.radiansToDegrees*playerBod.getAngle());
	}

	private void drawMap(SpriteBatch batch) {
		for (Room room : map.rooms) {
			for (int x = 0; x < room.tiles.length; x++) {
				for (int y = 0; y < room.tiles[0].length; y++) {
					if (room.tiles[x][y] != null) {
						batch.draw(room.tiles[x][y].texReg, x, y, TILE_WIDTH, TILE_HEIGHT);
					}
				}	
			}
		}
	}

	@Override
	public void dispose() {
		rayHandler.dispose();
	}

	private boolean fixedStep(float delta) {
		physicsTimeLeft += delta;
		if (physicsTimeLeft > MAX_TIME_PER_FRAME)
			physicsTimeLeft = MAX_TIME_PER_FRAME;

		boolean stepped = false;
		while (physicsTimeLeft >= TIME_STEP) {
			world.step(TIME_STEP, VELOCITY_ITERS, POSITION_ITERS);
			physicsTimeLeft -= TIME_STEP;
			stepped = true;
		}
		return stepped;
	}

//	Vector3 testPoint = new Vector3();
//	QueryCallback callback = new QueryCallback() {
//		@Override
//		public boolean reportFixture(Fixture fixture) {
//			if (fixture.testPoint(testPoint.x, testPoint.y)) {
//				hitBody = fixture.getBody();
//				return false;
//			} else
//				return true;
//		}
//	};
//
//	/** another temporary vector **/
//	Vector2 target = new Vector2();
//	private MouseJoint mouseJoint;
//
//	@Override
//	public boolean touchDown(int x, int y, int pointer, int newParam) {
//		// translate the mouse coordinates to world coordinates
//		testPoint.set(x, y, 0);
//		camera.unproject(testPoint);
//
//		// ask the world which bodies are within the given
//		// bounding box around the mouse pointer
//		hitBody = null;
//		world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f, testPoint.x + 0.1f, testPoint.y + 0.1f);
//
//		// if we hit something we create a new mouse joint
//		// and attach it to the hit body.
//		if (hitBody != null) {
//			MouseJointDef def = new MouseJointDef();
//			def.bodyA = groundBody;
//			def.bodyB = hitBody;
//			def.collideConnected = true;
//			def.target.set(testPoint.x, testPoint.y);
//			def.maxForce = 1000.0f * hitBody.getMass();
//
//			mouseJoint = (MouseJoint) world.createJoint(def);
//			hitBody.setAwake(true);
//		}
//
//		return false;
//	}
//
//	@Override
//	public boolean touchDragged(int x, int y, int pointer) {
//		camera.unproject(testPoint.set(x, y, 0));
//		target.set(testPoint.x, testPoint.y);
//		// if a mouse joint exists we simply update
//		// the target of the joint based on the new
//		// mouse coordinates
//		if (mouseJoint != null) {
//			mouseJoint.setTarget(target);
//		}
//		return false;
//	}
//
//	@Override
//	public boolean touchUp(int x, int y, int pointer, int button) {
//		// if a mouse joint exists we simply destroy it
//		if (mouseJoint != null) {
//			world.destroyJoint(mouseJoint);
//			mouseJoint = null;
//		}
//		return false;
//	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector3 mousePoint3 = new Vector3(screenX, screenY, 0);
		camera.unproject(mousePoint3);
		Vector2 mousePoint = new Vector2(mousePoint3.x, mousePoint3.y);
		float angle = mousePoint.sub(playerBod.getPosition()).rotate(-90).angleRad();
		float currentAngle = playerBod.getAngle();
		playerBod.setTransform(playerBod.getPosition(), angle);
		Vector2 vec = RoomnesiaUtils.vecFromAngle(currentAngle).sub(RoomnesiaUtils.vecFromAngle(angle));
		Vector2 velo = playerBod.getLinearVelocity();
//		Gdx.app.log("debug", String.format("velo.len() %s", playerBod.getMass()));
		if (velo.len() < 0.5f) {
			playerBod.applyLinearImpulse(velo.scl(-1f*playerBod.getMass()), playerBod.getPosition(), true);
		} else {
//			playerBod.applyLinearImpulse(velo.scl(-0.5f*playerBod.getMass()), playerBod.getPosition(), true);
			playerBod.applyForceToCenter(velo.scl(-8f*vec.len()), true);
		}
		return super.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (controllsLocked) {
			return true;
		}
		if (showTitle) {
			showTitle = false;
		}

		Vector2 lookVec = RoomnesiaUtils.vecFromAngle(playerBod.getAngle());
		lookVec.rotate(90);
		float moveImpulse = 0.4f;
		
		switch (keycode) {


		case Input.Keys.W:
			playerBod.applyLinearImpulse(lookVec.scl(moveImpulse), playerBod.getPosition(), true);
			return true;

		case Input.Keys.A:
//			lookVec.rotate(90);
//			playerBod.applyForceToCenter(lookVec.x*10, lookVec.y*10, true);
//			playerBod.applyForceToCenter(-10, 0, true);
			playerBod.applyLinearImpulse(new Vector2(-moveImpulse, 0), playerBod.getPosition(), true);
			return true;

		case Input.Keys.S:
			playerBod.applyLinearImpulse(lookVec.scl(-moveImpulse), playerBod.getPosition(), true);
//			playerBod.applyForceToCenter(lookVec.x*-10, lookVec.y*-10, true);
			return true;

		case Input.Keys.D:
//			lookVec.rotate(-90);
//			playerBod.applyForceToCenter(lookVec.x*10, lookVec.y*10, true);
//			playerBod.applyForceToCenter(10, 0, true);
			playerBod.applyLinearImpulse(new Vector2(moveImpulse, 0), playerBod.getPosition(), true);
			return true;

		case Input.Keys.SPACE:
			if (roomVisitCount > 2) {
				if (matchLight == null) {
					matchLight = new PointLight(rayHandler, RAYS_NUM, null, LIGHT_DISTANCE/2, playerBod.getPosition().x + PLAYER_RADIUS/3, playerBod.getPosition().y);
					matchLight.attachToBody(playerBod, PLAYER_RADIUS / 2, PLAYER_RADIUS / 2);
					matchLight.setColor(1f, 0.3f, 0.01f, 1f);
					matchLight.setSoftnessLength(0.0f);
				} 
				if (matchLightDuration < 0f) {
					matchLightDuration = 1f;
					Assets.playSound(Assets.matchSound);
				}
				
				Torch torch = currentRoom.lightAt(playerBod.getPosition());
				if (torch != null) {
					Gdx.app.log("debug", String.format("Lighting torch %s", torch));
					torch.light();
				}
			}
			return true;

//		case Input.Keys.UP:
//			camera.translate(0, 1);
//			camera.update();
//			return true;
//
//		case Input.Keys.LEFT:
//			camera.translate(-1, 0);
//			camera.update();
//			return true;
//
//		case Input.Keys.DOWN:
//			camera.translate(0, -1);
//			camera.update();
//			return true;
//
//		case Input.Keys.RIGHT:
//			camera.translate(1, 0);
//			camera.update();
//			return true;
//			
		case Input.Keys.F1:
			debugEnabled = !debugEnabled;
			return true;
//
//		case Input.Keys.F5:
//			for (Light light : lights) {
//				light.setColor(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1f);
//				Gdx.app.log("debug", "light " + light.getColor().toString());
//			}
//			return true;
//
//		case Input.Keys.F6:
//			for (Light light : lights) {
//				light.setDistance(MathUtils.random(LIGHT_DISTANCE * 0.5f, LIGHT_DISTANCE * 2f));
//				Gdx.app.log("debug", "light distance " + light.getDistance());
//			}
//			return true;
//
//		case Input.Keys.F9:
//			rayHandler.diffuseBlendFunc.reset();
//			return true;
//
//		case Input.Keys.F10:
//			rayHandler.diffuseBlendFunc.set(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
//			return true;
//
//		case Input.Keys.F11:
//			rayHandler.diffuseBlendFunc.set(GL20.GL_SRC_COLOR, GL20.GL_DST_COLOR);
//			return true;

		default:
			return false;

		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		normalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
}
