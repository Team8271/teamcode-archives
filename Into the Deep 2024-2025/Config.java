package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import dev.narlyx.tweetybird.Drivers.Mecanum;
import dev.narlyx.tweetybird.Odometers.ThreeWheeled;
import dev.narlyx.tweetybird.TweetyBird;

public class Config {
    private final LinearOpMode opMode;




    // - - - Things that don't need to be changed - - - \\ START
    public DcMotor fl, fr, bl, br, leftSlide, rightSlide, intakeMotor, vertSlide; //Motors
    public DcMotor slideWithEncoder; //Used to define which horizontal slide is equipped with encoder wires.
    public Servo intakeFlip, clawLeft, clawRight, boxServo;
    public TouchSensor frontTouch, topTouch, magnetTouch, horzTouch;
    private double clawLeftOpen, clawRightOpen, clawLeftClosed, clawRightClosed; //Claw Values
    public ThreeWheeled odometer;   //Used for setting up odom pods
    public Mecanum mecanum;         //Used for mecanum wheels for TweetyBird
    public TweetyBird tweetyBird;   //Used for TweetyBird setup

    // - - - Things that don't need to be changed - - - \\ END
    // - - - - - - - - - - - - - - - - - - - - - - - - - \\
    // - - - Things that can change - - - \\ START
    public final double intakeCollectPosition   = 0.95,
            intakeUpPosition        = 0.3,
            intakeTransferPosition  = 0.1,
            intakePower             = .8,
            boxDumpPosition         = 0.83,
            boxTransferPosition     = 0.07,
            boxStoragePosition      = 0.2,
            clawClosedValue         = 0.30, //Smaller # = More Closed (0-1) (left claw)
            clawOpenValue           = 0.5,  //Larger # = More Open (0-1)  (left claw)
            slowSpeed               = 0.3,
            fastSpeed               = 0.4;

    public int          aboveChamber            = 1665,
            belowChamber            = 1200,
            wallHeight              = 280,
            highBasket              = 0,    //This value is not currently used
            horizontalMax           = 450,
            verticalMax             = 3300,
            intakeOnDistance        = 250,  //The distance at which the intake flips down
            brakeDistance           = 100;  //Horizontal slide stops Braking at this point
    // - - - Things that can change - - - \\ END



    //Don't change this
    public Config(LinearOpMode opMode){this.opMode = opMode;}

    //Initialization Method
    public void init(){
        //Used to define all motors and servos
        HardwareMap hwMap = opMode.hardwareMap;
        ///Define Motors
        //Define the front left wheel
        fl = hwMap.get(DcMotor.class, "FL");
        fl.setDirection(DcMotor.Direction.REVERSE);
        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Define the front right wheel
        fr = hwMap.get(DcMotor.class, "FR");
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Define the back left wheel
        bl = hwMap.get(DcMotor.class, "BL");
        bl.setDirection(DcMotor.Direction.REVERSE);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Define the back right wheel
        br = hwMap.get(DcMotor.class, "BR");
        br.setDirection(DcMotor.Direction.FORWARD);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Defining the Horizontal Left Motor
        leftSlide = hwMap.get(DcMotor.class,"HorzLeft");
        leftSlide.setDirection(DcMotor.Direction.FORWARD);
        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Defining the Horizontal Right Motor
        rightSlide = hwMap.get(DcMotor.class,"HorzRight");
        rightSlide.setDirection(DcMotor.Direction.REVERSE);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // - - - - - - - - - - - - - - - - - - - - - -
        //Define which horizontal slide has the encoder
        slideWithEncoder = leftSlide;

        //Defining the Intake Motor
        intakeMotor = hwMap.get(DcMotor.class,"IntakeMotor");
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Defining the Vertical Motor
        vertSlide = hwMap.get(DcMotor.class,"Vert");
        vertSlide.setDirection(DcMotor.Direction.REVERSE);
        vertSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vertSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        ///Define Servos
        intakeFlip = hwMap.get(Servo.class,"IntakeFlip");
        boxServo = hwMap.get(Servo.class,"BoxServo");
        clawLeft = hwMap.get(Servo.class,"ClawLeft");
        clawRight = hwMap.get(Servo.class,"ClawRight");

        ///Define touch sensors
        frontTouch = hwMap.get(TouchSensor.class,"FrontTouch");
        topTouch = hwMap.get(TouchSensor.class,"TopTouch");
        magnetTouch = hwMap.get(TouchSensor.class,"MagnetTouch");
        horzTouch = hwMap.get(TouchSensor.class,"HorzTouch");


        mecanum = new Mecanum.Builder()
                .setFrontLeftMotor(fl)
                .setFrontRightMotor(fr)
                .setBackLeftMotor(bl)
                .setBackRightMotor(br)
                .build();

        odometer = new ThreeWheeled.Builder()
                .setLeftEncoder(bl)
                .setRightEncoder(fr)
                .setMiddleEncoder(br)

                .setEncoderTicksPerRotation(2000)
                .setEncoderWheelRadius(0.944882)

                //Change the true/false values to correct directions
                .setFlipLeftEncoder(true)
                .setFlipRightEncoder(true)
                .setFlipMiddleEncoder(true)

                .setSideEncoderDistance(12)
                .setMiddleEncoderOffset(9.75)
                .build();

        odometer.resetTo(0,0,0);






        //Claw stuff and maths
        clawLeftOpen    = clawOpenValue;
        clawRightOpen   = 1-clawOpenValue;
        clawLeftClosed  = clawClosedValue;
        clawRightClosed = 1-clawClosedValue;

    }

    //Sets up and starts tweetybird/odom
    public void initTweetyBird(){
        odometer.resetTo(0,0,0);
        tweetyBird = new TweetyBird.Builder()
                // Your configuration options here
                .setDistanceBuffer(1) //inches
                .setDriver(mecanum)
                .setLinearOpMode(opMode)
                .setMaximumSpeed(0.7)
                .setMinimumSpeed(0.4)
                .setOdometer(odometer)
                .setRotationBuffer(4)
                .setDebuggingEnabled(false)
                .build();
    }

    public void closeClaw(){
        clawLeft.setPosition(clawLeftClosed); //Set left servo to close position
        clawRight.setPosition(clawRightClosed); //Set right servo to close position

    }

    public void openClaw(){
        clawLeft.setPosition(clawLeftOpen); //Set left servo to open position
        clawRight.setPosition(clawRightOpen); //Set right servo to open position
    }

    public void setSlidePosition(DcMotor slide, int target, double power){
        slide.setTargetPosition(target);
        if(slide.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
            slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        slide.setPower(power);
    }
    public void moveUntilSensor(@NonNull TouchSensor sensor, double speed){
        //Set all wheels to move at speed
        fr.setPower(speed);
        br.setPower(speed);
        fl.setPower(speed);
        fr.setPower(speed);
        opMode.telemetry.addLine("Waiting for " + sensor);
        opMode.telemetry.update();
        while(!sensor.isPressed()){
            opMode.sleep(100); //Waiting
        }

        //This part pushes the bot into the plane more. Ensure bot isn't at angle to wall
        fr.setPower(.6);
        br.setPower(.6);
        fl.setPower(.6);
        fr.setPower(.6);
        opMode.sleep(300);
        //Stop all wheels
        fr.setPower(0);
        br.setPower(0);
        fl.setPower(0);
        fr.setPower(0);
    }
}