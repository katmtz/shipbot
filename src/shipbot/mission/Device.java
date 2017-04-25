package shipbot.mission;

import java.util.List;

import shipbot.tasks.Task;

/** 
 * Abstract representation of goal states for manipulated devices as specified by 
 * the mission file. This object doesn't store information about *how* to complete
 * the manipulation, only the location of the device and what it should look like 
 * by the end of the mission.
 * 
 * @author kat
 *
 */
public abstract class Device {
	
	public abstract void addGoalState(int goal_state);
	
	public abstract int getGoalState();
	
	public abstract List<Task> getTasks();
	
	public abstract String getDescription();
	
	public int[] getCoordinates() {
		return this.getStation().getCoordinates();
	}
	
	public int getOrientation() {
		return this.getStation().getOrientation();
	}
	
	public boolean needsLeftReach() {
		return this.getStation().needsLeftReach();
	}
	
	public boolean needsRightReach() {
		return this.getStation().needsRightReach();
	}
	
	@Override
	public String toString() {
		String format = "%s at station %s";
		return String.format(format, this.id(), this.getStation().toString());
	}
	
	protected abstract String id();
	
	protected abstract Station getStation();

	public abstract int getCVId();
}
