package org.jetmar.qatools.embed.envoy.xyz;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import org.jetmar.qatools.embed.envoy.AbstractEnvoyProcess;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by jj on 21.03.17.
 */
public class EnvoyXProcess <E extends EnvoyXExecutable> extends AbstractEnvoyProcess<E, EnvoyXProcess> {

    public EnvoyXProcess(Distribution distribution, EnvoyConfig config, IRuntimeConfig runtimeConfig, E executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
    }

    @Override
    protected List<String> getCommandLine(Distribution distribution, EnvoyConfig config, IExtractedFileSet exe)
            throws IOException {
        List<String> ret = new ArrayList<>();
        ret.addAll(asList(exe.executable().getAbsolutePath()));
        ret.addAll(asList(
                "-o",
                String.format("\"-p %s -h %s\"", config.net().port(), config.net().host()),
                "-D", config.storage().dbDir().getAbsolutePath(),
                "-w"
        ));
        if (config.args().isEmpty()) {
            ret.add("start");
        } else {
            ret.addAll(
                    config.args()
            );
        }
        return ret;
    }
}
