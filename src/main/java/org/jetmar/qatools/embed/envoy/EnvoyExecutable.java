package org.jetmar.qatools.embed.envoy;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;

import java.io.IOException;

/**
 * Created by jj on 21.03.17.
 */
public class EnvoyExecutable extends AbstractEnvoyExecutable<EnvoyConfig, EnvoyProcess> {
    final IRuntimeConfig runtimeConfig;

    public EnvoyExecutable(Distribution distribution,
                           EnvoyConfig config, IRuntimeConfig runtimeConfig, IExtractedFileSet exe) {
        super(distribution, config, runtimeConfig, exe);
        this.runtimeConfig = runtimeConfig;
    }

    @Override
    protected EnvoyProcess start(Distribution distribution, EnvoyConfig config, IRuntimeConfig runtime)
            throws IOException {
        return new EnvoyProcess(distribution, config, runtime, this);
    }
}
