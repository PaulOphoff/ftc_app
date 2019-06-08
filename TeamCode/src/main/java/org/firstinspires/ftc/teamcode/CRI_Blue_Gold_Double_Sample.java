package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="CRI_Blue_Gold_Double_Sample", group="Linear Opmode")
//@Disabled
public class CRI_Blue_Gold_Double_Sample extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBackDrive;
    private DcMotor rightBackDrive;
    private DcMotor leftLift;
    private DcMotor rightLift;
    private DcMotor MtDew;
    private DcMotor DrPepper;
    private DcMotor MineralLifter;
    private Servo latch;
    private NormalizedColorSensor SampleSensor;
    double leftBackPower;
    double rightBackPower;
    static final double countsPerMotor          = 1120 ;
    static final double gearReduction           = 1.0 ;
    static final double wheelDiameter           = 4.0 ;
    static final double countsPerInch           = (countsPerMotor * gearReduction) /
            (wheelDiameter * Math.PI);
    static final double spinInchesPerDegrees    = (15.375 * Math.PI) / 334.0206185567;
    static final double rotateDegrees           = (30.75 * Math.PI) / 360;
    static final double spinCountsPerDegree     = (countsPerInch * spinInchesPerDegrees);

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftBackDrive  = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBackDrive");
        leftLift = hardwareMap.get(DcMotor.class, "leftLift");
        rightLift = hardwareMap.get(DcMotor.class, "rightLift");
        MtDew = hardwareMap.get(DcMotor.class, "MtDew");
        DrPepper = hardwareMap.get(DcMotor.class, "DrPepper");
        MineralLifter = hardwareMap.get(DcMotor.class, "MineralLifter");
        latch = hardwareMap.servo.get("latch");
        SampleSensor = hardwareMap.get(NormalizedColorSensor.class, "color");
        leftLift.setDirection(DcMotor.Direction.FORWARD);
        rightLift.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
        MineralLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        MineralLifter.setMode(DcMotor.RunMode.RUN_TO_POSITION);



        waitForStart();

        land();

        unhook();

        moveToSample();

        if (isSampleGold()) {
            middlePath();
        }
        else {
            goToNextSample();

            if (isSampleGold()) {
                rightPath();
            }
            else {
                goToLastSample();

                if (isSampleGold()) {
                    leftPath();
                }
                else {
                    lastResort();
                    updateTelemetry("Last Resort");
                }
            }
        }
    }

    private void land() {
        double startTime = getRuntime();
        while (opModeIsActive() && (getRuntime() - startTime <= .4)) {
            rightLift.setPower(1);
            leftLift.setPower(-1);
            updateTelemetry("Landing");
        }
        stopMotors();
    }

    private void unhook() {
        double startTime = getRuntime();
        while (opModeIsActive() && (getRuntime() - startTime <= .5)) {
            rightLift.setPower(-1);
            leftLift.setPower(1);
            updateTelemetry("Unhooking");
            latch.setPosition(0);
            updateTelemetry("unlatching");
            stopMotors();
        }
    }

    private void moveToSample() {
        double startTime = getRuntime();

        stopMotors();
        encoderDrive(.5, .375, -.375, .1);
        updateTelemetry("Positioning To Sample");
        encoderDrive(.5, -18, 18, 5);
        updateTelemetry("Driving To First Sample");
        stopMotors();
        encoderSpin(.5, 84);
        updateTelemetry("Spinning 90 Degrees");
        stopMotors();
    }

    private void middlePath() {
        sample("middle");
        moveToDoubleSampleMiddle();
        scoreMarker();
        hitGold();
        moveBackToCrater();

    }

    private void sample(String samplePosition) {
        updateTelemetry("Sampling " + samplePosition);

        encoderDrive(.5, -5, 5, 1);
        encoderSpin(.5, 81);
        encoderSpin(.5, -81);
        encoderDrive(.5, 5, -5, 1);

        updateTelemetry("Sampling " + samplePosition + " - Done!");
    }

    private void moveToDoubleSampleMiddle() {

        encoderDrive(.5, 14.5, -14.5, 7);
        updateTelemetry("Positioning For Marker");
        stopMotors();
        encoderSpin(.5, -45);
        updateTelemetry("Positioning For Marker 2");
        MineralLifter.setTargetPosition(-450);
        MineralLifter.setPower(.6);
        updateTelemetry("Lifting Arm Up");
        encoderDrive(.5, -30, 30, 15);
        updateTelemetry("Moving To Depot");
        stopMotors();
    }

    private void hitGold() {

        encoderSpin(.5, 82.5);
        encoderDrive(.5, -30, 30, 15);
        MineralLifter.setTargetPosition(0);
        MineralLifter.setPower(.6);
        MtDew.setPower(1);
        DrPepper.setPower(1);
        encoderDrive(.5, -30, 30, 15);
        stopMotors();
        encoderDrive(.5, 30, -30, 15);
        stopMotors();
    }

    private void moveBackToCrater() {

        encoderDrive(.5, 27, -27, 15);
        encoderSpin(.5, -82.5);
        encoderDrive(.5, 60, -60, 15);
        encoderSpin(.5, 82.5);
        encoderDrive(.5, 40, -40, 15);
    }

    private void rightPath() {
        sample("Right");
        moveToDoubleSampleRight();
        scoreMarker();
        hitGold();
        moveBackToCrater();

    }

    private void moveToDoubleSampleRight() {

        stopMotors();
        encoderSpin(.5, -45);
        updateTelemetry("Positioning For Marker 2");
        MineralLifter.setTargetPosition(-450);
        MineralLifter.setPower(.6);
        updateTelemetry("Lifting Arm Up");
        encoderDrive(.5, -18, 18, 15);
        updateTelemetry("Moving To Depot");
        stopMotors();
    }

    private void leftPath() {
        sample("Left");
        moveToDoubleSampleLeft();
        scoreMarker();
        hitGold();
        moveBackToCrater();

    }

    private void lastResort() {
        moveToDoubleSampleLeft();
        scoreMarker();
        hitGold();
        moveBackToCrater();
    }

    private void moveToDoubleSampleLeft() {

        encoderDrive(.5, 14.5, -14.5, 7);
        updateTelemetry("Positioning For Marker");
        stopMotors();
        encoderSpin(.5, -45);
        updateTelemetry("Positioning For Marker 2");
        MineralLifter.setTargetPosition(-450);
        MineralLifter.setPower(.6);
        updateTelemetry("Lifting Arm Up");
        encoderDrive(.5, -42, 42, 15);
        updateTelemetry("Moving To Depot");
        stopMotors();
    }


    private void goToNextSample() {
        double startTime = getRuntime();

        encoderDrive(.5, 14.5, -14.5, 3);
        updateTelemetry("MovingToNextSample");

        stopMotors();
    }

    private void goToLastSample() {
        double startTime = getRuntime();

        encoderDrive(.5, -29, 29, 5);
        updateTelemetry("MovingToLastSample");

        stopMotors();
    }

    private void moveToMarker() {
        double startTime = getRuntime();

        encoderDrive(.5, 14.5, -14.5, 7);
        updateTelemetry("Positioning For Marker");
        MineralLifter.setTargetPosition(-450);
        MineralLifter.setPower(.6);
        updateTelemetry("Lifting Arm Up");
        stopMotors();
        encoderSpin(.5, -82.5);
        updateTelemetry("Positioning For Marker 2");
        encoderDrive(.5, -30, 30, 15);
        updateTelemetry("Moving To Depot");
        stopMotors();
    }

    private void driveToWall() {
        encoderDrive(.5, 30, -30, 10);
    }
    private void driveToDepot() {
        encoderSpin(.5, -136.5);
        encoderDrive(.5, -55, 55, 15);
    }
    private void scoreMarker() {
        double startTime = getRuntime();
        while (opModeIsActive() && (getRuntime() - startTime <= 1.5)) {
            Push(.6);
            updateTelemetry("Scoring marker");
            updateTelemetry("Pulling Arm Down");
        }
    }



    private void Push(double speed) {
        MtDew.setPower(speed);
    }

    private void stopMotors() {
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
        leftLift.setPower(0);
        rightLift.setPower(0);
        MtDew.setPower(0);
    }

    private boolean isSampleGold() {
        double startTime = getRuntime();
        long goldCount = 0;
        long silverCount = 0;

        while(getRuntime() - startTime <= .5) {
            NormalizedRGBA sampleColors = SampleSensor.getNormalizedColors();
            float max = Math.max(Math.max(sampleColors.red, sampleColors.green), sampleColors.blue);
            sampleColors.red /= max;
            sampleColors.green /= max;
            sampleColors.blue /= max;
            double goldness = 75 * (sampleColors.red + sampleColors.green -2 * sampleColors.blue);
            if(goldness >= 55) {
                goldCount++;
            } else {
                silverCount++;
            }
            telemetry.addData("Sampling", "Gold : (%d)\tSilver : (%d)\t Goldness : (%.2f)", goldCount, silverCount, goldness);
            telemetry.update();
        }

        return goldCount > silverCount;
    }
    private void updateTelemetry(String currentTask) {
        // Show the elapsed game time and wheel power.
        telemetry.addData("Time", "%.1f", getRuntime());
        telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftBackPower, rightBackPower);
        telemetry.addData("Task", currentTask);
        telemetry.update();
    }

    private void encoderDrive(double speed, double leftInches, double rightInches, double timeoutSeconds) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftBackDrive.getCurrentPosition() + (int)(leftInches * countsPerInch);
            newRightTarget = rightBackDrive.getCurrentPosition() + (int)(rightInches * countsPerInch);
            leftBackDrive.setTargetPosition(newLeftTarget);
            rightBackDrive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            double startTime = getRuntime();

            leftBackDrive.setPower(Math.abs(speed));
            rightBackDrive.setPower(Math.abs(speed));

            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (getRuntime() < startTime + timeoutSeconds) &&
                    (leftBackDrive.isBusy() && rightBackDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        leftBackDrive.getCurrentPosition(),
                        rightBackDrive.getCurrentPosition());
                telemetry.update();
            }
            leftBackDrive.setPower(0);
            rightBackDrive.setPower(0);

            leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
    private void encoderSpin(double speed, double spinDegrees) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftBackDrive.getCurrentPosition() + (int)(spinDegrees * spinCountsPerDegree);
            newRightTarget = rightBackDrive.getCurrentPosition() + (int)(spinDegrees * spinCountsPerDegree);
            leftBackDrive.setTargetPosition(newLeftTarget);
            rightBackDrive.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            double startTime = getRuntime();

            leftBackDrive.setPower(Math.abs(speed));
            rightBackDrive.setPower(Math.abs(speed));

            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    /* (getRuntime() < startTime) && */
                    (leftBackDrive.isBusy() && rightBackDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Spinning", "Current: %7d\tTarget: %7d", leftBackDrive.getCurrentPosition(), newLeftTarget);
                telemetry.update();
            }

            leftBackDrive.setPower(0);
            rightBackDrive.setPower(0);

            leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}