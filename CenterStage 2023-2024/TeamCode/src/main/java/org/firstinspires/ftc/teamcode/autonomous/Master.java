package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.modules.TelemetrySelector;
import org.firstinspires.ftc.teamcode.util.Hardwaremap;
import org.firstinspires.ftc.teamcode.util.Prop;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "Main Autonomous", group = "1")
public class Master extends LinearOpMode {

    // Defining HardwareMap
    Hardwaremap robot;

    // Defining TelemetrySelector
    TelemetrySelector telemetrySelector;

    // Enum Values
    public enum positions {
        RED,
        BLUE,
        LONG,
        SHORT
    }

    public enum autoStyles {
        CYCLE,
        BACKSTAGE,
        BACKDROP,
        NULL
    }

    public enum parkPositions {
        FRONT,
        BACK,
        NULL
    }

    public enum propPosition {
        LEFT,
        CENTER,
        RIGHT
    }

    // Variables
    positions currentColor;
    positions currentDistance;
    autoStyles autoStyle;
    parkPositions parkPosition;

    /**
     * Start of opmode
     */
    @Override
    public void runOpMode() {
        // Setting up and initializing robot
        robot = new Hardwaremap(this);
        robot.init();
        //robot.initTweetyBird(this);
        robot.tweetyBird.disengage();

        robot.initVision(); //TODO: Specific to Grace
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        // Setting up TelemetrySelector
        telemetrySelector = new TelemetrySelector(this);

        // Defining telemetry options
        String[] startPositionOptions = { "Blue Long", "Blue Short", "Red Long", "Red Short" };
        String[] autoStyleOptions = { "Cycle", "Backdrop", "Backstage", "Only-Spike" };
        String[] parkOptions = { "Corner", "Center" };

        // Asking
        String startPosSelect = startPositionOptions[0];
        startPosSelect = telemetrySelector.simpleSelector("What is your current position",startPositionOptions);

        String autoStyleSelect = autoStyleOptions[0];
        autoStyleSelect = telemetrySelector.simpleSelector("What style of auto to run",autoStyleOptions);

        String parkSelect = null;
        if (autoStyleSelect!=autoStyleOptions[3]) {
            parkSelect = telemetrySelector.simpleSelector("Where should the bot park",parkOptions);
        }

        // Setting global variables
        if (startPosSelect.equals(startPositionOptions[0])) {
            currentColor = positions.BLUE;
            currentDistance = positions.LONG;
        } else if (startPosSelect.equals(startPositionOptions[1])) {
            currentColor = positions.BLUE;
            currentDistance = positions.SHORT;
        } else if (startPosSelect.equals(startPositionOptions[2])) {
            currentColor = positions.RED;
            currentDistance = positions.LONG;
        } else if (startPosSelect.equals(startPositionOptions[3])) {
            currentColor = positions.RED;
            currentDistance = positions.SHORT;
        }

        if (autoStyleSelect.equals(autoStyleOptions[0])) {
            autoStyle = autoStyles.CYCLE;
        } else if (autoStyleSelect.equals(autoStyleOptions[1])) {
            autoStyle = autoStyles.BACKDROP;
        } else if (autoStyleSelect.equals(autoStyleOptions[2])) {
            autoStyle = autoStyles.BACKSTAGE;
        } else if (autoStyleSelect.equals(autoStyleOptions[3])) {
            autoStyle = autoStyles.NULL;
        }

        if (parkSelect != null) {
            if (parkSelect.equals(parkOptions[0])) {
                parkPosition = parkPositions.FRONT;
            } else if (parkSelect.equals(parkOptions[1])) {
                parkPosition = parkPositions.BACK;
            }
        } else {
            parkPosition = parkPositions.NULL;
        }

        //Telemetry
        telemetry.addLine("[*] Bot fully initialized and ready to start");
        telemetry.addLine();
        telemetry.addLine("[+] Selections memory dump");
        telemetry.addData("Selected Side", "Color: " + currentColor + " Side: " + currentDistance);
        telemetry.addData("Selected Style", autoStyle);
        telemetry.addData("Selected Parking Position", parkPosition);
        telemetry.update();

        // Wait for Start
        waitForStart();

        // Starting Tweetybird
        robot.tweetyBird.engage();

        /**
         * Prop Placement
         */
        // Methods
        if ((currentColor == positions.BLUE && currentDistance == positions.LONG) ||
                currentColor == positions.RED && currentDistance == positions.SHORT) {
            placePropTrussLeft();
        }

        if ((currentColor == positions.BLUE && currentDistance == positions.SHORT) ||
                currentColor == positions.RED && currentDistance == positions.LONG) {
            placePropTrussRight();
        }

        /**
         * Main Action
         */
        // Flipping input if needed
        if (currentColor == positions.RED) {
            robot.tweetyBird.flipInput(true);
        }

        // Methods
        if (autoStyle == autoStyles.CYCLE) {
            if (currentDistance == positions.LONG) {
                cycleLong();
            } else if (currentDistance == positions.SHORT) {
                cycleShort();
            }
        } else if (autoStyle == autoStyles.BACKSTAGE) {
            if (currentDistance == positions.LONG) {
                backstageLong();
            } else if (currentDistance == positions.SHORT) {
                backstageShort();
            }
        } else if (autoStyle == autoStyles.BACKDROP) {
            if (currentDistance == positions.LONG) {
                backdropLong();
            } else if (currentDistance == positions.SHORT) {
                backdropShort();
            }
        }

        /**
         * Parking
         */
        // Methods
        if (parkPosition == parkPositions.FRONT) {
            parkFront();
        } else if (parkPosition == parkPositions.BACK) {
            parkBack();
        }

        // Kill everything after stop
        while (opModeIsActive()) {
            telemetry.addLine("[*] Autonomous finished, you may stop when you are ready.");
            telemetry.update();
        }
        robot.tweetyBird.stop();

    }

    private propPosition scanProp() {
        telemetry.addLine("[*] Scanning for Prop Placement...");
        telemetry.update();

        //Code here
        List<Prop> detectedProps = robot.lineDetection.getDetectedProps();
        Prop selectedProp;

        if (detectedProps.size()>0) {
            selectedProp = detectedProps.get(0);

            if (selectedProp != null) {
                if (selectedProp.getX()<90) {
                    return propPosition.LEFT;
                } else if (selectedProp.getX()>200) {
                    return propPosition.RIGHT;
                } else {
                    return propPosition.CENTER;
                }
            }
        }

        // Cannot Determine
        return propPosition.CENTER;
    }

    private void placePropTrussLeft() {
        telemetry.addLine("[*] Placing Pixel on Spike Mark (truss left)...");
        telemetry.update();

        //Code here
        propPosition position = scanProp();

        if (position == propPosition.LEFT) { // Left
            robot.tweetyBird.straightLineTo(-1,26,-90);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            robot.tweetyBird.adjustTo(-5,0,0);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Place Pixel
            robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            sleep(500);
            robot.tweetyBird.adjustTo(5,0,0);
        } else if (position == propPosition.CENTER) { // Center
            robot.tweetyBird.straightLineTo(9,38,-90);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Place Pixel
            robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            sleep(500);
            robot.tweetyBird.adjustTo(4,0,0);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
        } else { // Right
            robot.tweetyBird.straightLineTo(12,39,-180);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Place Pixel
            robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            sleep(500);
            robot.tweetyBird.adjustTo(0,5,0);
        }
    }

    private void placePropTrussRight() {
        telemetry.addLine("[*] Placing Pixel on Spike Mark (truss right)...");
        telemetry.update();

        //Code here
        propPosition position = scanProp();

        if (position == propPosition.LEFT) { // Left
            robot.tweetyBird.straightLineTo(-12,39,180);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Place Pixel
            robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            sleep(500);
            robot.tweetyBird.adjustTo(0,5,0);
        } else if (position == propPosition.CENTER) { // Center
            robot.tweetyBird.straightLineTo(-9,38,90);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Place Pixel
            robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            sleep(500);
            robot.tweetyBird.adjustTo(-4,0,0);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
        } else { // Right
            robot.tweetyBird.straightLineTo(1,26,90);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            robot.tweetyBird.adjustTo(4,0,0);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Place Pixel
            robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            sleep(500);
            robot.tweetyBird.adjustTo(-4,0,0);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
        }
    }

    private void cycleLong() {
        telemetry.addLine("[*] Cycling (long)...");
        telemetry.update();

        //Code here
        robot.tweetyBird.straightLineTo(9,49,-90);

        for (int i = 0; i<10; i++) {
            robot.tweetyBird.speedLimit(0.9);

            // To
            robot.tweetyBird.straightLineTo(20,49,-90);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            robot.tweetyBird.straightLineTo(-40,50,-90);

            robot.tweetyBird.straightLineTo(-72,50,-270);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            robot.tweetyBird.straightLineTo(-85,28,-270);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            // From

            if (i<9) {
                robot.tweetyBird.speedLimit(0.9);

                robot.tweetyBird.straightLineTo(-72,50,-90);

                robot.tweetyBird.waitWhileBusy();
                robot.tweetyBird.waitWhileBusy();
                robot.tweetyBird.waitWhileBusy();

                robot.tweetyBird.straightLineTo(-40,50,-90);

                robot.tweetyBird.straightLineTo(20,49,-90);
            }
        }
    }

    private void cycleShort() {
        telemetry.addLine("[*] Cycling (short)...");
        telemetry.update();

        //Code here
    }

    private void backdropLong() {
        telemetry.addLine("[*] Placing on Backdrop (long)...");
        telemetry.update();

        //Code here
    }

    private void backdropShort() {
        telemetry.addLine("[*] Placing on Backdrop (short)...");
        telemetry.update();

        //Code here
    }

    private void backstageLong() {
        telemetry.addLine("[*] Placing in Backstage (long)...");
        telemetry.update();

        //Code here
    }

    private void backstageShort() {
        telemetry.addLine("[*] Placing in Backstage (short)...");
        telemetry.update();

        //Code here
    }

    private void parkFront() {
        telemetry.addLine("[*] Parking (front)...");
        telemetry.update();

        //Code here
    }

    private void parkBack() {
        telemetry.addLine("[*] Parking (back)...");
        telemetry.update();

        //Code here
    }
}
