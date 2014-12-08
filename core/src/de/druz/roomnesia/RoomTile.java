package de.druz.roomnesia;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Filter;

public class RoomTile {

	public String name;
	public TextureRegion texReg;
	public ChainShape shape;
	public Filter filterData;
	public MyUserData userData;
	
	public RoomTile(String name, Filter filter, MyUserData userdata, Vector2... vertices) {
		super();
		this.name = name;
		this.texReg = Assets.tileMap.get(name);
		this.filterData = filter;
		this.userData = userdata;
		if (vertices != null && vertices.length > 0) {
			shape = new ChainShape();
			shape.createLoop(vertices);
		}
	}

	@Override
	public String toString() {
		return "RoomTile [name=" + name + "]";
	}
	
}
