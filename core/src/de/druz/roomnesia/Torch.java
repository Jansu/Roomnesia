package de.druz.roomnesia;

import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Torch {

	private static final float RADIUS = 0.25f;
	public boolean isLit = false;
	public PointLight light;
	public Vector2 pos;
	
	public Torch(World world, Room room, Vector2 pos) {
		super();
		this.pos = pos;

		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		Body body = world.createBody(chainBodyDef);
		body.setTransform(pos.x, pos.y, 0);
		Shape shape = new CircleShape();
		shape.setRadius(RADIUS);
		Fixture fix = body.createFixture(shape, 0);
		room.bodies.add(body);
		room.lights.add(this);
	}

	public void light() {
		if (!isLit) {
			light = new PointLight(Roomnesia.rayHandler, Roomnesia.RAYS_NUM, null, Roomnesia.LIGHT_DISTANCE*1.5f, pos.x, pos.y);
//			light.attachToBody(body, RADIUS, RADIUS);
			light.setColor(0.9f, 0.25f, 0.01f, 1f);
			light.setSoftnessLength(0.5f);
			
			isLit = true;
		}
	}
	
	public void render(SpriteBatch batch) {
		if (isLit) {			
			light.setColor(MathUtils.random(0.9f,0.95f), MathUtils.random(0.2f,0.25f), MathUtils.random(0.05f,0.1f), 1f);
			batch.draw(Assets.litwoodTex, pos.x-RADIUS, pos.y-RADIUS, RADIUS, RADIUS, RADIUS*2, RADIUS*2, 1, 1, 0f);
		} else {
			batch.draw(Assets.woodTex, pos.x-RADIUS, pos.y-RADIUS, RADIUS, RADIUS, RADIUS*2, RADIUS*2, 1, 1, 0f);
		}
	}
	
	public void dispose() {
		Gdx.app.log("debug", String.format("disposing light", light));
		if (light != null) {
			light.remove();
			light.dispose();
		}
//		Roomnesia.world.destroyBody(body);
	}
}
