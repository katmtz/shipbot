package shipbot.tasks;

import java.util.HashMap;
import java.util.Map;

import shipbot.hardware.DriveMotor;
import shipbot.hardware.SystemState;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;

/**
 * Task that specifies directions to the drive-train motors.
 * 
 * @author kat
 *
 */
public class MoveTask extends Task {
	
	private Task parent;
	private TaskStatus status = TaskStatus.WAITING;
	private int x;
	private int y;
	private SystemState sys;
	
	private final String status_format = "[ MOVE_TASK ] (%s) %s";
	
	public MoveTask(SystemState sys, int[] args) {
		this.sys = sys;
		this.x = args[0];
		this.y = args[1];
		parent = this;
	}
	
	public MoveTask(int x, int y) {
		this.x = x;
		this.y = y;
		parent = this;
	}
	
	public MoveTask(Task parent, int x, int y) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	@Override
	public void executeTask() {
		status = TaskStatus.ACTIVE;
		// Write x & y offsets to correct motor files
		Map<String, Integer> data = new HashMap<String, Integer>();
		data.put(DriveMotor.X, this.x);
		data.put(DriveMotor.Y, this.y);
		DeviceData.writeMotorData(Config.DRIVE_MOTOR_ID, data);
		
		// Wait for acknowledgement
		int timer = 0;
		while (status == TaskStatus.ACTIVE) {
			int response_x = sys.getXPosition();
			int response_y = sys.getYPosition();
			if (response_x == x && response_y == y) {
				status = TaskStatus.COMPLETE;
			}
			if (timer > 100) {
				// Timeout?
				status = TaskStatus.ABORTED;
			}
			timer++;
		}
	}

	@Override
	protected TaskStatus getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		String format = "Move Task, X=%d Y=%d";
		return String.format(format, x,y);
	}

}
