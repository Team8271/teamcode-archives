
package org.firstinspires.ftc.teamcode;


import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorDigitalTouch;


/**
 * Created by TeameurekaRobotics on 12/30/2016
 *
 * This file contains an example Hardware Setup Class.
 *
 * It can be customized to match the configuration of your Bot by adding/removing hardware, and then used to instantiate
 * your bot hardware configuration in all your OpModes. This will clean up OpMode code by putting all
 * the configuration here, needing only a single instantiation inside your OpModes and avoid having to change configuration
 * in all OpModes when hardware is changed on robot.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 *
 */

public class Tank8271HardwareSetup {

   /* Declare Public OpMode members.
    *these are the null statements to make sure nothing is stored in the variables.
    */

    //motors
    public DcMotor motorBL = null;
    public DcMotor motorBR = null;
    public DcMotor motorFL = null;
    public DcMotor motorFR = null;
    public DcMotor slideMotor = null;
    public DcMotor armMotor = null;
    public DcMotor duckBlue = null;
    public DcMotor duckRed = null;

    //gyro
    public BNO055IMU imu;

    //servos
    public Servo servoHandL = null;
    public Servo servoHandR = null;

    //sensors
   // public GyroSensor gyro  = null;
    public TouchSensor rumbleTouch = null;
    public TouchSensor magC = null;
    public TouchSensor magE = null;

    /* local OpMode members. */
    HardwareMap hwMap        = null;

    //Create and set default servo positions & MOTOR STOP variables.
    //Possible servo values: 0.0 - 1.0  For CRServo 0.5=stop greater or less than will spin in that direction
    final static double CLOSED = 0.3;
    final static double OPEN = 0.7;
    final static double MOTOR_STOP = 0.0; // sets motor power to zero
    // CRservo init positions
    double SpinLeft = 0.3;
    double SpinRight = 0.5;
    double STOP = 0.5;

    //Motor hold variables
    double  armMinPos        = 0.0;      // encoder position for arm at bottom
    double  armMaxPos        = 5380.0;   // encoder position for arm at top
    int     armHoldPosition;             // reading of arm position when buttons released to hold
    double  slopeVal         = 2000.0;   // increase or decrease to perfect holding power

    /* Constructor   // this is not required as JAVA does it for you, but useful if you want to add
    * function to this method when called in OpModes.
    */
    public Tank8271HardwareSetup() {
    }

    //Initialize standard Hardware interfaces
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        /************************************************************
         * MOTOR SECTION
         ************************************************************/
        // Define Motors to match Robot Configuration File
        motorBL = hwMap.dcMotor.get("motorBL");
        motorBR = hwMap.dcMotor.get("motorBR");
        motorFL = hwMap.dcMotor.get("motorFL");
        motorFR = hwMap.dcMotor.get("motorFR");
        slideMotor = hwMap.dcMotor.get("slideMotor");
        armMotor = hwMap.dcMotor.get("motorArm");
        duckBlue = hwMap.dcMotor.get("duckBlue");
        duckRed = hwMap.dcMotor.get("duckRed");

        // Set the drive motor directions:
        motorBL.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        motorBR.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        motorFL.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        motorFR.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        armMotor.setDirection(DcMotor.Direction.FORWARD);
        //duckSpin.setDirection(DcMotor.Direction.FORWARD);

        //Keep the motors from moving during initialize.
        motorBL.setPower(MOTOR_STOP);
        motorBR.setPower(MOTOR_STOP);
        motorFL.setPower(MOTOR_STOP);
        motorFR.setPower(MOTOR_STOP);
        armMotor.setPower(MOTOR_STOP);
        duckBlue.setPower(MOTOR_STOP);
        duckRed.setPower(MOTOR_STOP);

        // Set motors to run USING or WITHOUT encoders
        // Depending upon your configuration and use
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //armMotor.getZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //this appears to have been the problem. If set to run WITH encoders it would crash
        duckBlue.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        duckRed.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        /************************************************************
         * SERVO SECTION
         ************************************************************/
        // Define Motors to match Robot Configuration File
        servoHandR = hwMap.servo.get("servoL"); //contPort-0
        servoHandL = hwMap.servo.get("servoR"); //expanPort-1

        //Set servo hand grippers to closed.
        servoHandL.setPosition(0.7);
        servoHandR.setPosition(0.3);;

        //Continous Rotation Servo
        //basketServo.setPosition(STOP);

        /************************************************************
         * SENSOR SECTION
         ************************************************************/
        //Define sensors
        //gyro = hwMap.gyroSensor.get("gyro");
        rumbleTouch = hwMap.touchSensor.get("rumble");
        magC = hwMap.touchSensor.get("magC");
        magE = hwMap.touchSensor.get("magE");

        //gyro parameters
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public void setAllPower(double p) { setMotorPower(p, p, p, p); }

    public void setMotorPower(double FR, double FL, double BR, double BL)
    {
        motorFR.setPower(FR);
        motorFL.setPower(FL);
        motorBR.setPower(BR);
        motorBL.setPower(BL);
    }

}

