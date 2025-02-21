package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@Autonomous(name = "PARK - Board Side",group = "5")
//@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class ParkBoard extends LinearOpMode {
    Hardwaremap robot;

    @Override
    public void runOpMode() {
        //Init Robot
        robot = new Hardwaremap(this);
        robot.init();
        robot.initVision();

        //Closes Dropper on init
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        //Waiting for start
        waitForStart();
        robot.tweetyBird.engage(); //Engages tweetybird (allows it to control the robot)

        /*
        //Moving forward on the Y axis 28 inches
        robot.tweetyBird.straightLineTo(0,28,0);

        //This will stop the code and wait for tweetybird to stop moving... Its broken so paste it 3 times.
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        //Wait 200 MS
        sleep(200);
        //Open the dropper to SINGLE
        robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);

        //Wait another 400 MS
        sleep(400);
        //Close the dropper
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);*/

        //And instantly start moving to the parked position (30 inches to the right, and stay an inch away from the wall)
        robot.tweetyBird.straightLineTo(30,1,0);

        //For debugging purposes, keep everything active until
        robot.breakPoint("End of OpMode");

        //TweetyBird must me manually called to stop, it just keeps things more stable
        robot.tweetyBird.stop();
    }
}

