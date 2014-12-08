package de.druz.roomnesia;

import com.badlogic.gdx.physics.box2d.Filter;

public class MyFilter extends Filter {
	public MyFilter(short groupIndex) {
		this.groupIndex = groupIndex;  
	}
}
