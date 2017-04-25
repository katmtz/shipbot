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
 * Tells the stepper motors where to position!
 * 
 * @author kat
 *
 */
public class AlignTask extends Task {

	private Device device;
	private TaskStatus status = TaskStatus.WAITING;
	private int depth;
	private int height;
	
	private static String STEPPER_POS = "position";
	
	public AlignTask(Device device) {
		// indicate whether the y axis should be fully out or kept back
		this.device = device;
		this.height = Config.DEVICE_HEIGHT;
		this.depth = Config.DEVICE_DEPTH;
	}
	
	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		
		// Move to predetermined position based on system state
		try {
			if (sys.deviceIsUpward()) {
				// SEND Z+CLEARANCE
				Map<String, Integer> data = new HashMap<String, Integer>();
				int target_z = Config.DEVICE_HEIGHT + Config.CLEARANCE;
				data.put(AlignTask.STEPPER_POS, target_z);
				DeviceData.writeArduinoData(Config.Z_STEPPER_ID, data);
				if (!this.await(Config.Z_STEPPER_ID)) {
					this.status = TaskStatus.ABORTED;
					MessageLog.printError("ALIGN TASK", "Z-axis stepper was unconfirmed!");
					return;
				}
				
				// SEND Y
				int target_y = Config.DEVICE_DEPTH;
				data.put(AlignTask.STEPPER_POS, target_y);
				DeviceData.writeArduinoData(Config.Y_STEPPER_ID, data);
				if (!this.await(Config.Y_STEPPER_ID)) {
					this.status = TaskStatus.ABORTED;
					MessageLog.printError("ALIGN TASK", "Y-axis stepper was unconfirmed!");
					return;
				}
			} else {
				// SEND Y + CLEARANCE
				Map<String, Integer> data = new HashMap<String, Integer>();
				int target_y = Config.DEVICE_DEPTH + Config.CLEARANCE;
				data.put(AlignTask.STEPPER_POS, target_y);
				DeviceData.writeArduinoData(Config.Y_STEPPER_ID, data);
				if (!this.await(Config.Y_STEPPER_ID)) {
					this.status = TaskStatus.ABORTED;
					MessageLog.printError("ALIGN TASK", "Y-axis stepper was unconfirmed!");
					return;
				}
				
				// SEND Z 
				int target_z = Config.DEVICE_HEIGHT;
				data.put(AlignTask.STEPPER_POS, target_z);
				DeviceData.writeArduinoData(Config.Z_STEPPER_ID, data);
				if (!this.await(Config.Z_STEPPER_ID)) {
					this.status = TaskStatus.ABORTED;
					MessageLog.printError("ALIGN TASK", "Z-axis stepper was unconfirmed!");
					return;
				}
			}
			this.status = TaskStatus.COMPLETE;
			return;
		} catch (IOException e) {
			MessageLog.printError("ALIGN TASK", "Exception during align task.");
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
		String format = "Align Task, Y=%d Z=%d [%s]";
		return String.format(format, depth, height, status.toString());
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}
}
