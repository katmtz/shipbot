#!/bin/bash

echo "Initializing Java control system."
java -jar controlsys.jar -classpath shipbot.mission.Mission > javaout.txt &
echo "Control system initialized."

echo "Establishing serial pipeline to Arduino."
python SerialPipeline.py

echo "System demo done, java debug log is out.txt"
