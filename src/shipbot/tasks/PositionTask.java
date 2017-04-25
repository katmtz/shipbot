package shipbot.tasks;

import java.io.IOException;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;

public class PositionTask extends Task {
	
	private TaskStatus status;
	private Device device;

	public PositionTask(Device device) {
		this.device = device;
		this.status = TaskStatus.WAITING;
	}

	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		
		// CHECK ORIENTATION OF TESTBED DEVICE
		int target_fixed;
		if (sys.deviceIsUpward()) {
			// POSITION FIXED HEBI DOWN
			target_fixed = -90;
		} else {
			target_fixed = 0;
		}
		
		// CHECK IF WE NEED A FINE OFFSET
		int target_rotator = 0;
		int height_offset = 0;
		// HELLA MATH DONE AT MIDNIGHT????
		if (this.device.needsLeftReach()) {
			// Push arm to the left and boost height by length of the rotator
			target_rotator = -90;
			height_offset = Config.ROTATOR_LENGTH;
		} else if (this.device.needsRightReach()) {
			// push arm to the right and boost height by length of rotator
			target_rotator = 90;
			height_offset = Config.ROTATOR_LENGTH;
		} else {
			if (sys.needsFineAdjustment()) {
				// TODO: GOOD GOD THIS TRIG IS BAD HELP ME........ ;__;
				int X = sys.getFineAdjustment();
				int L = Config.ROTATOR_LENGTH;
				// A = arcsin( L / (sqrt(X^2 + L^2)))
				double ang_A = Math.toDegrees(Math.asin((L / Math.sqrt(Math.pow(X, 2) + Math.pow(L, 2)))));
				double ang_B = 90 - ang_A;
				double Y = X * Math.tan(Math.toRadians(ang_B));
				height_offset = -1 * (int) Y;
				
				// D = sqrt(X^2 + Y^2)
				double D = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
				// ang C = arccos( (2L^2 - D^2)/(2L^4) )
				double ang_C = Math.acos((2*Math.pow(L, 2) - Math.pow(D, 2)) / (2*Math.pow(L, 4)));
				target_rotator = (int) ang_C;
			}
		}
		sys.storeHeightOffset(height_offset);
		try {
			DeviceData.writeToHebis(target_fixed, target_rotator, 0);
			// assume hebis react instantly, save the new values
			sys.updateArmPosition(target_fixed, target_rotator);
			status = TaskStatus.COMPLETE;
			return;
		} catch (IOException e) {
			status = TaskStatus.ABORTED;
			MessageLog.printError("POSITION TASK", "IO exception writing to hebis.");
			return;
		}
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		return String.format("PositionTask [%s]", status);
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
