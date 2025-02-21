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

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * This file works in conjunction with the External Hardware Class sample called: ConceptExternalHardwareClass.java
 * Please read the explanations in that Sample about how to use this class definition.
 *
 * This file defines a Java Class that performs all the setup and configuration for a sample robot's hardware (motors and sensors).
 * It assumes three motors (left_drive, right_drive and arm) and two servos (left_hand and right_hand)
 *
 * This one file/class can be used by ALL of your OpModes without having to cut & paste the code each time.
 *
 * Where possible, the actual hardware objects are "abstracted" (or hidden) so the OpMode code just makes calls into the class,
 * rather than accessing the internal hardware directly. This is why the objects are declared "private".
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with *exactly the same name*.
 *
 * Or.. In OnBot Java, add a new file named RobotHardware.java, drawing from this Sample; select Not an OpMode.
 * Also add a new OpMode, drawing from the Sample ConceptExternalHardwareClass.java; select TeleOp.
 *
 */

public class TonyRobotHardware {

    /* Declare OpMode members. */
    private TonyOmniOpMode myOpMode = null;   // gain access to methods in the calling OpMode.

    // Define Motor and Servo objects  (Make them private so they can't be accessed externally)
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;

    public DcMotor encoderLeft;
    public DcMotor encoderRight;
    public DcMotor encoderAux;

    // Define a constructor that allows the OpMode to pass a reference to itself.
    public TonyRobotHardware(TonyOmniOpMode opmode) {
        myOpMode = opmode;
    }

    public void init()    {
        // Define and Initialize Motors (note: need to use reference to actual OpMode).
        frontLeft = myOpMode.hardwareMap.get(DcMotor.class, "FL");
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontRight = myOpMode.hardwareMap.get(DcMotor.class, "FR");
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backLeft = myOpMode.hardwareMap.get(DcMotor.class, "BL");
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backRight = myOpMode.hardwareMap.get(DcMotor.class, "BR");
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        encoderLeft  = frontRight;
        encoderRight = frontLeft;
        encoderAux  = backRight;

        // If there are encoders connected, switch to RUN_USING_ENCODER mode for greater accuracy
        // leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // RightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        ResetDriveEncoders();
    }

    public void ResetDriveEncoders()
    {
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    //constants that define the geometry of the robot
    final static double L = 25; //distance between encoders 1 and 2 in cm
    final static double B = 7; //distance between the midpoint of encoder 1 and 2 and encoder 3
    final static double R = 2.5; //wheel radius in cm
    final static double N = 8192; //encoder ticks per revolution, REV ENCODER
    final static double cm_per_tick = 2.0 * Math.PI * R / N;

    //keep track of encoder positions between updates
    public int currentRightPos = 0;
    public int currentLeftPos = 0;
    public int currentAuxPos = 0;

    private int oldRightPos = 0;
    private int oldLeftPos = 0;
    private int oldAuxPos = 0;

    /***************************************************************\
     * Odometry Notes!
     *
     * n1, n2, & n3 are encoder values for left, right, and front (aux) omni-wheels
     * dn1, dn2, & dn3 are the differences of encoder values between two reads
     * dX, dY, & dTheta describe the robot movement between two reads (in field coordinates)
     * X, Y, & Theta are the coordinates on the field and the heading of the robot
     \***************************************************************/

    //XYHVector is a tuple (x, y, h) where h is the heading of the robot

    public XyhVector START_POS = new XyhVector(0, 0, Math.toRadians(0));
    public XyhVector pos = new XyhVector(START_POS);

    public XyhVector targetPos = new XyhVector(50, 50, 0);

    public void CalculateOdometry()
    {
        oldRightPos = currentRightPos;
        oldLeftPos = currentLeftPos;
        oldAuxPos = currentAuxPos;

        currentRightPos = -encoderRight.getCurrentPosition();
        currentLeftPos = -encoderLeft.getCurrentPosition();
        currentAuxPos = encoderAux.getCurrentPosition();

        int dn1 = currentLeftPos - oldLeftPos;
        int dn2 = currentRightPos - oldRightPos;
        int dn3 = currentAuxPos - oldAuxPos;

        //robot has moved and turned a tiny bit in between movements
        double dTheta = cm_per_tick * (dn2-dn1) / L;
        double dX = cm_per_tick * (dn1 + dn2) / 2;
        double dY = cm_per_tick * (dn3 - (dn2 - dn1) * B / L);

        //small movement of the robot gets added to the field coordinate system
        double theta = pos.h + (dTheta / 2.0);
        pos.x += dX * Math.cos(theta) - dY * Math.sin(theta);
        pos.y += dX * Math.sin(theta) + dY * Math.cos(theta);
        pos.h += dTheta;
    }

    public void DriveOdo(XyhVector TargetPos, double speed)
    {
        double CX = TargetPos.x - pos.x;
        double CY = TargetPos.y - pos.y;
        double CTheta = currentAuxPos - oldAuxPos;

        if(pos.x < TargetPos.x)
        {
            frontLeft.setPower(speed - CTheta);
            backLeft.setPower(speed - CTheta);
            frontRight.setPower(speed + CTheta);
            backRight.setPower(speed + CTheta);
        }
    }

    /**
     * Calculates the left/right motor powers required to achieve the requested
     * robot motions: Drive (Axial motion) and Turn (Yaw motion).
     * Then sends these power levels to the motors.
     *
     * @param Drive     Fwd/Rev driving power (-1.0 to 1.0) +ve is forward
     * @param Turn      Right/Left turning power (-1.0 to 1.0) +ve is CW
     */
    public void driveRobot(double Drive, double Turn) {
        // Combine drive and turn for blended motion.
        double left  = Drive + Turn;
        double right = Drive - Turn;

        // Scale the values so neither exceed +/- 1.0
        double max = Math.max(Math.abs(left), Math.abs(right));
        if (max > 1.0)
        {
            left /= max;
            right /= max;
        }

        // Use existing function to drive both wheels.
        setDrivePower(left, right);
    }

    /**
     * Pass the requested wheel motor powers to the appropriate hardware drive motors.
     *
     * @param leftWheel     Fwd/Rev driving power (-1.0 to 1.0) +ve is forward
     * @param rightWheel    Fwd/Rev driving power (-1.0 to 1.0) +ve is forward
     */
    public void setDrivePower(double leftWheel, double rightWheel) {
        // Output the values to the motor drives.
        frontLeft.setPower(leftWheel);
        frontRight.setPower(rightWheel);
    }

    /**
     * Pass the requested arm power to the appropriate hardware drive motor
     *
     * @param power driving power (-1.0 to 1.0)
     */

    /**
     * Send the two hand-servos to opposing (mirrored) positions, based on the passed offset.
     *
     * @param offset
     */

    /*public void setHandPositions(double offset) {
        offset = Range.clip(offset, -0.5, 0.5);
        leftHand.setPosition(MID_SERVO + offset);
        rightHand.setPosition(MID_SERVO - offset);
    }*/
}
