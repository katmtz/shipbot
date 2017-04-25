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
		try {
			int timeout = 0;
			while (DeviceData.waiting(motor_id)) {
				if (timeout > Config.MAX_TIMEOUT) {
					MessageLog.printError(this.toString(), "Timed out waiting for z-axis response");
					return false;
				}
				timeout++;
				Thread.sleep(Config.SLEEPTIME);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
