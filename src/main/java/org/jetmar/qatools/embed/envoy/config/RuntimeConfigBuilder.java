package org.jetmar.qatools.embed.envoy.config;

import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.io.progress.Slf4jProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import org.jetmar.qatools.embed.envoy.Command;
import org.jetmar.qatools.embed.envoy.ext.ArtifactStoreBuilder;



/**
 * Created by jj on 21.03.17.
 */
public class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

    public RuntimeConfigBuilder defaults(Command command) {
        processOutput().setDefault(ProcessOutput.getDefaultInstance(command.commandName()));
        commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
        artifactStore().setDefault(storeBuilder().defaults(command)
                .build());
        // JJ TODO
        processOutput().overwriteDefault(EnvoyProcessOutputConfig.getDefaultInstance(command)); // .getInstance(command, logger));

        return this;
    }

    public RuntimeConfigBuilder defaultsWithLogger(Command command, org.slf4j.Logger logger) {
        defaults(command);
        processOutput().overwriteDefault(EnvoyProcessOutputConfig.getInstance(command, logger));

        IDownloadConfig downloadConfig = new DownloadConfigBuilder()
                .defaultsForCommand(command)
                .progressListener(new Slf4jProgressListener(logger))
                .build();

        artifactStore().overwriteDefault(storeBuilder().defaults(command).download(downloadConfig).build());
        return this;
    }

    private ArtifactStoreBuilder storeBuilder() {
        return new ArtifactStoreBuilder();
    }



}
