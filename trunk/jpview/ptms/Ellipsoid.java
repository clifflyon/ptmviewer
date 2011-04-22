/*
 * Sphere.java
 *
 * Created on September 12, 2004, 3:32 PM
 */

package jpview.ptms;

import jpview.graphics.EnvironmentMap;
import jpview.graphics.Vec3f;

/**
 * 
 * @author clyon
 */
public class Ellipsoid implements PTM {

	private int width = 0;

	private int height = 0;

	private Vec3f[][] normals = null;

	protected EnvironmentMap em;

	protected int x(int i) {
		return i % width;
	}

	protected int y(int i) {
		return i / height;
	}

	/** Creates a new instance of Sphere */
	public Ellipsoid(int w, int h) {
		width = w;
		height = h;
		computeNormals();
	}

	public int blue(int i) {
		return 0;
	}

	public void computeNormals() {
		/** assume ellipsoid is centered in with x height */
		normals = new Vec3f[width][height];
		for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				float u = ((float) (w - width / 2)) / (width / 2);
				float v = -((float) (h - height / 2)) / (height / 2);
				float tmp = u * u + v * v;
				if (tmp > 1) {
					normals[w][h] = new Vec3f(0, 0, 0); /* hack for black */
				} else {
					normals[w][h] = new Vec3f(u, v, (float) Math.sqrt(1 - tmp));
				}
			}
		}
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int green(int i) {
		return 0;
	}

	public Vec3f normal(int i) {
		int x = this.x(i);
		int y = this.y(i);
		return normals[x][y];
	}

	public Vec3f normal(int x, int y) {
		return normals[x][y];
	}

	public int red(int i) {
		return 0;
	}

	public void setEnvironmentMap(EnvironmentMap map) {
		em = map;
	}

	public EnvironmentMap getEnvironmentMap() {
		return em;
	}

	public int getType() {
		return PTM.PRIMITIVE;
	}

	public Vec3f[] getNormals() {
		return null;
	}
	
	public void resize(int w, int h)
	{
		// TODO
	}

	public void setKSpec(float f) {
	}

	public void setKDiff(float f) {
	}

	public void setExp(int i) {
	}

	public int getExp() {
		return 0;
	}

	public float getKDiff() {
		return 0f;
	}

	public float getKSpec() {
		return 0f;
	}

	public void release() {
	}

	public float getDGain() {
		return 0f;
	}

	public void setDGain(float f) {
	}

	public int[] getEnvironmentMapCache() {
		return null;
	}

	public float getLuminance() {
		return 0;
	}

	public void setLuminance(float f) {
		;
	}

	public void recache() {
	}

	public int[] getEnvironmentMapMap() {
		return null;
	}

	public boolean useEnv() {
		return useEnv;
	}

	public void useEnv(boolean b) {
		useEnv = b;
	}

	public int getZ() {
		return 0;
	}

	public void setZ(int z) {
	}

	private boolean useEnv = false;
}
