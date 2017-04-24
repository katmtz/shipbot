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
	private DriveMotor drive;
	private Motor stepper_y;
	private Motor stepper_z;
	private HebiMotor hebi_arm;
	
	private boolean base_adjustment = false;
	
	public SystemState() {
		// Initialize onboard hardware
		cv = new CVSensing();
		stepper_z = new StepperMotor(Config.Z_STEPPER_ID);
		stepper_y = new StepperMotor(Config.Y_STEPPER_ID);
		drive = new DriveMotor(Config.DRIVE_MOTOR_ID);
		hebi_arm = new HebiMotor();
	}
	
	public int[] getGoalCenter() {
		int height;
		int depth;
		
		int pos = 0 + cv.getHorizontalOffset();
		
		// If the device is oriented upwards, we want
		// fixed depth & position, but offset height
		if (cv.isUpward()) {
			depth = Config.DEVICE_DEPTH;
			height = Config.DEVICE_HEIGHT + Config.CLEARANCE;
		} else {
			depth = Config.DEVICE_DEPTH - Config.CLEARANCE;
			height = Config.DEVICE_HEIGHT;
		}
		
		int[] coords = { pos, height, depth };
		return coords;
	}
	
	public int[] getEffectorCenter() {
		int height;
		int depth;
		int pos;
		
		// calculate position based on orientation and reach
		if (hebi_arm.isReaching()) {
			if (drive.get(DriveMotor.ORIENT) == Config.FRONT_FACING) {
				pos = drive.get(DriveMotor.X) - Config.REACH_OFFSET;
			} else {
				pos = drive.get(DriveMotor.Y) + Config.REACH_OFFSET;
			}
		} else {
			if (drive.get(DriveMotor.ORIENT) == Config.FRONT_FACING) {
				pos = Config.REACH_OFFSET;
			} else {
				pos = (-1) * Config.REACH_OFFSET;
			}
		}
	
		// calculate height & depth based on fixed hebi position (front or down)
		if (hebi_arm.isFront()) {
			height = stepper_z.get(StepperMotor.POS) + Config.ARM_HEIGHT_FRONT;
			depth = stepper_y.get(StepperMotor.POS) + Config.ARM_DEPTH_FRONT;
		} else {
			height = stepper_z.get(StepperMotor.POS) + Config.ARM_HEIGHT_DOWN;
			depth = stepper_y.get(StepperMotor.POS) + Config.ARM_DEPTH_DOWN;
		}
		
		int[] coords = { pos, height, depth };
		return coords;
	}
	
	/**
	 * Gets X position relative to testbed, using upper left corner
	 * as (0,0).
	 * 
	 * @return X position
	 */
	public int getXPosition() {
		return drive.get(DriveMotor.X);
	}
	
	/**
	 * Gets Y position relative to testbed, using the upper right
	 * corner as (0,0)
	 * 
	 * @return Y position
	 */
	public int getYPosition() {
		return drive.get(DriveMotor.Y);
	}
	
	/**
	 * Gets the direction the robot is facing (either FRONT/LONG-SIDE 
	 * or SIDE/SHORT-SIDE).
	 * 
	 * @return
	 */
	public int getOrientation() {
		return drive.get(DriveMotor.ORIENT);
	}
	
	/**
	 * Get Y-axis depth of upper assembly.
	 * 
	 * @return
	 */
	public int getDepth() {
		return stepper_y.get(StepperMotor.POS);
	}
	
	/**
	 * Get Z-axis height of upper assembly.
	 * 
	 * @return
	 */
	public int getHeight() {
		return stepper_z.get(StepperMotor.POS);
	}

<<<<<<< HEAD
	public void getNewCapture(Device device) {
		cv.getNewCapture(device.getCVId());
		this.base_adjustment = (Math.abs(cv.getHorizontalOffset()) >= Config.OFFSET_THRESHOLD);
		return;
=======
	public boolean getNewCapture(Device device) {
		boolean retval = cv.getNewCapture(device.getCVId());
		if (retval == true) {
			this.base_adjustment = (Math.abs(cv.getHorizontalOffset()) >= Config.OFFSET_THRESHOLD);
		}
		return retval;
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451
	}
	
	public int[] getArmPosition() {
		int fixed = this.hebi_arm.get(HebiMotor.FIXED);
		int reach = this.hebi_arm.get(HebiMotor.REACH);
		int effector = this.hebi_arm.get(HebiMotor.REACH);
		int[] data = { fixed, reach, effector };
		return data;
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
	
	/* UPDATE METHODS: sends updates about accepted commands to virtual representation */
	public void updateLocation(int x, int y, int r) {
		this.drive.set(DriveMotor.X, x);
		this.drive.set(DriveMotor.Y, y);
		this.drive.set(DriveMotor.ORIENT, r);
	}
	
	public void updateSteppers(int depth, int height) {
		this.stepper_y.set(StepperMotor.POS, depth);
		this.stepper_z.set(StepperMotor.POS, height);
	}
	
	public void updateArm(int fixed, int reach, int effector) {
		this.hebi_arm.set(HebiMotor.FIXED, fixed);
		this.hebi_arm.set(HebiMotor.REACH, reach);
		this.hebi_arm.set(HebiMotor.EFFECTOR, reach);
	}
}