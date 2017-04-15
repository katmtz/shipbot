package shipbot.tasks;

import java.util.HashMap;
import java.util.Map;

import shipbot.hardware.DriveMotor;
import shipbot.hardware.SystemState;
import shipbot.mission.Station;
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
	
	private TaskStatus status = TaskStatus.WAITING;
	private int x;
	private int y;
	private int orient;
	
	public MoveTask(Station s) {
		int[] coords = s.getCoordinates();
		this.x = coords[0];
		this.y = coords[1];
		this.orient = s.getOrientation();
	}
	
	/**
	 * Initialize a move task for moving to a particular switch
	 * on a breaker box! 
	 * 
	 * @param switch_no
	 */
	public MoveTask(Integer switch_no) {
		// load x & y from system state (due to cv adjustments!)
		// keep previous orientation info
		this.x = 666;
		this.y = 420;
		this.orient = 1;
	}

	@Override
	public void executeTask(SystemState sys) {
		status = TaskStatus.ACTIVE;
		
		try {
			// Write new target x,y, and orientation to data file as a command
			Map<String, Integer> data = new HashMap<String, Integer>();
			data.put(DriveMotor.X, this.x);
			data.put(DriveMotor.Y, this.y);
			data.put(DriveMotor.ORIENT, this.orient);
			DeviceData.writeMotorData(Config.DRIVE_MOTOR_ID, data);
			
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
			sys.updateLocation(x, y, orient);
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

}
