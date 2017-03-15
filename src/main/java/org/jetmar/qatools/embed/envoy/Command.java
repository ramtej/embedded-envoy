package org.jetmar.qatools.embed.envoy;

import de.flapdoodle.embed.process.runtime.Executable;
import org.jetmar.qatools.embed.envoy.xxxx.GenericProcess;
import org.jetmar.qatools.embed.envoy.xxxx.GenericProcessConfig;

/**
 * Created by jj on 14.03.17.
 */
public enum Command {

    Envoy("envoy", EnvoyExecuteable.class)
    ;

    private final String commandName;
    private final Class<? extends Executable<GenericProcessConfig, GenericProcess>> executableClass;

    Command(String commandName,
            Class<? extends Executable<GenericProcessConfig, GenericProcess>>
                    executableClass) {
        this.commandName = commandName;
        this.executableClass = executableClass;
    }
    public String commandName() {
        return this.commandName;
    }
}
