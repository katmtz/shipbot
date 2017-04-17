package shipbot.tasks;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;

/**
 * Represents a task (motion, end effector articulation) that has been scheduled.
 * 
 * @author kat
 *
 */
public abstract class Task {
	
	public abstract void executeTask(SystemState sys);
	
	protected abstract TaskStatus getStatus();
	
	public abstract Device getAssociatedDevice();
	
}
