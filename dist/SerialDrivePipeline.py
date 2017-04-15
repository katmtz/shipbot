import serial
import time
from sys import stdin
import atexit

# Serial port connected to arduino
serial_port = "/dev/cu.usbmodem1411"

# Use to swap between terminal and serial output
USE_STDOUT = True
DEBUG = False

class DeviceData:
	# indicates we read a stop from the file
	killed = False

	# path to drivetrain data file
	data_path = "devices/actuators/DRIVE_0.txt"

	# Command header strings
	CMD_ALL = "a"
	CMD_LEFT = "l"
	CMD_RIGHT = "r"
	CMD_UP = "u"
	CMD_DOWN = "d"
	CMD_CALIB = "f"

	# Data format strings
	CMD_FORMAT = "{c_x} {c_y} {c_o} {t_x} {t_y} {t_o}\n"
	DATA_FORMAT = "@ 0\nack {ack}\n"

	def __init__(self, serial_port):
		# Commands read from controller
		self.command = {
			"x": "0",
			"y": "0"
			"r": "0"
		}

		# Track current (known) position
		self.state = {
			"x": "0",
			"y": "0"
			"r": "0"
		}

		# Initialize the serial port if we're using it
		if not USE_STDOUT:
			if DEBUG:
				print "Establishing serial comm @ {" + serial_port + "}"
			self.serial = serial.Serial(serial_port, 9600)
			if DEBUG:
				print "Port open!"

		# Sync with controller
		if DEBUG:
			print "Synchronizing with controller..."
		self.synchronize()
		if DEBUG:
			print "Synchronized!"
		# TODO: Send an initial zeroing command to arduino?

	# wrapper loop to ensure no permahanging
	def synchronize(self):
		synchronized = False
		timeout = 0
		while not synchronized:
			if timeout > 5000:
				if DEBUG:
					print "Timed out while synchronizing."
				return False
			synchronized = self.control_sync()
			timeout++
		return True

	# opens data file to examine
	def control_sync(self):
		file = open(self.data_path, 'r')
		for line in file:
			if "NO DATA" in line:
				file.close()
				return True
		return False

	def update_drive(self):
		file = open(self.device_paths["drive"], 'r')
		for line in file:
			if DEBUG:
				print ">" + line

			if "STOP" in line:
				file.close()
				self.serial_close()
				return True

			[ key, value ] = line.split(' ')
			if (key is "@"):
				if "0" in value:
					file.close()
					return False
			if ("x" in key):
				self.command["x"] = value.strip('\n')
			elif ("y" in key):
				self.command["y"] = value.strip('\n')
			elif ("r" in key):
				self.command["orientation"] = value.strip('\n')
				file.close()
				return True
		# No recognized terminating lines!!
		file.close()
		return False

	def update(self):
		read_drive = False
		while not read_drive:
			read_drive = self.update_drive()
		if DEBUG:
			print "Read new commands from controller!"

	def writeToData(self):
		# Write an ACK to the control
		file = open(self.device_paths["drive"], 'w')
		message = DATA_FORMAT.format(ack=self.response_code)
		file.write(drive_message)
		file.close()

	def writeToSerial(self):
		# Write drive motor data out to Arduino
		if self.command["r"] != self.state["r"]:
			# orientation change, we send all data
		else:
			# we're facing forward
			if self.state["r"] is self.FRONT:
				# if we're moving left in the x direction
				if self.command["x"] > self.state["x"]:
					diff = int(self.command['x']) - int(self.state['x'])
					if USE_STDOUT:
						print "[SERIAL_OUT] <" + CMD_LEFT  + ">"
						print "[SERIAL_OUT] <" + str(diff) + ">"
					else:
						self.serial.write(CMD_LEFT)
						self.serial.write(str(diff))

				# if we're moving right in the x direction
				elif self.command["x"] <= self.state["x"]:
					diff = int(self.state["x"]) - int(self.state["x"])
					if USE_STDOUT:
						print "[SERIAL_OUT] <" + CMD_RIGHT + ">"
						print "[SERIAL_OUT] <" + str(diff) + ">"
					else:
						self.serial.write(CMD_RIGHT)
						self.serial.write(str(diff))

				# if we're moving down in the y direction
				elif self.command["y"] > self.state["y"]:
					diff = int(self.command['y']) - int(self.state['y'])
					if USE_STDOUT:
						if USE_STDOUT:
						print "[SERIAL_OUT] <" + CMD_DOWN + ">"
						print "[SERIAL_OUT] <" + str(diff) + ">"
					else:
						self.serial.write(CMD_DOWN)
						self.serial.write(str(diff))

				# if we're moving up in the y direction
				elif self.command["y"] <= self.state["y"]:			
					diff = int(self.state["y"]) - int(self.state["y"])
					if USE_STDOUT:
						print "[SERIAL_OUT] <" + CMD_UP + ">"
						print "[SERIAL_OUT] <" + str(diff) + ">"
					else:
						self.serial.write(CMD_UP)
						self.serial.write(str(diff))

		# message = CMD_FORMAT.format(x=self.data["X"], y=self.data["Y"], d=self.data["D"], h=self.data["H"])
		# hex_str = ':'.join(x.encode('hex') for x in message)
		# if not USE_STDOUT:
		# 	self.serial.write(message)
		# if (USE_STDOUT or DEBUG):
		# 	print "[SERIAL_OUT] Message is (" + str(len(message)) + " bytes) <<" + hex_str + ">>"


	def recieve(self):
		rec_x = False
		rec_y = False
		rec_depth = False
		rec_height = False
		if DEBUG:
			time.sleep(1)
		while not (rec_x and rec_y and rec_depth and rec_height):
			if USE_STDOUT:
				line = "1"
				print "[SERIAL_IN] " + line
			else:
				line = self.serial.readline().strip("\n")
				if DEBUG:
					print "[SERIAL_IN] " + line
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
				print "Pretended to close serial port."
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

