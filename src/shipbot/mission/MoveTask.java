package shipbot.mission;

/**
 * Task that specifies directions to the drive-train motors.
 * 
 * @author kat
 *
 */
public class MoveTask extends Task {
	
	private Task parent;
	private TaskStatus status = TaskStatus.WAITING;
	private int x;
	private int y;
	
	private final String status_format = "[ MOVE_TASK ] (%s) %s";
	
	public MoveTask(int x, int y) {
		this.x = x;
		this.y = y;
		parent = this;
	}
	
	public MoveTask(Task parent, int x, int y) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	@Override
	public void executeTask() {
		status = TaskStatus.ACTIVE;
		// Write x & y offsets to correct motor files
		// Wait for acknowledgement
		status = TaskStatus.COMPLETE;
	}

	@Override
	protected TaskStatus getStatus() {
		return status;
	}

}
