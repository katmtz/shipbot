package shipbot.tasks;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;

public class PositionTask extends Task {
	
	private TaskStatus status;
	private boolean flip;
	private Device device;

	public PositionTask(Device device) {
		this.device = device;
		this.flip = device.needsReach();
		this.status = TaskStatus.WAITING;
	}

	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		if (this.flip) {
			// rotate the flipping hebi!
		}
		// adjust angle based on system state
		
		this.status = TaskStatus.SKIPPED;
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		if (this.flip) {
			return String.format("PositionTask, needs reach hebi engaged! [%s]", status);
		} else {
			return String.format("PositionTask, no reach needed. [%s]", status);
		}
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
