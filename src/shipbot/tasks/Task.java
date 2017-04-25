package shipbot.tasks;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;

/**
 * Represents a task (motion, end effector articulation) that has been scheduled.
 * 
 * @author kat
 *
 */
public abstract class Task {
	
	public abstract void executeTask(SystemState sys);
	
	protected abstract TaskStatus getStatus();
	
	public abstract Device getAssociatedDevice();
	
	protected boolean await(String motor_id) {
		System.out.print("AWAITING ");
		System.out.println(motor_id);
		try {
			int timeout = 0;
			while (DeviceData.waiting(motor_id)) {
				if (timeout > Config.MAX_TIMEOUT) {
					System.out.println("TIMED OUT");
					return false;
				}
				timeout++;
				Thread.sleep(Config.SLEEPTIME);
			}
			return true;
		} catch (Exception e) {
			System.out.println("EXCEPTION");
			return false;
		}
	}
}
