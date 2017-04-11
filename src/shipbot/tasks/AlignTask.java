package shipbot.tasks;

import shipbot.hardware.SystemState;

public class AlignTask extends Task {

	private int z = -1;
	private int y = -1;
	private TaskStatus status = TaskStatus.WAITING;
	
	public AlignTask() {
		
	}
	
	@Override
	public void executeTask(SystemState sys) {
		// TODO Auto-generated method stub

	}

	@Override
	protected TaskStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
