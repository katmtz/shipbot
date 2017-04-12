import serial
import time
from sys import stdin
import atexit

# Serial port connected to arduino
serial_port = "/dev/cu.usbmodem1411"

# Use to swap between terminal and serial output
DEBUG = True

class DeviceData:
	# indicates we read a stop from the files
	killed = False

	# paths to arduino controlled devices
	device_paths = {
		"drive": "devices/actuators/DRIVE_0.txt",
		"depth": "devices/actuators/STEP_0.txt",
		"height": "devices/actuators/STEP_1.txt"
	}

	def __init__(self, serial_port):
		# Create a data dictionary & initialize it
		self.data = {
			"X": "0",
			"Y": "0",
			"D": "0",
			"H": "0"
		}
		self.response = [
			"0",
			"0",
			"0",
			"0"
		]
		if not DEBUG:
			self.serial = serial.Serial(serial_port, 9600)
		print "Awaiting data initialization!"
		self.writeToData()
		self.update()

	def update_drive(self):
		file = open(self.device_paths["drive"], 'r')
		for line in file:
			if "STOP" in line:
				file.close()
				self.serial_close()
				return True;

			if len(line) <= 1:
				continue
			[ key, value ] = line.split(' ')
			if (key is "@"):
				#print ">" + value
				if "0" in value:
					file.close()
					return False
			if ("x" in key):
				#print "X was : " + value
				self.data["X"] = value.strip('\n')
			elif ("y" in key):
				self.data["Y"] = value.strip('\n')
				file.close()
				#print "Updated drive!"
				return True
		file.close()
		return False

	def update_depth(self):
		file = open(self.device_paths["depth"], 'r')
		for line in file:
			if "STOP" in line:
				file.close()
				self.serial_close()
				return True;

			#print ">" + line
			if len(line) <= 1:
				continue
			[ key, value ] = line.split(' ')
			if ("@" in key):
				if "0" in value:
					file.close()
					return False
			if ("position" in key):
				self.data["D"] = value.strip('\n')
				file.close()
				#print "Updated depth!"
				return True
		file.close()
		return False

	def update_height(self):
		#print "hi"
		file = open(self.device_paths["height"], 'r')
		for line in file:
			if "STOP" in line:
				file.close()
				self.serial_close()
				return True;

			#print " - " + line
			if len(line) <= 1:
				continue
			[ key, value ] = line.split(' ')
			if ("@" in key):
				if "0" in value:
					file.close()
					return False
			if ("position" in key):
				self.data["H"] = value.strip('\n')
				file.close()
				#print "Updated height!"
				return True
		file.close()
		return False

	def update(self):
		read_drive = False
		read_height = False
		read_depth = False
		while not (read_drive or (read_height and read_depth)):
			#print "file lines as read:"
			read_drive = read_drive or self.update_drive()
			read_height = read_height or self.update_height()
			read_depth = read_depth or self.update_depth()
		# print "Read new data from files!"

	def writeToData(self):
		drive = open(self.device_paths["drive"], 'w')
		drive_message = "@ 0\nx " + self.response[0] + "\ny " + self.response[1]
		drive.write(drive_message)
		drive.close()

		depth = open(self.device_paths["depth"], 'w')
		depth_msg = "@ 0\nposition " + self.response[2]
		depth.write(depth_msg)
		depth.close()

		height = open(self.device_paths["height"], 'w')
		height_msg = "@ 0\nposition " + self.response[3]
		height.write(height_msg)
		height.close()

	def writeToSerial(self):
		format_str = "{x}\n{y}\n{d}\n{h}\n"
		message = format_str.format(x=self.data["X"], y=self.data["Y"], d=self.data["D"], h=self.data["H"])
		if DEBUG:
			hex_str = ':'.join(x.encode('hex') for x in message)
			print "[SERIAL_OUT] Message is (" + str(len(message)) + " bytes) <<" + hex_str + ">>"
		else:
			self.serial.write(message)

	def recieve(self):
		rec_x = False
		rec_y = False
		rec_depth = False
		rec_height = False
		if DEBUG:
			time.sleep(1)
		while not (rec_x and rec_y and rec_depth and rec_height):
			if DEBUG:
				line = "1"
			else:
				line = self.serial.readline().strip("\n")
			if (not rec_x):
				self.response[0] = line
				rec_x = True
			elif (not rec_y):
				self.response[1] = line
				rec_y = True
			elif (not rec_depth):
				self.response[2] = line
				rec_depth = True
			elif (not rec_height):
				self.response[3] = line
				rec_height = True

	def serial_close(self):
		if not self.killed:
			self.killed = True
			if DEBUG:
				print "[DEBUG] Mock closed serial port."
			else:
				self.serial.close()
		else:
			return;

	def serial_active(self):
		return not self.killed

# /dev/cu._____ should be the serial port the arduino
# is connected to.
dev = DeviceData(serial_port)
print "Starting file communication!"

while dev.serial_active():
	dev.writeToSerial()
	dev.recieve()
	dev.writeToData()
	dev.update()

print "Recieved stop signal from file system!"
