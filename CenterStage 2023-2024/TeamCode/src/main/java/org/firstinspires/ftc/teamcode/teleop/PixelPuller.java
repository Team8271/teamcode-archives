package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@TeleOp(name = "Pixel Puller Test",group = "Testing")
//@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class PixelPuller extends LinearOpMode {
    Hardwaremap robot;

    @Override
    public void runOpMode() {
        //Init Robot
        robot = new Hardwaremap(this);
        robot.init();

        //Waiting for start
        waitForStart();

        boolean debounce = false;
        int i = 0;

        while (opModeIsActive()) {
            if (gamepad1.a&&!debounce) {
                debounce = true;
                if (i<5) {
                    i++;
                } else {
                    i=0;
                }
            }

            if (!gamepad1.a&&debounce) {
                debounce = false;
            }

            robot.pixelPull.setPosition(robot.pixelPullerPositions.get(i));

        }
    }
}
