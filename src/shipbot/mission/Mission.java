package shipbot.mission;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import shipbot.staticlib.Config;
import shipbot.staticlib.MessageLog;

/**
 * Loads and stores the mission file, maintains the mission log, and 
 * executes tasks extracted from the mission file.
 * 
 * @author kat
 *
 */
public class Mission {

	private List<Task> tasks;
	private File mission_file;
	
	/**
	 * Create a new mission from a specified mission file.
	 * 
	 * @param mission_file_path - the path to desired mission file
	 */
	public Mission(String mission_file_path) {
		// load mission file
		mission_file = new File(mission_file_path);
		this.verifyMissionFile();
		// parse tasks
	}
	
	/**
	 * Execute all tasks in mission.
	 */
	public void executeMission() {
		MessageLog.logMissionStatus("Starting task execution.");
		// Execute tasks.
		for (Task task : tasks) {
			task.executeTask();
			MessageLog.logTaskStatus(task.toString());
		}
		MessageLog.logMissionStatus("Task execution complete.");
	}
	
	/**
	 * Ensure that mission file meets expectations. Log any inconsistencies.
	 */
	private void verifyMissionFile() {
		if (mission_file == null) {
			MessageLog.printError(this.toString(), "Mission file improperly loaded, ABORT.");
			return;
		}
		if (!(mission_file.exists() && mission_file.isFile())) {
			MessageLog.printError(this.toString(), "Mission file does not exist or is not a file.");
			return;
		}
		if (!mission_file.canRead()) {
			MessageLog.printError(this.toString(), "Mission file was not set to readable, attempting to correct.");
			mission_file.setReadable(true);
			return;
		}
	}
 }
