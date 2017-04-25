package shipbot.tasks;

import java.util.HashMap;
import java.util.Map;

import shipbot.hardware.StepperMotor;
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
				// Move Z+clearance, then y
				int target_z = Config.DEVICE_HEIGHT + Config.CLEARANCE;
				
			} else {
				// Move Y-clearance, then z
			}
		} catch (Exception e) {
			this.status = TaskStatus.ABORTED;
			return;
		}
		this.status = TaskStatus.COMPLETE;
		return;
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
