import numpy as np
import cv2

class Shuttlecock:

	# LOW -> HIGH color thresholds in BGR format
	rgb_low = [ 120, 0, 0 ]
	rgb_high = [ 255, 84, 68 ]

	def __init__(self, path):
		self.path = path
		self.np_low = np.array(self.rgb_low, dtype="uint8")
		self.np_high = np.array(self.rgb_high, dtype="uint8")

	def loadImage(self):
		# load the image
		image = cv2.imread(self.path)

		# mask it for the desired color as a binary
		mask = cv2.inRange(image, self.np_low, self.np_high)
		output = cv2.bitwise_and(image, image, mask = mask)
		output_gray = cv2.cvtColor(output,cv2.COLOR_BGR2GRAY)
		ret,thresh = cv2.threshold(output_gray, 15, 255, cv2.THRESH_BINARY)

		# close any holes
		kernel = np.ones((5,5),np.uint8)
		closed_thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel)
		
		# find the contours
		img, contours, hierarchy = cv2.findContours(closed_thresh, cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
		
		if (len(contours) > 0):
			cnt = contours[0]
			rect = cv2.minAreaRect(cnt)
			box = cv2.boxPoints(rect)
			box = np.int0(box)
			cv2.drawContours(image,[box],0,(0,0,255),2)

		cv2.imshow("image", image)
		cv2.waitKey(0)
		cv2.destroyAllWindows()

shuttlecock = Shuttlecock("shuttlecock_img.jpg")
shuttlecock.loadImage()
#shuttlecock.filter()
