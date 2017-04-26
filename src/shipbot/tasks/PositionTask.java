package shipbot.tasks;

import java.io.IOException;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.mission.Station;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;

public class PositionTask extends Task {
	
	private TaskStatus status;
	private Device device;
	
	private boolean use_static = false;
	
	private int fixed;
	private int rotator;
	private int effector;

	public PositionTask(Device device) {
		this.device = device;
		this.status = TaskStatus.WAITING;
	}

	public PositionTask(int fixed, int rotator, int effector) {
		this.use_static = true;
		this.fixed = fixed;
		this.rotator = rotator;
		this.effector = effector;
	}

	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		
		int rotator_offset = 0;
		int engage_offset = 0;
		if (sys.needsFineAdjustment()) {
			int horiz_offset = sys.getFineAdjustment();
			int[] vals = Config.getAnglesAndOffset(horiz_offset);
			rotator_offset = vals[0];
			engage_offset = vals[1];
		}
		
		if (this.use_static) {
			try {
				DeviceData.writeToHebis(fixed, rotator + rotator_offset, effector + engage_offset);
				// assume hebis react instantly, save the new values
				sys.updateArmPosition(fixed, rotator+rotator_offset);
				status = TaskStatus.COMPLETE;
				return;
			} catch (IOException e) {
				status = TaskStatus.ABORTED;
				MessageLog.printError("POSITION TASK", "IO exception writing to hebis.");
				return;
			}
		}
		
		int target_fixed = 0;
		int target_rotator = 0;
		int target_effector = 0;
		switch (this.device.getStation()) {
			case A:
				break;
			case B: 
				target_effector = -40;
				break;
			case C:
				target_effector = 167;
				break;
			case D:
				break;
			case E:
				target_fixed = -90;
				target_rotator = 30;
				target_effector = -13;
				break;
			case F:
				// varies based on switch!
				break;
			case G:
				target_fixed = -90;
				break;
			default:
				System.out.println("WHAT STATION???");
				break;
		} 
		
		try {
			DeviceData.writeToHebis(target_fixed, target_rotator, target_effector);
			// assume hebis react instantly, save the new values
			sys.updateArmPosition(target_fixed, target_rotator);
			
			effector = target_effector;
			rotator = target_rotator;
			fixed = target_fixed;
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
		return String.format("PositionTask, hebis at %d %d %d [%s]", fixed, rotator, effector, status);
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
