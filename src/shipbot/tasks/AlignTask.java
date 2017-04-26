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
	
	private boolean use_static = false;
	
	private static String STEPPER_POS = "position";
	
	public AlignTask(Device device) {
		// indicate whether the y axis should be fully out or kept back
		this.device = device;
		this.height = Config.DEVICE_HEIGHT;
		this.depth = Config.DEVICE_DEPTH;
	}
	
	public AlignTask(int z, int y) {
		this.use_static = true;
		this.height = z;
		this.depth = y;
	}

	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		
		int z_offset = 0;
		int y_offset = 0;
		if (sys.needsFineAdjustment()) {
			int horiz_offset = sys.getFineAdjustment();
			int[] vals = Config.getAnglesAndOffset(horiz_offset);
			if (this.device.getDeviceDirection() == Config.ORIENT_UP) {
				y_offset = -1 * vals[2];
			} else {
				z_offset = vals[2];
			}
		}
		
		if (this.use_static) {
			this.depth += y_offset;
			this.height += z_offset;
			try {
				DeviceData.writeArduinoData(Config.Z_STEPPER_ID, this.height);
				if (!this.await(Config.Z_STEPPER_ID)) {
					MessageLog.printError("ALIGN TASK", "Z position unconfirmed.");
					this.status = TaskStatus.ABORTED;
					return;
				}
				
				DeviceData.writeArduinoData(Config.Y_STEPPER_ID, this.depth);
				if (!this.await(Config.Y_STEPPER_ID)) {
					MessageLog.printError("ALIGN TASK", "Y position unconfirmed.");
					this.status = TaskStatus.ABORTED;
					return;
				}
			
				this.status = TaskStatus.COMPLETE;
				return;
			} catch (IOException e) {
				MessageLog.printError("ALIGN TASK", "Exception during align task.");
				this.status = TaskStatus.ABORTED;
				return;
			}
		}
		
		int target_y = 270 + y_offset;
		int target_z = 10 + z_offset;
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
				target_z = 335;
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
