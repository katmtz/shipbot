import serial
import time

"""
Pipeline to drivetrain controls, with options to use verbose debug printing and 
mock serial communication over stdout.

- sendAll: sends a new target x, y, and orientation
- sendLeft: send a left (relative to robot) movement offset
- sendRight: send a right ...
- sendUp: send an up ...
- sendDown: send a down ...
- recieve: wait for a response from the Arduino
"""
class DrivePipeline:
	RECIEVE_TIMEOUT = 1000

	# Defined orientation codes
	FRONT = 1
	SIDE = 0

	x = 0
	y = 0
	r = 1

	def __init__(self, port, verbose=False, serial_mock=False):
		self.debug = verbose
		self.serial_mock = serial_mock

		if self.debug:
			print "[DRIVE] Opening serial port!"

		if self.serial_mock:
			print "[DRIVE] Skipped serial open due to mock-up."
			return

		self.serial = serial.Serial(serial_MEGA, 9600)

	def recieve(self):
		if self.serial_mock:
			print "[DRIVE] sleeping 1s to mock communication delay."
			time.sleep(1)
			return 1

		timeout = 0
		while (self.serial.in_waiting() < 1):
			if timeout > self.RECIEVE_TIMEOUT:
				if self.debug:
					print "[DRIVE] timed out waiting for ACK!"
				return 0

			timeout++
			if self.debug:
				print "[DRIVE] waiting for ack from Arduino"
		
		line = self.serial.readline()
		if self.debug:
			print "[DRIVE] recieved:" + line
		return 1

	def send(self, t_x, t_y, t_r):
		# for now, just sending all every time
		# eventually this should route as needed
		self.sendAll(t_x, t_y, t_r)

	def sendAll(self, t_x, t_y, t_r):
		if self.debug:
			print "[DRIVE] sending ALL command"

		# Write 'ALL' command and data
		format_str = "{c_x} {c_y} {c_o} {t_x} {t_y} {t_r}\n"
		msg = format_str.format(c_x=self.x, c_y=self.y, c_r=self.r, t_x=t_x, t_y=t_y, t_r=t_r)

		if self.serial_mock:
			print ">a"
			print ">" + msg
		else:
			self.serial.write("a")
			self.serial.write(msg)
			if self.debug:
				print "[DRIVE] wrote message:"
				print msg

		# Wait for response
		response = self.recieve()
		
		# Update state variables
		self.x = t_x
		self.y = t_y
		self.r = t_r

	def sendLeft(self, offset):
		if self.debug:
			print "[DRIVE] sending LEFT command"

		if self.serial_mock:
			print ">l"
			print ">" + str(offset)
		else:
			self.serial.write("l\n")
			self.serial.write(str(offset) + "\n")
			if self.debug:
				print "[DRIVE] wrote message:"
				print "l\n" + str(offset)

		# Wait for response
		response = self.recieve()

		if (self.r == FRONT):
			self.x += offset
		else:
			self.y += offset

	def sendRight(self, offset):
		if self.debug:
			print "[DRIVE] sending RIGHT command"

		if self.serial_mock:
			print ">r"
			print ">" + str(offset)
		else:
			self.serial.write("r\n")
			self.serial.write(str(offset) + "\n")
			if self.debug:
				print "[DRIVE] wrote message:"
				print "r\n" + str(offset)

		# Wait for response
		response = self.recieve()

		if (self.r == FRONT):
			self.x -= offset
		else:
			self.y -= offset

	def sendUp(self, offset):
		if self.debug:
			print "[DRIVE] sending UP command"

		if self.serial_mock:
			print ">u"
			print ">" + str(offset)
		else:
			self.serial.write("u\n")
			self.serial.write(str(offset) + "\n")
			if self.debug:
				print "[DRIVE] wrote message:"
				print "u\n" + str(offset)

		# Wait for response
		response = self.recieve()

		if (self.r == FRONT):
			self.y -= offset
		else:
			self.x -= offset

	def sendDown(self, offset):
		if self.debug:
			print "[DRIVE] sending DOWN command"

		if self.serial_mock:
			print ">d"
			print ">" + str(offset)
		else:
			self.serial.write("d\n")
			self.serial.write(str(offset) + "\n")
			if self.debug:
					print "[DRIVE] wrote message:"
					print "d\n" + str(offset)

		# Wait for response
		response = self.recieve()

		if (self.r == FRONT):
			self.y += offset
		else:
			self.x += offset

	def close(self):
		if self.debug:
			print "[DRIVE] Closing serial port!"

		if self.serial_mock:
			return
		else:
			self.serial.close()

"""
Pipeline to stepper motor controls, with options for verbose debug printing 
and a serial mockup.

- Initialization opens the connection and sends the init commands for
both motors.
- Send absolute y & z positions.
- Recieve serial message back.
- Close serial port.
"""
class StepperPipeline:
	RECIEVE_TIMEOUT = 1000

	def __init__(self, port, verbose=False, serial_mock=False):
		self.debug = verbose
		self.serial_mock = serial_mock
		
		if (self.debug):
			print "[STEPPER] opening serial port at [" + port +"]"
		
		if self.serial_mock:
			print ">y"
			print ">i"
			print ">z"
			print ">i"
			return

		# Open a serial port
		self.serial = serial.Serial(port, 9600)

		# Initialize y & z steppers
		self.serial.write("y")
		self.serial.write("i")
		self.serial.write("z")
		self.serial.write("i")

	def send(self, y, z):
		if (self.debug):
			print "[STEPPER] sending y & z absolute positions"

		if (self.serial_mock):
			print ">y"
			print ">a"
			print ">" + str(y)
			print ">z"
			print ">a"
			print ">" + str(z)
			return

		self.serial.write("y")
		self.serial.write("a")
		self.serial.write(str(y))

		self.serial.write("z")
		self.serial.write("a")
		self.serial.write(str(z))

	def recieve(self):
		if (self.serial_mock):
			print "[STEPPER] waiting 1 sec to mock communication delay"
			time.sleep(1)
			return 1

		timeout = 0
		while (self.serial.in_waiting() < 1):
			if timeout > RECIEVE_TIMEOUT:
				if (self.debug):
					print "[STEPPER] timed out waiting for response."
				return 0

			if (self.debug):
				print "[STEPPER] waiting for Arduino to send confirmation."
			time.sleep(.5)
			timeout++
		line = self.serial.readline()
		if self.debug:
			print "[STEPPER] recieved line:" + line
		return 1

	def close(self):
		if (self.debug):
			print "[STEPPER] closing serial port."

		if (self.serial_mock):
			print "[STEPPER] serial mock enabled."
			return

		self.serial.close()
		return