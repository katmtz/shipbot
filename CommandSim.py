drive_file = "devices/actuators/DRIVE_0.txt"
depth_file = "devices/actuators/STEP_0.txt"
height_file = "devices/actuators/STEP_1.txt"

commands = [
	{ "x": "0", "y": "1", "d": "0", "h": "0"},
	{ "x": "1", "y": "0", "d": "5", "h": "5"}
]

drive_format = "@ 1\nx {x}\ny {y}"
step_format = "@ 1\nposition {p}"

for command in commands:
	drive = open(drive_file, 'w')
	msg = drive_format.format(x=command['x'], y=command['y'])
	print "<<" + msg + ">>"
	drive.write(msg)
	drive.close()

	depth = open(depth_file, 'w')
	depth.write(step_format.format(p=command['d']))
	depth.close()

	height = open(height_file, 'w')
	height.write(step_format.format(p=command['h']))
	height.close()

	updated = False
	while not updated:
		height = open(height_file, 'r')
		line = height.readline()
		if "@ 0" in line:
			updated = True

	print "Executed command."

print "Command execution complete!"