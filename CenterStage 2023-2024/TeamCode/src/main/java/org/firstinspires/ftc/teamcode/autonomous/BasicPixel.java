package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@Autonomous(name = "BASIC - Pixel Side",group = "4")
//@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class BasicPixel extends LinearOpMode {
    Hardwaremap robot;

    @Override
    public void runOpMode() {
        //Init Robot
        robot = new Hardwaremap(this);
        robot.init();
        robot.initVision();

        //Close dropper for INIT
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        //Ask for side
        robot.askSide();

        //Waiting for start
        waitForStart();

        //Start TweetyBird
        robot.tweetyBird.engage();

        //Scan
        robot.scanProp();

        //Don't Flip Input
        robot.tweetyBird.flipInput(false);

        //Save resources
        robot.visionPortal.close();

        //Spike Mark 1
        if (robot.propPosition.equals(Hardwaremap.propPositions.LEFT)) {
            if (robot.side == Hardwaremap.fieldSides.RED) {
                robot.tweetyBird.straightLineTo(-12,39,180);

                robot.tweetyBird.waitWhileBusy();
                robot.tweetyBird.waitWhileBusy();
                robot.tweetyBird.waitWhileBusy();

                //Place Pixel
                robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
                sleep(500);
                robot.tweetyBird.adjustTo(0,5,0);
            } else {
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

                robot.tweetyBird.straightLineTo(0,50.4,-90);
            }
        }

        //Spike Mark 2
        if (robot.propPosition.equals(Hardwaremap.propPositions.CENTER)) {
            robot.resetSideFlipped();
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
        }

        //Spike Mark 3
        if (robot.propPosition.equals(Hardwaremap.propPositions.RIGHT)) {
            if (false) {//(robot.side == Hardwaremap.fieldSides.BLUE) { TODO: Fix
                robot.tweetyBird.straightLineTo(12,39,-180);

                robot.tweetyBird.waitWhileBusy();
                robot.tweetyBird.waitWhileBusy();
                robot.tweetyBird.waitWhileBusy();

                //Place Pixel
                robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
                sleep(500);
                robot.tweetyBird.adjustTo(0,5,0);
            } else {
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

                robot.tweetyBird.straightLineTo(0,50.4,90);

                robot.tweetyBird.waitWhileBusy();
                robot.tweetyBird.waitWhileBusy();
                robot.tweetyBird.waitWhileBusy();
            }
        }

        //Close Grabber
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        robot.resetSideFlipped();

        /*
        //Maneuver around certain spike marks
        if ((robot.propPosition == Hardwaremap.propPositions.LEFT && robot.side == Hardwaremap.fieldSides.RED)||
                (robot.propPosition == Hardwaremap.propPositions.RIGHT && robot.side == Hardwaremap.fieldSides.BLUE)) {
            robot.tweetyBird.straightLineTo(-20,50.4,90);
            robot.tweetyBird.straightLineTo(-20.9,50.4,90);
        } else {
            robot.tweetyBird.straightLineTo(-20,50.4,90);
            robot.tweetyBird.straightLineTo(-20.9,50.4,90);
        } */

        //Towards Stack
        if (robot.propPosition== Hardwaremap.propPositions.CENTER) {
            robot.tweetyBird.straightLineTo(-12,50,90);
        } else {
            robot.tweetyBird.straightLineTo(0,50,90);
        }

        //robot.tweetyBird.straightLineTo(-20.9,50.4,90);

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        //Suck pixel
        //TODO: Latero


        //Cross field
        robot.tweetyBird.straightLineTo(47,51,70);
        robot.tweetyBird.straightLineTo(84,50,0);

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        //Raise Arm
        robot.setArmPosition(Hardwaremap.ArmPosition.UP);

        //Pos 1
        if ((robot.propPosition == Hardwaremap.propPositions.LEFT&&robot.side== Hardwaremap.fieldSides.BLUE)||
                (robot.propPosition == Hardwaremap.propPositions.RIGHT&&robot.side== Hardwaremap.fieldSides.RED)) {
            robot.tweetyBird.straightLineTo(84,19,-90);
        }

        //Pos 2
        if (robot.propPosition == Hardwaremap.propPositions.CENTER) {
            robot.tweetyBird.straightLineTo(84,27,-90);
        }

        //Pos 3
        if ((robot.propPosition == Hardwaremap.propPositions.RIGHT&&robot.side== Hardwaremap.fieldSides.BLUE)||
                (robot.propPosition == Hardwaremap.propPositions.LEFT&&robot.side== Hardwaremap.fieldSides.RED)) {
            robot.tweetyBird.straightLineTo(84,35,-90);
        }

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        //Backup
        robot.tweetyBird.adjustTo(3,0,0);

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        //Drop Pixel
        robot.setDropperPosition(Hardwaremap.DroperPosition.OPEN);
        sleep(2000);
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        //Park
        robot.tweetyBird.straightLineTo(84,50,0);

        //Drop Arm
        robot.setArmPosition(Hardwaremap.ArmPosition.DOWN);

        /*
        //Cycle Repeat
        for (int i = 0; i<1; i++) {
            //To Stack
            robot.tweetyBird.straightLineTo(-16,50,90);
            robot.tweetyBird.straightLineTo(-21.5,50,90);

            //Insure dropper closed
            sleep(200);
            robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Pull pixel
            pullPixelAndTake();


            //robot.tweetyBird.straightLineTo(80,52,90);
        }*/

        //Finish up the last tweetybird action
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        robot.breakPoint("End of OpMode");

        //Always tell tweetybird to manually stop
        robot.tweetyBird.stop();
    }

    private void pullPixelAndTake() {
        robot.pixelPull.setPosition(robot.pixelPullerPositions.get(3));
        sleep(1250);

        robot.tweetyBird.adjustTo(9,0,0);
        robot.intake.setPower(0.6);

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        robot.pixelPull.setPosition(robot.pixelPullerPositions.get(0));
        robot.tweetyBird.adjustTo(-9,0,0);

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        sleep(500);
        robot.intake.setPower(1);

        robot.tweetyBird.adjustTo(2,0,0);
        sleep(1000);
        robot.intake.setPower(0);
    }
}
