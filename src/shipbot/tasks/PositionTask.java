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
		
		int reach;
		int fixed;
		int effector = 0;
		
		if (this.flip) {
			reach = 180;
		} else {
			reach = 0;
		}
		
		if (sys.deviceIsUpward()) {
			fixed = -90;
		} else {
			fixed = 0;
		}
		
		try {
			DeviceData.writeToHebis(true, fixed, reach, effector);
			
			int timeout = 0;
			while (DeviceData.waiting(Config.HEBI_ID)) {
				if (timeout > Config.MAX_TIMEOUT) {
					MessageLog.printError("POSITION TASK", "Timed out waiting for hebi response");
					this.status = TaskStatus.ABORTED;
				}
				timeout++;
				Thread.sleep(Config.SLEEPTIME);
			}
			sys.updateArm(fixed, reach, effector);
		} catch (InterruptedException e) {
			this.status = TaskStatus.ABORTED;
			MessageLog.printError("POSITION TASK", "Interrupted while waiting for hebi response.");
		} catch (IOException e) {
			this.status = TaskStatus.ABORTED;
			return;
		}
		
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
