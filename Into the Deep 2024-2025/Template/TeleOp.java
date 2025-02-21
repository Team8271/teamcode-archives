package org.firstinspires.ftc.teamcode.Template;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Template TeleOp")
public class TeleOp extends LinearOpMode {
    Configuration robot;

    @Override
    public void runOpMode(){
        //Initialization Start
        robot = new Configuration(this);
        robot.init();
        robot.initTweetyBird();
        ElapsedTime runTime = new ElapsedTime();

        waitForStart(); //Wait for Start

        Driver1 driverOne = new Driver1(this,robot);
        Driver2 driverTwo = new Driver2(this,robot);


        while(opModeIsActive()){
            int threadsRunning = Driver1.activeCount() + Driver2.activeCount();

            ///Telemetry
            telemetry.addLine("RunTime: " + runTime.toString());
            telemetry.addLine("Threads Running: " + threadsRunning);
            telemetry.addLine("Robot Position: " + Math.round(robot.odometer.getX()) + ", " + Math.round(robot.odometer.getY()) + ", " + Math.round(robot.odometer.getZ()));
            telemetry.update();
            sleep(500);
        }
    }
}


//Driver1 thread1 = new Driver1(this, robot);
class Driver1 extends Thread{
    private final LinearOpMode opMode;
    private final Configuration robot;

    public Driver1(LinearOpMode opMode, Configuration robot){
        this.opMode = opMode;
        this.robot = robot;
    }

    @Override
    public void run(){
        while(opMode.opModeIsActive()){
            ///Driver Controls
            double axialControl = -opMode.gamepad1.left_stick_y;
            double lateralControl = opMode.gamepad1.left_stick_x;
            double yawControl = opMode.gamepad1.right_stick_x;
            double mainThrottle = .2+(opMode.gamepad1.right_trigger*0.8);
            boolean resetFCD = opMode.gamepad1.dpad_up;

            //FCD Reset
            if(resetFCD){
                robot.odometer.resetTo(0,0,0);
            }

            ///Mecanum DriveTrain Start
            double gamepadRadians = Math.atan2(lateralControl, axialControl);
            double gamepadHypot = Range.clip(Math.hypot(lateralControl, axialControl), 0, 1);
            double robotRadians = -robot.odometer.getZ();
            double targetRadians = gamepadRadians + robotRadians;
            double lateral = Math.sin(targetRadians)*gamepadHypot;
            double axial = Math.cos(targetRadians)*gamepadHypot;

            //Calculate Power
            double leftFrontPower = axial + lateral + yawControl;
            double rightFrontPower = axial - lateral - yawControl;
            double leftBackPower = axial - lateral + yawControl;
            double rightBackPower = axial + lateral - yawControl;

            //Send Power
            robot.fl.setPower(leftFrontPower * mainThrottle);
            robot.fr.setPower(rightFrontPower * mainThrottle);
            robot.bl.setPower(leftBackPower * mainThrottle);
            robot.br.setPower(rightBackPower * mainThrottle);
        }
    }
}



//Driver2 thread2 = new Driver2(this, robot);
class Driver2 extends Thread{
    private final LinearOpMode opMode;
    private final Configuration robot;

    public Driver2(LinearOpMode opMode, Configuration robot){this.opMode = opMode;this.robot = robot;}

    @Override
    public void run(){
        while(opMode.opModeIsActive()){
            //Put Driver 2 Specific Stuff Here
            ///Driver Controls
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
