package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@Autonomous(name = "OLD CYCLE - Board Side",group = "3")
@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class OldCycleBoard extends LinearOpMode {
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

        robot.visionPortal.close();

        //Spike Mark 1
        if (robot.propPosition.equals(Hardwaremap.propPositions.LEFT)) {
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
            sleep(400);
            robot.tweetyBird.adjustTo(5,0,0);
        }

        //Spike Mark 2
        if (robot.propPosition.equals(Hardwaremap.propPositions.CENTER)) {
            robot.tweetyBird.straightLineTo(4,30,0);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Place Pixel
            robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            sleep(400);
            robot.tweetyBird.adjustTo(0,-4,0);
        }

        //Spike Mark 3
        if (robot.propPosition.equals(Hardwaremap.propPositions.RIGHT)) {
            robot.tweetyBird.straightLineTo(1,26,90);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            robot.tweetyBird.adjustTo(2,0,0);

            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();
            robot.tweetyBird.waitWhileBusy();

            //Place Pixel
            robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            sleep(400);
            robot.tweetyBird.adjustTo(-2,0,0);
        }

        //Close Grabber
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        robot.resetSideFlipped();

        //Maneuver around certain spike marks
        if ((robot.propPosition == Hardwaremap.propPositions.RIGHT && robot.side == Hardwaremap.fieldSides.RED)||
                (robot.propPosition == Hardwaremap.propPositions.LEFT && robot.side == Hardwaremap.fieldSides.BLUE)) {
            robot.tweetyBird.straightLineTo(0,22,45);
            robot.tweetyBird.straightLineTo(24,10,0);
        } else {
           robot.tweetyBird.straightLineTo(12,28,-90);
        }

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        placeBackdrop();


       //Pixel
        robot.tweetyBird.straightLineTo(25,50,-90);
        robot.tweetyBird.straightLineTo(0,50,-90);
        //Lower Arm
        robot.setArmPosition(Hardwaremap.ArmPosition.DOWN);
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
       robot.tweetyBird.straightLineTo(-60,50,-90);

        robot.tweetyBird.straightLineTo(-65,50,90);
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.straightLineTo(-67.6,49.8,90);

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        pullPixelAndTake();
        robot.intake.setPower(0.8);

        //
        robot.tweetyBird.straightLineTo(0,50,90);
        robot.tweetyBird.straightLineTo(30,50,90);

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        robot.intake.setPower(0);

        placeBackdrop();
        robot.setArmPosition(Hardwaremap.ArmPosition.DOWN);



        //Finish up the last TweetyBird action
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        robot.breakPoint("End of OpMode");

        //Always tell TweetyBird to manually stop
        robot.tweetyBird.stop();
    }

    private void pullPixelAndTake() {
        robot.pixelPull.setPosition(robot.pixelPullerPositions.get(3));
        sleep(1250);

        robot.tweetyBird.adjustTo(9,0,0);
        robot.intake.setPower(1);

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

    private void placeBackdrop() {
        //Raise Arm
        robot.setArmPosition(Hardwaremap.ArmPosition.UP);

        //Go to position on backdrop
        robot.tweetyBird.straightLineTo(30,28,-90);

        //Pos 1
        if (robot.propPosition == Hardwaremap.propPositions.LEFT) {
            robot.tweetyBird.straightLineTo(30,18,-90);
        }

        //Pos 2
        if (robot.propPosition == Hardwaremap.propPositions.CENTER) {
            robot.tweetyBird.straightLineTo(30,27,-90);
        }

        //Pos 3
        if (robot.propPosition == Hardwaremap.propPositions.RIGHT) {
            robot.tweetyBird.straightLineTo(30,34,-90);
        }

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();


        //Touch Backdrop
        robot.tweetyBird.adjustTo(9,0,0);

        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        //Drop Pixel
        robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
        sleep(1000);
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        //Back up
        robot.tweetyBird.adjustTo(-5,0,0);
    }
}
