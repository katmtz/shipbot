import numpy as np
import cv2

class ValveSmall:

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

	def __init__(self, path):
		self.path = path
		self.np_low = np.array(self.blue_low, dtype="uint8")
		self.np_high = np.array(self.blue_high, dtype="uint8")

		self.np_m_l = np.array(self.mark_low, dtype="uint8")
		self.np_m_h = np.array(self.mark_high, dtype="uint8")

	def inRange(self, area, ratio):
		if (area < self.area_min):
			return (False, None)
		if ratio > 1.5:
			if (ratio > self.rp_max or ratio < self.rp_min):
				return (False, None)
			else:
				return (True, "profile")
		else:
			if (ratio > self.rf_max or ratio < self.rf_min):
				return (False, None)
			else:
				return (True, "front")

	def load(self):
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
			if len(cnt) > 4:
				ellipse = cv2.fitEllipse(cnt)
				center,dim, angle = ellipse
				if (dim[0] > 0 and dim[1] > 0):
					area = dim[0] * dim[1]
					ratio = dim[1] / dim[0]
					ret, orient = self.inRange(area, ratio)
					if (ret):
						if (orient is "front"):
							cv2.ellipse(image,ellipse,(0,255,0),2)
						else:
							cv2.ellipse(image,ellipse,(0,0,255),2)
						print ("Detected blue {orient} facing valve.".format(orient=orient))
						print ("position: " + str(center[0]) + " " + str(center[1]))
						print ("ratio: " + str(ratio))
						print ("area: " + str(area))
						#print ("angle: " + str(angle))

		cv2.imshow("image", image)
		cv2.waitKey(0)
		cv2.destroyAllWindows()

f_l = ValveSmall("imgs/valve_front_lite.jpg")
f_l.load()

p_lo = ValveSmall("imgs/valve_prof_lores.jpg")
p_lo.load()

f_d = ValveSmall("imgs/valve_front_dark.jpg")
f_d.load()

f_lsm = ValveSmall("imgs/valve_front_litesm.jpg")
f_lsm.load()

p_hi = ValveSmall("imgs/valve_prof_hires.jpg")
p_hi.load()





