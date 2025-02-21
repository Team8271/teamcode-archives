package org.firstinspires.ftc.teamcode.util;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import dev.narlyx.ftc.tweetybird.TweetyBirdProcessor;
import com.qualcomm.hardware.bosch.BNO055IMUNew;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.templates.TemplateProcessor;
import org.firstinspires.ftc.teamcode.vision.LineDetection;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Hardwaremap {
    /**
     * Opmode References
     */
    private LinearOpMode currentOpmode;

    /**
     * Other used class references
     */
    //TweetyBird
    public TweetyBirdProcessor tweetyBird;
    public FtcDashboard dashboard = FtcDashboard.getInstance();

    /**
     * Global Enums
     */

    public enum DroperPosition {
        CLOSED,
        SINGLE,
        OPEN,
    }

    public enum ArmPosition {
        DOWN,
        UP
    }

    public enum propPositions {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum fieldSides {
        RED,
        BLUE
    }

    //Global Positions
    public Dictionary<Integer,Double> pixelPullerPositions = new Hashtable<>();

    public propPositions propPosition = null;

    public fieldSides side = null;

    /**
     * Hardware Definitions
     */
    //Motor Section
    public DcMotor fl;
    public DcMotor bl;
    public DcMotor fr;
    public DcMotor br;

    public DcMotor intake;

    public DcMotor lsuspend;
    public DcMotor rsuspend;

    //Encoder Pods
    public DcMotor le,re,be;

    //Servos
    public Servo armL;
    public Servo armR;
    public Servo dropper;

    public CRServo lextend;
    public CRServo rextend;

    public Servo pixelPull;

    //Sensors
    public BNO055IMUNew imu = null;
    public WebcamName intakeCamera = null;

    //Temp memory
    private double yawOffset = 0;

    /**
     * Vision
     */
    public VisionPortal visionPortal;
    public AprilTagProcessor aprilTag;
    public TemplateProcessor cameraStreaming;
    public LineDetection lineDetection;


    /**
     * Constructor
     */
    public Hardwaremap(LinearOpMode opMode) {
        //Setting opmode
        currentOpmode = opMode;
    }

    /**
     * Initialize Method
     * This is the method that will be called to start the init process
     */
    public void init() {
        //Getting the current hardwaremap
        HardwareMap hwMap = currentOpmode.hardwareMap;

        //Setting Positions
        pixelPullerPositions.put(0,0.9);
        pixelPullerPositions.put(1,0.16);
        pixelPullerPositions.put(2,0.16);
        pixelPullerPositions.put(3,0.18);
        pixelPullerPositions.put(4,0.21);
        pixelPullerPositions.put(5,0.22);

        //Initializing Motors
        fl = hwMap.get(DcMotor.class, "FL");
        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        bl = hwMap.get(DcMotor.class, "BL");
        bl.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        fr = hwMap.get(DcMotor.class, "FR");
        fr.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        br = hwMap.get(DcMotor.class, "BR");
        br.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intake = hwMap.get(DcMotor.class, "Intake");
        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        lsuspend = hwMap.get(DcMotor.class, "Lsuspend");
        lsuspend.setDirection(DcMotorSimple.Direction.FORWARD);
        lsuspend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        rsuspend = hwMap.get(DcMotor.class, "Rsuspend");
        rsuspend.setDirection(DcMotorSimple.Direction.FORWARD);
        rsuspend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Initializing Encoders
        le = hwMap.get(DcMotor.class, "FR");
        le.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        le.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        re = hwMap.get(DcMotor.class, "FL");
        re.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        re.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        be = hwMap.get(DcMotor.class, "BL");
        be.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        be.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        //Servos
        armL = hwMap.get(Servo.class,"left arm");
        armL.setDirection(Servo.Direction.FORWARD);
        armR = hwMap.get(Servo.class,"right arm");
        armR.setDirection(Servo.Direction.REVERSE);
        dropper = hwMap.get(Servo.class,"dropper");
        dropper.setDirection(Servo.Direction.FORWARD);

        lextend = hwMap.get(CRServo.class,"Lextend");
        lextend.setDirection(DcMotorSimple.Direction.REVERSE);
        rextend = hwMap.get(CRServo.class,"Rextend");
        rextend.setDirection(DcMotorSimple.Direction.FORWARD);

        pixelPull = hwMap.get(Servo.class,"PixelPuller");
        pixelPull.setDirection(Servo.Direction.FORWARD);

        //Webcams
        intakeCamera = hwMap.get(WebcamName.class, "Webcam 1");

        //Initializing Imu
        imu = hwMap.get(BNO055IMUNew.class,"imu");
        IMU.Parameters parameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        );
        imu.initialize(parameters);

        //Initialize TweetyBird
        initTweetyBird();
        tweetyBird.disengage();
    }

    /**
     * Initialize TweetyBird Script
     * Only to be called by this class
     */
    private void initTweetyBird() {
        tweetyBird = new TweetyBirdProcessor.Builder()
                //Setting OpMode
                .setOpMode(currentOpmode)

                //Setitng Hardware Configuration
                .setFrontLeftMotor(fl)
                .setFrontRightMotor(fr)
                .setBackLeftMotor(bl)
                .setBackRightMotor(br)

                .setLeftEncoder(re)
                .setRightEncoder(le)
                .setMiddleEncoder(be)

                .flipLeftEncoder(true)
                .flipRightEncoder(true)
                .flipMiddleEncoder(true)

                .setSideEncoderDistance(11+(7.0/8.0))
                .setMiddleEncoderOffset(-2)

                .setTicksPerEncoderRotation(2000)
                .setEncoderWheelRadius(1.88976/2.0)

                //Setting Up Basic Configuration
                .setMinSpeed(0.25)
                .setMaxSpeed(0.8)
                .setStartSpeed(0.4)
                .setSpeedModifier(0.04)
                .setStopForceSpeed(0.1)

                .setCorrectionOverpowerDistance(5)
                .setDistanceBuffer(1.5)
                .setRotationBuffer(2)

                //Build(create) TweetyBird
                .build();
        tweetyBird.disengage();
    }

    public void initVision() {
        lineDetection = new LineDetection(currentOpmode.telemetry);

        aprilTag = new AprilTagProcessor.Builder()
                .build();

        /*
        CameraName switchableCamera = ClassFactory.getInstance()
                .getCameraManager().nameForSwitchableCamera(frontCam, backCam);*/
        visionPortal = new VisionPortal.Builder()
                .setCamera(intakeCamera)
                //.addProcessors(aprilTag, lineDetection)
                .addProcessors(lineDetection,aprilTag)
                .setCameraResolution(new Size(640,480))
                .build();

        FtcDashboard.getInstance().startCameraStream(lineDetection,0);
    }

    /**
     * Easy Movement Controller
     */
    public void setMovementPower(double axial, double lateral, double yaw, double speed) {
        //Creating Individual Power for Each Motor
        double frontLeftPower  = ((axial + lateral + yaw) * speed);
        double frontRightPower = ((axial - lateral - yaw) * speed);
        double backLeftPower   = ((axial - lateral + yaw) * speed);
        double backRightPower  = ((axial + lateral - yaw) * speed);

        //Set Motor Power
        fl.setPower(frontLeftPower);
        fr.setPower(frontRightPower);
        bl.setPower(backLeftPower);
        br.setPower(backRightPower);
    }

    public void setDropperPosition(DroperPosition position) {
        if (position == DroperPosition.OPEN) {
            dropper.setPosition(0.05);
        }
        if (position == DroperPosition.CLOSED) {
            dropper.setPosition(0.9999);
        }
        if (position == DroperPosition.SINGLE) {
            dropper.setPosition(0.5);
        }
    }

    public void setArmPosition(ArmPosition position) {
        if(position==ArmPosition.DOWN) {
            if (armL.getPosition()==0) {
                armL.setPosition(0.15);
                armR.setPosition(0.14);
            }
            int step = 0;
            while (armL.getPosition()>=0.15) {
                if(step<3) {
                    step++;
                    armL.setPosition(armL.getPosition()-0.01);
                    armR.setPosition(armR.getPosition()-0.01);
                } else {
                    step = 0;
                    armL.setPosition(armL.getPosition()+0.01);
                    armR.setPosition(armR.getPosition()+0.01);
                }
                currentOpmode.sleep(5);
            }
        } else {
            armL.setPosition(0.82);
            armR.setPosition(0.79);
        }
    }

    public void scanProp() {
        List<Prop> detectedProps = lineDetection.getDetectedProps();
        Prop selectedProp;

        if (detectedProps.size()>0) {
            selectedProp = detectedProps.get(0);

            if (selectedProp != null) {
                if (selectedProp.getX()<90) {
                    propPosition = Hardwaremap.propPositions.LEFT;
                } else if (selectedProp.getX()>200) {
                    propPosition = Hardwaremap.propPositions.RIGHT;
                } else {
                    propPosition = Hardwaremap.propPositions.CENTER;
                }
            }
        }

        if (propPosition == null) {
            propPosition = Hardwaremap.propPositions.CENTER;
        }
    }

    public void askSide() {
        TelemetrySelector selector = new TelemetrySelector(currentOpmode);
        String[] options = {"Red Side","Blue Side"};
        String selection = selector.simpleSelector("Select Side",options);


        if (selection.equals(options[0])) { //First selection
           side = fieldSides.RED;
        } else { //Second Selection
            side = fieldSides.BLUE;
        }

        currentOpmode.telemetry.clearAll();
        currentOpmode.telemetry.update();
    }

    public void breakPoint(String caption) {
        boolean proceedControl = false;

        while (currentOpmode.opModeIsActive()&&!proceedControl) {
            proceedControl = currentOpmode.gamepad1.a||currentOpmode.gamepad2.a;

            currentOpmode.telemetry.addLine("You have reached a break point, please press A to proceed.");
            currentOpmode.telemetry.addData("Caption",caption);
            currentOpmode.telemetry.update();
        }

        currentOpmode.telemetry.addLine("Proceeding");
        currentOpmode.telemetry.update();
    }

    public void breakPoint() {
        breakPoint("N/A");
    }

    public void resetSideFlipped() {
       if (side == fieldSides.BLUE) {
           tweetyBird.flipInput(true);
       } else {
           tweetyBird.flipInput(false);
       }
    }

    /**
     * Yaw
     */
    public double getZ() {
        return tweetyBird.getZ()-yawOffset;
    }

    public void resetZ() {
        yawOffset = tweetyBird.getZ();
    }

}
