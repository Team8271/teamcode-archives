package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@TeleOp(name = "Main Teleop",group = "1")
//@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class MainTeleop extends LinearOpMode {
    Hardwaremap robot;

    @Override
    public void runOpMode() {
        //Init Robot
        robot = new Hardwaremap(this); //Create a Hardware Map
        robot.init(); //Initialize needed components
        robot.initVision(); //Initializing Vision TODO: This was purely for debuging purposes

        //Creating Driver Threads
        MainDriver driver1 = new MainDriver(this,robot); //Base Drivetrain Control
        driver1.start();
        SecondaryDriver driver2 = new SecondaryDriver(this,robot); //Arm Control
        driver2.start();

        //Waiting for start
        waitForStart();

        //While the thread runs, output debug data is being sent to the FTC Dashboard
        while (opModeIsActive()) {
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("X",robot.tweetyBird.getX());
            packet.put("Y",robot.tweetyBird.getY());
            packet.put("Z",robot.tweetyBird.getZ());
            packet.put("Arm Position",robot.armL.getPosition());
            packet.fieldOverlay()
                    .setFill("blue")
                    .fillCircle(robot.tweetyBird.getX(),robot.tweetyBird.getY(),17/2);

            robot.dashboard.sendTelemetryPacket(packet);
        }
    }
}

//Drivetrain Thread
class MainDriver extends Thread {
    //Needed Classes
    private LinearOpMode opMode;
    private Hardwaremap robot;

    private boolean fcdDebounce = false;
    private boolean fcd = true;

    //Constructor
    public MainDriver(LinearOpMode opMode,Hardwaremap hwMap) {
        this.opMode = opMode;
        this.robot = hwMap;
    }

    //Thread
    public void run() {
        //Waiting for start
        opMode.waitForStart();

        //Runtime loop
        while (opMode.opModeIsActive()) {
            //Controls
            double inputX = opMode.gamepad1.left_stick_x;
            double inputY = -opMode.gamepad1.left_stick_y;
            double inputZ = opMode.gamepad1.right_stick_x;
            double inputThrottle = opMode.gamepad1.right_trigger;
            boolean fcdToggleButton = opMode.gamepad1.dpad_down;
            boolean fcdResetButton = opMode.gamepad1.dpad_up;

            //Toggle fcd(field centric driving) on or off
            if(fcdToggleButton&&!fcdDebounce) {
                fcdDebounce=true;
                fcd=!fcd;
            }

            //Debounce
            if(!fcdToggleButton&&fcdDebounce) {
                fcdDebounce=false;
            }

            //ResetZ
            if(fcdResetButton) {
                robot.resetZ();
            }

            //Axial, lateral, yaw, and throttle output
            double axial = inputY;
            double lateral = inputX;
            double yaw = inputZ;
            //inputThrottle = (1/4)+(inputThrottle*(3/4));

            //Field centric calculations if enabled
            if(fcd) {
                double gamepadRadians = Math.atan2(inputX, inputY);
                double gamepadHypot = Range.clip(Math.hypot(inputX, inputY), 0, 1);
                double robotRadians = -robot.getZ();
                double targetRadians = gamepadRadians + robotRadians;
                lateral = Math.sin(targetRadians)*gamepadHypot;
                axial = Math.cos(targetRadians)*gamepadHypot;
            }

            //Sending movements
            robot.setMovementPower(axial,lateral,yaw,Range.clip(inputThrottle,0.2,1));
        }
    }
}

class SecondaryDriver extends Thread {
    //Needed Classes
    private LinearOpMode opMode;
    private Hardwaremap robot;

    //Constructor
    public SecondaryDriver(LinearOpMode opMode,Hardwaremap hwMap) {
        this.opMode = opMode;
        this.robot = hwMap;
    }

    //Thread
    public void run() {
        //Waiting for start
        opMode.waitForStart();

        boolean armDebounce = false;

        //Runtime loop
        while (opMode.opModeIsActive()) {
            //Controls
            boolean closeDropper = (!opMode.gamepad2.a&&!opMode.gamepad2.x)||(opMode.gamepad2.a&&opMode.gamepad2.x);
            boolean singleDropper = opMode.gamepad2.a&&!opMode.gamepad2.x;
            boolean openDropper = opMode.gamepad2.x&&!opMode.gamepad2.a;
            boolean raiseArm = opMode.gamepad2.right_bumper;
            boolean lowerArm = opMode.gamepad2.left_bumper;
            double intakeController = opMode.gamepad2.right_trigger;
            double reverseIntakeController = opMode.gamepad2.left_trigger;
            double armExtendControl = -opMode.gamepad2.left_stick_y;
            double suspendControl = (opMode.gamepad2.dpad_up?1:0)+(opMode.gamepad2.dpad_down?-1:0);
            double pixelPullerControl = 0;//opMode.gamepad2.left_trigger;

            //Setting arm extension directly to the control
            robot.lextend.setPower(armExtendControl);
            robot.rextend.setPower(armExtendControl);

            //Dropper positions
            if (closeDropper) {
                robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);
            }
            if (singleDropper&&!armDebounce) {
                robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            }
            if (openDropper&&robot.armL.getPosition()>0.5&&!armDebounce) {
                robot.setDropperPosition(Hardwaremap.DroperPosition.OPEN);
            }

            //Lower and raise arm
            if(lowerArm&&!armDebounce) {
                armDebounce = true;
                robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);
                robot.setArmPosition(Hardwaremap.ArmPosition.DOWN);

                robot.lextend.setPower(0);
                robot.rextend.setPower(0);
            }
            if(raiseArm&&!armDebounce) {
                armDebounce = true;
                robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);
                robot.setArmPosition(Hardwaremap.ArmPosition.UP);
            }
            if((!lowerArm&&!raiseArm)&&armDebounce) {
                armDebounce = false;
            }

            //Intake
            if(robot.armL.getPosition()<0.5) {
                robot.intake.setPower(intakeController-reverseIntakeController);
            } else {
                robot.intake.setPower(0);
            }

            //Pixel Puller
           if (pixelPullerControl>0) {
                pixelPullerControl = pixelPullerControl*0.06;
                robot.pixelPull.setPosition(0.22-pixelPullerControl);
           } else {
              robot.pixelPull.setPosition(0.9);
           }

           //Suspend
            if (suspendControl == 0) {
                if (robot.lsuspend.getMode()!= DcMotor.RunMode.RUN_TO_POSITION) {
                    robot.lsuspend.setTargetPosition(robot.lsuspend.getCurrentPosition());
                    robot.rsuspend.setTargetPosition(robot.rsuspend.getCurrentPosition());

                    robot.lsuspend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    robot.rsuspend.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    robot.lsuspend.setPower(0.5);
                    robot.rsuspend.setPower(0.5);
                }
            } else {
                if (robot.lsuspend.getMode()!= DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
                    robot.lsuspend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    robot.rsuspend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                }
                robot.lsuspend.setPower(suspendControl);
                robot.rsuspend.setPower(suspendControl);
            }


        }
    }
}