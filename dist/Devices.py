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
	RECIEVE_TIMEOUT = 5

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
			print "[DRIVE] Opening serial port at [" + port + "]"

		if self.serial_mock:
			print "[DRIVE] Skipped serial open due to mock-up."
			return

		self.serial = serial.Serial(port, 9600, timeout=1)

	def recieve(self):
		if self.serial_mock:
			print "Waiting for drive response..."
			msg = raw_input()
			#while (msg != "DONE"):
			#	msg = raw_input()
			return 1
			
		timeout = 0
		while (timeout < self.RECIEVE_TIMEOUT):
			line = self.serial.readline()
			timeout += 1
			if "DONE" in line:
				return 1
		if self.verbose:
			print "[DRIVE] timed out waiting for ack"
		return 0

	def send(self, t_x, t_y, t_r):
		# for now, just sending all every time
		# eventually this should route as needed
		self.sendAll(t_x, t_y, t_r)

	def sendAll(self, t_x, t_y, t_r):
		if self.debug:
			print "[DRIVE] sending ALL command"

		# Write 'ALL' command and data
		format_str = "{c_x} {c_y} {c_o} {t_x} {t_y} {t_r}\n"
		msg = format_str.format(c_x=self.x, c_y=self.y, c_o=self.r, t_x=t_x, t_y=t_y, t_r=t_r)

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
	y = 0
	z = 0

	RECIEVE_TIMEOUT = 3
	pos_init = False

	def __init__(self, port, verbose=False, serial_mock=False):
		self.debug = verbose
		self.serial_mock = serial_mock
		
		if (self.debug):
			print "[STEPPER] opening serial port at [" + port +"]"
		
		if self.serial_mock:
			print ">yi"
			print ">zi"
		else:
			# Open a serial port
			self.serial = serial.Serial(port, 9600, timeout=1)

			# Initialize y & z steppers
			if self.debug:
				print "[STEPPER] waiting for y axis init response..."
			self.serial.write("yi")
			self.serial.flush()
			response_y = self.recieve()
			if self.debug:
				print "[STEPPER] recieved y init!"

			if self.debug:
				print "[STEPPER] waiting for z axis init response..."
			self.serial.write("zi")
			self.serial.flush()
			response_z = self.recieve()
			if self.debug:
				print "[STEPPER] recieved z init!"

	def send(self, y, z):
		if self.debug:
			print "[STEPPER] update steppers to y:" + y + " z:" + z
		
		if not self.pos_init:
			if self.debug:
				print "[STEPPER] using absolute to initialize position"
			self.pos_init = True
			self.sendAbsolute(y,z)
			return

		if (self.debug):
			print "[STEPPER] sending y & z relative positions"

		if self.serial_mock:
			return

		diff_y = int(y) - self.y
		diff_z = int(z) - self.z

		if (diff_y != 0):
			if self.debug:
				print " - Sending a y offset of " + str(diff_y)
			self.serial.write("yr")
			self.serial.write(str(diff_y))
			self.serial.flush()
			response_y = self.recieve()
		if (diff_z != 0):
			if self.debug:
				print " - Sending a z offset of " + str(diff_z)
			self.serial.write("zr")
			self.serial.write(str(diff_z))
			self.serial.flush()
			response_z = self.recieve()

		self.y += diff_y
		self.z += diff_z

	def sendAbsolute(self, y, z):
		if (self.debug):
			print "[STEPPER] sending y & z absolute positions"

		if (self.serial_mock):
			print ">ya"
			print ">" + str(y)
			print ">za"
			print ">" + str(z)
			return

		self.serial.write("ya")
		self.serial.write(y)
		self.serial.flush()
		response_y = self.recieve()
		self.y = y

		self.serial.write("za")
		self.serial.write(z)
		self.serial.flush()
		response_z = self.recieve()
		self.z = z

	def recieve(self):
		if (self.serial_mock):
			print "Waiting for stepper response..."
			msg = raw_input()
			#while (msg != "DONE"):
			#	msg = raw_input()
			return 1
		
		timeout = 0
		while (timeout < self.RECIEVE_TIMEOUT):
			line = self.serial.readline()
			timeout += 1
			if len(line) > 0:
				print "[STEPPER] >" + line
				return 1
		if self.debug:
			print "[STEPPER] timed out waiting for ack"
		return 0

	def close(self):
		if (self.debug):
			print "[STEPPER] closing serial port."

		if (self.serial_mock):
			print "[STEPPER] serial mock enabled."
			return

		self.serial.close()
		return
