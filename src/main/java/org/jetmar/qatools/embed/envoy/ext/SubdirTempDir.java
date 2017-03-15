package org.jetmar.qatools.embed.envoy.ext;

import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;

import java.io.File;
import java.io.IOException;

import static de.flapdoodle.embed.process.io.file.Files.createTempDir;

/**
 * Created by jj on 14.03.17.
 */
public class SubdirTempDir extends PropertyOrPlatformTempDir {
    private static final File tempDir;
    private static SubdirTempDir _instance = new SubdirTempDir();

    static {
        try {
            String customTempDir = System.getProperty("de.flapdoodle.embed.io.tmpdir");
            if (customTempDir != null) {
                tempDir = new File(customTempDir);
            } else {
                tempDir = createTempDir(new File(System.getProperty("java.io.tmpdir")), "envoy-embed");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp dir", e);
        }
    }

    public static IDirectory defaultInstance() {
        return _instance;
    }

    @Override
    public File asFile() {
        return tempDir;
    }
}
