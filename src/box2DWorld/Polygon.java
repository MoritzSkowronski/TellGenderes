package box2DWorld;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import glass.Fragment;
import processing.core.PApplet;
import processing.core.PVector;
import shiffman.box2d.Box2DProcessing;

public class Polygon {

	private Body body;
	private Vec2 centroid;
	private Vec2[] realWorldCorners;
	private Vec2[] corners;
	private float widthheight;

	private float mass;
	private Box2DProcessing box2d;
	private boolean isDead;
	private PApplet p;

	public Polygon(PApplet p, Box2DProcessing box2d, Fragment fragment, PVector velocity,
			float angularVelocity) {

		this.box2d = box2d;
		this.p = p;

		realWorldCorners = new Vec2[fragment.getPoints().size()];
		for (int i = realWorldCorners.length - 1; i >= 0; i--) {
			PVector temp = fragment.getPoints().get(i);
			realWorldCorners[0 + ((realWorldCorners.length - 1) - i)] = new Vec2(temp.x, temp.y);
		}

		this.widthheight = (fragment.getMaxX() - fragment.getMinX())
				+ (fragment.getMaxY() - fragment.getMinY());

		corners = new Vec2[realWorldCorners.length];
		mass = fragment.getArea();
		centroid = new Vec2(fragment.getCentroid().x, fragment.getCentroid().y);

		corners = calculateCornersFromRealWorld(centroid, realWorldCorners);

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(box2d.coordPixelsToWorld(centroid));
		body = box2d.createBody(bd);

		PolygonShape ps = new PolygonShape();
		ps.set(corners, corners.length);

		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 1;
		fd.friction = 0.3f;
		fd.restitution = 0.5f;

		body.createFixture(fd);

		body.setLinearVelocity(
				new Vec2(p.random(-velocity.x, velocity.x), p.random(-velocity.y, 0)));
		body.setAngularVelocity(p.random(-angularVelocity, angularVelocity));
	}

	private Vec2[] calculateCornersFromRealWorld(Vec2 center, Vec2[] corners) {
		Vec2[] tempCorners = new Vec2[corners.length];
		for (int i = 0; i < tempCorners.length; i++) {
			tempCorners[i] = corners[tempCorners.length - 1 - i];
			tempCorners[i].subLocal(center);
			tempCorners[i] = box2d.vectorPixelsToWorld(tempCorners[i]);
		}
		return tempCorners;
	}

	public void display() {
		Vec2 pos = box2d.getBodyPixelCoord(body);
		float a = body.getAngle();

		Fixture f = body.getFixtureList();
		PolygonShape ps = (PolygonShape) f.getShape();

		p.rectMode(PApplet.CENTER);
		p.pushMatrix();
		p.translate(pos.x, pos.y);
		p.rotate(-a);
		p.fill(255);
		p.stroke(0);
		p.beginShape();
		// For every vertex, convert to pixel vector
		for (int i = 0; i < ps.getVertexCount(); i++) {
			Vec2 v = box2d.vectorWorldToPixels(ps.getVertex(i));
			p.vertex(v.x, v.y);
		}
		p.endShape(PApplet.CLOSE);
		p.popMatrix();
		testIfDead();
	}

	public boolean isDead() {

		return isDead;
	}
	
	public void kill(){
		
		box2d.destroyBody(body);
		isDead = true;
	}

	private void testIfDead() {

		Vec2 vector = box2d.getBodyPixelCoord(body);

		if (vector.y > p.height + widthheight) {
			box2d.destroyBody(body);
			isDead = true;
		}
	}
}
