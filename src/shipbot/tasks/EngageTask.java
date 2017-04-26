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
	private int z_engage;
	private int angle;
	private Device device;
	
	private boolean use_static = false;
	
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
		this.z_engage = 0;
		this.status = TaskStatus.WAITING;
	}

	public EngageTask(int i, int j) {
		this.use_static = true;
		this.z_engage = i;
		this.angle = j;
	}

	/**
	 * Sends command to end effector HEBI to perform a rotation
	 */
	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;

		if (this.use_static) {
			try {
				DeviceData.writeArduinoData(Config.Z_STEPPER_ID, z_engage);
				if (!this.await(Config.Z_STEPPER_ID)) {
					this.status = TaskStatus.ABORTED;
					MessageLog.printError("ENGAGE TASK", "Z-axis engage was unconfirmed!");
					return;
				}
				
				int[] arm_pos = sys.getArmPosition();
				DeviceData.writeToHebis(arm_pos[0], arm_pos[1], angle);
				
				this.status = TaskStatus.COMPLETE;
				return;
			} catch (IOException e) {
				MessageLog.printError("ENGAGE TASK", "Exception during engage task.");
				this.status = TaskStatus.ABORTED;
				return;
			}
		}
		
		this.angle = sys.getEngagement(this.angle);
		
		int z_target = 0;
		int effector_angle = 0;
		
		switch(this.device.getStation()) {
			case A:
				effector_angle = this.angle;
				break;
			case B:
				effector_angle = 60;
				break;
			case C:
				z_target = 42;
				effector_angle = 167;
				break;
			case D:
				effector_angle = this.angle;
				break;
			case E: 
				z_target = 300;
				effector_angle = (-13) + this.angle;
				break;
			case F:
				// depends on switch!!
				// SHOULD NEVER GET HERE!
				break;
			case G:
				z_target = 330;
				effector_angle = 160;
				break;
			default:
				System.out.println("WHAT STATIONS???? (engage)");
				break;
		}
		
		try {
			DeviceData.writeArduinoData(Config.Z_STEPPER_ID, z_target);
			if (!this.await(Config.Z_STEPPER_ID)) {
				this.status = TaskStatus.ABORTED;
				MessageLog.printError("ENGAGE TASK", "Z-axis engage was unconfirmed!");
				return;
			}
			
			int[] arm_pos = sys.getArmPosition();
			DeviceData.writeToHebis(arm_pos[0], arm_pos[1], effector_angle);
			
			z_engage = z_target;
			angle = effector_angle;
			this.status = TaskStatus.COMPLETE;
			return;
		} catch (IOException e) {
			MessageLog.printError("ENGAGE TASK", "Exception during engage task.");
			this.status = TaskStatus.ABORTED;
			return;
		}
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override 
	public String toString() {
		String format = "EngageTask, Z:%d THETA:%s [%s]";
		return String.format(format, z_engage, angle, status);
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
