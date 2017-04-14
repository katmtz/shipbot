import serial
import time
from sys import stdin

serial_MEGA = "/dev/cu.usbmodem1421"
RIGHT = 0
FRONT = 1

class DrivePipeline:
	x = 0
	y = 0
	r = 1

	def __init__(self):
		print "INIT"
		self.serial = serial.Serial(serial_MEGA, 9600)

	def writeAll(self, t_x, t_y, t_r):
		# Write 'ALL' command and data
		self.serial.write("a")
		format_str = "{c_x} {c_y} {c_o} {t_x} {t_y} {t_r}"
		msg = format_str.format(c_x=self.x, c_y=self.y, c_r=self.r, t_x=t_x, t_y=t_y, t_r=t_r)
		self.serial.write(msg)
		print "Wrote out <" + msg +">"

		# Wait for response
		# while (not self.serial.available()):
		# 	time.sleep(1)
		# status = self.serial.readLine()
		# print "Read back <" + status + ">"
		
		# Update state variables
		self.x = t_x
		self.y = t_y
		self.r = t_r

	def writeLeft(self, offset):
		self.serial.write("l\n")
		self.serial.write(str(offset) + "\n")
		print "Wrote out left move of <" + str(offset) + ">"

		# Wait for response
		# while (not self.serial.available()):
		# 	time.sleep(1)
		# status = self.serial.readLine()
		# print "Read back <" + status + ">"

		if (self.r == FRONT):
			self.x += offset
		else:
			self.y += offset

	def writeRight(self, offset):
		self.serial.write("r\n")
		self.serial.write(str(offset) + "\n")
		print "Wrote out right move of <" + str(offset) + ">"

		# Wait for response
		# while (not self.serial.available()):
		# 	time.sleep(1)
		# status = self.serial.readLine()
		# print "Read back <" + status + ">"

		if (self.r == FRONT):
			self.x -= offset
		else:
			self.y -= offset

	def writeUp(self, offset):
		self.serial.write("u\n")
		self.serial.write(str(offset) + "\n")
		print "Wrote out up move of <" + str(offset) + ">"

		# Wait for response
		# while (not self.serial.available()):
		# 	time.sleep(1)
		# status = self.serial.readline()
		# print "Read back <" + status + ">"

		if (self.r == FRONT):
			self.y -= offset
		else:
			self.x -= offset

	def writeDown(self, offset):
		self.serial.write("d\n")
		self.serial.write(str(offset) + "\n")
		print "Wrote out down move of <" + str(offset) + ">"

		# Wait for response
		# while (not self.serial.available()):
		# 	time.sleep(1)
		# status = self.serial.readLine()
		# print "Read back <" + status + ">"

		if (self.r == FRONT):
			self.y += offset
		else:
			self.x += offset

	def close(self):
		print "CLOSING"
		self.serial.close()

dev = DrivePipeline()

# dev.writeUp(300)
# time.sleep(1)
# dev.writeDown(300)
# time.sleep(1)
dev.writeRight(500)
# time.sleep(1)
# dev.writeLeft(300)

dev.close()