package shipbot.tasks;

import java.util.HashMap;
import java.util.Map;

import shipbot.hardware.StepperMotor;
import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;

/**
 * Tells the stepper motors where to position!
 * 
 * @author kat
 *
 */
public class AlignTask extends Task {

	private Device device;
	private TaskStatus status = TaskStatus.WAITING;
	private int depth;
	private int height;
	
	public AlignTask(Device device) {
		// indicate whether the y axis should be fully out or kept back
		this.device = device;
		this.height = Config.DEVICE_HEIGHT;
		this.depth = Config.DEVICE_DEPTH;
	}
	
	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		
		// Move to predetermined position based on system state
		try {
			Map<String, Integer> data = new HashMap<String,Integer>();
			data.put(StepperMotor.POS, this.depth);
			DeviceData.writeArduinoData(Config.Y_STEPPER_ID, data);
			data.put(StepperMotor.POS, this.height);
			DeviceData.writeArduinoData(Config.Z_STEPPER_ID, data);
			
			// hang, waiting for arduino to acknowledge & complete task
			int timeout = 0;
			while (DeviceData.waiting(Config.Y_STEPPER_ID) && DeviceData.waiting(Config.Z_STEPPER_ID)) {
				if (timeout > Config.MAX_TIMEOUT) {
					System.out.println(">> ALIGN TASK TIMEOUT");
					throw new Exception();
				}
				Thread.sleep(Config.SLEEPTIME);
				timeout++;
			}
			
			// write new positions to virtual representation
			sys.updateSteppers(this.depth, this.height);
		} catch (Exception e) {
			this.status = TaskStatus.ABORTED;
			return;
		}
		this.status = TaskStatus.COMPLETE;
		return;
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		String format = "Align Task, Y=%d Z=%d [%s]";
		return String.format(format, depth, height, status.toString());
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}
	

}
