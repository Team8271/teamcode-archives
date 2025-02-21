package org.firstinspires.ftc.teamcode.modules;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class TelemetrySelector {

    //OpMode
    private LinearOpMode opMode;

    //Constructor
    public TelemetrySelector(LinearOpMode opMode) {
        //Setting OpMode
        this.opMode = opMode;
    }

    //Class to call!
    public String simpleSelector(String name, String[] options) {
        //Creating string that eventually will be outputted
        StringBuilder output = new StringBuilder();

        //Values
        int curretlySelected = 1;
        boolean buttonHeld = false;

        boolean firstRun = true;


        while (opMode.opModeIsActive()||opMode.opModeInInit()) { //Runs in a loop

            //Controls
            boolean moveUp = (opMode.gamepad1.dpad_up || opMode.gamepad2.dpad_up);
            boolean moveDown = (opMode.gamepad1.dpad_down || opMode.gamepad2.dpad_down);
            boolean select = (opMode.gamepad1.a || opMode.gamepad2.a);


            if(firstRun&!select) {
                firstRun = false;
            }

            //Title
            output.append("-- ");
            output.append(name);
            output.append(" --\n\n\n");

            //Printing options
            for (int i = 1; i <= options.length; i++) {
                output.append(i==curretlySelected?"[#] ":"[  ] ");
                output.append(options[i-1]);
                output.append("\n\n");
            }


            //Functioning Controls
            if(moveUp&&!buttonHeld) { //Navigate up
                if(curretlySelected==1) {
                    curretlySelected = options.length;
                } else {
                    curretlySelected--;
                }
                buttonHeld = true;
            }

            if(moveDown&&!buttonHeld) { //Navigate down
                if(curretlySelected==options.length) {
                    curretlySelected = 1;
                } else {
                    curretlySelected++;
                }
                buttonHeld = true;
            }

            if(!(moveUp||moveDown||select)) { //Wait for next input
                buttonHeld = false;
            }

            if(select&!firstRun) { //Quit
                opMode.telemetry.clearAll();
                opMode.telemetry.update();
                break;
            }

            //Printing
            opMode.telemetry.addLine(String.valueOf(output));
            output.setLength(0);
            opMode.telemetry.update();
            opMode.telemetry.clearAll();
            opMode.telemetry.update();
        }

        return options[curretlySelected-1];


    }


}
