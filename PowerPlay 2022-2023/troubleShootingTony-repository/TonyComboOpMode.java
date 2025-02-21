package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.TonyRobotHardware;

@TeleOp(name = "TonyComboOpMode2", group = "Comp")
//@Disabled
public class TonyComboOpMode extends LinearOpMode {

    TonyRobotHardware robot       = new TonyRobotHardware();

    private ElapsedTime runtime = new ElapsedTime();
    /**
     * Create IMU
     */
    public BNO055IMU imu;

    public void runOpMode() throws InterruptedException {

        boolean isUp = true;

        double negLiftPow = 0;

        // Make sure your ID's match your configuration
        robot.init(hardwareMap);

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
            //establish joystick variables
            double y;
            double x;
            double rx;

            double speedBoost = gamepad1.right_trigger/2;

            y = (-gamepad1.left_stick_y); //+ speedBoost; // Remember, this is reversed therefore changed to neg!
            x = (gamepad1.left_stick_x); //+ speedBoost; // Counteract imperfect strafing
            rx = (gamepad1.right_stick_x); //+ speedBoost;

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

            robot.frontLeft.setPower(frontLeftPower );
            robot.backLeft.setPower(backLeftPower );
            robot.frontRight.setPower(frontRightPower );
            robot.backRight.setPower(backRightPower );

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
                //update position to hold
                robot.liftLHoldPos = robot.liftL.getCurrentPosition();
                robot.liftRHoldPos = robot.liftR.getCurrentPosition();
            }

            else //hold
            {
                robot.liftL.setPower((double)(robot.liftLHoldPos - robot.liftL.getCurrentPosition()) / robot.slopeVal);
                robot.liftR.setPower((double)(robot.liftRHoldPos - robot.liftR.getCurrentPosition()) / robot.slopeVal);
            }
    //endregion

            // speed boost telemetry
            telemetry.addData("Y:", y);
            telemetry.addData("x:", x);
            telemetry.addData("rx:", rx);
            telemetry.addData("Speed Boost", speedBoost);
            telemetry.addData("EncL", robot.leftEncoder.getCurrentPosition());
            telemetry.addData("EncR", robot.rightEncoder.getCurrentPosition());
            telemetry.addData("EncB", robot.backEncoder.getCurrentPosition());
            telemetry.addData("RTrigger", gamepad2.right_trigger);
            telemetry.addData("LTrigger", gamepad2.left_trigger);

            // touch sensor
            telemetry.addData("Touch Sensor:", robot.liftTouch.isPressed());

            //display debug telemetry
            telemetry.addData("heading",(int)botHeading);
            telemetry.update();
        }
    }
}
