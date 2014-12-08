package de.druz.roomnesia;

import com.badlogic.gdx.math.Vector2;

public class Door {

	public Vector2 pos;
	public boolean isExplored = false; 

	public Door(Vector2 pos, boolean isExplored) {
		super();
		this.pos = pos;
		this.isExplored = isExplored;
	}
}
