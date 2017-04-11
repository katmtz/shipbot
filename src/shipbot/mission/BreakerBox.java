package shipbot.mission;

import java.util.ArrayList;
import java.util.List;

import shipbot.staticlib.MessageLog;
import shipbot.tasks.Task;

/**
 * Represents a discrete breaker box, with 3 switches on the board, and the switches 
 * that need to be active on them. Tasks may include any number of the 3 switches being
 * engaged, but at least one should be specified if the object exists. Switches will
 * always need to be switched to off if they aren't currently off.
 * 
 * @author kat
 *
 */
public class BreakerBox extends Device {
	
	/** id: specifies whether breaker box A or B */
	private String id;
	/** switches: the list of switches (1, 2, or 3) to change */
	private List<Integer> switches;
	/** station: the station (and location) of this device */
	private Station station;
	
	public BreakerBox(Station s, String id) {
		this.station = s;
		this.id = id;
		switches = new ArrayList<Integer>();
	}

	@Override
	public int[] getCoordinates() {
		return this.station.getCoordinates();
	}

	@Override
	public int[] getGoalState() {
		int[] goals = new int[switches.size()];
		int i = 0;
		for (int n : switches) {
			goals[i] = n;
			i++;
		}
		return goals;
	}

	/**
	 * Provide a new goal state for this device - for breakerboxes
	 * this should be a switch id (1-3). If it's not a switch id,
	 * we log an error and don't handle it.
	 * 
	 * @param goal_state the switch id specified by the mission
	 */
	@Override
	public void addGoalState(int goal_state) {
		if (goal_state < 1 || goal_state > 3) {
			MessageLog.printError(this.name(), "Goal state not in expected range!");
			return;
		}
		switches.add(goal_state);
	}

	@Override
	protected Station getStation() {
		return this.station;
	}
	
	private String name() {
		String format = "BreakerBox %s";
		return String.format(format, this.id);
	}

	@Override
	public int[] getPosition() {
		int[] position = { 5, 5 };
		return position;
	}

	@Override
	public String getDescription() {
		String format = "Device: %s @ Station %s -- Goals:";
		StringBuilder sb = new StringBuilder(String.format(format, this.name(), station.name()));
		String append_format = " switch %d";
		for (Integer sw : switches) {
			sb.append(String.format(append_format, sw));
		}
		return sb.toString();
	}
}
