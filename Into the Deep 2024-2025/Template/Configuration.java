package org.firstinspires.ftc.teamcode.Template;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import dev.narlyx.tweetybird.Drivers.Mecanum;
import dev.narlyx.tweetybird.Odometers.ThreeWheeled;
import dev.narlyx.tweetybird.TweetyBird;

public class Configuration {
    private final LinearOpMode opMode;
    public Configuration(LinearOpMode opMode){this.opMode = opMode;}

    ///Defining parts of robot
    //Define DcMotors
    public DcMotor fl,fr,bl,br;
    //Define Servos
    public Servo exampleServo;
    //Define TouchSensors
    public TouchSensor frontSensor;

    ///Defining TweetyBird Stuff
    public ThreeWheeled odometer;
    public Mecanum mecanum;
    public TweetyBird tweetyBird;

    ///Quick Change Values
    public final double
            exampleServoMax = 5,
            exampleServoMin = 0;


    ///Robot Initialization Sequence
    public void init(){
        //Define the hardware map for accessing devices
        HardwareMap hwMap = opMode.hardwareMap;

        ///Define Motors
        //Front Left Motor
        fl = hwMap.get(DcMotor.class, "FL");            //Name in DS
        fl.setDirection(DcMotor.Direction.REVERSE);                //Motor Direction
        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);        //Reset the Encoder
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  //Set Brake Mode

        //Front Right Motor
        fr = hwMap.get(DcMotor.class, "FR");            //Name in DS
        fr.setDirection(DcMotorSimple.Direction.FORWARD);          //Motor Direction
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);        //Reset the Encoder
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  //Set Brake Mode

        //Back Left Motor
        bl = hwMap.get(DcMotor.class, "BL");            //Name in DS
        bl.setDirection(DcMotor.Direction.REVERSE);                //Motor Direction
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);        //Reset the Encoder
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  //Set Brake Mode

        //Back Right Motor
        br = hwMap.get(DcMotor.class, "BR");            //Name in DS
        br.setDirection(DcMotor.Direction.FORWARD);                //Motor Direction
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);        //Reset the Encoder
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  //Set Brake Mode


        ///Define Servos
        exampleServo = hwMap.get(Servo.class,"exampleServo");


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

        opMode.telemetry.addLine("Main Robot Initialization Completed!");
        opMode.telemetry.update();
    }

    ///Initialization Sequence for TweetyBird
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

        opMode.telemetry.addLine("TweetyBird Initialization Completed!");
        opMode.telemetry.update();
    }

}
