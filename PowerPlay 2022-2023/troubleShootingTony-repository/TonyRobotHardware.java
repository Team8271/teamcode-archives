/* Copyright (c) 2022 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.util.Encoder;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.Base64;

public class TonyRobotHardware {

    /* Declare OpMode members. */
    private TonyComboOpMode myOpMode = null;   // gain access to methods in the calling OpMode.
    private BasicParkTony myAutonomous = null;

    //Values captured from RoadRunner calibrations
    public static double TICKS_PER_REV = 8192;
    public static double WHEEL_DIAMETER_INCHES = 2; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed
    static final double COUNTS_PER_INCH = (TICKS_PER_REV * GEAR_RATIO) / (WHEEL_DIAMETER_INCHES * 3.1415);

/*  Roadrunner stuff
    public static double LATERAL_DISTANCE = 12.232; // in; distance between the left and right wheels
    public static double FORWARD_OFFSET = -4.375; // in; offset of the lateral wheel
    public static double X_MULTIPLIER = 0.972; //Multiplier in the X direction
    public static double Y_MULTIPLIER = 0.978; //Multiplier in the Y direction
*/

    // Define Motor and Servo objects  (Make them private so they can't be accessed externally)
    public DcMotor frontRight = null;
    public DcMotor frontLeft = null;
    public DcMotor backRight = null;
    public DcMotor backLeft = null;

    public DcMotor liftL = null;
    public DcMotor liftR = null;
    public DcMotor armMotor = null;

    //declare Dead Wheel encoders
    public Encoder leftEncoder, rightEncoder, backEncoder;

    //don't believe these are being used
    //public double leftEncoderPosInch;
    //public double rightEncoderPosInch;

    public Servo wrist = null;
    public Servo palmL = null;
    public Servo palmR = null;

    public Servo podLeft = null;
    public Servo podRight = null;
    public Servo podBack = null;

    public TouchSensor liftTouch = null;

    //IMU VARIABLES

    public BNO055IMU imu = null;

    Orientation angles;
    Acceleration gravity;

    public double          targetHeading = 0;
    public double          robotHeading  = 0;
    public double          headingOffset = 0;
    public double          headingError  = 0;

    public static final double     P_TURN_GAIN        = 0.02;     // Larger is more responsive, but also less stable
    public static final double     P_DRIVE_GAIN       = 0.03;     // Larger is more responsive, but also less stable

    //region Motor Hold Variables
    double armHoldMax   = 0;
    double armHoldMin   = 0;

    int armHoldpos;
    int liftLHoldPos;
    int liftRHoldPos;

    double slopeVal     = 1400;

    //various variables
    int liftLTarPos = 2200;
    int liftRTarPos = 2200;

    int armTarPos = 1500;

    HardwareMap hwMap = null;

    // *  Define a constructor that allows the OpMode to pass a reference to itself.
    public TonyRobotHardware() {
    }

    public void init(HardwareMap ahwMap)    {

        hwMap = ahwMap;

        //define and initialize odometry encoders
        leftEncoder = new Encoder(hwMap.get(DcMotorEx.class, "FL"));
        rightEncoder = new Encoder(hwMap.get(DcMotorEx.class, "encoderRight"));
        backEncoder = new Encoder(hwMap.get(DcMotorEx.class, "FR"));

        podLeft = hwMap.servo.get("podL");
        podRight = hwMap.servo.get("podR");
        podBack = hwMap.servo.get("podB");

        // Define and Initialize Motors
        frontLeft = hwMap.get(DcMotor.class, "FL");
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontRight = hwMap.get(DcMotor.class, "FR");
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backLeft = hwMap.get(DcMotor.class, "BL");
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backRight = hwMap.get(DcMotor.class, "BR");
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Define and Initialize Lift motors
        liftL = hwMap.get(DcMotor.class, "liftL");
        liftL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        liftR = hwMap.get(DcMotor.class, "liftR");
        liftR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //BRAKE behavior does not appear to work.
       // liftR.setMode(DcMotor.ZeroPowerBehavior.BRAKE);
       // liftL.setMode(DcMotor.ZeroPowerBehavior.BRAKE);


        armMotor = hwMap.dcMotor.get("arm");

        armHoldpos = armMotor.getCurrentPosition();
        liftLHoldPos = liftL.getCurrentPosition();
        liftRHoldPos = liftR.getCurrentPosition();



        //set drive motor directions
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        liftL.setDirection(DcMotor.Direction.REVERSE);
        liftR.setDirection(DcMotor.Direction.FORWARD);

        //servos
        wrist = hwMap.servo.get("wrist");
        palmL = hwMap.servo.get("palmL");
        palmR = hwMap.servo.get("palmR");

        wrist.setPosition(1);
        palmL.setPosition(1);
        palmR.setPosition(0);

        liftTouch = hwMap.get(TouchSensor.class, "liftTouch");

        //GYRO VARIABLES
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

    }

}
