package de.druz.roomnesia;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class RoomnesiaUtils {

	public static Vector2 vecFromAngle(float angleInRad) {
		return new Vector2(MathUtils.cos(angleInRad), MathUtils.sin(angleInRad));
	}
}
