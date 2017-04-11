package shipbot.mission;

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
		
	public abstract int[] getCoordinates();
	
	public abstract int[] getPosition();
	
	public abstract void addGoalState(int goal_state);
	
	public abstract int[] getGoalState();
	
	protected abstract Station getStation();
	
	public abstract String getDescription();
	
}
