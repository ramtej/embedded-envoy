package org.jetmar.qatools.embed.envoy.config;

import de.flapdoodle.embed.process.config.io.ProcessOutput;
import org.jetmar.qatools.embed.envoy.Command;

/**
 * Created by jj on 21.03.17.
 */
public class EnvoyProcessOutputConfig {

    private EnvoyProcessOutputConfig() {}

    public static ProcessOutput getDefaultInstance(Command command) {
        return ProcessOutput.getDefaultInstance(command.commandName());
    }

    public static ProcessOutput getInstance(Command command, org.slf4j.Logger logger) {
        return ProcessOutput.getInstance(command.commandName(), logger);
    }



}
