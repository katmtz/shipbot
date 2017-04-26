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
 * Extract the effector from testbed device and reset HEBI motors as needed.
 * 
 * @author kat
 *
 */
public class DisengageTask extends Task {

	private TaskStatus status;
	private Device device;
	
	private static String STEPPER_POS = "position";
	
	public DisengageTask(Device device) {
		this.device = device;
		this.status = TaskStatus.WAITING;
	}

	@Override
	public void executeTask(SystemState sys) {
		try {
			Map<String, Integer> data = new HashMap<String, Integer>();
			
			// EXTRACT EFFECTOR
			if (this.device.getDeviceDirection() == Config.ORIENT_UP) {
				MessageLog.logDebugMessage("DISENGAGE TASK", "Extracting effector vertically.");
				int target_z = Config.DEVICE_HEIGHT + Config.CLEARANCE;
				data.put(STEPPER_POS, target_z);
				DeviceData.writeArduinoData(Config.Z_STEPPER_ID, data);
				if (!this.await(Config.Z_STEPPER_ID)) {
					this.status = TaskStatus.ABORTED;
					MessageLog.printError("DISENGAGE TASK", "Z-axis stepper extract was unconfirmed!");
					return;
				}
			} else {
				MessageLog.logDebugMessage("DISENGAGE TASK", "Extracting effector horizontally.");
				int target_y = Config.DEVICE_DEPTH + Config.CLEARANCE;
				data.put(STEPPER_POS, target_y);
				DeviceData.writeArduinoData(Config.Y_STEPPER_ID, data);
				if (!this.await(Config.Y_STEPPER_ID)) {
					this.status = TaskStatus.ABORTED;
					MessageLog.printError("DISENGAGE TASK", "Y-axis stepper extract was unconfirmed!");
					return;
				}
			}
			
			// RETURN AXES TO TRAVEL POSITION
			data.put(STEPPER_POS, Config.Y_TRAVELLING);
			DeviceData.writeArduinoData(Config.Y_STEPPER_ID, data);
			if (!this.await(Config.Y_STEPPER_ID)) {
				this.status = TaskStatus.ABORTED;
				MessageLog.printError("DISENGAGE TASK", "Y-axis stepper reset was unconfirmed!");
				return;
			}
			data.put(STEPPER_POS, Config.Z_TRAVELLING);
			DeviceData.writeArduinoData(Config.Z_STEPPER_ID, data);
			if (!this.await(Config.Z_STEPPER_ID)) {
				this.status = TaskStatus.ABORTED;
				MessageLog.printError("DISENGAGE TASK", "Z-axis stepper reset was unconfirmed!");
				return;
			}
			
			// RETURN HEBIS TO TRAVEL POSITION
			DeviceData.writeToHebis(0, 0, 0);
			sys.updateArmPosition(0, 0);
			status = TaskStatus.COMPLETE;
		} catch (IOException e) {
			MessageLog.printError("DISENGAGE TASK", "Exception while disengaging effector.");
			status = TaskStatus.ABORTED;
			return;
		}
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
