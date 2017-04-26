package shipbot.mission;

import shipbot.staticlib.Config;

public enum Station {

	A (1156,0,false),
	B (851,0,false),
	C (546,0,false),
	D (241,0,false),
	E (0,0,false),
	F (0,0,true),
	G (0,245,true),
	H (0,550,true);
	
	private int x;
	private int y;
	private boolean needs_reach;
	private int orient;
	
	private Station(int x, int y, boolean orient) {
		this.x = x;
		this.y = y;
		if (orient) {
			this.orient = Config.SIDE_FACING;
		} else {
			this.orient = Config.FRONT_FACING;
		}
		if (this.y == 0 && this.x == 0) {
			this.needs_reach = true;
		} else {
			this.needs_reach = false;
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
