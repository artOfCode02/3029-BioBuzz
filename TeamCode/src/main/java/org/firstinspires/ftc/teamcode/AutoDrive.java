package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous
@Disabled
public class AutoDrive extends LinearOpMode {

    /* Declare OpMode members. */
    //private DcMotor leftDrive   = null;
    private final Hardware robot = new Hardware(hardwareMap);
    //private DcMotor         rightDrive  = null;

    private ElapsedTime runtime = new ElapsedTime();

    // Calculate the COUNTS_PER_INCH for your specific drive train.
    // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
    // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
    // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
    // This is gearing DOWN for less speed and more torque.
    // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    @Override
    public void runOpMode() {

        //leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.fl.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        robot.fr.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        robot.bl.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        robot.br.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);


        //leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.fl.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        robot.fr.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        robot.bl.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        robot.br.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Starting at: ",  "FL: %d, FR: %d, BL: %d, BR: %d",
                robot.fl.getCurrentPosition(),
                robot.fr.getCurrentPosition(),
                robot.bl.getCurrentPosition(),
                robot.br.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        //encoderDrive(DRIVE_SPEED,  48,  48, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        //encoderDrive(TURN_SPEED,   12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        //encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }

    /*
     *  Method to perform a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the OpMode running.
     */
    public void EncoderDrive(Hardware robot, double speed, double inches, double angleRad, double timeoutS) {
        int newTargetY;
        int newTargetX;

        if (opModeIsActive()) {
            // Determine new target position, and pass to motor controller
            newTargetY = (int)(inches * Math.sin(angleRad));
            newTargetX = (int)(inches * Math.cos(angleRad));
            robot.fl.setTargetPosition(newTargetY + newTargetX);
            robot.fr.setTargetPosition(newTargetY - newTargetX);
            robot.bl.setTargetPosition(newTargetY - newTargetX);
            robot.br.setTargetPosition(newTargetY + newTargetX);

            // Turn on RUN_TO_POSITION
            robot.fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Reset the timeout time and start motion
            runtime.reset();
            robot.fl.setPower(Math.abs(speed));
            robot.fr.setPower(Math.abs(speed));
            robot.bl.setPower(Math.abs(speed));
            robot.br.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            boolean motorsBusy = (robot.fl.isBusy() && robot.fr.isBusy()) && (robot.bl.isBusy() && robot.br.isBusy());
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (motorsBusy)) {

                // Display it for the driver.
                telemetry.addData("Running to: ",  "FL: %d, FR: %d, BL: %d, BR: %d",
                        newTargetY + newTargetX,
                        newTargetY - newTargetX,
                        newTargetY - newTargetX,
                        newTargetY + newTargetX);
                telemetry.addData("Currently at",  "FL: %d, FR: %d, BL: %d, BR: %d",
                        robot.fl.getCurrentPosition(),
                        robot.fr.getCurrentPosition(),
                        robot.bl.getCurrentPosition(),
                        robot.br.getCurrentPosition());
                telemetry.update();
            }

        }
    }

    /*
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = rightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            leftDrive.setTargetPosition(newLeftTarget);
            rightDrive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            leftDrive.setPower(Math.abs(speed));
            rightDrive.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftDrive.isBusy() && rightDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Running to",  " %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Currently at",  " at %7d :%7d",
                        leftDrive.getCurrentPosition(), rightDrive.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            leftDrive.setPower(0);
            rightDrive.setPower(0);

            // Turn off RUN_TO_POSITION
            leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move.
        }
    }*/
}