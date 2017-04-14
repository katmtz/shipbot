import serial
import time
from sys import stdin

serial_UNO = "/dev/cu.usbmodem1421"

class StepperPipeline:

	def __init__(self):
		self.uno = serial.Serial(serial_UNO, 9600)

		self.uno.write("y")
		self.uno.write("i")

		self.uno.write("z")
		self.uno.write("i")

	def send(self, y, z):
		self.uno.write("y")
		self.uno.write("a")
		self.uno.write(str(y))

		self.uno.write("z")
		self.uno.write("a")
		self.uno.write(str(z))

	def close(self):
		self.uno.close()

dev = StepperPipeline()
dev.send(200, 200)
dev.send(100, 100)
dev.send(700, 700)
dev.close()