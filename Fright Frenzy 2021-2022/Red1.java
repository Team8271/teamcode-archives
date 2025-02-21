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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;


@Autonomous(name="Red1", group="concept")  // @TeleOp(...) is the other common choice
//@Disabled
public class Red1 extends LinearOpMode {

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

        liftArm(DRIVE_POWER, 1500);
        StopDrivingTime(1000);

        DriveForwardEncoder(0.3, -700);
        extendArm(DRIVE_POWER, 500);
        maintainArmPos();
        StopDrivingTime(1000);

        openHands();
        maintainArmPos();
        StopDrivingTime(1000);

        SpinLeftEncoder(0.3, -750);
        extendArm(-DRIVE_POWER, 500);
        maintainArmPos();
        StopDrivingTime(1000);

        DriveForwardEncoder(0.3, -2000);
        maintainArmPos();
        StopDrivingTime(1000);

        StrafeLeftEncoder(0.3, 800);
        maintainArmPos();
        StopDrivingTime(1000);

        StrafeLeftEncoder(0.2, 50);
        maintainArmPos();
        StopDrivingTime(1000);

        RedDuckSpin(2000);
        maintainArmPos();
        StopDrivingTime(1000);

        DriveForwardEncoder(DRIVE_POWER, -100);
        maintainArmPos();
        StopDrivingTime(1000);

        DriveBackwardEncoder(DRIVE_POWER, 150);
        maintainArmPos();
        StopDrivingTime(1000);

        SpinRightEncoder(0.3, 750);
        liftArm(DRIVE_POWER, 700);
        StopDrivingTime(1000);

        DriveForwardEncoder(0.3, -620);
        maintainArmPos();
        StopDrivingTime(1000);

        StrafeLeftEncoder(DRIVE_POWER, 1000);
        maintainArmPos();
        StopDrivingTime(1000);

        StopDriving();

    }//runOpMode

    /** Below: Basic Drive Methods used in Autonomous code...**/
    //set Drive Power variable
    double DRIVE_POWER = 0.5;

    //region Time Driving Functions
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

    public void StopDrivingTime(long time) throws InterruptedException
    {
        DriveForwardTime(0, time);
    }

    public void StrafeLeft(double power, long time) throws InterruptedException
    {
        // write the values to the motors
        robot.motorFR.setPower(-power);
        robot.motorFL.setPower(-power);
        robot.motorBR.setPower(power);
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
        robot.duckBlue.setPower(0.7);
        StopDrivingTime(500);
        Thread.sleep(time);
    }

    public void RedDuckSpin(long time) throws InterruptedException
    {
        robot.duckRed.setPower(-0.7);
        StopDrivingTime(500);
        Thread.sleep(time);
    }

    public void StopDriving()
    {
        DriveForward(0);
    }

    //endregion

    public void StrafeRightEncoder(double power, int pos)
    {
        robot.motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.motorFR.setTargetPosition(pos);
        robot.motorBR.setTargetPosition(pos);
        //robot.motorFL.setTargetPosition(pos);
        robot.motorBL.setTargetPosition(pos);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.motorFR.setPower(power);
        robot.motorBR.setPower(-power);
        //robot.motorFL.setPower(power);
        robot.motorBL.setPower(-power);

        while(robot.motorBR.getCurrentPosition() > pos)
        {
            robot.motorFL.setPower(power);

            telemetry.addData("FRmotorPos", robot.motorFR.getCurrentPosition());
            telemetry.addData("FLmotorPos", robot.motorFL.getCurrentPosition());
            telemetry.addData("BRmotorPos", robot.motorBR.getCurrentPosition());
            telemetry.addData("BLmotorPos", robot.motorBL.getCurrentPosition());
            telemetry.update();
        }

        //turn motor power to 0
        robot.motorFR.setPower(0);
        robot.motorBR.setPower(0);
        robot.motorFL.setPower(0);
        robot.motorBL.setPower(0);

    }

    public void StrafeLeftEncoder(double power, int pos)
    {
        robot.motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.motorFR.setTargetPosition(pos);
        robot.motorBR.setTargetPosition(pos);
        //robot.motorFL.setTargetPosition(pos);
        robot.motorBL.setTargetPosition(pos);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.motorFR.setPower(-power);
        robot.motorBR.setPower(power);
        //robot.motorFL.setPower(power);
        robot.motorBL.setPower(power);

        while(robot.motorBR.getCurrentPosition() < pos)
        {
            robot.motorFL.setPower(-power);

            telemetry.addData("FRmotorPos", robot.motorFR.getCurrentPosition());
            telemetry.addData("FLmotorPos", robot.motorFL.getCurrentPosition());
            telemetry.addData("BRmotorPos", robot.motorBR.getCurrentPosition());
            telemetry.addData("BLmotorPos", robot.motorBL.getCurrentPosition());
            telemetry.update();
        }

        //turn motor power to 0
        robot.motorFR.setPower(0);
        robot.motorBR.setPower(0);
        robot.motorFL.setPower(0);
        robot.motorBL.setPower(0);

    }

    public void DriveForwardEncoder(double power, int pos)
    {
        robot.motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.motorFR.setTargetPosition(pos);
        robot.motorBR.setTargetPosition(pos);
        //robot.motorFL.setTargetPosition(pos);
        robot.motorBL.setTargetPosition(pos);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.motorFR.setPower(-power);
        robot.motorBR.setPower(-power);
        //robot.motorFL.setPower(power);
        robot.motorBL.setPower(power);

        while(robot.motorBR.getCurrentPosition() > pos)
        {
            robot.motorFL.setPower(power);

            telemetry.addData("FRmotorPos", robot.motorFR.getCurrentPosition());
            telemetry.addData("FLmotorPos", robot.motorFL.getCurrentPosition());
            telemetry.addData("BRmotorPos", robot.motorBR.getCurrentPosition());
            telemetry.addData("BLmotorPos", robot.motorBL.getCurrentPosition());
            telemetry.update();
        }

        //turn motor power to 0
        robot.motorFR.setPower(0);
        robot.motorBR.setPower(0);
        robot.motorFL.setPower(0);
        robot.motorBL.setPower(0);

    }

    public void DriveBackwardEncoder(double power, int pos)
    {
        robot.motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.motorFR.setTargetPosition(pos);
        robot.motorBR.setTargetPosition(pos);
        //robot.motorFL.setTargetPosition(pos);
        robot.motorBL.setTargetPosition(pos);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.motorFR.setPower(power);
        robot.motorBR.setPower(power);
        //robot.motorFL.setPower(power);
        robot.motorBL.setPower(-power);

        while(robot.motorBR.getCurrentPosition() < pos)
        {
            robot.motorFL.setPower(-power);

            telemetry.addData("FRmotorPos", robot.motorFR.getCurrentPosition());
            telemetry.addData("FLmotorPos", robot.motorFL.getCurrentPosition());
            telemetry.addData("BRmotorPos", robot.motorBR.getCurrentPosition());
            telemetry.addData("BLmotorPos", robot.motorBL.getCurrentPosition());
            telemetry.update();
        }

        //turn motor power to 0
        robot.motorFR.setPower(0);
        robot.motorBR.setPower(0);
        robot.motorFL.setPower(0);
        robot.motorBL.setPower(0);

    }

    public void SpinLeftEncoder(double power, int pos)
    {
        //-750 is a 90 degree left turn

        robot.motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.motorFR.setTargetPosition(pos);
        robot.motorBR.setTargetPosition(pos);
        //robot.motorFL.setTargetPosition(pos);
        robot.motorBL.setTargetPosition(pos);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.motorFR.setPower(-power);
        robot.motorBR.setPower(-power);
        //robot.motorFL.setPower(power);
        robot.motorBL.setPower(-power);

        while(robot.motorBR.getCurrentPosition() > pos)
        {
            robot.motorFL.setPower(-power);

            telemetry.addData("FRmotorPos", robot.motorFR.getCurrentPosition());
            telemetry.addData("FLmotorPos", robot.motorFL.getCurrentPosition());
            telemetry.addData("BRmotorPos", robot.motorBR.getCurrentPosition());
            telemetry.addData("BLmotorPos", robot.motorBL.getCurrentPosition());
            telemetry.update();
        }

        //turn motor power to 0
        robot.motorFR.setPower(0);
        robot.motorBR.setPower(0);
        robot.motorFL.setPower(0);
        robot.motorBL.setPower(0);

    }

    public void SpinRightEncoder(double power, int pos)
    {
        //750 is a 90 degree right turn

        robot.motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.motorFR.setTargetPosition(pos);
        robot.motorBR.setTargetPosition(pos);
        //robot.motorFL.setTargetPosition(pos);
        robot.motorBL.setTargetPosition(pos);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.motorFR.setPower(power);
        robot.motorBR.setPower(power);
        //robot.motorFL.setPower(power);
        robot.motorBL.setPower(power);

        while(robot.motorBR.getCurrentPosition() < pos)
        {
            robot.motorFL.setPower(power);

            telemetry.addData("FRmotorPos", robot.motorFR.getCurrentPosition());
            telemetry.addData("FLmotorPos", robot.motorFL.getCurrentPosition());
            telemetry.addData("BRmotorPos", robot.motorBR.getCurrentPosition());
            telemetry.addData("BLmotorPos", robot.motorBL.getCurrentPosition());
            telemetry.update();
        }

        //turn motor power to 0
        robot.motorFR.setPower(0);
        robot.motorBR.setPower(0);
        robot.motorFL.setPower(0);
        robot.motorBL.setPower(0);

    }

    public void openHands() throws InterruptedException
    {
        robot.servoHandL.setPosition(1.0);
        robot.servoHandR.setPosition(0.7);
    }

    public void liftArm(double power, int pos) throws InterruptedException
    {
        //sets arm's starting pos to 0
        robot.armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.armMotor.setTargetPosition(pos);
        robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.armMotor.setPower(power);
        while(robot.armMotor.isBusy())
        {
            telemetry.addData("armPos", robot.armMotor.getCurrentPosition());
            telemetry.update();
        }
    }

    public void maintainArmPos() throws InterruptedException
    {
        liftArm(DRIVE_POWER, 0);
    }

    public void extendArm(double power, long time) throws InterruptedException
    {
        robot.slideMotor.setPower(power);
        Thread.sleep(time);
    }

    //region IMU Functions
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

    //endregion

}//TestAutoDriveByTime
