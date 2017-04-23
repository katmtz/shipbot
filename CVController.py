import numpy as np
import cv2

path = "imgs/shuttlecock_lowres.jpg"
# NEEDS PYTHON3 !!!!

class Shuttlecock:

	data_path = "devices/CV.txt"

	hsb_low = [ 100, 65, 65 ]
	hsb_high = [ 120, 200, 200 ]

	rv_low = 2.8
	rv_high = 4.5
	rh_low = .2
	rh_high = .3

	area_min = 2000
	area_max = 1000000

	def __init__(self, img_path):
		self.img_path = img_path
		self.thresh_low = np.array(self.hsb_low, dtype="uint8")
		self.thresh_high = np.array(self.hsb_high, dtype="uint8")

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

		while not cmd_recieved:
			file = open(self.data_path, 'r')
			for line in file:
				if "@ 0" in line:
					break
				else:
					if "STOP" in line:
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

		# send capture to picamera!
		# for now, use dummy image
		self.loadImage()
		return True

	def writeData(self, offset, orientation, angle):
		format_str = "@ 0\nOFFSET {offset}\nORIENT {orient}\nANGLE {angle}\n"
		msg = format_str.format(offset=offset, orient=orientation, angle=angle)
		file = open(self.data_path, 'w')
		file.write(msg)
		file.close()

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
