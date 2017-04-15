package shipbot.tasks;

import shipbot.hardware.SystemState;

public class PositionTask extends Task {
	
	private TaskStatus status;
	private boolean flip;

	public PositionTask(boolean needsFlip) {
		this.flip = needsFlip;
		this.status = TaskStatus.WAITING;
	}

	@Override
	public void executeTask(SystemState sys) {
		this.status = TaskStatus.ACTIVE;
		if (this.flip) {
			// rotate the flipping hebi!
		}
		// adjust angle based on system state
		
		this.status = TaskStatus.COMPLETE;
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}

}
