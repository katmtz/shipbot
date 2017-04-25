package shipbot.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;

/**
 * Insert the effector and rotate it as needed.
 * 
 * @author kat
 */
public class EngageTask extends Task {
	
	private TaskStatus status;
	private int angle;
	private Device device;
	
	private static String STEPPER_POS = "position";
	
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

		if (sys.needsEngagement(this.angle)) {
			int target_angle = sys.getEngagement(this.angle);
			try {
				if (sys.deviceIsUpward()) {
					// WE NEED TO MOVE DOWN TO ENGAGE
					Map<String, Integer> data = new HashMap<String, Integer>();
					int target_y = Config.DEVICE_DEPTH;
					data.put(STEPPER_POS, target_y);
					DeviceData.writeArduinoData(Config.Y_STEPPER_ID, data);
					if (!this.await(Config.Y_STEPPER_ID)) {
						this.status = TaskStatus.ABORTED;
						MessageLog.printError("ENGAGE TASK", "Y-axis stepper engage was unconfirmed!");
						return;
					}
				}
				
				// EFFECTOR IS ENGAGED, ROTATE
				int[] arm_pos = sys.getArmPosition();
				DeviceData.writeToHebis(arm_pos[0], arm_pos[1], target_angle);
				// note: assume hebi commands work instantaneously.
				this.status = TaskStatus.COMPLETE;
				return;
			} catch (IOException e) {
				this.status = TaskStatus.ABORTED;
				MessageLog.printError("ENGAGE TASK", "IO exception while engaging effector.");
				return;
			}
			
		} else {
			MessageLog.logTaskStatus("Device already at desired state!");
			this.status = TaskStatus.COMPLETE;
			return;
		}
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override 
	public String toString() {
		String format = "EngageTask [%s]";
		return String.format(format, status);
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
