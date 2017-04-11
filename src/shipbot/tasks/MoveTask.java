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
	
	public MoveTask(int[] args) {
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
	public void executeTask(SystemState sys) {
		status = TaskStatus.ACTIVE;
		try {
			// Write x & y offsets to correct motor files
			Map<String, Integer> data = new HashMap<String, Integer>();
			data.put(DriveMotor.X, this.x);
			data.put(DriveMotor.Y, this.y);
			DeviceData.writeMotorData(Config.DRIVE_MOTOR_ID, data);
			
			// Wait for acknowledgement
			int timeout = 0;
			while (DeviceData.waiting(Config.DRIVE_MOTOR_ID)) {
				if (timeout > Config.MAX_TIMEOUT) {
					System.out.println(">> MOVE TASK TIMEOUT");
					throw new Exception();
				}
				
				Thread.sleep(Config.SLEEPTIME);
				timeout++;
			}
			
			// Update virtual representation
			sys.updateLocation(x, y);
			System.out.println(String.format("> wrote %d %d as new location coords", x,y));
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
		String format = "Move Task, X=%d Y=%d [%s]";
		return String.format(format, x,y, status.toString());
	}

}
