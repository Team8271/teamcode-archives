package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@Autonomous(name = "TweetyBird Debugger",group = "6")
//@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class TweetyDebug extends LinearOpMode {
    Hardwaremap robot;

    @Override
    public void runOpMode() {
        //Init Robot
        robot = new Hardwaremap(this);
        robot.init();
        robot.initVision();

        robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);

        //Waiting for start
        waitForStart();
        robot.tweetyBird.engage();

        while (opModeIsActive());

        robot.tweetyBird.stop();
    }
}
