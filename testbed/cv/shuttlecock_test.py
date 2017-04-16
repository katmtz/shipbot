import numpy as np
import cv2

class Shuttlecock:

	hsb_low = [ 100, 65, 65 ]
	hsb_high = [ 120, 200, 200 ]

	rv_low = 2.8
	rv_high = 4.5
	rh_low = .2
	rh_high = .3

	area_min = 2000
	area_max = 1000000

	def __init__(self, path):
		self.path = path
		self.np_low = np.array(self.hsb_low, dtype="uint8")
		self.np_high = np.array(self.hsb_high, dtype="uint8")

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

	def loadImage(self):
		# load the image
		image = cv2.imread(self.path)
		hsv_image = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

		# mask it for the desired color as a binary
		mask = cv2.inRange(hsv_image, self.np_low, self.np_high)
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
						cv2.drawContours(image,[box],0,(0,0,255),2)
					else:
						cv2.drawContours(image,[box],0,(0,255,0),2)

					print ("Detected {orient} shuttlecock!".format(orient=orient))
					print ("position: " + str(center[0]) + " " + str(center[1]))
					print ("ratio: " + str(ratio))
					print ("area: " + str(area))
					print ("angle: " + str(angle))

		cv2.imshow("image", image)
		cv2.waitKey(0)
		cv2.destroyAllWindows()

print ("standard")
img = Shuttlecock("imgs/shuttlecock_img.jpg")
img.loadImage()

print ("angled")
angle = Shuttlecock("imgs/shuttlecock_angled.jpg")
angle.loadImage()

print ("blurred")
blur = Shuttlecock("imgs/shuttlecock_blur.jpg")
blur.loadImage()

print ("horiz")
horiz = Shuttlecock("imgs/shuttlecock_horiz.jpg")
horiz.loadImage()

print ("lowres")
lowres = Shuttlecock("imgs/shuttlecock_lowres.jpg")
lowres.loadImage()



