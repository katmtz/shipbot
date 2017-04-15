package shipbot.mission;

import java.util.ArrayList;
import java.util.List;

import shipbot.hardware.CVSensing;
import shipbot.staticlib.MessageLog;
import shipbot.tasks.AlignTask;
import shipbot.tasks.CaptureTask;
import shipbot.tasks.EngageTask;
import shipbot.tasks.MoveTask;
import shipbot.tasks.PositionTask;
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
		this.switches = new ArrayList<Integer>();
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
		this.switches.add(goal_state);
	}

	@Override
	protected Station getStation() {
		return this.station;
	}
	
	private String name() {
		String format = "BreakerBox %s";
		return String.format(format, this.id);
	}

	public List<Task> getTasks() {
		List<Task> tasks = new ArrayList<Task>();
		// Move to station
		tasks.add(new MoveTask(this.station));
		
		// Capture & identify image
		tasks.add(new CaptureTask(CVSensing.BREAKER));
		
		// Raise arm (z stepper, partial y stepper)
		tasks.add(new AlignTask(false));
		// For each switch
		for (Integer sw_no : switches) {
			// Adjust L/R position
			tasks.add(new MoveTask(sw_no));
			// Position HEBI
			tasks.add(new PositionTask(this.station.needsFlip()));
			// Push arm in
			tasks.add(new AlignTask(true));
			// Rotate effector
			tasks.add(new EngageTask());
			// Pull arm back
			tasks.add(new AlignTask(false));
		}
		return tasks;
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
