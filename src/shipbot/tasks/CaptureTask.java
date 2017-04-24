package shipbot.tasks;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;

/**
 * Performs the process of capturing, analyzing, and storing information
 * about the device we need to interact with. Saves its info to system
 * state so that it can be used by other tasks.
 * 
 * @author kat
 *
 */
public class CaptureTask extends Task {

	private Device device;
	private TaskStatus status;
	
	/**
	 * Create a new CaptureTask
	 * @param device_type - static String from CVSensing
	 */
	public CaptureTask(Device device) {
		this.device = device;
		this.status = TaskStatus.WAITING;
	}

	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		boolean retval = sys.getNewCapture(this.device);
		if (retval) {
			this.status = TaskStatus.COMPLETE;
		} else {
			this.status = TaskStatus.ABORTED;
		}
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		String format = "Capture Task, device is %s [%s]";
		return String.format(format, this.device.toString(), status);
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
