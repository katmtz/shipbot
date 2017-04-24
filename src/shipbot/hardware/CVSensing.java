package shipbot.hardware;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import shipbot.staticlib.Config;
import shipbot.staticlib.MessageLog;

public class CVSensing {
	
	// Static codes
	public static Integer ORIENT_UP = 0;
	public static Integer ORIENT_SIDE = 1;
	
	public static Integer DEVICE_NONE = 0;
	public static Integer DEVICE_VALVE_SM = 1;
	public static Integer DEVICE_VALVE_LG = 2;
	public static Integer DEVICE_SHUTTLE = 3;
	public static Integer DEVICE_BREAKER = 4;
	
	// Data keys
	private String DEVICE_TYPE = "DEVICE";
	private String OFFSET = "OFFSET";
	private String ORIENTATION = "ORIENT";
	private String ANGLE = "ANGLE";
	
	// Data storage map
	private Map<String, Integer> data;
	
	private String path = "devices/CV.txt";
	
	public CVSensing() {
		this.data = new HashMap<String, Integer>();
		this.data.put(this.DEVICE_TYPE, CVSensing.DEVICE_NONE);
		this.data.put(this.OFFSET, 0);
		this.data.put(this.ANGLE, 0);
		this.data.put(this.ORIENTATION, CVSensing.ORIENT_UP);
	}
	
<<<<<<< HEAD
	public void getNewCapture(int device_type) {
=======
	public boolean getNewCapture(int device_type) {
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451
		String format_str = "@ 1\n%s %d\n";
		String msg = String.format(format_str, this.DEVICE_TYPE, device_type);
		try {
			// SEND COMMAND
			Writer writer = new FileWriter(this.path);
			writer.write(msg);
			writer.close();
			
			// AWAIT RESPONSE
<<<<<<< HEAD
			MessageLog.logDebugMessage("CV", "Ignoring CV FOR NOW");
			return;
/**			boolean responded = false;
=======
			int timeout = 0;
			boolean responded = false;
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451
			while (!responded) {
				if (timeout > Config.MAX_TIMEOUT) {
					return false;
				}
				
				Reader reader = new FileReader(this.path);
				StreamTokenizer tok = new StreamTokenizer(reader);
				tok.parseNumbers();
				char owner_tag = '@';
				tok.wordChars((int) owner_tag, (int) owner_tag);
				
				boolean ended = false;
				String key = "";
				Map<String, Integer> temp = new HashMap<String, Integer>();
				while (!ended) {
					int token = tok.nextToken();
					switch (token) {
						case StreamTokenizer.TT_EOF:
							ended = true;
							break;
						case StreamTokenizer.TT_WORD:
							key = tok.sval;
<<<<<<< HEAD
							MessageLog.logDebugMessage("CV", String.format("[debug] word token was <%s>", key));
							break;
						case StreamTokenizer.TT_NUMBER:
							int val = (int) tok.nval;
							MessageLog.logDebugMessage("CV", String.format("[debug] number token was %d", val));
=======
							//MessageLog.logDebugMessage("CV", String.format("[debug] word token was <%s>", key));
							break;
						case StreamTokenizer.TT_NUMBER:
							int val = (int) tok.nval;
							//MessageLog.logDebugMessage("CV", String.format("[debug] number token was %d", val));
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451
							temp.put(key, (Integer) val);
							break;
					}
				}
				reader.close();
				if (temp.containsKey("@")) {
					if (temp.get("@") == Config.OWNER_ARDUINO) {
						this.data.clear();
						this.data.putAll(temp);
						responded = true;
					} else {
						//MessageLog.logDebugMessage("CV", "Read own command!");
<<<<<<< HEAD
					}
				} else {
					MessageLog.printError("CV", "Missing owner tag!!");
					return;
=======
						timeout++;
					}
				} else {
					timeout++;
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451
				}
			}
*/
		} catch (IOException e) {
			MessageLog.printError("CV", "IO exception while communicating with CV file.");
			return false;
		}
		return true;
	}
	
	public int getHorizontalOffset() {
		return this.data.get(this.OFFSET);
	}
	
	public int getDeviceOrientation() {
		return this.data.get(ORIENTATION);
	}

	public boolean isUpward() {
		if (this.data.get(ORIENTATION) == CVSensing.ORIENT_UP) {
			return true;
		} else {
			return false;
		}
	}
}
