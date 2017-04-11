# Demo script to show changing values in filesystem
from Tkinter import *

class DeviceDataMockup:
	sensor_path = "shipbot/devices/sensors/IR_0.txt"
	sensor_val = 1.25
	sensor_text = "Uninitialized!"

	motor_path = "shipbot/devices/actuators/DRIVE_0.txt"
	motor_x = 0
	motor_y = 0
	motor_text = "Uninitialized!"

	def __init__(self, canvas):
		self.canvas = canvas
		self.sensordisp = self.canvas.create_text((100,100), text=self.sensor_text)
		self.motordisp = self.canvas.create_text((100,200), text=self.motor_text)
		self.writeToMotorData()
		self.writeToSensorData()
		return

	def writeToMotorData(self):
		message = "@ 0\nx {x}\ny {y}"
		f = open(self.motor_path, 'w')
		f.write(message.format(x=self.motor_x, y=self.motor_y))
		f.close()
		return

	def writeToSensorData(self):
		message = "@ 0\ndistance {val}"
		f = open(self.sensor_path, 'w')
		f.write(message.format(val=self.sensor_val))
		f.close()
		return

	def readLines(self, path):
		f = open(path, 'r')
		lines = []
		for line in f:
			lines.append(line)
		f.close()
		return lines

	def updateMotorText(self):
		# Generate some changes
		self.motor_x += 1
		self.motor_y += 1
		self.writeToMotorData()
		# Read changes
		lines = self.readLines(self.motor_path)
		self.motor_text = "Motor Data:\n"
		for line in lines:
			self.motor_text += line

	def updateSensorText(self):
		# Generate some changes
		self.sensor_val += 0.1
		self.writeToSensorData()
		# Read changes
		lines = self.readLines(self.sensor_path)
		self.sensor_text = "Sensor Data:\n"
		for line in lines:
			self.sensor_text += line

	def updateText(self):
		self.canvas.delete(self.sensordisp, self.motordisp)
		self.updateMotorText()
		self.updateSensorText()
		self.sensordisp = self.canvas.create_text((100,100), text=self.sensor_text)
		self.motordisp = self.canvas.create_text((100, 200), text=self.motor_text)
		self.canvas.after(50, self.updateText)


# Main draw function
root = Tk()
canvas = Canvas(root, width = 300, height = 300)
canvas.pack()

test = DeviceDataMockup(canvas)
test.updateText()

root.mainloop()
root.quit()
