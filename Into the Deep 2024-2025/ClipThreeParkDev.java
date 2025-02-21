package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Clip Three Park Dev")
public class ClipThreeParkDev extends LinearOpMode {
    public ElapsedTime runTime;
    Config robot;
    @Override
    public void runOpMode(){
        robot = new Config(this);
        robot.init();
        robot.initTweetyBird();

        robot.slideWithEncoder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        closeClaw();

        telemetry.addLine("Initialized");
        telemetry.update();

        runTime = new ElapsedTime();

        waitForStart(); //WAIT FOR USER TO PRESS START

        runTime.reset();

        robot.boxServo.setPosition(robot.boxStoragePosition); //Set box servo
        setSlidePosition(robot.vertSlide, robot.aboveChamber, 0.8); //Set slide above chamber


        ///Clip preload
        robot.tweetyBird.engage();
        tweetyBirdMoveTo(-6,29,0);
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();

        telemetry.addLine("TweetyBird disengaged");
        telemetry.update();

        moveUntilSensor(robot.frontTouch, 0.2); //Move into submersible

        setSlidePosition(robot.vertSlide, robot.wallHeight,0.3); //Clip specimen
        sleep(1000);
        robot.openClaw(); //Open claw


        ///Pushing samples into observation
        robot.tweetyBird.engage();
        waitForMove();
        tweetyBirdMoveTo(-6,18,0); //Move back from submersible
        tweetyBirdMoveTo(28,18,-180); //Move to left/below sample 1
        waitForMove();
        tweetyBirdMoveTo(29,43,-180); //Move to left/above sample 1
        waitForMove();
        tweetyBirdMoveTo(40,43,-180); //Move to above sample 1
        waitForMove();
        tweetyBirdMoveTo(37,11,-180); //Push sample 1 into observation
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();
        moveUntilSensor(robot.topTouch,0.4); //Top touch isn't touch-in (More power?)
        closeClaw(); //Grab 2nd specimen
        setSlidePosition(robot.vertSlide, robot.wallHeight+400,0.8);


        //flies backward
        clipCycle(0); //2nd
        //clipCycle(-2); //3rd
        //clipCycle(-4); //4th
        //clipCycle(-6); //5th

        //Sitting in observation and grabbed specimen
        robot.tweetyBird.engage();
        telemetry.addLine(robot.odometer.getX() + ", " + robot.odometer.getY() + ", " + robot.odometer.getZ());
        telemetry.update();

        tweetyBirdMoveTo(34,15,-180); //Back out of observation
        waitForMove(); //Added to prevent backing into other team observation?? Might work
        setSlidePosition(robot.vertSlide, robot.aboveChamber,0.4);
        tweetyBirdMoveTo(-10,15,0); //Rotate and move to submersible
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();
        moveUntilSensor(robot.frontTouch, 0.4);
        setSlidePosition(robot.vertSlide, robot.belowChamber,0.4);
        sleep(1000);
        robot.openClaw(); //Open the claw


        //Robot is sitting against submersible with specimen clipped and claw open
        robot.tweetyBird.engage();
        tweetyBirdMoveTo(-10,25,0); //Back off of submersible
        waitForMove();
        setSlidePosition(robot.vertSlide, robot.wallHeight, 0.3);
        tweetyBirdMoveTo(33,0,0); //Move to observation with no rotation
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();



        robot.tweetyBird.close();
    }
    //Stupid work around for tweety skipping waypoints
    public void tweetyBirdMoveTo(double x, double y, double z){
        robot.tweetyBird.addWaypoint(x,y,z);
        double targetX = robot.tweetyBird.getCurrentWaypoint().getX();
        double targetY = robot.tweetyBird.getCurrentWaypoint().getY();
        double targetZ = robot.tweetyBird.getCurrentWaypoint().getZ();
        telemetry.addLine(targetX + ", " + targetY + ", " + targetZ);

        sleep(100); //Wait for tweety a second
        //If tweetyBird is ignoring me try to fix it
        if(targetX != x || targetY != y || targetZ != z && opModeIsActive()){ //Target ain't target
            telemetry.addLine("Resetting Tweety Position");
            robot.tweetyBird.clearWaypoints();
            tweetyBirdMoveTo(x,y,z); //Try again
        }
        telemetry.update();
    }

    //Start in position after grabbing clip
    public void clipCycle(double offset){
        //Sitting in observation and grabbed specimen
        robot.tweetyBird.engage();
        telemetry.addLine(robot.odometer.getX() + ", " + robot.odometer.getY() + ", " + robot.odometer.getZ());
        telemetry.update();

        tweetyBirdMoveTo(34,15,-180); //Back out of observation
        waitForMove(); //Added to prevent backing into other team observation?? Might work
        setSlidePosition(robot.vertSlide, robot.aboveChamber,0.4);
        tweetyBirdMoveTo(-8+offset,15,0); //Rotate and move to submersible
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();
        moveUntilSensor(robot.frontTouch, 0.4);
        setSlidePosition(robot.vertSlide, robot.belowChamber,0.4);
        sleep(1000);
        robot.openClaw(); //Open the claw


        //Robot is sitting against submersible with specimen clipped and claw open
        robot.tweetyBird.engage();
        tweetyBirdMoveTo(-8,25,0); //Back off of submersible
        waitForMove();
        setSlidePosition(robot.vertSlide, robot.wallHeight, 0.3);
        tweetyBirdMoveTo(33,0,-180); //Move to observation with rotation
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();
        moveUntilSensor(robot.topTouch, 0.4);
        closeClaw(); //Grab specimen
        setSlidePosition(robot.vertSlide, robot.wallHeight+500,0.8);
        sleep(500);
    }

    public void closeClaw(){
        robot.closeClaw();
        sleep(500);
    }
    public void waitForMove(){
        robot.tweetyBird.waitWhileBusy();
    }
    public void moveTo(double x, double y, double z){
        robot.tweetyBird.addWaypoint(x,y,z);
    }
    public void setSlidePosition(@NonNull DcMotor slide, int target, double power){
        slide.setTargetPosition(target);
        if(slide.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
            slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        slide.setPower(power);
    }
    public void moveUntilSensor(@NonNull TouchSensor sensor, double speed){
        /*
        setAllWheelPower(speed);
        ElapsedTime startTime = runTime;
        while(!sensor.isPressed()){
            telemetry.addLine("Waiting for " + sensor.getDeviceName());
        }
        telemetry.update();
        setAllWheelPower(.4);
        sleep(400);
        setAllWheelPower(0);*/
        setAllWheelPower(speed);
        sleep(700);
        setAllWheelPower(0);

    }
    public void setAllWheelPower(double power){
        robot.fl.setPower(power);
        robot.fr.setPower(power);
        robot.bl.setPower(power);
        robot.br.setPower(power);
    }


//        setSlidePosition(robot.vertMotor, robot.aboveBasket, 0.5)

    public void bucketAuto(){
        robot.boxServo.setPosition(robot.boxStoragePosition); //Set box servo

        robot.tweetyBird.engage();
        //moveTo();
    }
}
