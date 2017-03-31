package org.jetmar.qatools.embed.envoy.xxxx;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.util.Arrays.asList;
import static org.jetmar.qatools.embed.envoy.distribution.Version.Main.PRODUCTION;
import static org.jetmar.qatools.embed.envoy.distribution.Version.Main.V1_1;
import static org.jetmar.qatools.embed.envoy.util.SocketUtil.findFreePort;


/**
 * Created by jj on 21.03.17.
 */
public class TestEnvoyStarter {

    private static class TestHandler extends Handler {

        public final List<LogRecord> RECORDS = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            System.out.println("log " + record.getMessage());
            RECORDS.add(record);
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    }

    // private static final Logger logger = Logger.getLogger(TestEnvoyStarter.class.getName());
    private final TestHandler testHandler = new TestHandler();

    protected EnvoyProcess process;




    @Before
    public void setUp() throws Exception {
        // logger.setLevel(Level.FINEST);
        // logger.addHandler(testHandler);

        // turns off the default functionality of unzipping on every run.
        IRuntimeConfig runtimeConfig = buildRuntimeConfig();

        EnvoyStarter<EnvoyExecutable, EnvoyProcess> runtime = EnvoyStarter.getInstance(runtimeConfig);
        final EnvoyConfig config = new EnvoyConfig(V1_1, new AbstractEnvoyConfig.Net(
                "localhost", findFreePort()
        ), new AbstractEnvoyConfig.Storage("test"), new AbstractEnvoyConfig.Timeout(),
                new AbstractEnvoyConfig.Credentials("user", "password"));

        /**
        config.getAdditionalParams().addAll(asList(
                "-E", "SQL_ASCII",
                "--locale=C",
                "--lc-collate=C",
                "--lc-ctype=C",
                "--log-level trace"
        ));
         */

        config.getAdditionalParams().addAll(asList(
                "--log-level trace",
                "--concurrency 2",
                "--config-path /home/jj/projects/dekrefa/embedded-envoy/src/test/resources/config/service-greeting-v1.2.0.json"
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
                                // .progressListener(new LoggingProgressListener(logger, Level.ALL))
                                .progressListener((new StandardConsoleProgressListener() {
                                    @Override
                                    public void info(String label, String message) {
                                        System.out.println(label + " " + message);
                                        /**
                                        if(label.startsWith("envoy")){
                                            System.out.print(".");//NOSONAR
                                        } else {
                                            super.info(label, message);//NOSONAR
                                        }
                                         */
                                    }
                                }
                                ))
                                .build()))
                .build();
    }

    @Test
    public void testEnvoy() throws Exception {
        System.out.println("-- this is a test --");
        while(true)
        {
            Thread.sleep(1000);
        }
    }

}
