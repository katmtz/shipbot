# A collection of classes that have nifty info about particular devices we find on the testbed.

import cv2
import numpy as np

# Device orientation codes, defined in shipbot.hardware.CVSensing
# - ORIENT_UP: device needs to be engaged vertically
# - ORIENT_SIDE: device needs to be engaged horizontally
ORIENT_UP = 0
ORIENT_SIDE = 1

# Detects a shuttlecock device (front-facing only!)
class Shuttlecock:
	# See shipbot.hardware.CVSensing: DEVICE_SHUTTLE
	device_id = 3

	# HSV Color Ranges
	hsb_low = [ 100, 65, 65 ]
	hsb_high = [ 120, 200, 200 ]

	# Vertical and Horizontal Dimension Ranges
	rv_low = 2.8
	rv_high = 4.5
	rh_low = .2
	rh_high = .3

	# Detected Object Size Range
	area_min = 2000
	area_max = 1000000

	# Initialize numpy color arrays
	def __init__(self):
		self.thresh_low = np.array(self.hsb_low, dtype="uint8")
		self.thresh_high = np.array(self.hsb_high, dtype="uint8")

	# Use width/height ratio, angle of rotation and area to determine
	# whether a detected object is valid and what position it's in
	def inRange(self, ratio, angle, area):
		if (area > self.area_max or area < self.area_min):
			return (False, None)

		if (ratio > 1):
			# Width is bigger than height!
			if (ratio < self.rv_low or ratio > self.rv_high):
				return (False, None)
			else:
				if (abs(angle) > 45):
					return (True, 90)
				else:
					return (True, 0)
		else:
			# Height is bigger than width
			if (ratio < self.rh_low or ratio > self.rh_high):
				return (False, None)
			else:
				if (abs(angle) > 45):
					return (True, 0)
				else:
					return (True, 90)

	# Recieves a path to an image, returns ( offset, orientation, angle )
	def processImage(self, path):
		# ENSURE PATH IS VALID BEFORE USE!!!
		image = cv2.imread(path)
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
				ret,theta = self.inRange(ratio, angle, area)
				if (ret):
					orient = ORIENT_SIDE
					if (theta == 90):
						x_offset = img_center[0] - center[0]
					else:
						x_offset = img_center[0] - (center[0] - 100)

					print ("Detected shuttlecock at {orient} deg.".format(orient=orient))
					print ("Horizontal offset: " + str(x_offset))
					return ( x_offset, orient, theta )

class BreakerBox:
	# HSB Color Range (Valve)
	hsb_low = [ 0, 150, 150 ]
	hsb_high = [ 18, 200, 255 ]
	
	def __init__(self):
		self.thresh_low = np.array(self.hsb_low, dtype="uint8")
		self.thresh_high = np.array(self.hsb_high, dtype="uint8")

	def processImage(self, path):
		# TODO: implement this lolol
		return False

class ValveSmall:
	device_id = 1

	mark_low = [ 35, 105, 105 ]
	mark_high = [ 55, 255, 255 ]

	blue_low = [ 100, 105, 105 ]
	blue_high = [ 135, 255, 255 ]

	area_max = 900000
	area_min = 4500

	rf_min = 0.89
	rf_max = 1.2

	rp_min = 1.89
	rp_max = 2.10

	def processImage(self):
		# TODO: finish implementing this!
		return False

# Detects a large valve (orange!)
class ValveLarge:
	device_id = 2

	# Target extracted HSB value
	hsb_target = [ 9, 194, 255 ]

	# HSB Color Range (Valve)
	hsb_low = [ 0, 150, 150 ]
	hsb_high = [ 18, 200, 255 ]

	# HSV Color Range (Marker)
	mark_low = [ 35, 105, 105 ]
	mark_high = [ 55, 255, 255 ]

	area_min = 6000

	rf_min = 0.5
	rf_max = 1.5

	rp_min = 1.5
	rp_max = 3.0

	def __init__(self):
		self.thresh_low = np.array(self.hsb_low, dtype="uint8")
		self.thresh_high = np.array(self.hsb_high, dtype="uint8")

		self.mark_low = np.array(self.mark_low, dtype="uint8")
		self.mark_high = np.array(self.mark_high, dtype="uint8")

	def inRange(self, area, ratio):
		if (area < self.area_min):
			print (" * Bad Area: " + str(area))
			return (False, None)
		if ratio > 1.5:
			if (ratio > self.rp_max or ratio < self.rp_min):
				print (" * Bad Ratio: " + str(ratio))
				return (False, None)
			else:
				return (True, ORIENT_SIDE)
		else:
			if (ratio > self.rf_max or ratio < self.rf_min):
				print (" * Bad Ratio: " + str(ratio))
				return (False, None)
			else:
				return (True, ORIENT_UP)

	def inBounds(self, bounds, sel):
		(bound_x,bound_y) = bounds[0]
		(bound_w, bound_h) = bounds[1]
		bound_xmax = bound_x + bound_w
		bound_ymax = bound_y + bound_h

		(sel_x, sel_y) = sel[0]
		if (sel_x < bound_x or sel_x > bound_xmax):
			return False
		if (sel_y < bound_y or sel_y > bound_ymax):
			return False
		return True

	def findMarker(self, image, hsv_image, bounds):
		mask = cv2.inRange(hsv_image, self.mark_low, self.mark_high)
		output = cv2.bitwise_and(hsv_image, hsv_image, mask = mask)
		output_gray = cv2.cvtColor(output,cv2.COLOR_BGR2GRAY)
		ret,thresh = cv2.threshold(output_gray, 15, 255, cv2.THRESH_BINARY)

		# close any holes
		kernel = np.ones((5,5),np.uint8)
		closed_thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)

		# find the contours within bounds
		img, contours, hierarchy = cv2.findContours(closed_thresh, cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
		for cnt in contours:
			rect = cv2.minAreaRect(cnt)
			center,dim, angle = rect
			if self.inBounds(bounds, rect) and (dim[0] > 0 and dim[1] > 0):
				box = cv2.boxPoints(rect)
				box = np.int0(box)
				#cv2.drawContours(image,[box],0,(0,255,0),3)
				return rect[0]
		return (-1, -1)

	def calculateAngle(self, center, point):
		x = point[0] - center[0]
		y = point[1] - center[1]
		tan_val = y / x
		rad = np.arctan(tan_val)
		deg = np.degrees(rad)
		# IV quad
		#print (str(x) + " " + str(y))
		if (y > 0 and x > 0):
			#print ("fourth quad")
			return deg + 90
		# III quad
		elif (y > 0 and x < 0):
			#print ("3rd quad")
			return 180 + (90 - deg)
		# II quad
		elif (y < 0 and x < 0):
			#print ("2nd quad")
			return 270 + deg
		# I quad
		else:
			#print ("1st quad")
			return 90 - deg

	def processImage(self, path):
		# Load image from path as HSV
		image = cv2.imread(path)
		height, width, channels = image.shape
		img_center = ( width/2, height/2 )
		hsv_image = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

		# Mask out for desired color
		mask = cv2.inRange(hsv_image, self.thresh_low, self.thresh_high)
		output = cv2.bitwise_and(hsv_image, hsv_image, mask=mask)
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
				ret,orient = self.inRange(area, ratio)
				if (ret):
					x_offset = img_center[0] - center[0]
					#cv2.drawContours(image,[box],0,(0,0,255),3)
					mark_center = self.findMarker(image, hsv_image, rect)
					theta = self.calculateAngle(center, mark_center)
					print ("Detected large valve!")
					print (" - Horizontal offset: " + str(x_offset))
					print (" - Angle: " + str(theta))
					#cv2.imshow("image", image)
					#cv2.waitKey(0)
					#cv2.destroyAllWindows()
					return ( x_offset, orient, theta )


		#cv2.imshow("image", image)
		#cv2.waitKey(0)
		#cv2.destroyAllWindows()
		return False