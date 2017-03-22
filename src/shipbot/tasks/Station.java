package shipbot.tasks;

public enum Station {

	A (0,0),
	B (1,0),
	C (2,0),
	D (3,0),
	E (3,1),
	F (3,2),
	G (3,3),
	H (3,4);
	
	private int x;
	private int y;
	
	private Station(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int[] getCoordinates() {
		int[] coords = { this.x, this.y };
		return coords;
	}
}
