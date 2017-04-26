# Manages file I/O, image captures, and using correct image processing objects.
from DeviceRecognition import ValveLarge

path = "imgs/capture.jpeg"
valve = ValveLarge()
retval = valve.processImage(path)
if not (retval):
	print ("Nothing detected!")
else:	
	(offset,orient,angle) = retval
	print ("Offset: " + str(offset))
	print ("Orientation (1=horizontal): " + str(orient))
	print ("Angle: " + str(angle))