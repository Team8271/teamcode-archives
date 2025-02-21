/**
 * This file contains an minimal example of a Linear Autonomous "OpMode".
 *
 * This particular OpMode just executes a basic Autonomous driving for time, not using encoders
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Don't forget to comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;


@Autonomous(name="Red2", group="concept")  // @TeleOp(...) is the other common choice
//@Disabled
public class Red2 extends LinearOpMode {

    Tank8271HardwareSetup robot = new Tank8271HardwareSetup();

    private ElapsedTime runtime = new ElapsedTime();

    private TFObjectDetector tfod;

    private Orientation lastAngles = new Orientation();

    private double currAngle = 0.0;

    @Override
    public void runOpMode() throws InterruptedException {
        //adds feedback telemetry to DS
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        /************************
         * Autonomous Code Below://
         *************************/


        liftArm(-1, 500);

        extendArm(DRIVE_POWER, 500);

        //forward
        //drop cube
        //back
        //strafe right
        //forward
        //spin right 90
        //forward


    }//runOpMode

    /** Below: Basic Drive Methods used in Autonomous code...**/
    //set Drive Power variable
    double DRIVE_POWER = 0.5;

    public void DriveForward(double power)
    {
        // write the values to the motors
        robot.motorFR.setPower(-power);//still need to test motor directions for desired movement
        robot.motorFL.setPower(power);
        robot.motorBR.setPower(-power);
        robot.motorBL.setPower(power);
    }

    public void DriveForwardTime(double power, long time) throws InterruptedException
    {
        DriveForward(power);
        Thread.sleep(time);
    }

    public void StopDriving()
    {
        DriveForward(0);
    }

    public void StopDrivingTime(long time) throws InterruptedException
    {
        DriveForwardTime(0, time);
    }

    public void StrafeLeft(double power, long time) throws InterruptedException
    {
        // write the values to the motors
        robot.motorFR.setPower(power);
        robot.motorFL.setPower(-power);
        robot.motorBR.setPower(-power);
        robot.motorBL.setPower(power);
        Thread.sleep(time);
    }

    public void StrafeRight(double power, long time) throws InterruptedException
    {
        StrafeLeft(-power, time);
    }

    public void SpinRight (double power, long time) throws InterruptedException
    {
        // write the values to the motors
        robot.motorFR.setPower(power);
        robot.motorFL.setPower(power);
        robot.motorBR.setPower(power);
        robot.motorBL.setPower(power);
        Thread.sleep(time);
    }

    public void SpinLeft (double power, long time) throws InterruptedException
    {
        SpinRight(-power, time);
    }

    public void BlueDuckSpin(long time) throws InterruptedException
    {
        robot.duckBlue.setPower(1);
        StopDrivingTime(500);
        Thread.sleep(time);
    }

    public void RedDuckSpin(long time) throws InterruptedException
    {
        robot.duckRed.setPower(-1);
        StopDrivingTime(500);
        Thread.sleep(time);
    }

    public void liftArm(double power, long time) throws InterruptedException
    {
        robot.armMotor.setPower(power);
        Thread.sleep(time);
    }

    public void extendArm(double power, long time) throws InterruptedException
    {
        robot.slideMotor.setPower(power);
        Thread.sleep(time);
    }

    public void resetAngle()
    {
        //change axes order relative to your control hub positioning
        lastAngles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYZ, AngleUnit.DEGREES);
        currAngle = 0;
    }

    public double getAngle()
    {
        Orientation orientation =  robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYZ, AngleUnit.DEGREES);

        double deltaAngle = orientation.firstAngle - lastAngles.firstAngle;

        if(deltaAngle > 180)
        {
            deltaAngle -= 360;
        }
        else if(deltaAngle <= -180)
        {
            deltaAngle += 360;
        }

        currAngle += deltaAngle;
        lastAngles = orientation;
        telemetry.addData("Gyro", orientation.firstAngle);
        return currAngle;
    }

    public void turn(double degrees)
    {
        resetAngle();

        double error = degrees;

        while(opModeIsActive() && Math.abs(error) > 2)
        {
            double motorPower = (error < 0 ? -0.3 : 0.3);
            robot.setMotorPower(motorPower, -motorPower, motorPower, -motorPower);
            error = degrees - getAngle();
            telemetry.addData("error", error);
            telemetry.update();
        }
        robot.setAllPower(0);
    }

    public void turnTo (double degrees)
    {
        Orientation orientation = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYZ, AngleUnit.DEGREES);

        double error = degrees - orientation.firstAngle;
        if(error > 180)
        {
            error -= 360;
        }
        else if(error <= -180)
        {
            error += 360;
        }

        turn(error);
    }



}//TestAutoDriveByTime
