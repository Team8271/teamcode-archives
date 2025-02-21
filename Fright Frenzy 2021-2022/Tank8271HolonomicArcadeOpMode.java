package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/*
   Holonomic concepts from:
   http://www.vexforum.com/index.php/12370-holonomic-drives-2-0-a-video-tutorial-by-cody/0
   Robot wheel mapping:
          X FRONT X
        X           X
      X  FL       FR  X
              X
             XXX
              X
      X  BL       BR  X
        X           X
          X       X
*/
@TeleOp(name = "Cleo Arcade Holonomic", group = "8271Tank")
//@Disabled
public class Tank8271HolonomicArcadeOpMode extends LinearOpMode
{
    // create timer
    private ElapsedTime runtime = new ElapsedTime();

    //  DON'T FORGET TO RENAME HARDWARE CONFIG FILE NAME HERE!!!!!!
    Tank8271HardwareSetup robot = new Tank8271HardwareSetup();

    @Override
    public void runOpMode() throws InterruptedException
    {
        robot.init(hardwareMap);  //Initialize hardware from the Hardware Setup Class

        robot.armHoldPosition = robot.armMotor.getCurrentPosition();

        waitForStart();
        runtime.reset(); // starts timer once start button is pressed

        while(opModeIsActive())
        {
        //region HolonomicDrive
            float maxSpeed = 0.3f + gamepad1.right_trigger/2;

            // left stick: X controls Strafe & forward/backward
            float gamepad1LeftY = -gamepad1.right_stick_x;   // drives spin
            float gamepad1LeftX = -gamepad1.left_stick_x;    // strafe direction (side to side)

            // right stick: Y controls spin direction
            float gamepad1RightX = gamepad1.left_stick_y;  // drives forward/back

            // holonomic formulas
            float FrontLeft = -gamepad1RightX - gamepad1LeftX - gamepad1LeftY;
            float FrontRight = gamepad1RightX - gamepad1LeftX - gamepad1LeftY;
            float BackRight = gamepad1RightX + gamepad1LeftX - gamepad1LeftY;
            float BackLeft = -gamepad1RightX + gamepad1LeftX - gamepad1LeftY;

            // clip the right/left values so that the values never exceed +/- 1
            FrontRight = Range.clip(FrontRight, -maxSpeed, maxSpeed);
            FrontLeft = Range.clip(FrontLeft, -maxSpeed, maxSpeed);
            BackLeft = Range.clip(BackLeft, -maxSpeed, maxSpeed);
            BackRight = Range.clip(BackRight, -maxSpeed, maxSpeed);

            // write the clipped values from the formula to the motors
            robot.motorFR.setPower(FrontRight);
            robot.motorFL.setPower(FrontLeft);
            robot.motorBL.setPower(BackLeft);
            robot.motorBR.setPower(BackRight);
        //endregion

        //region intake - uses two 180 servos
            if(gamepad2.a)//close new controller is a=X
            {
                robot.servoHandL.setPosition(0.6);
                robot.servoHandR.setPosition(0.3); //was 0.3
            }

            else if(gamepad2.b)//Open b=O
            {
                robot.servoHandL.setPosition(0.9);
                robot.servoHandR.setPosition(0.5); //was 0.7
            }
        //endregion

        //region duckSpin
            //blue duck spin
            if(gamepad1.right_bumper)
            {
                robot.duckBlue.setPower(1);
            }
            //red duck spin
            else if(gamepad1.left_bumper)
            {
                robot.duckRed.setPower(-1);
            }
            else
            {
                robot.duckBlue.setPower(0);
                robot.duckRed.setPower(0);
            }
        //endregion

        //region Intake rumble
            if(robot.rumbleTouch.isPressed())
            {
                gamepad1.rumble(0.9, 0.9, 200);
                gamepad2.rumble(0.9, 0.9, 200);
            }
        //endregion

        //region run armMotor
            if (gamepad2.left_stick_y > 0.1 || gamepad2.left_stick_y < -0.1) // && robot.armMotor.getCurrentPositionJ() > 0 && robot.armMotor.getCurrentPosition() < 100 //add this to check encoder within limits
            {
                robot.armMotor.setPower(-gamepad2.left_stick_y/1.5); // let stick drive UP (note this is positive value on joystick)
                robot.armHoldPosition = robot.armMotor.getCurrentPosition(); // while the lift is moving, continuously reset the arm holding position
            }

            else //joystick is released - try to maintain the current position
            {
                robot.armMotor.setPower((double)(robot.armHoldPosition - robot.armMotor.getCurrentPosition()) / robot.slopeVal);   // Note that if the lift is lower than desired position,
            }
        //endregion

        //region run slidemotor
            if (!robot.magC.isPressed() && !robot.magE.isPressed())
            {
                robot.slideMotor.setPower(-gamepad2.right_stick_y);
            }
            else {
                if (robot.magC.isPressed() == true) {
                    telemetry.addData("DETECTED", "MagC - reverse direction");
                    if (gamepad2.right_stick_y > 0) {
                        telemetry.addData("Joystick Y", gamepad2.right_stick_y);
                        robot.slideMotor.setPower(-gamepad2.right_stick_y);
                    } else {
                        robot.slideMotor.setPower(0);
                    }
                } else if (robot.magE.isPressed() == true) {
                    telemetry.addData("DETECTED", "magE - reverse direction");
                    if (gamepad2.right_stick_y < 0) {
                        telemetry.addData("Motor", gamepad2.right_stick_y);
                        robot.slideMotor.setPower(-gamepad2.right_stick_y);
                    } else {
                        robot.slideMotor.setPower(0);
                    }
                }
            }
        //endregion

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
            telemetry.addData("rightJoystickX", String.format("%.2f", gamepad2.right_stick_x));
            telemetry.addData("leftJoystickX", String.format("%.2f", gamepad2.left_stick_x));

            telemetry.addData("rightJoystickY", String.format("%.2f", gamepad2.right_stick_y));
            telemetry.addData("leftJoystickY", String.format("%.2f", gamepad2.left_stick_y));
            telemetry.addData("ArmPosition: ", +robot.armMotor.getCurrentPosition());
            telemetry.addData("arm hold", +robot.armHoldPosition);

            /*
            telemetry.addData("Text", "*** Robot Data***");
            telemetry.addData("Joy XL YL XR", String.format("%.2f", gamepad1LeftX) + " " + String.format("%.2f", gamepad1LeftY) + " " + String.format("%.2f", gamepad1RightX));
            telemetry.addData("f left pwr", "front left  pwr: " + String.format("%.2f", FrontLeft));
            telemetry.addData("f right pwr", "front right pwr: " + String.format("%.2f", FrontRight));
            telemetry.addData("b right pwr", "back right pwr: " + String.format("%.2f", BackRight));
            telemetry.addData("b left pwr", "back left pwr: " + String.format("%.2f", BackLeft));
            */
            telemetry.update();
        }
    }
}