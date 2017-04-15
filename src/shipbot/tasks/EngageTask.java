package shipbot.tasks;

import shipbot.hardware.SystemState;

public class EngageTask extends Task {
	
	private TaskStatus status;
	private int angle;
	
	/**
	 * Create a new EngageTask that rotates the end effector by
	 * specified angle.
	 * 
	 * @param angle
	 */
	public EngageTask(int angle) {
		this.angle = angle;
		this.status = TaskStatus.WAITING;
	}
	
	/**
	 * When called without a particular angle, an EngageTask
	 * is intended to flip a switch, not turn a valve.
	 */
	public EngageTask() {
		this.status = TaskStatus.WAITING;
		this.angle = 180;
	}

	/**
	 * Sends command to end effector HEBI to perform a rotation
	 */
	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		// check if system is already at needed position
		
		// Write angle to effector hebi

		this.status = TaskStatus.COMPLETE;
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}

}
