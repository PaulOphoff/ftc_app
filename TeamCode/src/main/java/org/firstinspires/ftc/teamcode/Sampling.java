package org.firstinspires.ftc.teamcode;

class Sampling {

    private BlackoutAutonomousOpMode _robot;

    public Sampling(BlackoutAutonomousOpMode robot) {
        _robot = robot;
    }

    public void sample(String samplePosition) {
        _robot.updateTelemetry("Sampling " + samplePosition);

        _robot.encoderDrive(.5, -5, 5, 1);
        _robot.encoderSpin(.5, 81);
        _robot.encoderSpin(.5, -81);
        _robot.encoderDrive(.5, 5, -5, 1);

        _robot.updateTelemetry("Sampling " + samplePosition + " - Done!");
    }
}
