package org.tool.astar;

public class Vector {

	public int x;
	public int y;
	
	public Vector(){}
	
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Vector v) {
		this.x = v.x;
		this.y = v.y;
	}
}
