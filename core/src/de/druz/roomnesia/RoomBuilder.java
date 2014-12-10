package de.druz.roomnesia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;


public class RoomBuilder {
	private static final Filter filterDoor = new MyFilter(Roomnesia.GROUP_INDEX_DOOR);

	private static final RoomTile CENTER = new RoomTile(Assets.CENTER, null, null);
	private static final RoomTile DOOR_TOP = new RoomTile(Assets.DOOR_TOP, filterDoor, new MyUserData(new Vector2(0, 1)),			new Vector2[] {new Vector2(0,0.75f),new Vector2(1,0.75f),new Vector2(1,1f),new Vector2(0,1f)});
	private static final RoomTile DOOR_LEFT = new RoomTile(Assets.DOOR_LEFT, filterDoor, new MyUserData(new Vector2(-1, 0)),         new Vector2[] {new Vector2(0,0),new Vector2(0.25f,0),new Vector2(0.25f,1f),new Vector2(0,1f)});
	private static final RoomTile DOOR_BOTTOM = new RoomTile(Assets.DOOR_BOTTOM, filterDoor, new MyUserData(new Vector2(0, -1)),		new Vector2[] {new Vector2(0,0),new Vector2(1,0),new Vector2(1,0.25f),new Vector2(0,0.25f)});  
	private static final RoomTile DOOR_RIGHT = new RoomTile(Assets.DOOR_RIGHT, filterDoor, new MyUserData(new Vector2(1, 0)),       new Vector2[] {new Vector2(0.75f,0),new Vector2(1,0),new Vector2(1,1f),new Vector2(0.75f,1f)});
	private static final RoomTile TOP_LEFT = new RoomTile(Assets.TOP_LEFT, null, null, 							new Vector2[] {new Vector2(0,0),new Vector2(0.5f,0),new Vector2(0.5f,0.5f),new Vector2(1,0.5f),new Vector2(1f,1f),new Vector2(0f,1f)});
	private static final RoomTile BOTTOM_LEFT = new RoomTile(Assets.BOTTOM_LEFT, null, null, 					new Vector2[] {new Vector2(0,0),new Vector2(1,0),new Vector2(1,0.5f),new Vector2(0.5f,0.5f),new Vector2(0.5f,1f),new Vector2(0f,1f)});
	private static final RoomTile TOP_RIGHT = new RoomTile(Assets.TOP_RIGHT, null, null, 						new Vector2[] {new Vector2(0,0.5f),new Vector2(0.5f,0.5f),new Vector2(0.5f,0f),new Vector2(1,0f),new Vector2(1f,1f),new Vector2(0f,1f)});
	private static final RoomTile BOTTOM_RIGHT = new RoomTile(Assets.BOTTOM_RIGHT, null, null, 					new Vector2[] {new Vector2(0,0),new Vector2(1,0),new Vector2(1,1f),new Vector2(0.5f,1f),new Vector2(0.5f,0.5f),new Vector2(0f,0.5f)});
	private static final RoomTile TOP = new RoomTile(Assets.TOP, null, null, 									new Vector2[] {new Vector2(0,0.5f),new Vector2(1,0.5f),new Vector2(1,1f),new Vector2(0,1f)});
	private static final RoomTile LEFT = new RoomTile(Assets.LEFT, null, null, 									new Vector2[] {new Vector2(0,0),new Vector2(0.5f,0),new Vector2(0.5f,1f),new Vector2(0,1f)});
	private static final RoomTile BOTTOM = new RoomTile(Assets.BOTTOM, null, null, 								new Vector2[] {new Vector2(0,0),new Vector2(1,0),new Vector2(1,0.5f),new Vector2(0,0.5f)});
	private static final RoomTile RIGHT = new RoomTile(Assets.RIGHT, null, null,								new Vector2[] {new Vector2(0.5f,0),new Vector2(1,0),new Vector2(1,1f),new Vector2(0.5f,1f)});
	private static final RoomTile CORNER_TOP_LEFT = new RoomTile(Assets.CORNER_TOP_LEFT, null, null, 			new Vector2[] {new Vector2(0,0.5f),new Vector2(0.5f,0.5f),new Vector2(0.5f,1f),new Vector2(0,1f)});
	private static final RoomTile CORNER_BOTTOM_LEFT = new RoomTile(Assets.CORNER_BOTTOM_LEFT, null, null, 		new Vector2[] {new Vector2(0,0),new Vector2(0.5f,0),new Vector2(0.5f,0.5f),new Vector2(0,0.5f)});
	private static final RoomTile CORNER_TOP_RIGHT = new RoomTile(Assets.CORNER_TOP_RIGHT, null, null, 			new Vector2[] {new Vector2(0.5f,0.5f),new Vector2(1,0.5f),new Vector2(1,1f),new Vector2(0.5f,1f)});
	private static final RoomTile CORNER_BOTTOM_RIGHT = new RoomTile(Assets.CORNER_BOTTOM_RIGHT, null, null,  	new Vector2[] {new Vector2(0.5f,0),new Vector2(1,0),new Vector2(1,0.5f),new Vector2(0.5f,0.5f)});
	private static final RoomTile FUSEBOX = new RoomTile(Assets.FUSEBOX, null, null);
	
	private static final RoomTile BLOCKED = new RoomTile("BLOCKED", null, null);

	
	public static Room buildRoom(World world, RoomMap map, int startX, int startY, Vector2 movedBy) {
		Room room;
		int x1;
		int x2;
		int y1;
		int y2;
		boolean notDone = true;
		do {
			Gdx.app.log("debug", "Building new Room at Start " + startX + ", " + startY);
			room = new Room();
			x1 = startX-(MathUtils.random(2)+3);
			x2 = startX+(MathUtils.random(2)+3);
			y1 = startY-(MathUtils.random(2)+3);
			y2 = startY+(MathUtils.random(2)+3);
			x1 = giveValidX(x1);
			x2 = giveValidX(x2);
			y1 = giveValidY(y1);
			y2 = giveValidY(y2);
			try {
				notDone = !makeRoom(world, map, room, startX, startY, movedBy, x1, y1, x2, y2);
			} catch (Exception e) {
				Gdx.app.error("debug", String.format("Unable to make room for %d,%d %d,%d starting at %d,%d", x1, y1, x2, y2, startX, startY), e);
			}
		} while (notDone);
		map.rooms.add(room);
		
		return room;
	}
	
	private static int giveValidX(int x) { 
		if (x < 0) {
			return 0;
		} else if (x > Roomnesia.TILES_NUM_WIDTH-1) {
			return Roomnesia.TILES_NUM_WIDTH-1;
		}
		return x;
	}
	
	private static int giveValidY(int y) { 
		if (y < 0) {
			return 0;
		} else if (y > Roomnesia.TILES_NUM_HEIGHT-1) {
			return Roomnesia.TILES_NUM_HEIGHT-1;
		}
		return y;
	}

	private static boolean makeRoom(World world, RoomMap roomMap, Room room, int startX, int startY, Vector2 orgMoveBy, int x1, int y1, int x2, int y2) {
		Vector2 pos = new Vector2(startX, startY);
		Vector2 moveBy = orgMoveBy.cpy().limit(1).rotate(90);
		pos.add(moveBy);
		Room[][] origMap = roomMap.map;
		Room[][] map = new Room[origMap.length][origMap[0].length];
		for (int i = 0; i < origMap.length; i++) {
			map[i] = origMap[i].clone();
		}
		Set<Vector2> blockedTiles = blockEntrences(new Vector2(startX, startY), roomMap, map, origMap);
		
		List<Vector2> topTiles = new ArrayList<Vector2>();
		List<Vector2> bottomTiles = new ArrayList<Vector2>();
		List<Vector2> leftTiles = new ArrayList<Vector2>();
		List<Vector2> rightTiles = new ArrayList<Vector2>();

		// add Door at entrancePoint
		if (orgMoveBy.x > 0.5f) {
			setDoor(room, startX,startY, DOOR_LEFT);
		} else if (orgMoveBy.x < -0.5f) {
			setDoor(room, startX,startY, DOOR_RIGHT);
		} else if (orgMoveBy.y > 0.5f) {
			setDoor(room, startX,startY, DOOR_BOTTOM);
		} else if (orgMoveBy.y < -0.5f) {
			setDoor(room, startX,startY, DOOR_TOP);
		} 
//		Roomnesia.currentRoom.doorAt(startPos).isExplored = true;
		
		Gdx.app.log("debug", String.format("Starting mapping"));
		int endX = Math.round(startX-moveBy.x);
		int endY = Math.round(startY-moveBy.y);
		int iteratitons = 0;
		do {
			iteratitons++;
			if (iteratitons > 100) {
				throw new IllegalStateException("Did not finish room after 100 iterations");
			}
//			Gdx.app.log("debug", String.format("checking for pos %s, moved by %s", pos, moveBy));
			if (canMoveBy(room, map, pos, moveBy.cpy().rotate(90), x1, y1, x2, y2, blockedTiles)) {
//				Gdx.app.log("debug", String.format("move Left"));
				moveBy.rotate(90);
				// can move left, has to be inner corner 
				determineInnerCorner(room, pos, moveBy);
			} else if (canMoveBy(room, map, pos, moveBy, x1, y1, x2, y2, blockedTiles)) {
//				Gdx.app.log("debug", String.format("move straight"));
				// can move straight, has to be either top, left, bottom, right
				determineStraight(room, pos, moveBy, map, topTiles, bottomTiles, leftTiles, rightTiles);
			} else if (canMoveBy(room, map, pos, moveBy.cpy().rotate(-90), x1, y1, x2, y2, blockedTiles)) {
//				Gdx.app.log("debug", String.format("move right"));
				moveBy.rotate(-90);
				// can move right, has to be edge corner
				determineOuterCorner(room, pos, moveBy);
			} else {
				// fuckit, how did this happen?
				moveBy.rotate(180);
				setTile(room, null, pos);
				Gdx.app.error("error", String.format("Can not move in either direction at %s", pos));
			}
			pos.add(moveBy);
		} while (!(Math.round(pos.x) == endX && Math.round(pos.y) == endY));
		
		// set last tile
		Vector2 startVec = new Vector2(startX, startY);
		float angleBetweenMoveAndToGoal = startVec.sub(pos).angle(moveBy);
		Gdx.app.log("debug", String.format("Angle: ", angleBetweenMoveAndToGoal));
		if (angleBetweenMoveAndToGoal > 1) {
			determineOuterCorner(room, pos, moveBy.cpy().rotate(-90));
		} else if (angleBetweenMoveAndToGoal < -1) {
			determineInnerCorner(room, pos, moveBy.cpy().rotate(-90));
		} else {
			if (moveBy.x > 0.5f) {
				// moving right
			    setTile(room, TOP, pos);
			} else if (moveBy.x < -0.5f) {
				// moving left
		    	setTile(room, BOTTOM, pos);
		    } else if (moveBy.y > 0.5f) {
				// moving up
		        setTile(room, LEFT, pos);
			} else {
				// moving down
		        setTile(room, RIGHT, pos);
			}
		}
		
//		int doorsLeftCount = roomMap.unexpoloredDoorCount();
//		int diff = doorsLeftCount - room.doors.size();
//		Gdx.app.log("debug", String.format("Doors left: %d, diff %d Roomnesia.isAllRoomsLit %s", doorsLeftCount, diff, ""+(Roomnesia.roomVisitCount > 15 && diff == 0 && Roomnesia.isAllRoomsLit)));

		if (Roomnesia.roomVisitCount > 10) { // && diff == 0 && Roomnesia.isAllRoomsLit) {
			Gdx.app.log("debug", String.format("", "################### No more doors to explore left!!! ################### "));
			if (topTiles.size() > 0) {
				Vector2 topTile =  topTiles.get(MathUtils.random(topTiles.size()-1));
				setTile(room, FUSEBOX, topTile);
				Roomnesia.fusebox = topTile.cpy();
			} else {
				Vector2 fusePos =  new Vector2(pos);
				setTile(room, FUSEBOX, fusePos );
				Roomnesia.fusebox = fusePos.cpy();
			}
		} else {
			// add Doors
			if (Roomnesia.roomVisitCount > 2) {
				for (int tryCount = 0; tryCount < topTiles.size(); tryCount++) {
					Vector2 tilePos = topTiles.get(MathUtils.random(topTiles.size()-1));
					if (canMakeDoorAt(blockedTiles, map, room, tilePos, new Vector2(0, 1))) {
//						setTile(room, DOOR_TOP, tilePos);
//						room.doors.add(new Door(tilePos, DOOR_TOP.userData.moveVec));
				        setDoor(room, Math.round(tilePos.x), Math.round(tilePos.y), DOOR_TOP);
						break;
					}
				}
				for (int tryCount = 0; tryCount < bottomTiles.size(); tryCount++) {
					Vector2 tilePos = bottomTiles.get(MathUtils.random(bottomTiles.size()-1));
					if (canMakeDoorAt(blockedTiles, map, room, tilePos, new Vector2(0, -1))) {
//						setTile(room, DOOR_BOTTOM, tilePos);
//						room.doors.add(new Door(tilePos, DOOR_BOTTOM.userData.moveVec));
				        setDoor(room, Math.round(tilePos.x), Math.round(tilePos.y), DOOR_BOTTOM);
						break;
					}
				}
				for (int tryCount = 0; tryCount < leftTiles.size(); tryCount++) {
					Vector2 tilePos = leftTiles.get(MathUtils.random(leftTiles.size()-1));
					if (canMakeDoorAt(blockedTiles, map, room, tilePos, new Vector2(-1, 0))) {
//						setTile(room, DOOR_LEFT, tilePos);
//						room.doors.add(new Door(tilePos, DOOR_LEFT.userData.moveVec));
				        setDoor(room, Math.round(tilePos.x), Math.round(tilePos.y), DOOR_LEFT);
						break;
					}
				}
				for (int tryCount = 0; tryCount < rightTiles.size(); tryCount++) {
					Vector2 tilePos = rightTiles.get(MathUtils.random(rightTiles.size()-1));
					if (canMakeDoorAt(blockedTiles, map, room, tilePos, new Vector2(1, 0))) {
//						setTile(room, DOOR_RIGHT, tilePos);
//						room.doors.add(new Door(tilePos, DOOR_RIGHT.userData.moveVec));
				        setDoor(room, Math.round(tilePos.x), Math.round(tilePos.y), DOOR_RIGHT);
						break;
					}
				}
				
			}
		}

		List<Vector2> centerTiles = new ArrayList<Vector2>();
		// add central tiles
		for (int y = 1; y < Roomnesia.TILES_NUM_HEIGHT-1; y++) {
			for (int x = 1; x < Roomnesia.TILES_NUM_WIDTH-1; x++) {
				if (room.tiles[x][y] == null) {
					if ((room.tiles[x-1][y] == LEFT || room.tiles[x-1][y] == DOOR_LEFT || room.tiles[x-1][y] == CENTER || room.tiles[x-1][y] == CORNER_BOTTOM_LEFT || room.tiles[x-1][y] == CORNER_TOP_LEFT)) {
						if (room.tiles[x][y-1] == BOTTOM || room.tiles[x][y-1] == DOOR_BOTTOM || room.tiles[x][y-1] == CENTER || room.tiles[x][y-1] == CORNER_BOTTOM_LEFT || room.tiles[x][y-1] == CORNER_BOTTOM_RIGHT) {
							room.tiles[x][y] = CENTER;
							centerTiles.add(new Vector2(x, y));
						} else {
							Gdx.app.log("debug", String.format("fuck", null));
						}
					}
				}
			}
		}

//		if (Roomnesia.roomVisitCount > 2) {
			if (centerTiles.size() > 0) {
				for (int i = 0; i < Math.min(3, centerTiles.size()); i++) {
					makeTorch(world, room, centerTiles.get(MathUtils.random(centerTiles.size()-1)));
				}
			}
//		}
		
		// add tiles for realz
		for (int x = 0; x < Roomnesia.TILES_NUM_WIDTH; x++) {
			for (int y = 0; y < Roomnesia.TILES_NUM_HEIGHT; y++) {
				if (room.tiles[x][y] !=  null) {
					addRoomTile(room.tiles[x][y], world, roomMap, room, x, y);
				}
			}
		}
		return true;
	}

	private static void setDoor(Room room, int startX, int startY, RoomTile doorLeft) {
		room.tiles[startX][startY] = doorLeft;
		room.doors.add(new Door(new Vector2(startX, startY) , doorLeft.userData.moveVec));
	}

	public static void determineStraight(Room room, Vector2 pos, Vector2 moveBy, Room[][] map, List<Vector2> topTiles,
			List<Vector2> bottomTiles, List<Vector2> leftTiles, List<Vector2> rightTiles) {
		if (moveBy.x > 0.5f) {
			// moving right
			if (Math.round(pos.y)+1 < Roomnesia.TILES_NUM_HEIGHT && map[Math.round(pos.x)][Math.round(pos.y+1)] != null 
					&& map[Math.round(pos.x)][Math.round(pos.y+1)].tiles[Math.round(pos.x)][Math.round(pos.y+1)] == DOOR_BOTTOM) {
			    // there is a door above, place matching door
		        setTile(room, DOOR_TOP, pos);
		        setDoor(room, Math.round(pos.x), Math.round(pos.y), DOOR_TOP);
			} else {
			    setTile(room, TOP, pos);
			    topTiles.add(pos.cpy());
		    }
		} else if (moveBy.x < -0.5f) {
			// moving left
		    if (Math.round(pos.y) > 0 && map[Math.round(pos.x)][Math.round(pos.y-1)] != null 
		    		&& map[Math.round(pos.x)][Math.round(pos.y-1)].tiles[Math.round(pos.x)][Math.round(pos.y-1)] == DOOR_TOP) {
		        // there is a door below, place matching door
		        setTile(room, DOOR_BOTTOM, pos);
		        setDoor(room, Math.round(pos.x), Math.round(pos.y), DOOR_BOTTOM);
		    } else {
		        setTile(room, BOTTOM, pos);
		        bottomTiles.add(pos.cpy());
		    }
		} else if (moveBy.y > 0.5f) {
			// moving up
		    if (Math.round(pos.x) > 0 && map[Math.round(pos.x-1)][Math.round(pos.y)] != null 
		    		&& map[Math.round(pos.x-1)][Math.round(pos.y)].tiles[Math.round(pos.x-1)][Math.round(pos.y)] == DOOR_RIGHT) {
		        // there is a door to the left, place matching door
		        setTile(room, DOOR_LEFT, pos);
		        setDoor(room, Math.round(pos.x), Math.round(pos.y), DOOR_LEFT);
		    } else {
		        setTile(room, LEFT, pos);
		        leftTiles.add(pos.cpy());
		    }
		} else {
			// moving down
		    if (Math.round(pos.x)+1 < Roomnesia.TILES_NUM_WIDTH && map[Math.round(pos.x+1)][Math.round(pos.y)] != null 
		    		&& map[Math.round(pos.x+1)][Math.round(pos.y)].tiles[Math.round(pos.x+1)][Math.round(pos.y)] == DOOR_LEFT) {
		        // there is a door to the left, place matching door
		        setTile(room, DOOR_RIGHT, pos);
		        setDoor(room, Math.round(pos.x), Math.round(pos.y), DOOR_RIGHT);
		    } else {
		        setTile(room, RIGHT, pos);
		        rightTiles.add(pos.cpy());
		    }
		}
	}

	public static void determineInnerCorner(Room room, Vector2 pos, Vector2 moveBy) {
		if (moveBy.x > 0.5f) {
			// moving right
			setTile(room, CORNER_TOP_RIGHT, pos);
		} else if (moveBy.x < -0.5f) {
			// moving left
			setTile(room, CORNER_BOTTOM_LEFT, pos);
		} else if (moveBy.y > 0.5f) {
			// moving up
			setTile(room, CORNER_TOP_LEFT, pos);
		} else {
			// moving down
			setTile(room, CORNER_BOTTOM_RIGHT, pos);
		}
	}

	public static void determineOuterCorner(Room room, Vector2 pos, Vector2 moveBy) {
		if (moveBy.x > 0.5f) {
			// moving right
			setTile(room, TOP_LEFT, pos);
		} else if (moveBy.x < -0.5f) {
			// moving left
			setTile(room, BOTTOM_RIGHT, pos);
		} else if (moveBy.y > 0.5f) {
			// moving up
			setTile(room, BOTTOM_LEFT, pos);
		} else {
			// moving down
			setTile(room, TOP_RIGHT, pos);
		}
	}

	private static String stringifyMap(Room[][] map) {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < map.length; x++) {
			sb.append("[");
			for (int y = 0; y < map[0].length; y++) {
				if (map[x][y] != null) {
					sb.append(map[x][y].toString()).append(",");
				}
			}
			sb.append("]\n");
		}
		return sb.toString();
	}

	private static Set<Vector2> blockEntrences(Vector2 start, RoomMap roomMap, Room[][] map, Room[][] origMap) {
		Set<Vector2> toBlock = new HashSet<Vector2>();
		Room blocker = new Room();
		Gdx.app.log("debug", String.format("blockEntrences"));
		for (Room room : roomMap.rooms) {
			Gdx.app.log("debug", String.format("blockEntrences room %d", room.doors.size()));
			if (room.doors.size() > 0) {
				for (Door door : room.doors) {
					if (start.cpy().sub(door.pos).len() < 1.1f) {
						continue;
					} else {
						Gdx.app.log("debug", String.format("startPos %s door pos %s len %s", start, door.pos, start.cpy().sub(door.pos).len()));
					}
					Gdx.app.log("debug", String.format("Blocking Door %s", door.pos));
					RoomTile tile = room.tiles[Math.round(door.pos.x)][Math.round(door.pos.y)];
					Vector2 moveBy = tile.userData.moveVec.cpy();
					Vector2 pos = door.pos.cpy().add(moveBy.scl(2));
					for (int x = -1; x <= 1; x++) {
						for (int y = -1; y <= 1; y++) {
							if (origMap[Math.round(pos.x)+x][Math.round(pos.y)+y] == null) {
								Gdx.app.log("debug", String.format("wtf %s %d %d %d %d", map[Math.round(pos.x)+x][Math.round(pos.y)+y], Math.round(pos.x),x,Math.round(pos.y),y));
//								map[Math.round(pos.x)+x][Math.round(pos.y)+y] = blocker;
								toBlock.add(new Vector2(Math.round(pos.x)+x, Math.round(pos.y)+y));
							}
						}
					}
				}
			}
		}
		
		return toBlock;
	}

	private static void makeTorch(World world, Room room, Vector2 pos) {
		if (room.lightAt(pos) == null) {
			Torch torch = new Torch(world, room, pos);
		} else {
			Gdx.app.log("debug", String.format("Not adding a torch at %s cause there is already one", pos));
		}
	}

	private static boolean canMakeDoorAt(Set<Vector2> blockedTiles, Room[][] map, Room room, Vector2 tilePos, Vector2 moveBy) {
		Vector2 pos = tilePos.cpy().add(moveBy);
		if (!canMoveBy(room, map, pos, moveBy, 0, 0, Roomnesia.TILES_NUM_WIDTH-1, Roomnesia.TILES_NUM_HEIGHT-1, blockedTiles, false, false) 
				|| !canMoveBy(room, map, pos, moveBy.cpy().scl(2f), 0, 0, Roomnesia.TILES_NUM_WIDTH-1, Roomnesia.TILES_NUM_HEIGHT-1, blockedTiles, false, false)
				|| !canMoveBy(room, map, pos.cpy().add(moveBy.cpy().rotate(90)), moveBy, 0, 0, Roomnesia.TILES_NUM_WIDTH-1, Roomnesia.TILES_NUM_HEIGHT-1, blockedTiles, false, false) 
				|| !canMoveBy(room, map, pos.cpy().add(moveBy.cpy().rotate(90)), moveBy.cpy().scl(2f), 0, 0, Roomnesia.TILES_NUM_WIDTH-1, Roomnesia.TILES_NUM_HEIGHT-1, blockedTiles, false, false)
				|| !canMoveBy(room, map, pos.cpy().add(moveBy.cpy().rotate(-90)), moveBy, 0, 0, Roomnesia.TILES_NUM_WIDTH-1, Roomnesia.TILES_NUM_HEIGHT-1, blockedTiles, false, false) 
				|| !canMoveBy(room, map, pos.cpy().add(moveBy.cpy().rotate(-90)), moveBy.cpy().scl(2f), 0, 0, Roomnesia.TILES_NUM_WIDTH-1, Roomnesia.TILES_NUM_HEIGHT-1, blockedTiles, false, false)) {
			return false;
		}
		return true;
	}

	private static void setTile(Room room, RoomTile tile, Vector2 pos) {
		room.tiles[Math.round(pos.x)][Math.round(pos.y)] = tile;
	}

	private static boolean canMoveBy(Room room, Room[][] map, Vector2 orgPos, Vector2 moveBy, Set<Vector2> blockedTiles) {
		return canMoveBy(room, map, orgPos, moveBy, 0, 0, Roomnesia.TILES_NUM_WIDTH-1, Roomnesia.TILES_NUM_HEIGHT-1, blockedTiles, false, false);
	}
	
	private static boolean canMoveBy(Room room, Room[][] map, Vector2 orgPos, Vector2 moveBy, int x1, int y1, int x2, int y2, Set<Vector2> blockedTiles) {
		return canMoveBy(room, map, orgPos, moveBy, x1, y1, x2, y2, blockedTiles, true, false);
	}
	
	private static boolean canMoveBy(Room room, Room[][] map, Vector2 orgPos, Vector2 moveBy, int x1, int y1, int x2, int y2, Set<Vector2> blockedTiles, boolean checkRightRecursivley, boolean ignoreOwnTilesAtRecursiveCheck) {
		Vector2 pos = orgPos.cpy().add(moveBy);
//		Gdx.app.log("debug", String.format("Checking if can move to %s", pos));
		if (Math.round(pos.x) >= Roomnesia.TILES_NUM_WIDTH || Math.round(pos.y) >= Roomnesia.TILES_NUM_HEIGHT
				|| Math.round(pos.x) < 0 || Math.round(pos.y) < 0) {
			return false;
		}
		if (!ignoreOwnTilesAtRecursiveCheck && room.tiles[Math.round(pos.x)][Math.round(pos.y)] != null) {
			return false;
		}
		if (blockedTiles != null) {
			for (Vector2 vec : blockedTiles) {
				if (pos.cpy().sub(vec).len() < 0.9f) {
					Gdx.app.log("debug", String.format("Tile is blocked %s", pos));
					return true;
				}
			}
		}
		if (Math.round(pos.x) < x1 || Math.round(pos.x) > x2
				|| Math.round(pos.y) < y1 || Math.round(pos.y) > y2
				|| map[Math.round(pos.x)][Math.round(pos.y)] != null) {
			return false;
		}
		if (!checkRightRecursivley || canMoveBy(room, map, pos, moveBy.cpy().rotate(-90), x1, y1, x2, y2, blockedTiles, false, true)) {
			return true;
		}
		Gdx.app.debug("debug", String.format("could move to %s but the tile right next to it is not available", pos));
		return false;
	}

	/**
	 * Tries to make a square room within the given boundaries. Honors existing rooms. 
	 */
	private static void makeSquareRoom(World world, RoomMap roomMap, Room room, int x1, int y1, int x2, int y2) {
		Gdx.app.log("debug", String.format("Making new room from %d, %d to %d, %d", x1, y1, x2, y2));
		Room[][] map = roomMap.map;
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				if (map[x][y] == null) {
					// take this field
					if (x == x1) {
						// left or cornerLeft
						if (y == y1) {
							room.tiles[x][y] = BOTTOM_LEFT;
						} else if (y == y2 - 1) {
							room.tiles[x][y] = TOP_LEFT;
						} else {
							if (map[x][y+1] != null) {
								room.tiles[x][y] = TOP_LEFT;
							} else if (map[x][y-1] != null) {
								room.tiles[x][y] = BOTTOM_LEFT;
							} else {
								room.tiles[x][y] = LEFT;
							}
						}
					} else if (x == x2 - 1) {
						// right or cornerRight
						if (y == y1) {
							room.tiles[x][y] = BOTTOM_RIGHT;
						} else if (y == y2 - 1) {
							room.tiles[x][y] = TOP_RIGHT;
						} else {
							if (map[x][y+1] != null) {
								room.tiles[x][y] = TOP_RIGHT;
							} else if (map[x][y-1] != null) {
								room.tiles[x][y] = BOTTOM_RIGHT;
							} else {
								room.tiles[x][y] = RIGHT;
							}
						}
					} else {
						if (y == y1) {
							// bottom
							if (map[x+1][y] != null) {
								room.tiles[x][y] = BOTTOM_RIGHT;
							} else if (map[x-1][y] != null) {
								room.tiles[x][y] = BOTTOM_LEFT;
							} else {
								room.tiles[x][y] = BOTTOM;
							}
						} else if (y == y2 - 1) {
							// top
							if (map[x+1][y] != null) {
								room.tiles[x][y] = TOP_RIGHT;
							} else if (map[x-1][y] != null) {
								room.tiles[x][y] = TOP_LEFT;
							} else {
								room.tiles[x][y] = TOP;
							}
						} else {
							// maybe center, check corner cases first
							// TODO MORE checks neccessary to see if there is an existing room next to this tile 
							if (room.tiles[x][y-1] == LEFT) {
								room.tiles[x][y] = CORNER_BOTTOM_LEFT;
							} else if (room.tiles[x][y-1] == RIGHT) {
								room.tiles[x][y] = CORNER_BOTTOM_RIGHT;
							} else if (room.tiles[x-1][y] == CORNER_TOP_RIGHT) {
								room.tiles[x][y] = TOP;
							} else if (room.tiles[x-1][y] == CORNER_BOTTOM_RIGHT) {
								room.tiles[x][y] = BOTTOM;
							} else {
								room.tiles[x][y] = CENTER;
							}
						}
					}
				}
			}
		}
		for (int x = x1; x < x2; x++) {
			for (int y = y1; y < y2; y++) {
				if (room.tiles[x][y] !=  null) {
					addRoomTile(room.tiles[x][y], world, roomMap, room, x, y);
				}
			}
		}
	}



	public static Room addTorchRoom(World world, RoomMap map, int x, int y) {
		Room room = new Room();
		map.rooms.add(room);
		
		y = y+2;

		addRoomTile(TOP_LEFT, world, map, room,      x-2, y+3);
		addRoomTile(TOP, world, map, room,           x-1, y+3);
		addRoomTile(TOP, world, map, room,           x,   y+3);
		addRoomTile(TOP, world, map, room,           x+1, y+3);
		addRoomTile(TOP_RIGHT, world, map, room,     x+2, y+3);
		addRoomTile(LEFT, world, map, room,          x-2, y+2);
		addRoomTile(LEFT, world, map, room,          x-2, y+1);
		addRoomTile(LEFT, world, map, room,          x-2, y);
		addRoomTile(LEFT, world, map, room,          x-2, y-1);
		addRoomTile(CORNER_BOTTOM_RIGHT, 	world, map, room,    x-1, y+2);
		addRoomTile(CORNER_TOP_RIGHT, 		world, map, room,    x-1, y+1);
		addRoomTile(CORNER_BOTTOM_RIGHT, 	world, map, room,    x-1, y);
		addRoomTile(CORNER_TOP_RIGHT, 		world, map, room,    x-1, y-1);
		addRoomTile(CORNER_BOTTOM_LEFT,		world, map, room,    x,   y+2);
		addRoomTile(CORNER_TOP_LEFT, 		world, map, room,    x,   y+1);
		addRoomTile(CORNER_BOTTOM_LEFT, 	world, map, room,    x,   y);
		addRoomTile(CORNER_TOP_LEFT, 		world, map, room,    x,   y-1);
		addRoomTile(CENTER, world, map, room,        x+1, y+2);
		addRoomTile(CENTER, world, map, room,        x+1, y+1);
		addRoomTile(CENTER, world, map, room,        x+1, y);
		addRoomTile(CENTER, world, map, room,        x+1, y-1);
		addRoomTile(RIGHT, world, map, room,         x+2, y-1);
		addRoomTile(RIGHT, world, map, room,         x+2, y);
		addRoomTile(RIGHT, world, map, room,         x+2, y+1);
		addRoomTile(RIGHT, world, map, room,         x+2, y+2);
		addRoomTile(BOTTOM_LEFT, world, map, room,   x-2, y-2);
		addRoomTile(BOTTOM, world, map, room,        x-1, y-2);
		addRoomTile(DOOR_BOTTOM, world, map, room,   x,   y-2);
		addRoomTile(BOTTOM, world, map, room,        x+1, y-2);
		addRoomTile(BOTTOM_RIGHT, world, map, room,  x+2, y-2);
		
		room.doors.add(new Door(new Vector2(x, y-2), new Vector2(0, -1)));
		
		return room;
	}
	
	public static Room addStartRoom(World world, RoomMap map, int x, int y) {
		Room room = new Room();
		map.rooms.add(room);

		addRoomTile(TOP_LEFT, world, map, room, x-1, y+1);
		addRoomTile(DOOR_TOP, world, map, room, x, y+1);
		addRoomTile(TOP_RIGHT, world, map, room, x+1, y+1);
		addRoomTile(LEFT, world, map, room, x-1, y);
		addRoomTile(CENTER, world, map, room, x, y);
		addRoomTile(RIGHT, world, map, room, x+1, y);
		addRoomTile(BOTTOM_LEFT, world, map, room, x-1, y-1);
		addRoomTile(BOTTOM, world, map, room, x, y-1);
		addRoomTile(BOTTOM_RIGHT, world, map, room, x+1, y-1);

		room.doors.add(new Door(new Vector2(x, y+1), new Vector2(0, 1)));
		
		return room;
	}

	private static void addRoomTile(RoomTile tile, World world, RoomMap map, Room room, int x, int y) {
//		Gdx.app.log("debug", String.format("Adding RoomTile %s at %d, %d", tile.name, x, y));
		map.map[x][y] = room;
		room.tiles[x][y] = tile;
		
		if (tile.shape != null) {
			BodyDef chainBodyDef = new BodyDef();
			chainBodyDef.type = BodyType.StaticBody;
			Body body = world.createBody(chainBodyDef);
			body.setTransform(x, y, 0);
			Fixture fix = body.createFixture(tile.shape, 0);
			if (tile.filterData != null) {
				fix.setFilterData(tile.filterData);
//				Gdx.app.log("debug", "Setting filterdata for " + tile.name + " " + tile.filterData);
			}
			if (tile.userData != null) {
				fix.setUserData(tile.userData);
			}
			room.bodies.add(body);
		}
	}
	
	public static void destroyRoom(World world, RoomMap map, Room room) {
//		Gdx.app.log("debug", String.format("About to destroy %s", room));
		if (!room.isRoomLit()) {
			Gdx.app.log("debug", "Desrtoying room");
//			Gdx.app.log("debug", room.toString());
			if (room.bodies != null) {
				for (Body body : room.bodies) {
//					Gdx.app.log("debug", "Destroy Body " + body);
					world.destroyBody(body);
				}
			}
//			for (Door door : room.doors) {
//				for (Room room2 : map.rooms) {
//					Door door2 = room2.doorAt(door.pos);
//					if (door2 != null) {
//						door2.isExplored = false;
//					}
//				}
//			}
			for (Torch light : room.lights) {
				light.dispose();
			}
			for (int x = 0; x < room.tiles.length; x++) {
				for (int y = 0; y < room.tiles[0].length; y++) {
					if (room.tiles[x][y] != null) {
						map.map[x][y] = null;
						room.tiles[x][y] = null;
//						Gdx.app.log("debug", String.format("Setting map and room.tiles to null at %d, %d", x, y));
					}
				}
			}
			map.rooms.remove(room);
		}
	}
}
