package de.druz.roomnesia;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Room {

	public ArrayList<Torch> lights = new ArrayList<Torch>();
	public ArrayList<Door> doors = new ArrayList<Door>();
	public List<Body> bodies = new ArrayList<Body>();
	public RoomTile[][] tiles = new RoomTile[Roomnesia.TILES_NUM_WIDTH][Roomnesia.TILES_NUM_HEIGHT];
	public boolean isLit = false;
	
	public boolean isRoomLit() {
		if (isLit) {
			return true;
		}
		if (lights == null || lights.size() == 0) {
			return false;
		}
		for (Torch torch : lights) {
			if (!torch.isLit) {
				return false;
			}
		}
		isLit = true;
		return true;
	}

	public boolean isFullyExplored() {
		for (Door door : doors) {
			if (!door.isExplored()) {
				return false;
			}
		}
		return true;
	}

	public int unexploredDoorsCount() {
		int count = 0;
		for (Door door : doors) {
			if (!door.isExplored()) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < tiles.length; x++) {
			sb.append("[");
			for (int y = 0; y < tiles[0].length; y++) {
				if (tiles[x][y] != null) {
					sb.append(tiles[x][y].toString()).append(",");
				}
			}
			sb.append("]\n");
		}
		return "Room [bodies= " + bodies.size() + ", tiles=" + sb.toString() + "]";
	}

	public Door doorAt(Vector2 pos) {
		for (Door door : doors) {
			float length = door.pos.cpy().sub(pos).len();
//			Gdx.app.log("debug", String.format("looking for %s vs %s length %s", pos, door.pos, length));
			boolean found = length  < 1.1f;
			if (found) {
				return door;
			}
		}
		return null;
	}

	public Torch lightAt(Vector2 pos) {
		for (Torch light : lights) {
			float length = light.pos.cpy().sub(pos).len();
//			Gdx.app.log("debug", String.format("looking for %s vs %s length %s", pos, light.pos, length));
			boolean found = length  < 1.1f;
			if (found) {
				return light;
			}
		}
		return null;
	}
}
