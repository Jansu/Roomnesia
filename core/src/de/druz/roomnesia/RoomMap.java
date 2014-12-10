package de.druz.roomnesia;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class RoomMap {

	public Room[][] map = new Room[Roomnesia.TILES_NUM_WIDTH][Roomnesia.TILES_NUM_HEIGHT];
	public List<Room> rooms = new ArrayList<Room>();
	
	public Room getRoomForPos(Vector2 position) {
		int x = MathUtils.floor(position.x);
		int y = MathUtils.floor(position.y);
		return map[x][y];
	}
	
	public int unexpoloredDoorCount() {
		int count = 0;
		for (Room room : rooms) {
			count += room.unexploredDoorsCount();
		}
		return count;
	}

	public boolean isAllRoomsLit() {
		for (Room room : rooms) {
			if (!room.isRoomLit()) {
				return false;
			}
		}
		return true;
	}
}
