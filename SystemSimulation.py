
# Collection of relatively simple simulations of sensor & actuator states

class DriveTrain:
	def __init__(self):
		self.x = 0;
		self.y = 0;
		self.speed_x = 1;
		self.speed_y = 1;

	def getPosition(self):
		return (self.x, self.y);

	def move(self, goal_x, goal_y):
		if self.x > goal_x:
			self.x -= self.speed_x
		elif self.x < goal_x:
			self.x += self.speed_x

		if self.y > goal_y:
			self.y -= self.speed_y
		elif self.y < goal_y:
			self.y += self.speed_y

class Stepper:
	def __init__(self):
		self.position = 0
		self.speed = 1

	def getPosition(self):
		return self.position

	def move(self, goal):
		if self.position < goal:
			self.position += speed
		elif self.position > goal:
			self.position -= speed

class RotationalMotor:
	def __init__(self, min_val, max_val):
		self.theta = 0
		self.min = min_val
		self.max = max_val
		self.speed = 1

	def getPosition(self):
		return self.theta

	def move(self, goal):
		if self.theta > goal:
			self.theta -= self.speed
		elif self.theta < goal:
			self.theta += self.speed
