package shipbot.tasks;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;

public class EngageTask extends Task {
	
	private TaskStatus status;
	private int angle;
	private Device device;
	
	/**
	 * Create a new EngageTask that rotates the end effector by
	 * specified angle.
	 * 
	 * @param angle
	 */
	public EngageTask(Device device) {
		this.device = device;
		this.angle = device.getGoalState();
		this.status = TaskStatus.WAITING;
	}

	/**
	 * Sends command to end effector HEBI to perform a rotation
	 */
	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		// check if system is already at needed position
		
		// Write angle to effector hebi

		this.status = TaskStatus.SKIPPED;
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override 
	public String toString() {
		String format = "EngageTask, rotate %d degrees [%s]";
		return String.format(format, angle, status);
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
