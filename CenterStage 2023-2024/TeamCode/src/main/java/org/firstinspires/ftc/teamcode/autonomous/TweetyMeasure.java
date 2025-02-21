package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@Autonomous(name = "TweetyBird Tape Measure",group = "6")
//@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class TweetyMeasure extends LinearOpMode {
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
        robot.tweetyBird.disengage();

        while (opModeIsActive()) {
            telemetry.addData("X",robot.tweetyBird.getX());
            telemetry.addData("Y",robot.tweetyBird.getY());
            telemetry.addData("Z",robot.tweetyBird.getZ());
            telemetry.update();
        }

        robot.tweetyBird.stop();
    }
}
