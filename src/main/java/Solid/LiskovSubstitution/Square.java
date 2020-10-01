package Solid.LiskovSubstitution;

public class Square implements Shape {

	public int side;
	
	public Square(int side) {
		this.side = side;
	}

	public void setSide(int side) {
		this.side = side;
	}

	@Override
	public int computeArea() {
		return this.side * this.side;
	}
}
