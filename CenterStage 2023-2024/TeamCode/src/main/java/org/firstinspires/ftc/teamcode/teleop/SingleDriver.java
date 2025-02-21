package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@TeleOp(name = "Single Driver",group = "2")
//@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class SingleDriver extends LinearOpMode {
    Hardwaremap robot;

    @Override
    public void runOpMode() {
        //Init Robot
        robot = new Hardwaremap(this); //Create a Hardware Map
        robot.init(); //Initialize needed components
        robot.initVision(); //Initializing Vision TODO: This was purely for debuging purposes

        //Creating Driver Threads
        DrivetrainController driver1 = new DrivetrainController(this,robot); //Base Drivetrain Control
        driver1.start();
        AcceessoriesController driver2 = new AcceessoriesController(this,robot); //Arm Control
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
class DrivetrainController extends Thread {
    //Needed Classes
    private LinearOpMode opMode;
    private Hardwaremap robot;

    private boolean fcdDebounce = false;
    private boolean fcd = true;

    //Constructor
    public DrivetrainController(LinearOpMode opMode, Hardwaremap hwMap) {
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

class AcceessoriesController extends Thread {
    //Needed Classes
    private LinearOpMode opMode;
    private Hardwaremap robot;

    //Constructor
    public AcceessoriesController(LinearOpMode opMode, Hardwaremap hwMap) {
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
            boolean singleDropper = opMode.gamepad1.a;
            boolean openDropper = opMode.gamepad1.x;
            boolean raiseArm = opMode.gamepad1.right_bumper;
            boolean lowerArm = opMode.gamepad1.left_bumper;
            double intakeController = opMode.gamepad1.left_trigger;
            double reverseIntakeController = opMode.gamepad1.y?intakeController*-2:0;
            double armExtendControl = -opMode.gamepad1.right_stick_y;

            //Setting arm extension directly to the control
            robot.lextend.setPower(armExtendControl);
            robot.rextend.setPower(armExtendControl);

            //Dropper
            if (singleDropper&&!armDebounce) {
                robot.setDropperPosition(Hardwaremap.DroperPosition.SINGLE);
            } else if (openDropper&&robot.armL.getPosition()>0.5&&!armDebounce) {
                robot.setDropperPosition(Hardwaremap.DroperPosition.OPEN);
            } else {
                robot.setDropperPosition(Hardwaremap.DroperPosition.CLOSED);
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

        }
    }
}