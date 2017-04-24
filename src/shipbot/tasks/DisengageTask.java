package shipbot.tasks;

import java.io.IOException;
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
			
			// If device is upward, we need to extract Z then Y.
			if (sys.deviceIsUpward()) {
				MessageLog.logDebugMessage("DISENGAGE TASK", "Extracting effector vertically.");
				// Extract effector from device vertically
				steppers.put(StepperMotor.POS, (Config.DEVICE_HEIGHT + Config.CLEARANCE));
				DeviceData.writeArduinoData(Config.Z_STEPPER_ID, steppers);
				
				// Pull back y axis to travel position
				steppers.put(StepperMotor.POS, Config.Y_TRAVELLING);
				DeviceData.writeArduinoData(Config.Y_STEPPER_ID, steppers);
				
				// Move z axis to travel position
				steppers.put(StepperMotor.POS, Config.Z_TRAVELLING);
				DeviceData.writeArduinoData(Config.Z_STEPPER_ID, steppers);
			} else {
				MessageLog.logDebugMessage("DISENGAGE TASK", "Extracting effector horizontally.");
				// Extract effector horizontally
				steppers.put(StepperMotor.POS, Config.DEVICE_DEPTH + Config.CLEARANCE);
				DeviceData.writeArduinoData(Config.Y_STEPPER_ID, steppers);
				
				// Move z axis to travel pos
				steppers.put(StepperMotor.POS, Config.Z_TRAVELLING);
				DeviceData.writeArduinoData(Config.Z_STEPPER_ID, steppers);
				
				// Move y axis to travel pos
				steppers.put(StepperMotor.POS, Config.Y_TRAVELLING);
				DeviceData.writeArduinoData(Config.Y_STEPPER_ID, steppers);
			}

			// hang, waiting for arduino to acknowledge & complete task
			int timeout = 0;
			while (DeviceData.waiting(Config.Y_STEPPER_ID) || DeviceData.waiting(Config.Z_STEPPER_ID)) {
				if (timeout > Config.MAX_TIMEOUT) {
					MessageLog.printError("DISENGAGE TASK", "Timed out waiting for stepper response");
					this.status = TaskStatus.ABORTED;
					return;
				}
				Thread.sleep(Config.SLEEPTIME);
				timeout++;
			}
			sys.updateSteppers(Config.Y_TRAVELLING, Config.Z_TRAVELLING);
			
			// Reset hebi positions
			DeviceData.writeToHebis(true, arm_pos[0], 90, 0);
			sys.updateArm(arm_pos[0], 90, 0);
			status = TaskStatus.COMPLETE;
		} catch (InterruptedException e) {
			MessageLog.printError("DISENGAGE TASK", "???? Interrupt exception");
			status = TaskStatus.ABORTED;
			return;
		} catch (IOException e) {
			MessageLog.printError("DISENGAGE TASK", "Exception while disengaging effector.");
			status = TaskStatus.ABORTED;
			return;
		}
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
