package shipbot.tasks;

import java.util.HashMap;
import java.util.Map;

import shipbot.hardware.StepperMotor;
import shipbot.hardware.SystemState;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;

public class ApproachTask extends Task {

	private TaskStatus status = TaskStatus.WAITING;
	private int height = -1;
	private int depth = -1;
	
	public ApproachTask(int[] position) {
		this.depth = position[0];
		this.height = position[1];
	}
	
	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		try {
			Map<String, Integer> data = new HashMap<String,Integer>();
			data.put(StepperMotor.POS, this.depth);
			DeviceData.writeMotorData(Config.Y_STEPPER_ID, data);
			data.put(StepperMotor.POS, this.height);
			DeviceData.writeMotorData(Config.Z_STEPPER_ID, data);
			
			// hang, waiting for arduino to acknowledge & complete task
			int timeout = 0;
			while (DeviceData.waiting(Config.Y_STEPPER_ID) && DeviceData.waiting(Config.Z_STEPPER_ID)) {
				if (timeout > Config.MAX_TIMEOUT) {
					System.out.println(">> APPROACH TASK TIMEOUT");
					throw new Exception();
				}
				Thread.sleep(Config.SLEEPTIME);
				timeout++;
			}
			
			// write new positions to virtual representation
			sys.updateDepth(this.depth);
			sys.updateHeight(this.height);
		} catch (Exception e) {
			this.status = TaskStatus.ABORTED;
			return;
		}
		this.status = TaskStatus.COMPLETE;
		return;
	}

	@Override
	protected TaskStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		String format = "Approach Task, depth=%d height=%d [%s]";
		return String.format(format, this.depth, this.height, status.toString());
	}
}
