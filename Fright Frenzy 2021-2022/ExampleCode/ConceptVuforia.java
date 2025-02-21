/* Copyright (c) 2017 FIRST. All rights reserved.
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
package org.firstinspires.ftc.teamcode.ExampleCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

//import org.firstinspires.ftc.robotcontroller.external.samples.ConceptVuforiaNavigation;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * This OpMode illustrates the basics of using the Vuforia engine to determine
 * the identity of Vuforia VuMarks encountered on the field. The code is structured as
 * a LinearOpMode.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained in {@link ConceptVuforiaNavigation}.
 */

@Autonomous(name="Concept Vuforia", group ="Concept")
@Disabled
public class ConceptVuforia extends LinearOpMode {

    ExampleHardwareSetupHolonomic robot = new ExampleHardwareSetupHolonomic(); //get hardware members from HardwareSetUp class

    public static final String TAG = "Vuforia VuMark Sample";

    OpenGLMatrix lastLocation = null;

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;

    @Override public void runOpMode() {

        robot.init(hardwareMap); // get initializatin of hardware from HardwareSetUp class

        /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View, to save power
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        /*
         * licence key for rreynolds
         */
        parameters.vuforiaLicenseKey = "AYNNRt//////AAAAGfpIRuDyZ0LbtXFg3QTQrAB9l5fi8Ibg+wAf+xbO7sGn7r+YlAoco9KJt/BpcqO/lKY5CCvsUUN6WMfFl3SliLMd/m6hyR5gbRBBmEjrpw6BT4pgGlRdJEl5svrqOi+LqaQm2oil4GvVbUZDKU5za1NU5dgjP0dBtgVSKh49bgZCRWzlZAZkpTCLCZ0Gu30jZ3SUD0ixrez5AaKREZDdhByG17DVx25W/br9PUY9jXqkNfHUnh7Xs6f5JtUqZOSjkOJzLmn7ChjVHY6AFeQoucJeZwcz3Bg/Cmk1UEB/X9YGL+iY+TN0SjVyfSjfL/XooxfaGPS1aeIiOLYg+JMnzeHgwqzzqvLiY1W0kTFZ9k54";

        /*
         * We also indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        relicTrackables.activate();

        while (opModeIsActive()) {

            /**
             * See if any of the instances of {@link relicTemplate} are currently visible.
             * {@link RelicRecoveryVuMark} is an enum which can have the following values:
             * UNKNOWN, LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than
             * UNKNOWN will be returned, else 'NOT VISIBLE' will display
             */

            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate); // vuMark gets value from relicTemplate

            if (vuMark != RelicRecoveryVuMark.UNKNOWN) // if vuMark is NOT UNKNOWN run autoCode for value seen
            {

                telemetry.addData("VuMark", "%s visible", vuMark);
                telemetry.update();

                // This simple example code runs a different motor for 1 sec then turns it off
                // for each TemplateID found.
                // the run motor test can be replaced by your desired autonomous code.

                if (vuMark == RelicRecoveryVuMark.LEFT) {
                    // autonomous code here...
                    robot.armMotor.setPower(1.0);   // set motors to running
                    sleep(1000);                    // leave motors running for time
                    robot.armMotor.setPower(0.0);   // turn motor(s) off

                }
                else if (vuMark == RelicRecoveryVuMark.CENTER){
                    // autonomous code here...
                    robot.motorBackRight.setPower(1.0);
                    sleep(1000);
                    robot.motorBackRight.setPower(0.0);

                }
                else if (vuMark == RelicRecoveryVuMark.RIGHT){
                    // autonomous code here...
                    robot.motorBackLeft.setPower(1.0);
                    sleep(1000);
                    robot.motorBackLeft.setPower(0.0);

                }

                telemetry.addData("Autonomous", "Complete");
                break; //exit the opMode loop

            }
            else {
                telemetry.addData("VuMark", "NOT VISIBLE"); // else if vuMark IS UNKNOWN display NOT VISABLE
            }

            telemetry.update();

        }//OpModeIsActive
    }//runOpMode
}//MyConceptVuforia
