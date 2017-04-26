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
			this.base_adjustment = (Math.abs(cv.getHorizontalOffset()) >= Config.ROTATOR_LENGTH);
		}
		return retval;
	}

	/* CV adjustment info */
	public boolean needsBaseAdjustment() {
		if (this.base_adjustment) {
			System.out.println("USING COARSE ADJUST BASE");
		}
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
		if (Config.USE_CV) {
			return (Math.abs(cv.getHorizontalOffset()) <= Config.ROTATOR_LENGTH);
		} else {
			System.out.println("skipped cv so auto-running fineadjust");
			return true;
		}
	}

	public int getFineAdjustment() {
		if (Config.USE_CV) {
			return cv.getHorizontalOffset();
		} else {
			return 80;
		}
	}

	public void updateArmPosition(int fixed, int rotator) {
		arm.setPosition(fixed, rotator);
	}
}