package org.jetmar.qatools.embed.envoy;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.Slf4jLevel;
import de.flapdoodle.embed.process.io.Slf4jStreamProcessor;
import de.flapdoodle.embed.process.runtime.Starter;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;
import org.jetmar.qatools.embed.envoy.config.RuntimeConfigBuilder;
import org.jetmar.qatools.embed.envoy.ext.LogWatchStreamProcessor;

import java.lang.reflect.Constructor;
import java.util.HashSet;

import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by jj on 21.03.17.
 */
public class EnvoyStarter <E extends AbstractEnvoyExecutable<EnvoyConfig, P>, P extends AbstractEnvoyProcess<E, P>>
        extends Starter<EnvoyConfig, E, P> {
    final Class<E> execClass;

    public EnvoyStarter(final Class<E> execClass, final IRuntimeConfig runtimeConfig) {
        super(runtimeConfig);
        this.execClass = execClass;
    }

    public static EnvoyStarter<EnvoyExecutable, EnvoyProcess> getInstance(IRuntimeConfig config) {
        return new EnvoyStarter(EnvoyExecutable.class, config);
    }

    public static EnvoyStarter<EnvoyExecutable, EnvoyProcess> getDefaultInstance() {
        return getInstance(runtimeConfig(Command.Envoy));
    }

    public static IRuntimeConfig runtimeConfig(Command cmd) {
        LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(
                "started", new HashSet<>(singletonList("failed")),
                new Slf4jStreamProcessor(getLogger("envoy"), Slf4jLevel.TRACE));
        return new RuntimeConfigBuilder()
                // .defaultsWithLogger(cmd, getLogger(EnvoyProcess.class) )
                // .defaults(cmd)
                // .processOutput(new ProcessOutput(logWatch, logWatch, logWatch))
                // .processOutput(ProcessOutput.getDefaultInstance("test"))

                .build();
    }

    public static <E extends AbstractEnvoyExecutable<EnvoyConfig, P>, P extends AbstractEnvoyProcess<E, P>>
    EnvoyStarter<E, P> getCommand(Command command, IRuntimeConfig config) {
        return new EnvoyStarter(command.executableClass(), config);
    }

    public static <E extends AbstractEnvoyExecutable<EnvoyConfig, P>, P extends AbstractEnvoyProcess<E, P>>
    EnvoyStarter<E, P> getCommand(Command command) {
        return getCommand(command, runtimeConfig(command));
    }

    @Override
    protected E newExecutable(EnvoyConfig config, Distribution distribution,
                              IRuntimeConfig runtime, IExtractedFileSet exe) {
        try {
            Constructor<E> c = execClass.getConstructor(
                    Distribution.class, EnvoyConfig.class,
                    IRuntimeConfig.class, IExtractedFileSet.class
            );
            return c.newInstance(distribution, config, runtime, exe);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the executable", e);
        }
    }
}
