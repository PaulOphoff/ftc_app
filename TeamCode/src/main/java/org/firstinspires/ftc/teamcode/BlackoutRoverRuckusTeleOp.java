package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "BlackoutRoverRuckusTeleOp")
//@Disabled
public class BlackoutRoverRuckusTeleOp extends OpMode{

    private DcMotor rightBackDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor DrPepper = null;
    private DcMotor MtDew = null;
    private DcMotor RightLift = null;
    private DcMotor LeftLift = null;
    private DcMotor MineralLifter = null;
    private DcMotor Flipper = null;
    private Servo LatchingArm = null;


    double DriveSpeedModifier = 1;
    double MineralGrabbingSpeedModifier = 1;
    double MineralPushingSpeedModifier = .6;
    double LatchingLiftSpeedModifier = .6;
    double dead = .15;
    double flippingButton = 0;


    private final static int EncoderArmUp = -1310;

    @Override
    public void init() {
        initializeDriveTrain();
        initializeMineralGrabber();
        initializeLatchingLift();
        initializeLatchingServo();
        initializeMineralLifter();
        initializeFlipper();
    }

    @Override
    public void loop() {
        checkDriveTrain();
        checkMineralGrabber();
        checkLatchingLift();
        checkLatchingServo();
        checkMineralLifter();
        checkFlipper();
    }



    private void initializeDriveTrain(){
        leftBackDrive = hardwareMap.dcMotor.get("leftBackDrive");
        rightBackDrive = hardwareMap.dcMotor.get("rightBackDrive");
        MineralLifter = hardwareMap.dcMotor.get("MineralLifter");
        MineralLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        MineralLifter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }



    private void checkDriveTrain() {
        double leftDriveInput = gamepad1.left_stick_y;
        double rightDriveInput = gamepad1.right_stick_y;

        if (leftisDrivingForward(leftDriveInput) && MineralLifter.getCurrentPosition() > -600)  {
            leftDriveForward(DriveSpeedModifier * leftDriveInput);
        }

        else if (leftisDrivingBackward(leftDriveInput) && MineralLifter.getCurrentPosition() > -600) {
            leftDriveBackward(DriveSpeedModifier * -leftDriveInput);
        }

        else if (leftisDrivingForward(leftDriveInput) && MineralLifter.getCurrentPosition() <= -600) {
            leftBackDrive.setPower(.5 * leftDriveInput);
        }

        else if (leftisDrivingBackward(leftDriveInput) && MineralLifter.getCurrentPosition() <= -600) {
            leftBackDrive.setPower(.5 * leftDriveInput);
        }

        else {
            stopDrivingLeft();
        }



        if (rightisDrivingForward(rightDriveInput) && MineralLifter.getCurrentPosition() > -600) {
            rightDriveForward(DriveSpeedModifier * rightDriveInput);
        }

        else if (rightisDrivingBackward(rightDriveInput) && MineralLifter.getCurrentPosition() > -600) {
            rightDriveBackward(DriveSpeedModifier * -rightDriveInput);
        }

        else if (rightisDrivingForward(rightDriveInput) && MineralLifter.getCurrentPosition() <= -600) {
            rightBackDrive.setPower(.5 * -rightDriveInput);
        }

        else if (rightisDrivingBackward(rightDriveInput) && MineralLifter.getCurrentPosition() <= -600) {
            rightBackDrive.setPower(.5 * -rightDriveInput);
        }

        else {
            stopDrivingRight();
        }
    }

    public boolean leftisDrivingForward(double leftDriveInput) {
        return (leftDriveInput > dead);
    }

    public boolean rightisDrivingForward(double rightDriveInput) {
        return (rightDriveInput > dead);
    }

    public boolean leftisDrivingBackward(double leftDriveInput) {
        return (leftDriveInput < -dead);
    }

    public boolean rightisDrivingBackward(double rightDriveInput) {
        return (rightDriveInput < -dead);
    }



    public void leftDriveForward(double leftDriveInput) {
        leftBackDrive.setPower(DriveSpeedModifier * leftDriveInput);
    }

    public void rightDriveForward(double rightDriveInput) {
        rightBackDrive.setPower(DriveSpeedModifier * -rightDriveInput);
    }

    public void leftDriveBackward(double leftDriveInput) {
        leftBackDrive.setPower(DriveSpeedModifier * -leftDriveInput);
    }

    public void rightDriveBackward(double rightDriveInput) {
        rightBackDrive.setPower(DriveSpeedModifier * rightDriveInput);
    }

    public void stopDrivingLeft() {
        leftBackDrive.setPower(0);
    }

    public void stopDrivingRight() {
        rightBackDrive.setPower(0);
    }



    private void initializeMineralGrabber() {
        MtDew = hardwareMap.dcMotor.get("MtDew");
        DrPepper = hardwareMap.dcMotor.get("DrPepper");
    }



    private void checkMineralGrabber() {
        double grabbingRightSide = gamepad1.right_trigger;
        double grabbingLeftSide = gamepad1.left_trigger;
        boolean pushingRightSide = gamepad1.right_bumper;
        boolean pushingLeftSide = gamepad1.left_bumper;

        if (isGrabbingRightSide(grabbingRightSide)) {
            grabRightSide(MineralGrabbingSpeedModifier);
        }

        else if (isPushingRightSide(pushingRightSide)) {
            pushRightSide(MineralPushingSpeedModifier);
        }

        else {
            stopRightScorer();
        }



        if (isGrabbingLeftSide(grabbingLeftSide)) {
            grabLeftSide(MineralGrabbingSpeedModifier);
        }

        else if (isPushingLeftSide(pushingLeftSide)) {
            pushLeftSide(MineralPushingSpeedModifier);
        }

        else {
            stopLeftScorer();
        }
    }



    public boolean isGrabbingRightSide(double grabbingRightSide) {
        return (grabbingRightSide > dead);
    }

    public boolean isGrabbingLeftSide(double grabbingLeftSide) {
        return (grabbingLeftSide > dead);
    }

    public boolean isPushingRightSide(boolean pushingRightSide) {
        return (pushingRightSide);
    }

    public boolean isPushingLeftSide(boolean pushingLeftSide) {
        return (pushingLeftSide);
    }



    public void grabRightSide(double power) {
        MtDew.setPower(power);
    }

    public void grabLeftSide(double power) {
        DrPepper.setPower(-power);
    }

    public void pushRightSide(double pushingRightSide) {
        MtDew.setPower(-MineralPushingSpeedModifier);
    }

    public void pushLeftSide(double pushingLeftSide) {
        DrPepper.setPower(MineralPushingSpeedModifier);
    }

    public void stopRightScorer() {
        MtDew.setPower(0);
    }

    public void stopLeftScorer() {
        DrPepper.setPower(0);
    }



    private void initializeLatchingLift() {
        LeftLift = hardwareMap.dcMotor.get("leftLift");
        RightLift = hardwareMap.dcMotor.get("rightLift");

        LeftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private void checkLatchingLift() {
        boolean LatchingLiftDown = gamepad1.y;
        boolean LatchingLiftUp = gamepad1.a;

        if (LatchingLiftUp) {
            LiftUp(LatchingLiftSpeedModifier);
        }

        else if (LatchingLiftDown) {
            LiftDown(LatchingLiftSpeedModifier);
        }

        else {
            StopLift();
        }
    }

    public void LiftUp(double LatchingLiftSpeedModifier) {
        LeftLift.setPower(-LatchingLiftSpeedModifier);
        RightLift.setPower(LatchingLiftSpeedModifier);
    }

    public void LiftDown(double LatchingLiftSpeedModifier) {
        LeftLift.setPower(LatchingLiftSpeedModifier);
        RightLift.setPower(-LatchingLiftSpeedModifier);
    }

    public void StopLift() {
        LeftLift.setPower(0);
        RightLift.setPower(0);
    }



    private void initializeLatchingServo() {
        LatchingArm = hardwareMap.servo.get("latch");
    }



    private void checkLatchingServo() {
        boolean LatchHookButton = gamepad1.x;
        boolean LatchUnhookButton = gamepad1.b;


        if (LatchHookButton) {
            LatchingArm.setPosition(.5);
            displayLatchingServo();
        }

        else if (LatchUnhookButton) {
            LatchingArm.setPosition(0);
            displayLatchingServo();
        }
    }



    private void displayLatchingServo() {
        telemetry.addData("LatchingServo", LatchingArm.getPosition());
        telemetry.update();
    }



    private void initializeMineralLifter() {
        MineralLifter = hardwareMap.dcMotor.get("MineralLifter");
        MineralLifter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        MineralLifter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        MineralLifter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MineralLifter.setPower(1);
        MineralLifter.setTargetPosition(0);

    }



    private void checkMineralLifter() {
        boolean MineralLiftUp = gamepad1.dpad_up;
        boolean MineralLiftDown = gamepad1.dpad_down || gamepad2.dpad_left;
        boolean MineralLiftGetIntoCrater = gamepad2.dpad_down;
        boolean MineralLiftGetOutCrater = gamepad2.dpad_up;
        boolean MineralLiftGiveMorePower = gamepad2.x;

        if (MineralLifter.getTargetPosition() == 0 && MineralLifter.getCurrentPosition() != 0) {
            MineralLifter.setTargetPosition(0);
        }

        else if (MineralLiftDown) {
            MineralLifter.setTargetPosition(0);
            //    MineralLifter.setPower(0.1);
        }

        else if (MineralLiftUp) {
            MineralLifter.setTargetPosition(EncoderArmUp);
            //    MineralLifter.setPower(0.1);
        }

        else if (MineralLiftGetIntoCrater) {
            MineralLifter.setTargetPosition(-150);
            MineralLifter.setPower(1);
        }

        else if (MineralLiftGetOutCrater) {
            MineralLifter.setTargetPosition(-550);
            MineralLifter.setPower(1);
        }


        if (MineralLifter.getTargetPosition() == EncoderArmUp && MineralLifter.getCurrentPosition() != EncoderArmUp) {
            MineralLifter.setTargetPosition(EncoderArmUp);
        }

        else if (MineralLiftDown) {
            MineralLifter.setTargetPosition(0);
            //    MineralLifter.setPower(0.1);
        }

        else if (MineralLiftUp) {
            MineralLifter.setTargetPosition(EncoderArmUp);
            //    MineralLifter.setPower(0.1);
        }

        else if (MineralLiftGetIntoCrater) {
            MineralLifter.setTargetPosition(-150);
            MineralLifter.setPower(1);
        }

        else if (MineralLiftGetOutCrater) {
            MineralLifter.setTargetPosition(-550);
            MineralLifter.setPower(1);
        }

        if (MineralLifter.getTargetPosition() == -150 && MineralLifter.getCurrentPosition() != -150) {
            MineralLifter.setTargetPosition(-150);
        }

        else if (MineralLiftDown) {
            MineralLifter.setTargetPosition(0);
            //    MineralLifter.setPower(0.1);
        }

        else if (MineralLiftUp) {
            MineralLifter.setTargetPosition(EncoderArmUp);
            //    MineralLifter.setPower(0.1);
        }

        else if (MineralLiftGetIntoCrater) {
            MineralLifter.setTargetPosition(-150);
            MineralLifter.setPower(1);
        }

        else if (MineralLiftGetOutCrater) {
            MineralLifter.setTargetPosition(-550);
            MineralLifter.setPower(1);
        }

        if (MineralLifter.getTargetPosition() == -550 && MineralLifter.getCurrentPosition() != -550) {
            MineralLifter.setTargetPosition(-550);
        }

        else if (MineralLiftDown) {
            MineralLifter.setTargetPosition(0);
            //    MineralLifter.setPower(0.1);
        }

        else if (MineralLiftUp) {
            MineralLifter.setTargetPosition(EncoderArmUp);
            //    MineralLifter.setPower(0.1);
        }

        else if (MineralLiftGetIntoCrater) {
            MineralLifter.setTargetPosition(-150);
            MineralLifter.setPower(1);
        }

        else if (MineralLiftGetOutCrater) {
            MineralLifter.setTargetPosition(-550);
            MineralLifter.setPower(1);
        }

        // Check which direction the arm is moving
        // If arm is moving down
        if (MineralLifter.getTargetPosition() > -200){
            //when the arm is in its First half of movement
            if (MineralLifter.getCurrentPosition() < (0.5 * EncoderArmUp)) {
                MineralLifter.setPower(.2);
            }
            //from near the end to the very end of movement
            else if (MineralLifter.getCurrentPosition() > (0.1 * EncoderArmUp)) {
                MineralLifter.setPower(.1);
            }
            //between beginning and ending of movement
            else {
                MineralLifter.setPower(.2);
            }
        }
        // If arm is moving up
        else if (MineralLifter.getTargetPosition() == EncoderArmUp){
            //  when arm is at First half of movement
            if (MineralLifter.getCurrentPosition() > (0.8 * EncoderArmUp)) {
                MineralLifter.setPower(1);
            }
            //from near the end to the very end of movement
            // 0.1 * -1310 = -131
            // -1310 -- -1179
            else if (MineralLifter.getCurrentPosition() < (0.9 * EncoderArmUp)
                    && MineralLifter.getCurrentPosition() >= (EncoderArmUp)) {
                MineralLifter.setPower(.6);
            }
            // if overshoots
            else if (MineralLifter.getCurrentPosition() < EncoderArmUp){
                MineralLifter.setPower(0.3);
            }
            //between beginning and ending of movement
            else {
                MineralLifter.setPower(.375);
            }
        }
        if (MineralLifter.getTargetPosition() == EncoderArmUp && MineralLiftGiveMorePower) {
            MineralLifter.setPower(.8);
        }
    }


    private void initializeFlipper() {
        Flipper = hardwareMap.dcMotor.get("Flipper");
    }


    private void checkFlipper() {
        flippingButton = gamepad2.left_stick_y;

        if (isFlippingUp()) {
            Flipper.setPower(flippingButton);
        }

        else if (isFlippingDown()) {
            Flipper.setPower(flippingButton);
        }

        else if (isNotFlipping()) {
            Flipper.setPower(0);
        }
    }

    private boolean isFlippingUp() {
        return (flippingButton > dead);
    }

    private boolean isFlippingDown() {
        return (flippingButton < -dead);
    }

    private boolean isNotFlipping() {
        return ((flippingButton > -dead) && (flippingButton < dead));
    }
}