package com.team1816.lib.auto.modes;

import com.team1816.lib.auto.AutoModeEndedException;
import com.team1816.lib.auto.Color;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.season.auto.AutoModeManager;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Autonomous mode to do absolutely nothing
 */
public class DoNothingMode extends AutoMode {

    public DoNothingMode () {}

    public DoNothingMode(AutoModeManager.StartPos start, Color color) {
        double poseX;
        switch (start) {
            case ZERO -> {
                initialPose = new Pose2d(color == Color.BLUE ? 0 : (2 * Constants.fieldCenterX),
                        0,
                        Rotation2d.fromDegrees(color == Color.BLUE ? 0 : 180));
            }
            default -> {
                initialPose = new Pose2d(color == Color.BLUE ? 1.47 : (2 * Constants.fieldCenterX) - 1.47,
                        7.3,
                        Rotation2d.fromDegrees(color == Color.BLUE ? 0 : 180));
            }
        };
    }

    /**
     * Routine. Does nothing.
     *
     * @throws AutoModeEndedException
     * @see AutoMode#routine()
     */
    @Override
    protected void routine() throws AutoModeEndedException {
        GreenLogger.log("doing nothing");
    }
}
