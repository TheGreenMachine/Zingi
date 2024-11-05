package com.team1816.lib.hardware.factory;

import com.team1816.lib.hardware.RobotConfiguration;
import edu.wpi.first.wpilibj.DriverStation;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStream;

/**
 * This class is the yaml integration bridge for main robot configurations and uses SnakeYaml's Yaml parser alongside
 * JSONSchema2Pojo to parse and organize yaml files into accessible objects
 */

// NOTE:
// Since the Collections of configurations are injected by SnakeYaml,
// IDEs will report that the collections are never updated.
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class YamlConfig {

    private static final Yaml FORMATTER = new Yaml();

    static {
        FORMATTER.setBeanAccess(BeanAccess.FIELD);
    }

    public static RobotConfiguration loadFrom(InputStream input) {
        return loadInternal(input);
    }

    static RobotConfiguration loadInternal(InputStream input) {
        return loadFromRaw(input);
    }

    static RobotConfiguration loadFromRaw(InputStream input) {
        try {
            Representer representer = new Representer(new DumperOptions());
            representer.getPropertyUtils().setSkipMissingProperties(true);
            Yaml yaml = new Yaml(new Constructor(RobotConfiguration.class, new LoaderOptions()), representer);
            yaml.setBeanAccess(BeanAccess.FIELD);
            return yaml.load(input);
        } catch (Exception e) {
            DriverStation.reportError("Error in Yaml is ROBOT_NAME set?", false);
        }
        return null;
    }

    @Override
    public String toString() {
        return FORMATTER.dump(this);
    }
}
