package org.jetmar.qatools.embed.envoy;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.Executable;
import de.flapdoodle.embed.process.runtime.IStopable;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;

import java.io.IOException;

import static java.io.File.pathSeparatorChar;
import static java.io.File.separator;
import static org.apache.commons.lang3.SystemUtils.JAVA_HOME;

/**
 * Created by jj on 21.03.17.
 */
public abstract class AbstractEnvoyProcess <E extends Executable<EnvoyConfig, P>, P extends IStopable>
        extends AbstractProcess<EnvoyConfig, E, P> {

    public AbstractEnvoyProcess(Distribution distribution, EnvoyConfig config, IRuntimeConfig runtimeConfig, E executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
    }

    @Override
    protected void onBeforeProcessStart(ProcessBuilder processBuilder, EnvoyConfig config, IRuntimeConfig runtimeConfig) {
        if (config.credentials() != null) {
            processBuilder.environment().put("PGUSER", config.credentials().username());
            processBuilder.environment().put("PGPASSWORD", config.credentials().password());
        }
        processBuilder.environment().put("PATH",
                processBuilder.environment().get("PATH") + pathSeparatorChar
                        + JAVA_HOME + separator + "bin"
                        + pathSeparatorChar
                        + JAVA_HOME + separator + "jre" + separator + "bin"
        );
    }

    @Override
    protected void stopInternal() {
        System.out.println("stopInternal");
    }

    @Override
    protected void cleanupInternal() {
        System.out.println("cleanupInternal");

    }

}
