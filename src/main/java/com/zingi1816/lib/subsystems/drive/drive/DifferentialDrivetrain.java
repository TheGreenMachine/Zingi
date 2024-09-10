package com.zingi1816.lib.subsystems.drive.drive;

/**
 * This is the base interface for differential style drivetrains
 */
public interface DifferentialDrivetrain extends TrackableDrivetrain {
    double getLeftVelocityTicksActual();

    double getRightVelocityTicksActual();

    double getLeftVelocityTicksDemand();

    double getRightVelocityTicksDemand();

    double getLeftError();

    double getRightError();

    double getLeftDistance();

    double getRightDistance();
}
