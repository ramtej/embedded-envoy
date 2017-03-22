package org.jetmar.qatools.embed.envoy.xyz;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import org.jetmar.qatools.embed.envoy.AbstractEnvoyExecutable;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by jj on 21.03.17.
 */
public class EnvoyXExecutable extends AbstractEnvoyExecutable<EnvoyConfig, EnvoyXProcess> {

    public EnvoyXExecutable(Distribution distribution,
                           EnvoyConfig config, IRuntimeConfig runtimeConfig, IExtractedFileSet exe) {
        super(distribution, config, runtimeConfig, exe);
    }

    @Override
    protected EnvoyXProcess start(Distribution distribution, EnvoyConfig config, IRuntimeConfig runtime)
            throws IOException {
        return new EnvoyXProcess<>(distribution, config, runtime, this);
    }

    @Override
    public synchronized void stop() {
        // We don't want to cleanup after this particular single invocation
    }
}