package shipbot.tasks;

import shipbot.hardware.SystemState;

/**
 * Performs the process of capturing, analyzing, and storing information
 * about the device we need to interact with. Saves its info to system
 * state so that it can be used by other tasks.
 * 
 * @author kat
 *
 */
public class CaptureTask extends Task {

	private String device_type;
	private TaskStatus status;
	
	/**
	 * Create a new CaptureTask
	 * @param device_type - static String from CVSensing
	 */
	public CaptureTask(String device_type) {
		this.device_type = device_type;
		this.status = TaskStatus.WAITING;
	}

	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		
		// Take photo & process data
		sys.getNewCapture(this.device_type);
		
		this.status = TaskStatus.COMPLETE;
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		String format = "Capture Task, device is %s [%s]";
		return String.format(format, device_type, status);
	}

}
