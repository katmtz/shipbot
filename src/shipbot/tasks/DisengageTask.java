package shipbot.tasks;

import java.util.HashMap;
import java.util.Map;

import shipbot.hardware.StepperMotor;
import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;

/**
 * Extract the effector from testbed device and reset HEBI motors as needed.
 * 
 * @author kat
 *
 */
public class DisengageTask extends Task {

	private TaskStatus status;
	private Device device;
	
	public DisengageTask(Device device) {
		this.device = device;
		this.status = TaskStatus.WAITING;
	}

	@Override
	public void executeTask(SystemState sys) {
		int[] arm_pos = sys.getArmPosition();
		try {
			Map<String, Integer> steppers = new HashMap<String, Integer>();
			steppers.put(StepperMotor.POS, Config.Y_TRAVELLING);
			DeviceData.writeArduinoData(Config.Y_STEPPER_ID, steppers);
			steppers.put(StepperMotor.POS, Config.Z_TRAVELLING);
			DeviceData.writeArduinoData(Config.Z_STEPPER_ID, steppers);

			// hang, waiting for arduino to acknowledge & complete task
			int timeout = 0;
			while (DeviceData.waiting(Config.Y_STEPPER_ID) && DeviceData.waiting(Config.Z_STEPPER_ID)) {
				if (timeout > Config.MAX_TIMEOUT) {
					System.out.println(">> DISENGAGE TASK TIMEOUT");
					throw new Exception();
				}
				Thread.sleep(Config.SLEEPTIME);
				timeout++;
			}
			sys.updateSteppers(Config.Y_TRAVELLING, Config.Z_TRAVELLING);
			DeviceData.writeToHebis(true, arm_pos[0], 90, 0);
			sys.updateArm(arm_pos[0], 90, 0);
		} catch (Exception e) {
			MessageLog.printError("DISENGAGE TASK", "Exception while disengaging effector.");
		}
	}

	@Override
	protected TaskStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Device getAssociatedDevice() {
		// TODO Auto-generated method stub
		return null;
	}

}
