package org.jetmar.qatools.embed.envoy;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;
import org.jetmar.qatools.embed.envoy.config.AbstractEnvoyConfig;

/**
 * Created by jj on 21.03.17.
 */
public abstract class AbstractEnvoyExecutable<C extends AbstractEnvoyConfig, P extends AbstractEnvoyProcess>
        extends Executable<C, P> {
    public AbstractEnvoyExecutable(Distribution distribution, C config, IRuntimeConfig runtimeConfig, IExtractedFileSet executable) {
        super(distribution, config, runtimeConfig, executable);
    }
}
