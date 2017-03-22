package org.jetmar.qatools.embed.envoy;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.Slf4jLevel;
import de.flapdoodle.embed.process.io.Slf4jStreamProcessor;
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
    private final IRuntimeConfig runtimeConfig;

    volatile boolean processReady = false;
    boolean stopped = false;

    public EnvoyProcess(Distribution distribution, EnvoyConfig config,
                           IRuntimeConfig runtimeConfig, EnvoyExecutable executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
        this.runtimeConfig = runtimeConfig;
    }

    /**
     * @deprecated consider using {@link #stop()} method instead
     */
    @Deprecated
    public static boolean shutdownPostgres(EnvoyConfig config) {
        // return shutdownPostgres(config, new RuntimeConfigBuilder().defaults(Command.PgCtl).build());
        return true;
    }

    private static String runCmd(

            EnvoyConfig config, IRuntimeConfig runtimeConfig, Command cmd, String successOutput, int timeout, String... args) {

        System.out.println("runCmd(1)");

        return runCmd(config, runtimeConfig, cmd, successOutput, Collections.<String>emptySet(), timeout, args);
    }

    private static String runCmd(
            EnvoyConfig config, IRuntimeConfig runtimeConfig, Command cmd, String successOutput, Set<String> failOutput, long timeout, String... args) {
        System.out.println("Run..");
        try {
            LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(successOutput,
                    failOutput, new Slf4jStreamProcessor(LOGGER, Slf4jLevel.TRACE));

            IArtifactStore artifactStore = runtimeConfig.getArtifactStore();
            IDownloadConfig downloadCfg = ((EnvoyArtifactStore) artifactStore).getDownloadConfig();

            // TODO: very hacky and unreliable way to respect the parent command's configuration
            try { //NOSONAR
                IDirectory tempDir = SubdirTempDir.defaultInstance();
                if (downloadCfg.getPackageResolver() instanceof PackagePaths) {
                    tempDir = ((PackagePaths) downloadCfg.getPackageResolver()).getTempDir();
                }
                setFinalField(downloadCfg, "_packageResolver", new PackagePaths(cmd, tempDir));
                setFinalField(artifactStore, "_downloadConfig", downloadCfg);
            } catch (Exception e) {
                // fallback to the default config
                LOGGER.error("Could not use the configured artifact store for cmd, " +
                        "falling back to default " + cmd, e);
                downloadCfg = new DownloadConfigBuilder().defaultsForCommand(cmd)
                        .progressListener(new Slf4jProgressListener(LOGGER)).build();
                artifactStore = new ArtifactStoreBuilder().defaults(cmd).download(downloadCfg).build();
            }

            final IRuntimeConfig runtimeCfg = new RuntimeConfigBuilder().defaults(cmd)
                    .processOutput(new ProcessOutput(logWatch, logWatch, logWatch))
                    .artifactStore(artifactStore)
                    .commandLinePostProcessor(runtimeConfig.getCommandLinePostProcessor()).build();


            final EnvoyConfig postgresConfig = new EnvoyConfig(config).withArgs(args);
            // JJ TODO
            /**
            if (Command.InitDb == cmd) {
                postgresConfig.withAdditionalInitDbParams(config.getAdditionalInitDbParams());
            }
             */

            System.out.println("cmd : " + cmd);
            Executable<?, ? extends AbstractEnvoyProcess> exec = getCommand(cmd, runtimeCfg)
                    .prepare(postgresConfig);
            AbstractEnvoyProcess proc = exec.start();
            System.out.println("isProcessRunning " + proc.isProcessRunning() );
            logWatch.waitForResult(timeout);
            proc.waitFor();
            System.out.println("done");
            return logWatch.getOutput();
        } catch (IOException | InterruptedException e) {
            LOGGER.warn("Failed to run command {}", cmd.commandName(), e);
        }
        return null;
    }

    private static boolean shutdownPostgres(EnvoyConfig config, IRuntimeConfig runtimeConfig) {
        try {
            // JJ TODO return isEmpty(runCmd(config, runtimeConfig, Command.PgCtl, "server stopped", 2000, "stop"));
            return true;
        } catch (Exception e) {
            LOGGER.warn("Failed to stop postgres by pg_ctl!", e);
        }
        return false;
    }

    @Override
    protected void stopInternal() {
        synchronized (this) {
            if (!stopped) {
                stopped = true;
                LOGGER.info("trying to stop postgresql");
                if (!sendStopToPostgresqlInstance() && !sendTermToProcess() && waitUntilProcessHasStopped(2000)) {
                    LOGGER.warn("could not stop postgresql with pg_ctl/SIGTERM, trying to kill it...");
                    if (!sendKillToProcess() && !tryKillToProcess() && waitUntilProcessHasStopped(3000)) {
                        LOGGER.warn("could not kill postgresql within 4s!");
                    }
                }
            }
            if (waitUntilProcessHasStopped(5000)) {
                LOGGER.error("Postgres has not been stopped within 10s! Something's wrong!");
            }
            deleteTempFiles();
        }
    }

    private boolean waitUntilProcessHasStopped(int timeoutMillis) {
        long started = currentTimeMillis();
        while (currentTimeMillis() - started < timeoutMillis && isProcessRunning()) {
            try {
                sleep(50);
            } catch (InterruptedException e) {
                LOGGER.warn("Failed to wait with timeout until the process has been killed", e);
            }
        }
        return isProcessRunning();
    }

    protected final boolean sendStopToPostgresqlInstance() {
        /**
        final boolean result = shutdownPostgres(getConfig(), runtimeConfig);
        if (runtimeConfig.getArtifactStore() instanceof EnvoyArtifactStore) {
            final IDirectory tempDir = ((EnvoyArtifactStore) runtimeConfig.getArtifactStore()).getTempDir();
            if (tempDir != null && tempDir.asFile() != null && tempDir.isGenerated()) {
                LOGGER.info("Cleaning up after the embedded process (removing {})...", tempDir.asFile().getAbsolutePath());
                forceDelete(tempDir.asFile());
            }
        }
        return result;
         */
        return true;
    }

    @Override
    protected void onBeforeProcess(IRuntimeConfig runtimeConfig)
            throws IOException {
        super.onBeforeProcess(runtimeConfig);
        EnvoyConfig config = getConfig();
        // JJ TODO
        // runCmd(config, runtimeConfig, InitDb, "Success. You can now start the database server using", 1000);

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
                ret.addAll(asList(exe.executable().getAbsolutePath(),
                        String.format(
                        "-c /home/jj/projects/dekrefa/embedded-envoy/src/test/resources/config/service-envoy-v1.json"
                )));
                break;
            default:
                throw new RuntimeException("Failed to launch Postgres: Unknown command " +
                        config.supportConfig().getName() + "!");
        }
        return ret;
    }

    protected void deleteTempFiles() {
/**
        final AbstractEnvoyConfig.Storage storage = getConfig().storage();
        if ((storage.dbDir() != null) && (storage.isTmpDir()) && (!forceDelete(storage.dbDir()))) {
            LOGGER.warn("Could not delete temp db dir: {}", storage.dbDir());
        }
 */
    }

    @Override
    protected final void onAfterProcessStart(ProcessControl process,
                                              IRuntimeConfig runtimeConfig) throws IOException {
    System.out.println("onAfterProcessStart");
/**
        String output = runCmd(getConfig(), runtimeConfig, Command.Envoy, "", // JJ TODO CreateDb
                new HashSet<>(singleton("database creation failed")), 3000, getConfig().storage().dbName());
        System.out.println("output " + output);
 */
    }

    // @Override
    protected final void onAfterProcessStart_(ProcessControl process,
                                             IRuntimeConfig runtimeConfig) throws IOException {
        System.out.println("onAfterProcessStart");
        final Path pidFilePath = Paths.get(getConfig().storage().dbDir().getAbsolutePath(), "postmaster.pid");
        System.out.println(pidFilePath.getFileName());
        final File pidFile = new File(pidFilePath.toAbsolutePath().toString());
        int timeout = TIMEOUT;

        while (!pidFile.exists() && ((timeout = timeout - 100) > 0)) {
            System.out.println("--");
            try {
                sleep(100);
            } catch (InterruptedException ie) { /* safe to ignore */ }
        }


        int pid = -1;
        try {
            pid = Integer.valueOf(readLines(pidFilePath.toFile()).get(0));
        } catch (Exception e) {
            LOGGER.error("Failed to read PID file ({})", e.getMessage(), e);
        }
        if (pid != -1) {
            setProcessId(pid);
        } else {
            // fallback, try to read pid file. will throw IOException if that fails
            setProcessId(getPidFromFile(pidFile()));
        }
        int trial = 0;
        do {
            System.out.println("xx");

            String output = runCmd(getConfig(), runtimeConfig, Command.Envoy, "", // JJ TODO CreateDb
                    new HashSet<>(singleton("database creation failed")), 3000, getConfig().storage().dbName());
            try {
                if (isEmpty(output) || !output.contains("could not connect to database")) {
                    break;
                }
                LOGGER.warn("Could not create database first time ({} of {} trials)", trial, MAX_CREATEDB_TRIALS);
                sleep(100);
            } catch (InterruptedException ie) { /* safe to ignore */ }
        } while (trial++ < MAX_CREATEDB_TRIALS);
    }

    /**
     * Import into database from file
     *
     * @param file The file to import into database
     */
    public void importFromFile(File file) {
        importFromFileWithArgs(file);
    }

    /**
     * Import into database from file with additional args
     *
     * @param file
     * @param cliArgs additional arguments for psql (be sure to separate args from their values)
     */
    public void importFromFileWithArgs(File file, String... cliArgs) {
        if (file.exists()) {
            String[] args = {
                    "-U", getConfig().credentials().username(),
                    "-d", getConfig().storage().dbName(),
                    "-h", getConfig().net().host(),
                    "-p", String.valueOf(getConfig().net().port()),
                    "-f", file.getAbsolutePath()};
            if (cliArgs != null && cliArgs.length != 0) {
                args = ArrayUtils.addAll(args, cliArgs);
            }
            // JJ TODO
            // runCmd(getConfig(), runtimeConfig, Psql, "", new HashSet<>(singletonList("import into " + getConfig().storage().dbName() + " failed")), 1000, args);
        }
    }

    /**
     * Import into database from file with additional args
     *
     * @param file
     * @param cliArgs additional arguments for psql (be sure to separate args from their values)
     */
    public void restoreFromFile(File file, String... cliArgs) {
        if (file.exists()) {
            String[] args = {
                    "-U", getConfig().credentials().username(),
                    "-d", getConfig().storage().dbName(),
                    "-h", getConfig().net().host(),
                    "-p", String.valueOf(getConfig().net().port()),
                    file.getAbsolutePath()};
            if (cliArgs != null && cliArgs.length != 0) {
                args = ArrayUtils.addAll(args, cliArgs);
            }
            // JJ TODO runCmd(getConfig(), runtimeConfig, PgRestore, "", new HashSet<>(singletonList("restore into " + getConfig().storage().dbName() + " failed")), 1000, args);
        }
    }

    public void exportToFile(File file) {
        /**
         * JJ TODO
        runCmd(getConfig(), runtimeConfig, PgDump, "", new HashSet<>(singletonList("export from " + getConfig().storage().dbName() + " failed")),
                1000,
                "-U", getConfig().credentials().username(),
                "-d", getConfig().storage().dbName(),
                "-h", getConfig().net().host(),
                "-p", String.valueOf(getConfig().net().port()),
                "-f", file.getAbsolutePath()
        );
         */
    }

    public void exportSchemeToFile(File file) {
        /**
         * JJ TODO
        runCmd(getConfig(), runtimeConfig, PgDump, "", new HashSet<>(singletonList("export from " + getConfig().storage().dbName() + " failed")),
                1000,
                "-U", getConfig().credentials().username(),
                "-d", getConfig().storage().dbName(),
                "-h", getConfig().net().host(),
                "-p", String.valueOf(getConfig().net().port()),
                "-f", file.getAbsolutePath(),
                "-s"
        );
         */
    }

    public void exportDataToFile(File file) {
        /**
         * // JJ TODO
        runCmd(getConfig(), runtimeConfig, PgDump, "", new HashSet<>(singletonList("export from " + getConfig().storage().dbName() + " failed")),
                1000,
                "-U", getConfig().credentials().username(),
                "-d", getConfig().storage().dbName(),
                "-h", getConfig().net().host(),
                "-p", String.valueOf(getConfig().net().port()),
                "-f", file.getAbsolutePath(),
                "-a"
        );
         */

    }

    public boolean isProcessReady() {
        System.out.println("isProcessReady() " + processReady);
        return processReady;
    }

    @Override
    protected void cleanupInternal() {
    }
}
