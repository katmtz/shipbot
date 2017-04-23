package shipbot.tasks;

import java.io.IOException;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;

/**
 * Insert the effector and rotate it as needed.
 * 
 * @author kat
 *
 */
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
		int[] arm_pos = sys.getArmPosition();
		
		// Write angle to effector hebi
		try {
			DeviceData.writeToHebis(true, arm_pos[0], arm_pos[1], this.angle);
			sys.updateArm(arm_pos[0], arm_pos[1], this.angle);
			this.status = TaskStatus.COMPLETE;
		} catch (IOException e) {
			MessageLog.printError("Engage Task", "IO exception while engaging hebis.");
			this.status = TaskStatus.ABORTED;
		}
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
