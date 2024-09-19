package com.team1816.season.auto;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team1816.lib.auto.Color;
import com.team1816.lib.auto.DynamicAutoUtil;
import com.team1816.lib.auto.modes.AutoMode;
import com.team1816.lib.auto.modes.DoNothingMode;
import com.team1816.lib.auto.modes.DriveStraightMode;
import com.team1816.lib.auto.modes.TuneDrivetrainMode;
import com.team1816.lib.auto.paths.DynamicAutoPath;
import com.team1816.lib.hardware.factory.YamlConfig;
import com.team1816.lib.util.logUtil.GreenLogger;
import com.team1816.lib.variableInputs.Numeric;
import com.team1816.lib.variableInputs.VariableInput;
import com.team1816.season.auto.modes.*;
import com.team1816.season.auto.modes.distance.FourScoreFromDistanceMode;
import com.team1816.season.auto.modes.eject.Bottom345Ejects;
import com.team1816.season.auto.modes.eject.BottomMiddleEjects;
import com.team1816.season.auto.modes.eject.TopMiddleEjects;
import com.team1816.season.auto.paths.StartToAmpPath;
import com.team1816.season.auto.paths.arbitrary.ArbitraryStartToNoteOnePath;
import com.team1816.season.auto.paths.arbitrary.ArbitraryStartToNoteThreePath;
import com.team1816.season.auto.paths.noteToNote.NoteOneToNoteTwoPath;
import com.team1816.season.auto.paths.noteToNote.NoteThreeToNoteTwoPath;
import com.team1816.season.auto.paths.toNoteOne.AmpToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.BottomSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.MiddleSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteOne.TopSpeakerToNoteOnePath;
import com.team1816.season.auto.paths.toNoteThree.AmpToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.BottomSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.MiddleSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteThree.TopSpeakerToNoteThreePath;
import com.team1816.season.auto.paths.toNoteTwo.*;
import com.team1816.season.configuration.Constants;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * An integrated and optimized manager for autonomous mode selection and configuration
 */
@Singleton
public class AutoModeManager {

    /**
     * Properties: Selection
     */
    public static com.team1816.season.states.RobotState robotState;
    private final SendableChooser<DesiredAuto> autoModeChooser;
    private final SendableChooser<Color> sideChooser;
    private DesiredAuto desiredAuto;
    private Color teamColor;

    /**
     * Properties: Dynamic Auto
     */
    private final SendableChooser<ShootPos> startPosChooser;
    private final SendableChooser<DesiredCollect> firstCollectChooser;
    private final SendableChooser<DesiredCollect> secondCollectChooser;
    private final SendableChooser<DesiredCollect> thirdCollectChooser;
    private final SendableChooser<ShootPos> firstShootChooser;
    private final SendableChooser<ShootPos> secondShootChooser;

    private final SendableChooser<ScramChoice> scramChooser;
    private ScramChoice desiredScram;

    /**
     * Properties: Execution
     */
    private AutoMode autoMode;
    private static Thread autoModeThread;

    /**
     * Instantiates and AutoModeManager with a default option and selective computation
     *
     * @param rs RobotState
     */
    @Inject
    public AutoModeManager(com.team1816.season.states.RobotState rs) {
        robotState = rs;
        autoModeChooser = new SendableChooser<>(); // Shuffleboard dropdown menu to choose desired auto mode
        sideChooser = new SendableChooser<>(); // Shuffleboard dropdown menu to choose desired side / bumper color
        startPosChooser = new SendableChooser<>(); // Shuffleboard dropdown menu to choose desired starting position for dynamic auto

        SmartDashboard.putData("Auto mode", autoModeChooser); // appends chooser to shuffleboard

//        autoModeChooser.addOption("GETTYSBURG", DesiredAuto.RANGE_FOUR_SCORE);
        for (DesiredAuto desiredAuto : DesiredAuto.values()) {
            autoModeChooser.addOption(desiredAuto.name(), desiredAuto);
        }
        autoModeChooser.setDefaultOption(
            DesiredAuto.DRIVE_STRAIGHT.name(),
            DesiredAuto.DRIVE_STRAIGHT
        );

        SmartDashboard.putData("Robot color", sideChooser); // appends chooser to shuffleboard

        sideChooser.setDefaultOption(Color.BLUE.name(), Color.BLUE); // initialize options
        sideChooser.addOption(Color.RED.name(), Color.RED); // initialize options

    /**
     * Resets properties to default and resets the thread
     */
    public void reset() {
        autoMode = new DriveStraightMode();
        autoModeThread = new Thread(autoMode::run);
        desiredAuto = DesiredAuto.DRIVE_STRAIGHT;
        teamColor = Color.RED;
        robotState.allianceColor = teamColor;
    }

    /**
     * Updates the choosers in realtime
     *
     * @return true if updated
     */
    public boolean update() {
        DesiredAuto selectedAuto = autoModeChooser.getSelected();

        Color selectedColor = Color.BLUE;

        if (RobotBase.isSimulation()) {
            selectedColor = sideChooser.getSelected();
        } else if (RobotBase.isReal()) {
            var dsAlliance = DriverStation.getAlliance().isPresent() ? DriverStation.getAlliance().get() : sideChooser.getSelected(); //ternary hell
            selectedColor = (dsAlliance == DriverStation.Alliance.Red) ? Color.RED : Color.BLUE;
        }

        boolean autoChanged = desiredAuto != selectedAuto;
        boolean startPosChanged = desiredStart != selectedStartPos;
        boolean colorChanged = teamColor != selectedColor;
                selectedStartPos != desiredStart

        // if auto has been changed, update selected auto mode + thread
        if (autoChanged || colorChanged || startPosChanged ) {
            if (autoChanged) {
                GreenLogger.log(
                    "Auto changed from: " + desiredAuto + ", to: " + selectedAuto.name()
                );
            }
            if (colorChanged) {
                GreenLogger.log("Robot color changed from: " + teamColor + ", to: " + selectedColor);
            }

            autoMode = generateAutoMode(selectedAuto, selectedColor);

            autoModeThread = new Thread(autoMode::run);
        }
        desiredAuto = selectedAuto;
        teamColor = selectedColor;
        robotState.allianceColor = teamColor;

        desiredStart = selectedStartPos;

        return autoChanged || colorChanged;
    }

    /**
     * Outputs values to SmartDashboard
     */
    public void outputToSmartDashboard() {
        if (desiredAuto != null) {
            SmartDashboard.putString("AutoModeSelected", desiredAuto.name());
        }
        if (teamColor != null) {
            SmartDashboard.putString("RobotColorSelected", teamColor.name());
        }
    }

    /**
     * Returns the selected autonomous mode
     *
     * @return AutoMode
     * @see AutoMode
     */
    public AutoMode getSelectedAuto() {
        return autoMode;
    }

    /**
     * Returns the selected color
     *
     * @return Color
     * @see Color
     */
    public Color getSelectedColor() {
        return sideChooser.getSelected();
    }

    /**
     * Executes the auto mode and respective thread
     */
    public void startAuto() {
        autoModeThread.start();
    }

    /**
     * Stops the auto mode
     */
    public void stopAuto() {
        if (autoMode != null) {
            autoMode.stop();
            autoModeThread = new Thread(autoMode::run);
        }
    }

    /**
     * Enum for AutoModes
     */
    enum DesiredAuto {
        // Test : 2020 Legacy
//        DO_NOTHING,
//        TUNE_DRIVETRAIN,
        LIVING_ROOM,
        DRIVE_STRAIGHT,

        // System check
        SYSTEM_CHECK,

        TEST
        }

    /**
     * Generates each AutoMode by demand
     *
     * @param mode desiredMode
     * @return AutoMode
     * @see AutoMode
     */
    private AutoMode generateAutoMode(DesiredAuto mode, Color color) {
        switch (mode) {
//            case DO_NOTHING:
//                return new DoNothingMode();
//            case TUNE_DRIVETRAIN: // commented for competition purposes
//                return new TuneDrivetrainMode();
//            case LIVING_ROOM:
//                return (new LivingRoomMode(color));
            case DRIVE_STRAIGHT:
                return new DriveStraightMode();
            case TEST:
                if (!DriverStation.isFMSAttached()) return new TestMode();
            case SYSTEM_CHECK:
                if (!DriverStation.isFMSAttached()) return new SystemCheckMode();
            default:
                GreenLogger.log("Defaulting to drive straight mode");
                return new DriveStraightMode();
        }
    }
}
