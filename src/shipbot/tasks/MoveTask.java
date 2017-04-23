package shipbot.tasks;

import java.util.HashMap;
import java.util.Map;

import shipbot.hardware.DriveMotor;
import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;

/**
 * Sends the robot to a particular station, specified by XY coordinates
 * that use upper right corner of the testbed as (0,0) and an orientation
 * is either side-facing (the short end of the testbed) or front-facing
 * (the long end of the testbed). 
 * 
 * @author kat
 *
 */
public class MoveTask extends Task {
	
	private Device device;
	private TaskStatus status = TaskStatus.WAITING;
	private int x;
	private int y;
	private int orient;
	
	public MoveTask(Device device) {
		this.device = device;
		int[] coords = device.getCoordinates();
		this.x = coords[0];
		this.y = coords[1];
		this.orient = device.getOrientation();
	}
	
	/**
	 * Initialize a move task for moving to a particular switch
	 * on a breaker box! 
	 * 
	 * @param switch_no
	 */
	public MoveTask(Device device, Integer switch_no) {
		// load x & y from system state (due to cv adjustments!)
		// keep previous orientation info
		this.orient = device.getOrientation();
		int[] coords = device.getCoordinates();
		if (this.orient == Config.FRONT_FACING) {
			this.x = coords[0] + 10*switch_no;
			this.y = coords[1];
		} else {
			this.x = coords[0];
			this.y = coords[1] + 10*switch_no;
		}
	}

	@Override
	public void executeTask(SystemState sys) {
		status = TaskStatus.ACTIVE;
		
		try {
			// Write new target x,y, and orientation to data file as a command
			Map<String, Integer> data = new HashMap<String, Integer>();
			if (sys.needsBaseAdjustment()) {
				int offset = sys.getBaseAdjustment();
				MessageLog.logDebugMessage("MOVE TASK", String.format("Using offset of <%d> from CV", offset));
				if (this.orient == Config.FRONT_FACING) {
					// we're facing the long side so adjust x
					this.x += offset;
				} else {
					// we're facing the short side so adjust y
					this.y += offset;
				}
			} 
			
			data.put(DriveMotor.X, this.x);
			data.put(DriveMotor.Y, this.y);
			data.put(DriveMotor.ORIENT, this.orient);
			DeviceData.writeArduinoData(Config.DRIVE_MOTOR_ID, data);
			
			// Wait for acknowledgement
			int timeout = 0;
			while (DeviceData.waiting(Config.DRIVE_MOTOR_ID)) {
				if (timeout > Config.MAX_TIMEOUT) {
					MessageLog.printError("MOVE TASK", "Timed out while waiting for ack!");
					throw new Exception();
				}
				
				Thread.sleep(Config.SLEEPTIME);
				timeout++;
			}
			
			// Update virtual representation
			sys.updateLocation(this.x, this.y, this.orient);
		} catch (Exception e) {
			status = TaskStatus.ABORTED;
			return;
		}
		status = TaskStatus.COMPLETE;
		return;
	}

	@Override
	protected TaskStatus getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		String format = "Move Task, X=%d Y=%d Orient=%d [%s]";
		return String.format(format, x, y, orient, status.toString());
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
