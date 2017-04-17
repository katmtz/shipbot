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
	private Motor drive;
	private Motor stepper_y;
	private Motor stepper_z;
	private Motor hebi_arm;
	
	public SystemState() {
		// Initialize onboard hardware
		cv = new CVSensing();
		stepper_z = new StepperMotor(Config.Z_STEPPER_ID);
		stepper_y = new StepperMotor(Config.Y_STEPPER_ID);
		drive = new DriveMotor(Config.DRIVE_MOTOR_ID);
		hebi_arm = new HebiMotor();
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

	public void getNewCapture(Device device) {
		// cvfile.write(@ 1, device)
		// while (!cvfile.updated()): sleep
		// cv data updated!
		return;
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