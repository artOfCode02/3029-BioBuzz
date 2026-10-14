package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp
public class TeleDrive extends LinearOpMode {

    private final Hardware robot = new Hardware(hardwareMap);

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        if (isStopRequested()) { return; }

        while (opModeIsActive()) {
            Input(robot, gamepad1, gamepad2);
        }

    }

    // Input
    public void Input(Hardware robot, Gamepad gp1, Gamepad gp2) {
        // Gamepad 1 inputs
        double y  = -gp1.left_stick_y; // Forward/Backward
        double x  = gp1.left_stick_x;  // Strafe left/right
        double rx = gp1.right_stick_x; // Rotate

        robot.fl.setPower(y - x + rx);
        robot.fr.setPower(y + x - rx);
        robot.bl.setPower(y - x + rx);
        robot.br.setPower(y + x - rx);
    }
}
