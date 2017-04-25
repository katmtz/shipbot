package shipbot.mission;

import shipbot.staticlib.Config;

public enum Station {

	A (1220,0,false),
	B (915,0,false),
	C (610,0,false),
	D (305,0,false),
	E (0,0,true),
	F (0,305,true),
	G (0,610,false),
	H (0,915,false);
	
	private int x;
	private int y;
	private boolean needs_reach;
	private int orient;
	
	private Station(int x, int y, boolean reach) {
		this.x = x;
		this.y = y;
		this.needs_reach = reach;
		if (this.y != 0) {
			this.orient = Config.SIDE_FACING;
		} else {
			this.orient = Config.FRONT_FACING;
		}
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
	
	public boolean needsRightReach() {
		return ((this.orient == Config.SIDE_FACING) && this.needs_reach);
	}
	
	public boolean needsLeftReach() {
		return ((this.orient == Config.FRONT_FACING) && this.needs_reach);
	}
	
	/**
	 * Check the orientation the robot needs to be facing.
	 * True for stations F, G, and H
	 * 
	 * @return true if the station is along the front end
	 */
	public int getOrientation() {
		return this.orient;
	}
}
