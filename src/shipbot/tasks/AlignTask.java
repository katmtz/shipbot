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
		
		int target_y = 270;
		int target_z = 10;
		switch(this.device.getStation()) {
			case A:
				target_z = 10;
				target_y = 140;
				break;
			case B:
				target_z = 7;
				target_y = 140;
				break;
			case C:
				target_z = 70;
				target_y = 145;
				break;
			case D:
				target_z = 5;
				target_y = 150;
				break;
			case E:
				target_z = 330;
				target_y = 145;
				break;
			case F:
				// depends on switch
				break;
			case G:
				target_z = 360;
				target_y = 80;
				break;
			default:
				System.out.println("STATIONS? ?? ? (align)");
				break;
		}
		
		try {
			DeviceData.writeArduinoData(Config.Z_STEPPER_ID, target_z);
			if (!this.await(Config.Z_STEPPER_ID)) {
				MessageLog.printError("ALIGN TASK", "Z position unconfirmed.");
				this.status = TaskStatus.ABORTED;
				return;
			}
			
			DeviceData.writeArduinoData(Config.Y_STEPPER_ID, target_y);
			if (!this.await(Config.Y_STEPPER_ID)) {
				MessageLog.printError("ALIGN TASK", "Y position unconfirmed.");
				this.status = TaskStatus.ABORTED;
				return;
			}
			
			depth = target_y;
			height = target_z;
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
