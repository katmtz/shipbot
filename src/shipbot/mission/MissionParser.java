package shipbot.mission;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import shipbot.staticlib.MessageLog;

/**
 * Library for parsing a mission file.
 * 
 * @author kat
 *
 */
public class MissionParser {
	
	private static String name = "MISSION_PARSER";
	private String mission_path;
	private final String VALVE_REGEX = "[A-H]V[1-3]";
	private final String BBOX_REGEX = "[A-H][A-B]";
	private final String BSW_REGEX = "B[1-3]";
	
	private int time_requirement = -1;
	private List<Device> devices;
	
	public MissionParser(String mission_path) {
		this.mission_path = mission_path;
		this.verifyMissionPath();
		devices = new ArrayList<Device>();
		try {
			Reader reader = new FileReader(mission_path);
			StreamTokenizer t = new StreamTokenizer(reader);
			t.parseNumbers();
			
			boolean eof = false;
			int last_token = -1;
			boolean was_switch = false;
			while (!eof) {
				int token = t.nextToken();
				
				switch (token) {
					case StreamTokenizer.TT_EOF:
						eof = true;
						break;
						
					case StreamTokenizer.TT_WORD:
						String token_str = t.sval;
						if (token_str.matches(VALVE_REGEX)) {
							Station s = getStation(token_str.charAt(0));
							Device valve = new Valve(s, token_str.substring(1));
							devices.add(valve);
							was_switch = false;
						}
						if (token_str.matches(BBOX_REGEX)) {
							Station s = getStation(token_str.charAt(0));
							Device breaker = new BreakerBox(s, token_str.substring(1));
							devices.add(breaker);
							was_switch = false;
						}
						if (token_str.matches(BSW_REGEX)) {
							// get station of last device (a breaker)
							Device last = devices.get(devices.size() - 1);
							last.addGoalState(Integer.parseInt(token_str.substring(1)));
							was_switch = true;
						}
						break;
						
					case StreamTokenizer.TT_NUMBER:
						double val = t.nval;
						if (last_token == StreamTokenizer.TT_NUMBER || was_switch) {
							time_requirement = (int) val;
						} else {
							Device last = devices.get(devices.size() - 1);
							last.addGoalState((int) val);
						}
						break;
					default:
						break;
						
				}
				last_token = token;
			}
			
		} catch (IOException e) {
			// do stuff
		}
	}
	
	public List<Device> getAllDevices() {
		return devices;
	}
	
	public int getTimeLimit() {
		return time_requirement;
	}

	private void verifyMissionPath() {
		File mission_file = new File(mission_path);
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
	
	private Station getStation (char position) {
		Station station;
		switch (position) {
			case 'A':
				station = Station.A;
				break;
			case 'B':
				station = Station.B;
				break;
			case 'C':
				station = Station.C;
				break;
			case 'D':
				station = Station.D;
				break;
			case 'E':
				station = Station.E;
				break;
			case 'F':
				station = Station.F;
				break;
			case 'G':
				station = Station.G;
				break;
			case 'H':
				station = Station.H;
				break;
			default:
				MessageLog.printError(this.name, "Unrecognized character in mission file!");
				station = null;
		}
		return station;
	}
}
