from Devices import DrivePipeline, StepperPipeline

# Global configs!
axis_port = "/dev/ttyACM0"
base_port = "/dev/ttyACM1"
verbose = True
serialmock_axis = False
serialmock_base = True #False

class SerialPipeline:

	drive_path = "devices/actuators/DRIVE_0.txt"
	stepper_paths = [ 
		"devices/actuators/STEP_0.txt", 
		"devices/actuators/STEP_1.txt" 
	]

	command = {
		"X": "0",
		"Y": "0",
		"R": "0",
		"D": "270",
		"H": "10"
	}

	def __init__(self):
		self.rec_kill = False

		# Sync with controller files
		self.synchronize()

		# Initialize communication to pipelines (also inits hardware)
		self.drive_pipeline = DrivePipeline(base_port, verbose, serialmock_base)
		self.stepper_pipeline = StepperPipeline(axis_port, verbose, serialmock_axis)

		self.sendStatus(1)

	def processCommand(self):
		processed = False
		while not processed:
			drive_cmd = self.update_drive()
			depth_cmd = self.update_depth()
			height_cmd = self.update_height()

			if (self.rec_kill):
				if verbose:
					print "[SERIAL] recieved stop signal."
				return

			if (height_cmd and depth_cmd):
				if verbose:
					print "[SERIAL] processing stepper command."
				
				self.stepper_pipeline.send(self.command["D"], self.command["H"])
				#status = self.stepper_pipeline.recieve()
				self.sendStatus(1, False, True)
				processed = True
			elif (drive_cmd):
				if verbose:
					print "[SERIAL] processing drive command."

				self.drive_pipeline.send(self.command["X"], self.command["Y"], self.command["R"])
				self.sendStatus(1, True, False)
				processed = True
		if verbose:
			print "[SERIAL] command processed."

	def sendStatus(self, status, drive=True, steppers=True):
		format_str = "@ 0\nack {status}\n"
		msg = format_str.format(status=status)
		
		if (drive):
			drive_file = open(self.drive_path, 'w')
			drive_file.write(msg)
			drive_file.close()

		if (steppers):
			for path in self.stepper_paths:
				step_file = open(path, 'w')
				step_file.write(msg)
				step_file.close()

		if verbose:
			print "[SERIAL] wrote message to control files:"
			print msg

	def synchronize(self):
		# Sync with drive file:
		synced = False
		if verbose:
			print "[SERIAL] syncing with controller..."
		while not synced:
			drive_file = open(self.drive_path, 'r')
			for line in drive_file:
				if "NO DATA" in line:
					synced = True
			drive_file.close()
		if verbose:
			print "[SERIAL] - drive sync acquired"

		# Sync with stepper files
		for path in self.stepper_paths:
			synced = False
			while not synced:
				stepper_file = open(path, 'r')
				for line in stepper_file:
					if "NO DATA" in line:
						synced = True
				stepper_file.close()
		if verbose:
			print "[SERIAL] - stepper sync acquired"
			print "[SERIAL] synced with controller!"

	def isStopped(self):
		return self.rec_kill

	def serial_close(self):
		if not self.rec_kill:
			self.drive_pipeline.close()
			self.stepper_pipeline.close()
			self.rec_kill = True

	def update_drive(self):
		file = open(self.drive_path, 'r')
		for line in file:
			if "STOP" in line:
				file.close()
				self.serial_close()
				return False

			if len(line) <= 1:
				continue
			[ key, value ] = line.split(' ')
			if (key is "@"):
				if "0" in value:
					file.close()
					return False

			if ("x" in key):
				self.command["X"] = value.strip('\n')
			elif ("y" in key):
				self.command["Y"] = value.strip('\n')
				file.close()
				return True
			elif ("r" in key):
				self.command["R"] = value.strip("\n")
		file.close()
		return False

	def update_depth(self):
		file = open(self.stepper_paths[0], 'r')
		for line in file:
			if "STOP" in line:
				file.close()
				self.serial_close()
				return True

			if len(line) <= 1:
				continue
			[ key, value ] = line.split(' ')
			if ("@" in key):
				if "0" in value:
					file.close()
					return False
			if ("position" in key):
				self.command["D"] = value.strip('\n')
				file.close()
				return True
		file.close()
		return False

	def update_height(self):
		file = open(self.stepper_paths[1], 'r')
		for line in file:
			if "STOP" in line:
				file.close()
				self.serial_close()
				return True

			if len(line) <= 1:
				continue
			[ key, value ] = line.split(' ')
			if ("@" in key):
				if "0" in value:
					file.close()
					return False
			if ("position" in key):
				self.command["H"] = value.strip('\n')
				file.close()
				return True
		file.close()
		return False

print "> pipeline start"
pipe = SerialPipeline()
while not pipe.isStopped():
	pipe.processCommand()

print "> pipeline stop"
