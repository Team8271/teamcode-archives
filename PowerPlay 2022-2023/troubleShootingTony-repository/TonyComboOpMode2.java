package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TonyComboOpMode2", group = "OldTony")
//@Disabled
public class TonyComboOpMode2 extends LinearOpMode {

    TonyRobotHardware robot       = new TonyRobotHardware();
    private ElapsedTime runtime = new ElapsedTime();
    /**
     * Create IMU
     */
    public BNO055IMU imu;

    public void runOpMode() throws InterruptedException {

        boolean isUp = true;

        double negLiftPow = 0;

        double speedBoost =0;

        // Make sure your ID's match your configuration
        robot.init(hardwareMap);

        PodsUp();

        // Initialize IMU here as the hardware Class uses DEGREES and RADIANS are used here.
        //Hardware could be cleaned up, but currently Auto programs use DEGREES
        BNO055IMU imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            speedBoost = gamepad1.right_trigger/2;
            //establish joystick variables
            double y;
            double x;
            double rx;

            y = (-gamepad1.left_stick_y /2); // Remember, joystick is reversed therefore changed to neg!
            x = (gamepad1.left_stick_x /2); // Counteract imperfect strafing
            rx = (gamepad1.right_stick_x /2);

            // Read inverse (neg in front) IMU heading, as the IMU heading is CW positive
            double botHeading = -imu.getAngularOrientation().firstAngle;

            double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
            double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio, but only when
            // at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            //calculate motorPower for adjusted botHeading
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            //Set motor power using Boost method (calculatedPower +- speedBoost)
            robot.frontLeft.setPower(Boost(frontLeftPower,speedBoost));
            robot.backLeft.setPower(Boost(backLeftPower,speedBoost));
            robot.frontRight.setPower(Boost(frontRightPower,speedBoost));
            robot.backRight.setPower(Boost(backRightPower,speedBoost));

            /**********************************************
                        END OF CHASSIS CONTROL
            **********************************************/

            //open palm (x)
            if (gamepad2.b)
            {
                //open palm
                robot.palmL.setPosition(0.5); // center
                robot.palmR.setPosition(0.5); // center
            }

            //close palm (o)
            if (gamepad2.a)
            {
                robot.palmL.setPosition(1); // right limit
                robot.palmR.setPosition(0); // left limit
            }

    //region swing arm
            if(gamepad2.right_stick_y > 0.1 || gamepad2.right_stick_y < -0.1) {
                robot.armMotor.setPower(-gamepad2.right_stick_y / 0.5);
                robot.armHoldpos = robot.armMotor.getCurrentPosition();
            }

            else
            {
                robot.armMotor.setPower((double)(robot.armHoldpos - robot.armMotor.getCurrentPosition()) / robot.slopeVal);
            }
    //endregion

    //region spin wrist
            if(gamepad2.right_stick_x > 0.5 && isUp)
            {
                robot.wrist.setPosition(0);
                isUp = false;
            }

            if(gamepad2.right_stick_x < -0.5 && !isUp)
            {
                robot.wrist.setPosition(1);
                isUp = true;
            }

            telemetry.addData("Is Up", isUp);

    //endregion

    //region Lift Motors

            //Restrict downward linear slide movement if touch sensor is pressed
            if(robot.liftTouch.isPressed())
            {
                negLiftPow = 0;
            }
            else if(!robot.liftTouch.isPressed())
            {
                negLiftPow = gamepad2.left_trigger;
            }

            if(gamepad2.right_trigger > 0.1 || gamepad2.left_trigger > 0.1)
            {
                robot.liftL.setPower((gamepad2.right_trigger) - (negLiftPow));
                robot.liftR.setPower((gamepad2.right_trigger) - (negLiftPow));

                robot.liftLHoldPos = robot.liftL.getCurrentPosition();
                robot.liftRHoldPos = robot.liftR.getCurrentPosition();
            }

            else
            {
                robot.liftL.setPower((double)(robot.liftLHoldPos - robot.liftL.getCurrentPosition()) / robot.slopeVal);
                robot.liftR.setPower((double)(robot.liftRHoldPos - robot.liftR.getCurrentPosition()) / robot.slopeVal);
            }
    //endregion

            /**TESTING PODS positioning
             * ********************************
             */
            if(gamepad2.dpad_down) {
                robot.podLeft.setPosition(0.7); // 0.7 is down
                robot.podRight.setPosition(0.0); // 0 is down
                robot.podBack.setPosition(0.0); // 0.4 is down
            }
            if(gamepad2.dpad_up) {
                robot.podLeft.setPosition(0.0); //
                robot.podRight.setPosition(0.9); //
                robot.podBack.setPosition(0.9); //
            }

            //display debug telemetry
            telemetry.addData("heading",(int)botHeading);
            telemetry.update();
        }
    }

    /**
     *
     * @METHODS USED IN CODE ABOVE
     */
    //speedboost method
    public double Boost(double iMotorPower, double boostVal)
    {
        //determine if calculated power is pos/neg
        //to determine whether to add or subtract Boost value
        //returns double
        if(iMotorPower < 0){
            return iMotorPower -boostVal;
        }
        else
        {
            return iMotorPower + boostVal;
        }
    }

    public void PodsUp(){
        robot.podLeft.setPosition(0.0); //
        robot.podRight.setPosition(0.9); //
        robot.podBack.setPosition(0.9); //
    }


}
