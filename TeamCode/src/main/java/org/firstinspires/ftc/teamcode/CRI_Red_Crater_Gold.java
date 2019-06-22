package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="CRI_Red_Crater_Gold", group="Linear Opmode")
//@Disabled
public class CRI_Red_Crater_Gold extends BlackoutAutonomousOpMode{

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftLift;
    private DcMotor rightLift;
    private DcMotor MtDew;
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

        moveToSample();

        positionToSample();

        if (isSampleGold()) {
            sample("Middle");
        }
        else {
            stopMotors();
        }

        goToNextSample();

        if (isSampleGold()) {
            sample("Right");
        }
        else {
            stopMotors();
        }

        goToLastSample();

        if (isSampleGold()) {
            sample("Left");
        }
        else {
            stopMotors();
        }

        driveToWall();

        driveToDepot();

        scoreMarker();

        driveToCrater();
    }

    private void positionToSample() {
        double totalDistance = 0;
        while (((isSampleGold() == false) && (isSampleSilver() == false)) && totalDistance < 8) {
            encoderDrive(.5, 1.5, -1.5, 1);
            totalDistance += 1.5;
        }
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
        encoderSpin(.5, 136.5);
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

    private void driveToCrater() {
        encoderSpin(.5, 180);
        encoderDrive(1, -75, 75, 25);
        stopMotors();
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
}