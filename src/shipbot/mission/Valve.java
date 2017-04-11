package shipbot.mission;

import java.util.List;

import shipbot.tasks.Task;

/**
 * Rotational devices, including the small and large circular valves. 
 * They should have an angular rotational goal, and have relatively the same task breakdown.
 * 
 * @author kat
 *
 */
public class Valve extends Device {
	
	private String id;
	private Station station;
	private int goal_state = -1;
	
	public Valve(Station s, String id) {
		this.station = s;
		this.id = id;
	}

	@Override
	public int[] getCoordinates() {
		return this.station.getCoordinates();
	}

	@Override
	public void addGoalState(int goal_state) {
		this.goal_state = goal_state;
	}

	@Override
	protected Station getStation() {
		return station;
	}

	@Override
	public int[] getGoalState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getPosition() {
		int[] position = { 5, 5 };
		return position;
	}

	@Override
	public String getDescription() {
		String format = "Device: %s @ Station %s -- Rotate to [%d] degrees.";
		return String.format(format, this.id, station.toString(), this.goal_state);
	}

}
