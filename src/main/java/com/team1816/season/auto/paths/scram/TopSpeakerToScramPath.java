package com.team1816.season.auto.paths.scram;

import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.paths.AutoPath;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.season.auto.AutoModeManager;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import java.util.List;

public class TopSpeakerToScramPath extends DynamicAutoPath {
    private static AutoModeManager.Position startingPos = AutoModeManager.Position.TOP_SPEAKER;
    private static AutoModeManager.Position endingPos = AutoModeManager.Position.MIDDLE_ONE;

    public TopSpeakerToScramPath(Color color) {
        super(color, startingPos, endingPos);
    }

    @Override
    protected List<Pose2d> getWaypoints() {
        return updateWaypoints(List.of(
                new Pose2d(0.742, 6.709, Rotation2d.fromDegrees(0)),
                new Pose2d(8.08, 7.45, Rotation2d.fromDegrees(0))
        ));
    }

    @Override
    protected List<Rotation2d> getWaypointHeadings() {
        return updateHeadings(List.of(
                Rotation2d.fromDegrees(60),
                Rotation2d.fromDegrees(-5)
        ));
    }

    @Override
    protected boolean usingApp() {
        return true;
    }

    @Override
    public DynamicAutoPath getInstance() {
        return null;
    }
}