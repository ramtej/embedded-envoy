package org.jetmar.qatools.embed.envoy.xxxx;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import org.jetmar.qatools.embed.envoy.Command;
import org.jetmar.qatools.embed.envoy.EnvoyExecutable;
import org.jetmar.qatools.embed.envoy.EnvoyProcess;
import org.jetmar.qatools.embed.envoy.EnvoyStarter;
import org.jetmar.qatools.embed.envoy.config.AbstractEnvoyConfig;
import org.jetmar.qatools.embed.envoy.config.DownloadConfigBuilder;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;
import org.jetmar.qatools.embed.envoy.config.RuntimeConfigBuilder;
import org.jetmar.qatools.embed.envoy.distribution.Version;
import org.jetmar.qatools.embed.envoy.ext.ArtifactStoreBuilder;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static org.jetmar.qatools.embed.envoy.distribution.Version.Main.PRODUCTION;
import static org.jetmar.qatools.embed.envoy.util.SocketUtil.findFreePort;


/**
 * Created by jj on 21.03.17.
 */
public class TestEnvoyProcess {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TestEnvoyProcess.class);


    private static final Logger logger = Logger.getLogger(TestEnvoyProcess.class.getName());
    protected EnvoyProcess process;



    @Test
    public void testProcess1() throws Exception {
        System.out.println("-- this is a test --");
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.Envoy)
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(Command.Envoy)
                        .download(new DownloadConfigBuilder()
                                .defaultsForCommand(Command.Envoy).build()
                        )
                ).build();

        EnvoyStarter<EnvoyExecutable, EnvoyProcess> runtime = EnvoyStarter.getInstance(runtimeConfig);
        final EnvoyConfig config = new EnvoyConfig(PRODUCTION, new AbstractEnvoyConfig.Net("localhost", findFreePort()),
                new AbstractEnvoyConfig.Storage("test"), new AbstractEnvoyConfig.Timeout(),
                new AbstractEnvoyConfig.Credentials("user", "password"));
        config.getAdditionalParams().addAll(asList(
                "-E", "SQL_ASCII",
                "--locale=C",
                "--lc-collate=C",
                "--lc-ctype=C"
        ));
        EnvoyExecutable exec = runtime.prepare(config);
        process = exec.start();

    }

    @Test
    public void testProcess2() throws Exception {

        System.out.println("-- this is a test --");
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.Envoy)
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(Command.Envoy)
                        .download(new DownloadConfigBuilder()
                                .defaultsForCommand(Command.Envoy).build()
                        )
                )
                .processOutput(ProcessOutput.getDefaultInstance("name"))
                .build();

        EnvoyStarter<EnvoyExecutable, EnvoyProcess> runtime = EnvoyStarter.getInstance(runtimeConfig);
        System.out.println(runtime);

        final EnvoyConfig config = new EnvoyConfig(PRODUCTION, new AbstractEnvoyConfig.Net("localhost", findFreePort()),
                new AbstractEnvoyConfig.Storage("test"), new AbstractEnvoyConfig.Timeout(),
                new AbstractEnvoyConfig.Credentials("user", "password"));
        config.getAdditionalParams().addAll(asList(
                "-E", "SQL_ASCII",
                "--locale=C",
                "--lc-collate=C",
                "--lc-ctype=C"
        ));
        EnvoyExecutable exec = runtime.prepare(config);
        process = exec.start();

    }

    @Test
    public void testProcess3() throws Exception {
        LOG.info("Starting postgres");
        EnvoyProcess process = EnvoyStarter.getDefaultInstance().prepare(new EnvoyConfig(Version.Main.PRODUCTION,
                "test-db")).start();

        LOG.info("Stopping postgres");
        // process.stop();
    }

    @Test
    public void testProcess5() throws Exception {
        LOG.info("Starting postgres");
        EnvoyProcess process = EnvoyStarter.getDefaultInstance().prepare(new EnvoyConfig(Version.Main.PRODUCTION,
                "test-db")).start();

        LOG.info("Stopping postgres");
        process.stop();
    }



    }
