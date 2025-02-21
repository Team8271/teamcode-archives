package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@Autonomous(name = "PARK - Pixel Side",group = "5")
//@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class ParkPixel extends LinearOpMode {
    Hardwaremap robot;

    @Override
    public void runOpMode() {
        //Init Robot
        robot = new Hardwaremap(this);
        robot.init();
        robot.initVision();

        //Close dropper for INIT
        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        //Waiting for start
        waitForStart();
        robot.tweetyBird.engage(); //Start Tweetybird (allow it to move)

        //This will set the limit of how fast it can travel
        robot.tweetyBird.speedLimit(0.5);

        //Stringing together 3 waypoints, this will menuver around the spike marks
        robot.tweetyBird.straightLineTo(-20,20,0);
        robot.tweetyBird.straightLineTo(-20,40,0);
        robot.tweetyBird.straightLineTo(0,50,90);

        //Wait until the above string is completed before letting the code proceed, paste it 3 times because sometimes it will accidently skip one
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();
        robot.tweetyBird.waitWhileBusy();

        //Removing the speed limit and going max speed
        robot.tweetyBird.speedLimit(1);

        //Parking
        robot.tweetyBird.straightLineTo(94,50,90);



        //Keep the code alive for debug purposes
        robot.breakPoint("End of OpMode");

        //Always tell tweetybird to manually stop
        robot.tweetyBird.stop();
    }
}

