package shipbot.mission;

import shipbot.staticlib.Config;

public enum Station {

	A (1080,0,false),
	B (775,0,false),
	C (470,0,false),
	D (165,0,false),
	E (0,0,true),
	F (0,165,true),
	G (0,470,false),
	H (0,775,false);
	
	private int x;
	private int y;
	private boolean needs_flip;
	
	private Station(int x, int y, boolean flip) {
		this.x = x;
		this.y = y;
		this.needs_flip = flip;
	}
	
	/**
	 * Absolute center of the station, with the corner of the
	 * testbed as (0,0).
	 * 
	 * @return
	 */
	public int[] getCoordinates() {
		int[] coords = { this.x, this.y };
		return coords;
	}
	
	public boolean needsFlip() {
		return this.needs_flip;
	}
	
	/**
	 * Check the orientation the robot needs to be facing.
	 * True for stations F, G, and H
	 * 
	 * @return true if the station is along the front end
	 */
	public int getOrientation() {
		if (this.y != 0) {
			return Config.SIDE_FACING;
		} else {
			return Config.FRONT_FACING;
		}
	}
}
