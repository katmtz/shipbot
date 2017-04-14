/** \file axis.cpp
 *	\author Thibault Wildi(twildi)
 *  \date 2017-04-06
 */


#include "axis.hpp"
#include <stdlib.h>

// Constructor with variable initialization
/** \brief Class creator
 *
 * \param sPin Step pin
 * \param dPin Direction Pin
 * \param ePin End switch Pin
 * \param uFac Micro-Stepping Factor
 * \param maxSpd Maximum motor speed (rotation/s)
 * \param p Pitch of lead screw
 * \param res Resolution (steps/rotation)
 */
Axis::Axis(uint8_t sPin,
           uint8_t dPin,
           uint8_t ePin,
           uint8_t uFac,
           uint8_t maxSpd,
           uint8_t p,
           uint16_t res):
	stepPin(sPin),
	dirPin(dPin),
	endPin(ePin),
	resolution(res),
	uSteppingFactor(uFac),
	pitch(p),
	hitFlag(false),
	maxFreq(maxSpd*res*uFac)
{
	// Set the pin modes
	pinMode(sPin, OUTPUT);
	pinMode(dPin, OUTPUT);
	pinMode(ePin, INPUT);

	//Write defaults values
	digitalWrite(sPin, LOW);
	digitalWrite(dPin, LOW);
}


/** \brief Initializes the axis by finding it's reference
 *
 * \param void
 * \return error_t
 */
error_t Axis::init(void)
{
	uint32_t i = 0;
	uint32_t freq = ((uint32_t)INIT_SPEED * uSteppingFactor * resolution)/pitch;

	// Move until you hit first end
	digitalWrite(dirPin, HIGH);
	tone(stepPin, freq);
	while(!hitFlag)
		continue;

	hitFlag = false;
	pos = 0;

	// Move until you hit second end (manual tone)
	digitalWrite(dirPin, LOW);
	for(i = 0; hitFlag == false; i++)
	{
		digitalWrite(stepPin, HIGH);
		delayMicroseconds((uint32_t)1000000/(2*freq));
		digitalWrite(stepPin, LOW);
		delayMicroseconds((uint32_t)1000000/(2*freq));
	}

	// Save length and update position
	hitFlag = false;
	length = i;
	pos = length;

	// Move to the middle
	moveUSteps(-length/2);
}


/** \brief Moves the axis to a set position
 *
 * \param mm uint16_t
 * \return error_t
 *
 */
error_t Axis::moveTo(uint16_t mm)
{
	return moveUSteps(mm*resolution*uSteppingFactor/pitch - pos);
}


/** \brief Move the axis to by a set distance
 *
 * \param mm uint16_t
 * \return error_t
 *
 */
error_t Axis::move(uint16_t mm)
{
	return moveUSteps(mm*resolution*uSteppingFactor/pitch);
}


/** \brief Moves the axis by a set number of uSteps
 *
 * \param uSteps int32_t
 * \return error_t
 *
 */
error_t Axis::moveUSteps(int32_t uSteps)
{
	uint32_t dfreq;
	uint32_t nSteps = 0;
	uint32_t freq = 0;
	uint32_t prevMicro, currentMicro;

	// Check Range
	if((pos + uSteps < 0) || (pos + uSteps > length))
		return ERROR_UNREACHABLE;

	// Check direction
	if(uSteps == 0)
	{
		return EXIT_SUCCESS;
	}
	else if(uSteps < 0)
	{
		digitalWrite(dirPin, HIGH);
		uSteps = -uSteps;
	}
	else
	{
		digitalWrite(dirPin, LOW);
	}

	// Acceleration, steps of 10ms
	dfreq = ((uint32_t)uSteppingFactor * ACC_SPEED * resolution)/(pitch * 100);
	for(prevMicro = micros() ; nSteps < uSteps/2 ; freq += dfreq)
	{
		// Move
		tone(stepPin,freq);
		delay(10);

		// Check for collision
		if(hitFlag)
		{
			hitFlag = false;
			return ERROR_HIT_END;
		}

		// Update position
		currentMicro = micros();
		nSteps += (currentMicro - prevMicro)*freq/1000000 ;
		prevMicro = currentMicro;

		// Update Speed
		if((nSteps < uSteps/2) && (freq < maxFreq))
		{
			freq += dfreq;
			freq = (freq > maxFreq)?maxFreq:freq;	// Cap freq

		}
	}

	// Stop
	digitalWrite(stepPin, LOW);

	if(uSteps > 0)
		pos += nSteps;
	else
		pos -= nSteps;

	return EXIT_SUCCESS;
}

void Axis::sigHit(void)
{
	hitFlag = true;
}

