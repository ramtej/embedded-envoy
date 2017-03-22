package org.jetmar.qatools.embed.envoy.xxxx;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import org.jetmar.qatools.embed.envoy.Command;
import org.jetmar.qatools.embed.envoy.EnvoyExecutable;
import org.jetmar.qatools.embed.envoy.EnvoyProcess;
import org.jetmar.qatools.embed.envoy.EnvoyStarter;
import org.jetmar.qatools.embed.envoy.config.AbstractEnvoyConfig;
import org.jetmar.qatools.embed.envoy.config.DownloadConfigBuilder;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;
import org.jetmar.qatools.embed.envoy.config.RuntimeConfigBuilder;
import org.jetmar.qatools.embed.envoy.ext.ArtifactStoreBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static org.jetmar.qatools.embed.envoy.distribution.Version.Main.PRODUCTION;
import static org.jetmar.qatools.embed.envoy.util.SocketUtil.findFreePort;


/**
 * Created by jj on 21.03.17.
 */
public class TestEnvoyStarter {

    private static final Logger logger = Logger.getLogger(TestEnvoyStarter.class.getName());
    protected EnvoyProcess process;


    @Before
    public void setUp() throws Exception {
        logger.setLevel(Level.INFO);
        // logger.addHandler(testHandler);

        // turns off the default functionality of unzipping on every run.
        IRuntimeConfig runtimeConfig = buildRuntimeConfig();

        EnvoyStarter<EnvoyExecutable, EnvoyProcess> runtime = EnvoyStarter.getInstance(runtimeConfig);
        final EnvoyConfig config = new EnvoyConfig(PRODUCTION, new AbstractEnvoyConfig.Net(
                "localhost", findFreePort()
        ), new AbstractEnvoyConfig.Storage("test"), new AbstractEnvoyConfig.Timeout(),
                new AbstractEnvoyConfig.Credentials("user", "password"));
        config.getAdditionalInitDbParams().addAll(asList(
                "-E", "SQL_ASCII",
                "--locale=C",
                "--lc-collate=C",
                "--lc-ctype=C"
        ));

        EnvoyExecutable exec = runtime.prepare(config);
        process = exec.start();
    }

    protected IRuntimeConfig buildRuntimeConfig() {

        return new RuntimeConfigBuilder()
                .defaults(Command.Envoy)
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(Command.Envoy)
                        .download(new DownloadConfigBuilder()
                                .defaultsForCommand(Command.Envoy)
                                .progressListener(new LoggingProgressListener(logger, Level.ALL))
                                .build()))

                .build();
    }

    @Test
    public void testEnvoy() throws Exception {
        System.out.println("-- this is a test --");
    }

}
