package shipbot.mission;

public enum TaskStatus {

	WAITING ("waiting", true, false), 
	ACTIVE ("active", false, false), 
	ABORTED ("aborted", false, true), 
	COMPLETE ("complete", false, false);
	
	private final String name;
	private final Boolean pending;
	private final Boolean failed;
	
	private TaskStatus(String name, boolean pending, boolean failed) {
		this.name = name;
		this.pending = pending;
		this.failed = failed;
	}
	
	@Override
	public String toString() {
		return String.format("Task Status: %s", name);
	}
	
	/**
	 * Checks if this task has failed.
	 * @return
	 */
	public Boolean isOkay() {
		return !failed;
	}
	
	/**
	 * Checks if this task has successfully terminated.
	 * @return
	 */
	public Boolean isComplete() {
		return (!pending && !failed);
	}
	
	/**
	 * Checks if task is waiting to be executed.
	 * @return
	 */
	public Boolean needsExecution() {
		return pending;
	}
}
