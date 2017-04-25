package shipbot.tasks;

import java.io.IOException;

import shipbot.hardware.SystemState;
import shipbot.mission.Device;
import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;
import shipbot.staticlib.MessageLog;

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
	
		// TODO: IMPLEMENT
	}

	@Override
	protected TaskStatus getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		return String.format("PositionTask [%s]", status);
	}

	@Override
	public Device getAssociatedDevice() {
		return this.device;
	}

}
