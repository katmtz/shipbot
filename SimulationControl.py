# Maintains synchronization between simulation and sysdata.
from SystemSimulation import *

class SimulationControl:
	self.location_path = "devices/motors/DRIVE_0.txt"
	self.depth_path = "devices/motors/STEP_0.txt"
	self.height_path = "devices/motors/STEP_1.txt"
	self.fixedhebi_path = "devices/motors/HEBI_0.txt"
	self.effectorhebi_path = "devices/motors/HEBI_1.txt"
	self.reachhebi_path = "devices/motors/HEBI_2.txt"

	self.location_format = "@ 0\nx {x}\ny {y}"
	self.stepper_format = "@ 0\nposition {pos}"
	self.hebi_format = "@ 0\nangle {theta}"

	def __init__(self):
		self.location = new DriveTrain()
		self.depth = new Stepper()
		self.height = new Stepper()
		self.fixedhebi = new RotationalMotor(-90, 90)
		self.effectorhebi = new RotationalMotor(0, 360)
		self.reachhebi = new RotationalMotor(0, 180)

	def writeToLocationData(self, x, y):
		f = open(self.location_path, 'w')
		f.write(self.location_format.format(x=x, y=y))
		f.close()

	def writeToDepthData(self, pos):
		f = open(self.depth_path, 'w')
		f.write(stepper_format.format(pos=pos))
		f.close()

	def writeToHeightData(self, pos):
		f = open(self.height_path, 'w')
		f.write(stepper_format.format(pos=pos))
		f.close()

	def writeToFixedData(self, theta):
		f = open(self.fixedhebi_path, 'w')
		f.write(self.hebi_format.format(theta=theta))
		f.close()

	def writeToEffectorData(self, theta):
		f = open(self.effectorhebi_path, 'w')
		f.write(self.hebi_format.format(theta=theta))
		f.close()

	def writeToReachData(self, theta):
		f = open(self.reachhebi_path, 'w')
		f.write(self.hebi_format.format(theta=theta))
		f.close()

	def writeData(self):
		(x,y) = self.position.getPosition()
		self.writeToMotorData(x,y)
		self.writeToDepthData(self.depth.getPosition())
		self.writeToHeightData(self.height.getPosition())
		self.writeToFixedData(self.fixedhebi.getPosition())
		self.writeToEffectorData(self.effectorhebi.getPosition())
		self.writeToReachData(self.reachhebi.getPosition())