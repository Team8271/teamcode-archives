/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file contains a configuration for Mr. Reynolds' TestBed motors/servos/sensor
 *
 * It is intended to test basic function of program parameters
 *
 * Note: this Class uses a TestHardwareTeleOp configuration file.
 *
 * You could make a copy and adjust Configuration to match your bot for use as a basic testing
 * platform.
 */


@TeleOp(name="Tank", group="8271Tank")  // @Autonomous(...) is the other common choice
@Disabled
public class Tank8271_TeleOp extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    Tank8271HardwareSetup robot        = new Tank8271HardwareSetup();  // Use MyBotHardware Setup

    @Override
    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);                //Initialize hardware from the MyBotHardware Setup


        //region initialize variables

        //region Create array of lift positions
        int[] liftPos = {0, 10000, 20000, 30000, 40000};

        int currentLiftPos = liftPos[0];
        int liftHoldPos = currentLiftPos;
        double slopeVal = 2000;
        //endregion

        //region set right trigger to not be pressed at start
        Boolean rghtTrggrPress = false;
        Boolean buttonIsPressed = false;
        //endregion

        //endregion

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        /************************
         * TeleOp Code Below://
         *************************/

        while (opModeIsActive()) {  // run until the end of the match (driver presses STOP)
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            //region tank drive set to gamepad1 joysticks

            //(note: The joystick goes negative when pushed forwards)
            robot.motorBL.setPower(gamepad1.left_stick_y);
            robot.motorBR.setPower(gamepad1.right_stick_y);
            robot.motorFL.setPower(gamepad1.left_stick_y);
            robot.motorFR.setPower(gamepad1.right_stick_y);

            //endregion

            //region if right trigger is pressed, rghtTrggrPress is true
            if(gamepad1.right_trigger > 0.5)
            {
                rghtTrggrPress = true;
            }
            else {
                rghtTrggrPress = false;
            }
            //endregion

            //region Reset Encoders
            robot.armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            /*and hold liftMotor in its current position
            liftHoldPos = robot.liftMotor.getCurrentPosition();
            robot.liftMotor.setPower((double) (liftHoldPos - robot.liftMotor.getCurrentPosition()) / slopeVal);
            */

            telemetry.addData("Status", "Encoders reset");
            telemetry.update();
            //endregion

            //region if button has just been pressed while having not been pressed...
            if(rghtTrggrPress && !buttonIsPressed)
            {
                //increment level each time pressed
                if(currentLiftPos < liftPos.length)
                {
                    currentLiftPos = currentLiftPos + 1;
                }

                // if the current position reached the end of array (array.length) then reset to first level
                if (currentLiftPos >= liftPos.length)
                {
                    currentLiftPos = 0;
                }
            }
            //endregion

            // sets target to the array position determined above
            robot.armMotor.setTargetPosition(liftPos[currentLiftPos]);


            //drive motor to new level
            robot.armMotor.setPower(1.0);
            robot.armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftHoldPos = currentLiftPos;

            // display arm position while going to position
            if (robot.armMotor.isBusy()) {
                // Send telemetry message
                telemetry.addData("moving to target",  robot.armMotor.getCurrentPosition());
                telemetry.update();
            }
            else {
                // set to hold that position
                //robot.liftMotor.setPower((double) (liftHoldPos - robot.liftMotor.getCurrentPosition()) / slopeVal);
            }

            // reset the buttonPressed back to current state
            buttonIsPressed = rghtTrggrPress;


            //endregion

            //CR Servo commands
            if(gamepad1.left_bumper) //left bumper will spinLeft
            {
                //robot.slideMotor.setPosition(robot.SpinLeft);
            }
            else if (gamepad1.right_bumper) //right bumper will spinRight
            {
                //robot.slideMotor.setPosition(robot.SpinRight);
            }
            else
            {
                //robot.slideMotor.setPosition(robot.STOP);
            }


            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
    }
}
