package org.jetmar.qatools.embed.envoy;

import de.flapdoodle.embed.process.runtime.Executable;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;
import org.jetmar.qatools.embed.envoy.xxxx.GenericProcess;
import org.jetmar.qatools.embed.envoy.xxxx.GenericProcessConfig;

/**
 * Created by jj on 14.03.17.
 */
public enum Command {

    Envoy("envoy", EnvoyExecutable.class)
    ;

    private final String commandName;
    private final Class<? extends AbstractEnvoyExecutable<EnvoyConfig, ? extends AbstractEnvoyProcess>> executableClass;

    Command(String commandName,
            Class<? extends AbstractEnvoyExecutable<EnvoyConfig, ? extends AbstractEnvoyProcess>>
                    executableClass) {
        this.commandName = commandName;
        this.executableClass = executableClass;
    }

    public <E extends AbstractEnvoyExecutable<EnvoyConfig, P>, P extends AbstractEnvoyProcess> Class<E> executableClass() {
        return (Class<E>) this.executableClass;
    }

    public String commandName() {
        return this.commandName;
    }



}
