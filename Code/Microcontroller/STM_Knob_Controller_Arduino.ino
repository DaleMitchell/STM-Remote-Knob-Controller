#include <Arduino.h>
#include <EEPROM.h>
#include <Servo.h>
#include <Stepper.h>

/*
 * Pin Constants
 */
//stepperX
int in1PinX = 13;
const int in2PinX = 12;
const int in3PinX = 11;
const int in4PinX = 10;

//stepperY
const int in1PinY = 9;
const int in2PinY = 8;
const int in3PinY = 7;
const int in4PinY = 6;

//stepperZ
const int in1PinZ = 5;
const int in2PinZ = 4;
const int in3PinZ = 3;
const int in4PinZ = 2;

//servos
const int servoPinX = A0;
const int servoPinY = A1;
const int servoPinZ = A2;

//Create Stepper objects.
Stepper stepperX(513, in1PinX, in2PinX, in3PinX, in4PinX);
Stepper stepperY(513, in1PinY, in2PinY, in3PinY, in4PinY);
Stepper stepperZ(513, in1PinZ, in2PinZ, in3PinZ, in4PinZ);

//Create Servo objects.
Servo servoX, servoY, servoZ;

/*
 * User input variables.
 */
//Settings for speed in RPM.
int motorSpeed = 20;

//Steps for stepper motors.
int stepsX = 0;
int stepsY = 0;
int stepsZ = 0;

//Degrees for servo motors.
int degreesX = 0;
int degreesY = 0;
int degreesZ = 0;

//Strings and chars to parse from input.
// (ex. X_G_10 = X-axis, Gain knob,    change gain to 10)
// (ex. Z_V_10 = Z-axis, Voltage knob, move 10 degrees)
String userInput = "";
char axisInput; // userInput.charAt(0);
char knobTypeInput; // userInput.charAt(2);
int stepsInput = 0; // userInput.substring(4);
int degreesInput = 0; // userInput.substring(4);

int calibrationData[][4] = { { 1, 85, 85, 90 },
							 { 3, 55, 55, 60 },
							 { 10, 25, 25, 25 },
							 { 30, 0, 0, 0 } };

//EEPROM for servos.
//int lastDegreesX = EEPROM.read(0);
//int lastDegreesY = EEPROM.read(1);
//int lastDegreesZ = EEPROM.read(2);

void setup() {
	pinMode(in1PinX, OUTPUT);
	pinMode(in2PinX, OUTPUT);
	pinMode(in3PinX, OUTPUT);
	pinMode(in4PinX, OUTPUT);

	pinMode(in1PinY, OUTPUT);
	pinMode(in2PinY, OUTPUT);
	pinMode(in3PinY, OUTPUT);
	pinMode(in4PinY, OUTPUT);

	pinMode(in1PinZ, OUTPUT);
	pinMode(in2PinZ, OUTPUT);
	pinMode(in3PinZ, OUTPUT);
	pinMode(in4PinZ, OUTPUT);

//	servoX.write(lastDegreesX);
//	servoY.write(lastDegreesY);
//	servoZ.write(lastDegreesZ);
//
//	servoX.attach(servoPinX);
//	servoY.attach(servoPinY);
//	servoZ.attach(servoPinZ);

	// wait for serial port to connect. Needed for native USB port only
	while (!Serial)
		;
	Serial.setTimeout(100);
	Serial.begin(115200);
	stepperX.setSpeed(motorSpeed);
	stepperY.setSpeed(motorSpeed);
	stepperZ.setSpeed(motorSpeed);

}

// Parsing the String
// (ex. X_V_10 = X-axis, Voltage knob, move 10 degrees)
// (ex. Z_G_10 = Z-axis, Gain knob,    change gain to 10)
// Add functionality to just change voltage according to gain (10 degrees * gain 5 = __ volts)

// V for Fine Knobs (Voltage), stepper.
// G for Clunky Knobs (Gain), servo motors.

void loop() {
	if (Serial.available()) {
		Serial.println(calibrationDataToString());
		Serial.println("Acknowledged connection!\n");
		Serial.println("The syntax to control knobs is the following:");
		Serial.println("Axis_Type_Position\n");
		Serial.println("Example command: ");
		Serial.println(
				"\"Y_V_50\"      Move voltage knob on Y axis 50 steps clockwise");
		Serial.println(
				"\"X_G_30\"      Move gain knob on X axis to the 30x position\n");
		userInput = Serial.readString();
		if (userInput.charAt(0) == 'L') {
			Serial.print(calibrationDataToString());
		} else if (userInput.charAt(0) == 'S') {
			Serial.setTimeout(3000);
			Serial.println("Ready for calibration input: ");
			String calibrationInput = Serial.readStringUntil('\n');
			calibrationDataToArray(calibrationInput);
			Serial.println("Success");
			Serial.setTimeout(50);
		} else if (userInput.length() >= 5) {
			axisInput = userInput.charAt(0);
			knobTypeInput = userInput.charAt(2);
			if (knobTypeInput == 'V') {
				stepsInput = userInput.substring(4).toInt();
				Serial.println(String(userInput.charAt(0)));
				switch (axisInput) {
				case 'X':
					stepperX.step(stepsInput);
					break;
				case 'Y':
					stepperY.step(stepsInput);
					break;
				case 'Z':
					stepperZ.step(stepsInput);
					break;
				default:
					Serial.println(
							"Input Error: Invalid axis, must be X, Y, or Z for charAt(0)");
					break;
				}
				Serial.println(
						"Moved " + String(stepsInput) + " steps on axis ");

			} else if (knobTypeInput == 'G') {
				boolean validGain = true;
				boolean validAxis = true;
				int calRow = userInput.substring(4).toInt();
				switch (calRow) {
				case 1:
					calRow = 0;
					break;
				case 3:
					calRow = 1;
					break;
				case 10:
					calRow = 2;
					break;
				case 30:
					calRow = 3;
					break;
				default:
					validGain = false;
					Serial.println(
							"Input Error: Invalid gain, must be 1, 3, 10, or 30 for substring(4)");
				}
				if (validGain) {
					int degrees = 0;
					Serial.println(String(userInput.charAt(0)));
					switch (axisInput) {
					case 'X':
						degrees = calibrationData[calRow][1];
						servoX.write(degrees);
						servoX.attach(servoPinX);
						delay(2000);
						servoX.detach();
						//EEPROM.update(0, degrees);
						break;
					case 'Y':
						degrees = calibrationData[calRow][2];
						servoX.write(degrees);
						servoX.attach(servoPinX);
						delay(2000);
						servoX.detach();
						//EEPROM.update(1, degrees);
						break;
					case 'Z':
						degrees = calibrationData[calRow][3];
						servoX.write(degrees);
						servoX.attach(servoPinX);
						delay(2000);
						servoX.detach();
						//EEPROM.update(2, degrees);
						break;
					default:
						validAxis = false;
						Serial.println(
								"Input Error: Invalid axis, must be X, Y, or Z for charAt(0)");
						break;
					}
					if (validAxis) {
						Serial.println(
								"Moved " + String(degrees) + " degrees on "
										+ axisInput + " axis ");
					}
				}

			} else {
				Serial.println(
						"Input Error: Invalid knobType, must be G or V for charAt(2)");
			}
		} else {
			Serial.println("Input Error: Does not satisfy minimum length of 5");
		}
	}
}

String calibrationDataToString(){
	int iMax = 3;
	int jMax = 3;
	String result;
	result = "[";
    for (int i = 0 ; i < 4 ; i++){
    	result += "[";
        for (int j = 0 ; j < 4 ; j++){
            result += calibrationData[i][j];
            if(j == jMax){
            	result += "]";
            } else {
            	result += ", ";
            }
        }
        if(i == iMax){
        	result += "]";
        } else {
        	result += ", ";
        }
    }
	return result;
}

void calibrationDataToArray(String input) {
    int row = 0;
    int col = 0;
    for (int i = 0; i < input.length(); i++) {
        if (input.charAt(i) == '[') {
            row++;
        }
    }
    row--;
    for (int i = 0;; i++) {
        if (input.charAt(i) == ',') {
            col++;
        }
        if (input.charAt(i) == ']') {
            break;
        }
    }
    col++;
    input.replace("\\[", "");
    input.replace("\\]", "");
    char inputArray[input.length() + 1];
    input.toCharArray(inputArray, input.length());
    Serial.println("test 1");
    char* s1 = strtok(inputArray, ", ");

    for (int i = 0, j = -1; i < input.length(); i++) {
        if (i % col == 0) {
            j++;
        }
        calibrationData[j][i % col] = s1[i];

    }
}
