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
	
	private int height_offset = 0;
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

	/* If given angle is greater than 5 deg away from goal */
	public boolean needsEngagement(int angle) {
		int diff = angle - cv.getAngularPosition();
		if (Math.abs(diff) > 5) {
			return true;
		} else {
			return false;
		}
	}

	public int getEngagement(int angle) {
		int diff = angle - cv.getAngularPosition();
		return diff;
	}

	public int[] getArmPosition() {
		return this.arm.getPosition();
	}

	public boolean needsFineAdjustment() {
		return (Math.abs(cv.getHorizontalOffset()) < Config.ROTATOR_LENGTH);
	}

	public int getFineAdjustment() {
		return cv.getHorizontalOffset();
	}

	public void storeHeightOffset(int height_offset) {
		this.height_offset = height_offset;
	}

	public void updateArmPosition(int fixed, int rotator) {
		arm.setPosition(fixed, rotator);
	}

	public int getStoredHeightOffset() {
		int ret = this.height_offset;
		this.height_offset = 0;
		return ret;
	}
}