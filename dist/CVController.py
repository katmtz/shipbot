from DeviceRecognition import *

# static flag to enable picamera code
USE_CAMERA = False
MOCK_IMG_PATH = "imgs/single_img.jpg"
#MOCK_IMG_PATH = "imgs/shuttlecock_lowres.jpg"

class CVController:

	data_path = "devices/CV.txt"
	capture_path = "imgs/capture.jpg"

	format_str = "@ 0\nOFFSET {offset}\nORIENT {orient}\nANGLE {angle}\n"

	killed = False

	def __init__(self):
		print ("Awaiting sync...")
		synced = False
		while not synced:
			file = open(self.data_path, 'r')
			for line in file:
				if "NO DATA" in line:
					synced = True
			file.close()
		print ("Sync acquired.")
		self.writeData(0, 0, 0)

	def isActive(self):
		return not self.killed

<<<<<<< HEAD
		initialized = False
		while not initialized:
			file = open(self.data_path, 'r')
			for line in file:
				if "NO DATA" in line:
					initialized = True
			file.close()
		return

	def commandLoop(self):
		killed = False
		cmd_recieved = False
		device = "0"

=======
	def processCommand(self):
		print ("Awaiting command...")
		cmd_recieved = False
		device_code = 0
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451
		while not cmd_recieved:
			file = open(self.data_path, 'r')
			for line in file:
				if "@ 1" in line:
					cmd_recieved = True
				if cmd_recieved:
					if "STOP" in line:
<<<<<<< HEAD
						cmd_recieved = True
						killed = True
						break

					(key, value) = line.split(' ')
					if "DEVICE" in key:
						device = value
						cmd_recieved = True
						break
			file.close()

		if killed:
			return False

		# Shuttlecock code is 3!!
		if not ("3" in device):
			print ("Unexpected device!")
			return True
=======
						self.killed = True
					else:
						(key, value) = line.split(' ')
						if "DEVICE" in key:
							device_code = int(value.strip('\n'))
			file.close()

		if self.killed:
			print ("Recieved stop.")
			return
		else:
			print ("Recived device code: " + str(device_code))

		# Case on extracted device code!
		if (device_code == 1):
			# device type: valve small
			device = ValveSmall()
		elif (device_code == 2):
			# device is large valve
			device = ValveLarge()
		elif (device_code == 3):
			# device is shuttlecock
			device = Shuttlecock()
		elif (device_code == 4):
			# device is breaker box
			device = BreakerBox()
		else:
			# device code is unrecognized or 0
			return

		if USE_CAMERA:
			self.capture()
			path = self.capture_path
		else:
			path = MOCK_IMG_PATH
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451

		retval = device.processImage(path)

<<<<<<< HEAD
	def writeData(self, offset, orientation, angle):
		format_str = "@ 0\nOFFSET {offset}\nORIENT {orient}\nANGLE {angle}\n"
		msg = format_str.format(offset=offset, orient=orientation, angle=angle)
=======
		if not retval:
			print ("Detect FAILED!")
			self.writeData(0,0,0)
			return
		else:
			print ("Successful detection!")
			(offset,orient,angle) = retval
			self.writeData(offset,orient,angle)
			return

	def capture(self):
		if not USE_CAMERA:
			return
		else:
			# TODO: add picamera capture here!
			pass

	def writeData(self, offset, orientation, angle):
		msg = self.format_str.format(offset=offset, orient=orientation, angle=angle)
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451
		file = open(self.data_path, 'w')
		file.write(msg)
		file.close()

<<<<<<< HEAD
	def loadImage(self):
		# load the image
		image = cv2.imread(self.img_path)
		height, width, channels = image.shape
		img_center = ( width/2, height/2 )
		hsv_image = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

		# mask it for the desired color as a binary
		mask = cv2.inRange(hsv_image, self.thresh_low, self.thresh_high)
		output = cv2.bitwise_and(hsv_image, hsv_image, mask = mask)
		output_gray = cv2.cvtColor(output,cv2.COLOR_BGR2GRAY)
		ret,thresh = cv2.threshold(output_gray, 15, 255, cv2.THRESH_BINARY)

		# close any holes
		kernel = np.ones((5,5),np.uint8)
		closed_thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
		
		# find the contours
		img, contours, hierarchy = cv2.findContours(closed_thresh, cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
		
		for cnt in contours:
			rect = cv2.minAreaRect(cnt)
			center,dim, angle = rect
			if (dim[0] > 0 and dim[1] > 0):
				box = cv2.boxPoints(rect)
				box = np.int0(box)
				area = dim[0] * dim[1]
				ratio = dim[1] / dim[0]
				ret,orient = self.inRange(ratio, angle, area)
				if (ret):
					if (orient is "vertical"):
						x_offset = img_center[0] - center[0]
						theta = 90
					else:
						x_offset = img_center[0] - (center[0] - 100)
						theta = 0

					#print ("Detected {orient} shuttlecock!".format(orient=orient))
					#print ("position: " + str(center[0]) + " " + str(center[1]))
					#print ("ratio: " + str(ratio))
					#print ("area: " + str(area))
					#print ("angle: " + str(angle))
					self.writeData(x_offset, 0, theta)
					return


	def inRange(self, ratio, angle, area):
		if (area > self.area_max or area < self.area_min):
			return (False, "none")

		if (ratio > 1):
			# Width is bigger than height!
			if (ratio < self.rv_low or ratio > self.rv_high):
				return (False, "none")
			else:
				if (abs(angle) > 45):
					return (True, "horizontal")
				else:
					return (True, "vertical")
		else:
			# Height is bigger than width
			if (ratio < self.rh_low or ratio > self.rh_high):
				return (False, "none")
			else:
				if (abs(angle) > 45):
					return (True, "vertical")
				else:
					return (True, "horizontal")

dev = Shuttlecock(path)

active = True
while (active):
	active = dev.commandLoop()

print ("RECIEVED STOP")
=======
print ("CV control running.")
c = CVController()
while c.isActive():
	c.processCommand()
print ("CV control stopped.")
>>>>>>> 4667ea8ab7b55698b5f60e2bb540119249e6f451
