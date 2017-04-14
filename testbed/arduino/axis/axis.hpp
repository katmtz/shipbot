/** \file axis.hpp
 *	\author Thibault Wildi(twildi)
 *  \date 2017-04-06
 */

#ifndef _AXIS_H_
#define _AXIS_H_

#include <stdint.h>
#include <Arduino.h>

/**< Speed at which the axis initialize [mm/s] */
#define INIT_SPEED 20

/**< Speed at which the axis initialize [mm/s^2] */
#define ACC_SPEED 50


/**< Return type for Axis Class */
enum error_t {ERROR_HIT_END = -1,
              ERROR_UNREACHABLE = -2
             };

class Axis
{
private:
	const uint16_t resolution;      /**< Steps per full rotation */
	const uint8_t uSteppingFactor;
	const uint8_t pitch;            /**< In [mm] */
	const uint8_t dirPin;
	const uint8_t stepPin;
	const uint8_t endPin;
	const uint8_t maxFreq;         /**< Maximum speed [rotation/s] */
	uint32_t pos;                   /**< Position in uSteps */
	uint32_t length;                /**< Length of the axis in uSteps */
	bool hitFlag;

public:
	Axis(uint8_t sPin, uint8_t dPin, uint8_t ePin, uint8_t uFac = 1,
	     uint8_t maxSpd = 10, uint8_t p = 2, uint16_t res = 200);
	error_t init(void);
	error_t moveTo(uint16_t mm);
	error_t move(uint16_t mm);
	void sigHit(void);

private:
	error_t moveUSteps(int32_t uSteps);
	void hit(void);
};

#endif // _AXIS_H_
