package shipbot.hardware;

import java.util.List;

import shipbot.mission.Device;
import shipbot.staticlib.Config;

/** 
 * Represents the full system's current status at a high level, including
 * - location (position on the testbed)
 * - depth (y-axis stepper motion)
 * - height (z-axis stepper motion)
 * - rotation (fixed HEBI motor angular position)
 * - spin (end effector HEBI motor angular position)
 * 
 * Initiates reads and writes to device data files.
 * 
 * @author Kat Martinez
 *
 */
public class SystemState {
	
	// Devices
	private CVSensing cv;
	private ArmState arm;
	
	private boolean base_adjustment = false;
	
	public SystemState() {
		// Initialize onboard hardware
		cv = new CVSensing();
		arm = new ArmState();
	}

	public boolean getNewCapture(Device device) {
		boolean retval = cv.getNewCapture(device.getCVId());
		if (retval == true) {
			this.base_adjustment = (Math.abs(cv.getHorizontalOffset()) >= Config.OFFSET_THRESHOLD);
		}
		return retval;
	}

	/* CV adjustment info */
	
	public boolean deviceIsUpward() {
		return cv.isUpward();
	}

	public boolean needsBaseAdjustment() {
		return this.base_adjustment;
	}
	
	public int getBaseAdjustment() {
		this.base_adjustment = false;
		return cv.getHorizontalOffset();
	}

	public boolean needsEngagement(int angle) {
		// TODO check if the goal state matches the desired state
		return false;
	}

	public int getEngagement() {
		// TODO get the value we should be rotating the hebi effector
		return 0;
	}

	public int[] getArmPosition() {
		// TODO return [fixed, rotator] values
		return null;
	}
}