package de.druz.roomnesia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Door {

	public Vector2 pos;
	public Vector2 moveBy;
//	public boolean isExplored = false; 

	public Door(Vector2 pos, Vector2 moveBy) {
		super();
		this.pos = pos;
		this.moveBy = moveBy;
	}

	public boolean isExplored() {
		int x = Math.round(pos.x);
		int y = Math.round(pos.y);;
		boolean isRoomLit = Roomnesia.map.map[x][y].isRoomLit();
		Room room = Roomnesia.map.map[x+Math.round(moveBy.x)][y+Math.round(moveBy.y)];
		if (room != null) {
			Door connectedDoor = room.doorAt(pos);
			if (connectedDoor != null) {
				boolean isConnectedRoomLit = Roomnesia.map.map[Math.round(connectedDoor.pos.x)][Math.round(connectedDoor.pos.y)].isRoomLit();
				return isRoomLit && isConnectedRoomLit;
			} else {
				Gdx.app.log("debug", String.format("No connected door at %s", pos));
			}
		}
		return false;
	}
}
