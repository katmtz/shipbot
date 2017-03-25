package shipbot.mission;

import java.util.ArrayList;
import java.util.List;

import shipbot.hardware.SystemState;
import shipbot.staticlib.MessageLog;
import shipbot.tasks.MoveTask;
import shipbot.tasks.Task;

/**
 * Loads and stores the mission file, maintains the mission log, and 
 * executes tasks extracted from the mission file.
 * 
 * @author kat
 *
 */
public class Mission {

	private SystemState system;
	private List<Device> devices;
	private List<Task> tasks;
	
	/**
	 * Test mission execution.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "missions/mission_log.txt";
		Mission mission = new Mission(path);
		mission.executeMission();
	}
	
	/**
	 * Create a new mission from a specified mission file.
	 * 
	 * @param mission_file_path - the path to desired mission file
	 */
	public Mission(String mission_file_path) {
		MessageLog.logMissionStatus("New mission initialized.");
		system = new SystemState();
		MissionParser parser = new MissionParser(mission_file_path);
		
		MessageLog.logMissionStatus("Loaded tasks from mission file.");
		devices = parser.getAllDevices();
		tasks = new ArrayList<Task>();
		
		// Add a task to visit each device!
		for (Device dev : devices) {
			Task new_task = new MoveTask(system, dev.getCoordinates());
			tasks.add(new_task);
		}
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
 }
