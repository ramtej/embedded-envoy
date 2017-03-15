package org.jetmar.qatools.embed.envoy.ext;

import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.store.Downloader;
import de.flapdoodle.embed.process.store.IArtifactStore;

import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.store.Downloader;
import de.flapdoodle.embed.process.store.IArtifactStore;
import org.jetmar.qatools.embed.envoy.Command;
import org.jetmar.qatools.embed.envoy.config.DownloadConfigBuilder;

public class ArtifactStoreBuilder extends
        de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

    public ArtifactStoreBuilder defaults(Command command) {
        tempDir().setDefault(new SubdirTempDir());
        executableNaming().setDefault(new UUIDTempNaming());
        download().setDefault(new DownloadConfigBuilder().defaultsForCommand(command).build());
        downloader().setDefault(new Downloader());
        return this;
    }

    @Override
    public IArtifactStore build() {
        return new EnvoyArtifactStore(get(DOWNLOAD_CONFIG), get(TEMP_DIR_FACTORY), get(EXECUTABLE_NAMING), get(DOWNLOADER));
    }

    public ArtifactStoreBuilder defaultsWithoutCache(Command command) {
        tempDir().setDefault(new SubdirTempDir());
        executableNaming().setDefault(new UUIDTempNaming());
        download().setDefault(
                new DownloadConfigBuilder().defaultsForCommand(command)
                        .build());
        downloader().setDefault(new Downloader());
        // disable caching
        useCache().setDefault(false);
        return this;
    }
}