package org.jetmar.qatools.embed.envoy;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.Slf4jLevel;
import de.flapdoodle.embed.process.io.Slf4jStreamProcessor;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.progress.Slf4jProgressListener;
import de.flapdoodle.embed.process.runtime.Executable;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import de.flapdoodle.embed.process.store.IArtifactStore;
import org.apache.commons.lang3.ArrayUtils;
import org.jetmar.qatools.embed.envoy.config.AbstractEnvoyConfig;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;
import org.jetmar.qatools.embed.envoy.config.RuntimeConfigBuilder;
import org.jetmar.qatools.embed.envoy.ext.LogWatchStreamProcessor;
import org.slf4j.Logger;
import org.jetmar.qatools.embed.envoy.ext.SubdirTempDir;
import org.jetmar.qatools.embed.envoy.ext.EnvoyArtifactStore;
import org.jetmar.qatools.embed.envoy.ext.ArtifactStoreBuilder;

import static de.flapdoodle.embed.process.io.file.Files.forceDelete;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.jetmar.qatools.embed.envoy.EnvoyStarter.getCommand;
import static org.slf4j.LoggerFactory.getLogger;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import static org.jetmar.qatools.embed.envoy.util.ReflectUtil.setFinalField;
import org.jetmar.qatools.embed.envoy.config.DownloadConfigBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by jj on 21.03.17.
 */ // EnvoyExecuteable
public class EnvoyProcess extends AbstractEnvoyProcess<EnvoyExecutable, EnvoyProcess> {
    public static final int MAX_CREATEDB_TRIALS = 3;
    private static Logger LOGGER = getLogger(EnvoyProcess.class);
   // private final IRuntimeConfig runtimeConfig;

    volatile boolean processReady = false;
    boolean stopped = false;

    public EnvoyProcess(Distribution distribution, EnvoyConfig config,
                           IRuntimeConfig runtimeConfig, EnvoyExecutable executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
        // this.runtimeConfig = runtimeConfig;
    }

    @Override
    protected void onBeforeProcess(IRuntimeConfig runtimeConfig)
            throws IOException {

    }


    @Override
    protected List<String> getCommandLine(Distribution distribution, EnvoyConfig config, IExtractedFileSet exe)
            throws IOException {
        List<String> ret = new ArrayList<>();
        switch (config.supportConfig().getName()) {
            case "postgres__": //NOSONAR
                ret.addAll(asList(exe.executable().getAbsolutePath(),
                        "-p", String.valueOf(config.net().port()),
                        "-h", config.net().host(),
                        "-D", config.storage().dbDir().getAbsolutePath()
                ));
                break;
            case "pg_ctl": //NOSONAR
                ret.addAll(asList(exe.executable().getAbsolutePath(),
                        String.format("-o \"-p %s\" \"-h %s\"", config.net().port(), config.net().host()),
                        "-D", config.storage().dbDir().getAbsolutePath(),
                        "-w",
                        "start"
                ));
                break;
            /**
            case "envoy": //NOSONAR
                System.out.println("Starting Envoy " + exe.executable().getAbsolutePath() );
                ret.addAll(asList(exe.executable().getAbsolutePath(),
                        String.format("-o \"-p %s\" \"-h %s\"", config.net().port(), config.net().host()),
                        "-D", config.storage().dbDir().getAbsolutePath(),
                        "-w",
                        "start"
                ));
             */
            case "envoy": //NOSONAR
                System.out.println("Starting Envoy " + exe.executable().getAbsolutePath() );

                ret.addAll(asList(exe.executable().getAbsolutePath()));

                // ret.add("--log-level trace");
                // ret.add("--concurrency 2");

                ret.addAll(config.getAdditionalParams());

                break;
            default:
                throw new RuntimeException("Failed to launch Postgres: Unknown command " +
                        config.supportConfig().getName() + "!");
        }
        return ret;
    }

    @Override
    protected final void onAfterProcessStart(ProcessControl process,
                                             IRuntimeConfig runtimeConfig) throws IOException {
        // JJ TODO - Strange logging behavior. Further hacking required !!
        // super.onAfterProcessStart(process, runtimeConfig);
        ProcessOutput outputConfig = runtimeConfig.getProcessOutput();
        Processors.connect(process.getReader(), outputConfig.getOutput());
        Processors.connect(process.getError(), StreamToLineProcessor.wrap(outputConfig.getError() ));
    }
}
