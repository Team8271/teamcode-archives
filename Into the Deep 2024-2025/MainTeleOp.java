package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

//Set intake motor to auto run when intake is down unless reverse intake

@TeleOp(name="Main TeleOp")
public class MainTeleOp extends LinearOpMode {
    @Override
    public void runOpMode(){
        Config robot = new Config(this);
        robot.init();

        //stuff
        boolean debounce = false;
        boolean clawClosed = false;
        boolean verticalMacro = false;
        boolean intakeOverride = false;
        boolean intakeTransferMode = false;


        int positionToHold = robot.vertSlide.getCurrentPosition();
        //Set slides to RUN_WITHOUT_ENCODER
        if(robot.leftSlide.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER){
            robot.leftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        if(robot.rightSlide.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER){
            robot.rightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        telemetry.addLine("Initialized");
        telemetry.update();


        waitForStart();


        while(opModeIsActive()){


            telemetry.addData("Vertical Position", robot.vertSlide.getCurrentPosition());
            String pos = Math.round(robot.odometer.getX()) + ", " + Math.round(robot.odometer.getY()) + ", " + Math.round(Math.toDegrees(robot.odometer.getZ()));
            telemetry.addLine(pos);


            ///Driver One Controls
            double axialControl = -gamepad1.left_stick_y;
            double lateralControl = gamepad1.left_stick_x;
            double yawControl = gamepad1.right_stick_x;
            double mainThrottle = .2+(gamepad1.right_trigger*0.8);
            boolean resetFCD = gamepad1.dpad_up;

            //FCD Reset
            if(resetFCD){
                robot.odometer.resetTo(0,0,0);
            }

            //DriveTrain Start
            double gamepadRadians = Math.atan2(lateralControl, axialControl);
            double gamepadHypot = Range.clip(Math.hypot(lateralControl, axialControl), 0, 1);
            double robotRadians = -robot.odometer.getZ();
            double targetRadians = gamepadRadians + robotRadians;
            double lateral = Math.sin(targetRadians)*gamepadHypot;
            double axial = Math.cos(targetRadians)*gamepadHypot;

            double leftFrontPower = axial + lateral + yawControl;
            double rightFrontPower = axial - lateral - yawControl;
            double leftBackPower = axial - lateral + yawControl;
            double rightBackPower = axial + lateral - yawControl;

            robot.fl.setPower(leftFrontPower * mainThrottle);
            robot.fr.setPower(rightFrontPower * mainThrottle);
            robot.bl.setPower(leftBackPower * mainThrottle);
            robot.br.setPower(rightBackPower * mainThrottle);



            ///Driver Two Controls
            double horzControl = gamepad2.right_stick_x;
            double vertControl = -gamepad2.left_stick_y;
            boolean boxControl = gamepad2.left_trigger > .25;
            boolean clawToggleButton = gamepad2.a;

            boolean intakeNegativeInput = gamepad2.right_bumper;
            boolean intakePositiveInput = gamepad2.right_trigger > .25;
            boolean raiseIntake = gamepad2.y;
            boolean intakeTransfer = gamepad2.x;

            boolean aboveChamberButton = gamepad2.dpad_up;
            boolean belowChamberButton = gamepad2.dpad_right || gamepad2.dpad_left;
            boolean wallButton = gamepad2.dpad_down;




            ///Toggles Start
            //Claw Controls
            if(clawToggleButton && !debounce){
                debounce = true; //Debounce for toggle function
                clawClosed = !clawClosed; //Toggle claw
            }
            //Intake raise
            if(raiseIntake && !debounce){
                intakeOverride = !intakeOverride;
                debounce = true; //Debounce for toggle function
            }
            //Intake Transfer
            if(intakeTransfer && !debounce){
                intakeTransferMode = !intakeTransferMode;
                debounce = true;
            }
            //Reset debounce
            if(!clawToggleButton && !raiseIntake && !intakeTransferMode && debounce){
                debounce = false;
            }

            //Claw stuff
            if(clawClosed){
                robot.closeClaw();
                telemetry.addLine("Claw: Closed");
            }
            else{
                robot.openClaw();
                telemetry.addLine("Claw: Open");
            }

            ///Horizontal Slide Start
            //Setting Brake and Float based on position
            if(robot.slideWithEncoder.getCurrentPosition() <= robot.brakeDistance
                    && robot.slideWithEncoder.getZeroPowerBehavior() != DcMotor.ZeroPowerBehavior.BRAKE){
                robot.slideWithEncoder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
            else if(robot.slideWithEncoder.getZeroPowerBehavior() != DcMotor.ZeroPowerBehavior.FLOAT){
                robot.slideWithEncoder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            }


            //Slide fully extended
            if(robot.slideWithEncoder.getCurrentPosition() >= robot.horizontalMax){
                telemetry.addLine("Horizontal fully extended");
                if(horzControl <= 0){ //Negative input
                    robot.slideWithEncoder.setPower(horzControl);
                }
            }

            //Slide fully retracted
            else if(robot.horzTouch.isPressed()){
                telemetry.addLine("Horizontal fully retracted");
                if(robot.slideWithEncoder.getCurrentPosition() != 0){
                    robot.slideWithEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    robot.slideWithEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                }
                if(horzControl >= 0){ //Positive input
                    robot.slideWithEncoder.setPower(horzControl);
                }
            }

            //Slide gray zone
            else{
                telemetry.addLine("Horizontal gray zone");
                robot.slideWithEncoder.setPower(horzControl);
            }


            ///Intake Start
            if(intakeOverride){
                robot.intakeFlip.setPosition(robot.intakeUpPosition);
            }
            else if(robot.slideWithEncoder.getCurrentPosition() >= robot.intakeOnDistance){
                robot.intakeFlip.setPosition(robot.intakeCollectPosition);
                robot.intakeMotor.setPower(robot.intakePower);
            }
            else if(intakeTransferMode || robot.slideWithEncoder.getCurrentPosition() <= 50){
                robot.intakeFlip.setPosition(robot.intakeTransferPosition);
            }
            else{
                robot.intakeFlip.setPosition(robot.intakeUpPosition);
            }


            if(intakePositiveInput){
                robot.intakeMotor.setPower(robot.intakePower);
            }
            else if(intakeNegativeInput){
                robot.intakeMotor.setPower(-robot.intakePower);
            }
            else{
                robot.intakeMotor.setPower(0);
            }


            ///Vertical Slide Start
            //Macro stuff
            if(aboveChamberButton){
                verticalMacro = true;
                robot.vertSlide.setTargetPosition(robot.aboveChamber);
                if(robot.vertSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
                    robot.vertSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                robot.vertSlide.setPower(0.8);
            }
            if(belowChamberButton){
                verticalMacro = true;
                robot.vertSlide.setTargetPosition(robot.belowChamber);
                if(robot.vertSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
                    robot.vertSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                robot.vertSlide.setPower(0.4);
            }
            if(wallButton){
                verticalMacro = true;
                robot.vertSlide.setTargetPosition(robot.wallHeight);
                if(robot.vertSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
                    robot.vertSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                robot.vertSlide.setPower(0.8);
            }




            //Slide Topped Out
            if(robot.vertSlide.getCurrentPosition() >= robot.verticalMax){
                if(vertControl > 0){
                    vertControl = 0; //If wrong direction then no input (Goes to Stop and Hold)
                }
                telemetry.addLine("Vertical Slide Topped Out");

            }

            //Slide Bottomed Out
            if(robot.magnetTouch.isPressed()){
                robot.vertSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                if(vertControl < 0){
                    vertControl = 0; //If wrong direction then no input (Goes to Stop and Hold)
                    robot.vertSlide.setPower(0);
                }
                telemetry.addLine("Vertical Slide Bottomed Out");
            }

            //There is input and allowed to move freely
            if(vertControl != 0){
                verticalMacro = false;
                if(robot.vertSlide.getMode() != DcMotor.RunMode.RUN_WITHOUT_ENCODER){
                    robot.vertSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                }
                if(vertControl > 0){ //Going up
                    robot.vertSlide.setPower(vertControl);
                }
                else{ //Going down
                    robot.vertSlide.setPower(vertControl / 2);
                }
                robot.vertSlide.setPower(vertControl);
                positionToHold = robot.vertSlide.getCurrentPosition(); //Set hold pos
            }


            //No input  (STOP AND HOLD)
            if(vertControl == 0 && !verticalMacro && !robot.magnetTouch.isPressed()){
                robot.vertSlide.setTargetPosition(positionToHold);
                if(robot.vertSlide.getMode() != DcMotor.RunMode.RUN_TO_POSITION){
                    robot.vertSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                robot.vertSlide.setPower(0.5);
                telemetry.addLine("Vertical Slide Holding");
            }


            ///Box Control
            //Should make box go a little slower
            if(boxControl){
                robot.boxServo.setPosition(robot.boxDumpPosition);
                if(robot.boxServo.getPosition() > robot.boxDumpPosition){
                    robot.boxServo.setPosition(robot.boxServo.getPosition() -2);
                    sleep(200);
                }
            }
            else{
                robot.boxServo.setPosition(robot.boxTransferPosition);
            }

            telemetry.update();

        }
    }
}
