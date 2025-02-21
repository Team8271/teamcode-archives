package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@Autonomous(name="Third Auto (dev)")
public class ThirdAuto extends LinearOpMode {
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
        moveTo(-6,29,0);
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();

        moveUntilSensor(robot.frontTouch, 0.4); //Move into submersible

        setSlidePosition(robot.vertSlide, robot.wallHeight,0.4); //Clip specimen
        sleep(700);
        robot.openClaw(); //Open claw


        ///Pushing samples into observation
        robot.tweetyBird.engage();
        robot.tweetyBird.clearWaypoints();

        moveTo(-6,18,0); //Move back from submersible
        moveTo(28,18,0); //Move to left/below sample 1
        waitForMove();
        moveTo(29,45,0); //Move to left/above sample 1
        waitForMove();
        moveTo(40,45,0); //Move to above sample 1
        waitForMove();
        moveTo(37,11,0); //Push sample 1 into observation
        moveTo(40,45,-180); //Move left/above sample 2
        //waitForMove();
        moveTo(49,45,-180); //Move above sample 2
        waitForMove();
        moveTo(46,5,-180); //Push sample 2 into observation
        moveTo(46,5,-180);
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();
        moveUntilSensor(robot.topTouch,0.4); //Top touch isn't touch-in (More power?)
        closeClaw(); //Grab 2nd specimen
        setSlidePosition(robot.vertSlide, robot.wallHeight+400,0.8);


        clipCycle(-2); //2nd
        clipCycle(-4); //3rd
        clipCycle(-6); //4th
        //clipCycle(-6); //5th


        robot.tweetyBird.close();
    }
    //Start in position after grabbing clip
    public void clipCycle(double offset){
        //Sitting in observation and grabbed specimen
        robot.tweetyBird.engage();
        robot.tweetyBird.clearWaypoints();
        telemetry.addLine(robot.odometer.getX() + ", " + robot.odometer.getY() + ", " + robot.odometer.getZ());
        telemetry.update();

        moveTo(30,15,-180); //Back out of observation
        //waitForMove();
        setSlidePosition(robot.vertSlide, robot.aboveChamber,0.4);
        moveTo(-8+offset,15,0); //Rotate and move to submersible
        waitForMove();
        robot.tweetyBird.clearWaypoints();
        robot.tweetyBird.disengage();
        moveUntilSensor(robot.frontTouch, 0.5);
        setSlidePosition(robot.vertSlide, robot.belowChamber,0.5);
        sleep(1000);
        robot.openClaw(); //Open the claw


        //Robot is sitting against submersible with specimen clipped and claw open
        robot.tweetyBird.engage();
        robot.tweetyBird.clearWaypoints();
        moveTo(-8,25,0); //Back off of submersible
        waitForMove();
        setSlidePosition(robot.vertSlide, robot.wallHeight, 0.3);
        moveTo(33,0,-180); //Move to observation with rotation
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


    public void bucketAuto(){
        robot.boxServo.setPosition(robot.boxStoragePosition); //Set box servo

        robot.tweetyBird.engage();
        //moveTo();
    }
}
