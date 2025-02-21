package org.firstinspires.ftc.teamcode.SwerveDev;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="SwerveTeleOp")
public class SwerveTeleOp extends LinearOpMode {
  @Override
  public void runOpMode(){
    SwerveConfig robot = new SwerveConfig(this);
    robot.init();

    waitForStart();

    while(opModeIsActive()){
      ///Driver One Controls
      double axialControl = -gamepad1.left_stick_y;  //Up down
      double lateralControl = gamepad1.left_stick_x; //Left Right
      double yawControl = gamepad1.right_stick_x;
      double mainThrottle = .2+(gamepad1.right_trigger*0.8);
      boolean resetFCD = gamepad1.dpad_up;

      double gamepadRadians = Math.atan2(lateralControl, axialControl);
      double gamepadHypot = Range.clip(Math.hypot(lateralControl, axialControl), 0, 1);
      double robotRadians = -robot.odometer.getZ();
      double targetRadians = gamepadRadians + robotRadians;
      double lateral = Math.sin(targetRadians)*gamepadHypot;
      double axial = Math.cos(targetRadians)*gamepadHypot;

      /* Servo position is going to be:
       * more left/right based on lateral
       * more forward/back based on axial
       * add rotation with yaw control
       */







      //If servo position is set at 200 starting at 0
      //It should go to 20 ticks

      //Tell it to go to 200
      if(gamepad1.dpad_up){
        setServoPosition(robot.testServo, 200);
      }

      //Tell it to go to 0
      if(gamepad1.dpad_down){
        setServoPosition(robot.testServo, 200);
      }
      
      //Update telemetry
      if(gamepad1.a){
        telemetry.addLine("Test Servo Position" + robot.testServo.getPosition());
        telemetry.update();
      }
      
    }
  }

  //Called per loop to set Servo Positioning based on lateral, axial, and yaw values
  public void servoDrive(double lateral, double axial, double yaw){
    
  }


  //Method to set Servo position to desired position with minimal rotation
  public void setServoPosition(Servo servo, int position){
    //If degree inputted is closer than the opposite of that degree
    if(Math.abs(servo.getPosition()-position) <= Math.abs(servo.getPosition()-degreeOpposite(position))){
      servo.setPosition(position);
    }
    //If Opposite degree is closer
    else{
      servo.setPosition(degreeOpposite(position));
    }
  }

  //Method for opposite rotation of a Servo ie(90 & 270 keep wheel facing same direction)
  public int degreeOpposite(int degree){
    //For a positive int
    if(degree >= 0){
      return degree += 180;
    }
    //For a negative int
    else{
      return degree -= 180;
    }
  }
}
