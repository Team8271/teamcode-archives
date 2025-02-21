package org.firstinspires.ftc.teamcode.templates;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.util.Hardwaremap;

@TeleOp(name = "Template",group = "Testing")
@Disabled //DO NOT FORGET TO UNCOMMENT THIS FOR USE
public class TemplateOpmode extends LinearOpMode {
    Hardwaremap robot;

    @Override
    public void runOpMode() {
        //Init Robot
        robot = new Hardwaremap(this);
        robot.init();

        //Waiting for start
        waitForStart();

    }
}
