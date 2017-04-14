#include "axis.hpp"
#include <SPI.h>
#include <AMIS30543.h>    // Stepper motor driver lib

// Y Axis
#define Y_AXIS_SS_PIN 0
#define Y_AXIS_DIR_PIN 0
#define Y_AXIS_STEP_PIN 0
#define Y_USTEP_FACTOR 32
#define Y_AXIS_HITPIN 0
#define Y_MAX_SPD 10      // In rot/S
#define Y_PITCH 4         // In mm


// Z Axis
#define Z_AXIS_SS_PIN 0
#define Z_AXIS_DIR_PIN 0
#define Z_AXIS_STEP_PIN 0
#define Z_USTEP_FACTOR 16
#define Z_AXIS_HITPIN 0
#define Z_MAX_SPD 10      // In rot/S
#define Z_PITCH 2         // In mm

// Stepper Objects
AMIS30543 y_stepper;
AMIS30543 z_stepper;

// Axis Objects
Axis y_axis(Y_AXIS_STEP_PIN, Y_AXIS_DIR_PIN, Y_AXIS_HITPIN, Y_USTEP_FACTOR, Y_MAX_SPD, Y_PITCH);
Axis z_axis(Z_AXIS_STEP_PIN, Z_AXIS_DIR_PIN, Z_AXIS_HITPIN, Z_USTEP_FACTOR, Z_MAX_SPD, Z_PITCH);


void setup(void)
{
  Serial.begin(9600);
  SPI.begin();
  
  y_stepper.init(Y_AXIS_SS_PIN);
  z_stepper.init(Z_AXIS_SS_PIN);

  // Drive the NXT/STEP and DIR pins low initially.
  digitalWrite(Y_AXIS_DIR_PIN, LOW);
  pinMode(Y_AXIS_DIR_PIN, OUTPUT);
  digitalWrite(Y_AXIS_STEP_PIN, LOW);
  pinMode(Y_AXIS_STEP_PIN, OUTPUT);
  digitalWrite(Z_AXIS_DIR_PIN, LOW);
  pinMode(Z_AXIS_DIR_PIN, OUTPUT);
  digitalWrite(Z_AXIS_STEP_PIN, LOW);
  pinMode(Z_AXIS_STEP_PIN, OUTPUT);

  // Give the driver some time to power up.
  delay(1);

  // Stepper current limit
  y_stepper.setCurrentMilliamps(2048);
  z_stepper.setCurrentMilliamps(2048);

  // uStepping Factor
  y_stepper.setStepMode(Y_USTEP_FACTOR);
  z_stepper.setStepMode(Z_USTEP_FACTOR);

  // Enable the motor outputs.
  y_stepper.enableDriver();
  z_stepper.enableDriver();

  // Attach interrupts
  attachInterrupt(Z_AXIS_HITPIN, z_hit, RISING); 
  attachInterrupt(Y_AXIS_HITPIN, y_hit, RISING); 
}


void loop(void)
{
  static char cmd;
  static int32_t param;
  
  // Wait for axis
  while(!Serial.available())
    delay(50);
 
  // Read axis
  switch(Serial.read())
  {
    // Y axis
    case 'y':
      while(!Serial.available())
        delay(50);
        
      // Read cmd
      switch(Serial.read())
      {
        case 'i':
          Serial.println(y_axis.init());
          break;
          
        case 'r':
          while(!Serial.available())
            delay(50);
          param = Serial.parseInt();
          Serial.println(y_axis.move(param));
          break;
          
        case 'a':
          while(!Serial.available())
            delay(50);
          param = Serial.parseInt();
          Serial.println(y_axis.moveTo(param));
          break;;
      }
      break;

    // Z Axis
    case 'z':
      while(!Serial.available())
        delay(50);
        
      // Read cmd
      switch(Serial.read())
      {
        case 'i':
          Serial.println(z_axis.init());
          break;
          
        case 'r':
          while(!Serial.available())
            delay(50);
          param = Serial.parseInt();
          Serial.println(z_axis.move(param));
          break;
          
        case 'a':
          while(!Serial.available())
            delay(50);
          param = Serial.parseInt();
          Serial.println(z_axis.moveTo(param));
          break;;
      }
      break;
  }
}

void z_hit(void)
{
  digitalWrite(Z_AXIS_SS_PIN, LOW);
  z_axis.sigHit();
}

void y_hit(void)
{
  digitalWrite(Y_AXIS_SS_PIN, LOW);
  y_axis.sigHit();
}


