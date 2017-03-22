package shipbot.mission;

import shipbot.tasks.Station;

public abstract class Device {
	
	public abstract boolean hasInstruction();
	
	public abstract int getGoalState();
	
	public abstract int[] getCoordinates();
	
	public abstract void setGoalState(int goal_state);
	
	protected abstract Station getStation();
	
}
