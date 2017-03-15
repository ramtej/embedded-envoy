package org.jetmar.qatools.embed.envoy.xxxx;

import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.store.IArtifactStore;
import junit.framework.TestCase;
import org.jetmar.qatools.embed.envoy.Command;
import org.jetmar.qatools.embed.envoy.distribution.Version;
import org.jetmar.qatools.embed.envoy.ext.ArtifactStoreBuilder;

import java.io.IOException;

import static java.util.Arrays.asList;

/**
 * Created by jj on 14.03.17.
 */
public class TestDownloads extends TestCase {

    public void testDownloads() throws IOException {
        IArtifactStore artifactStore = new ArtifactStoreBuilder().defaults(Command.Envoy).build();

        for (Platform p : asList(Platform.OS_X, Platform.Linux, Platform.Windows)) {
            for (BitSize b : BitSize.values()) {
                for (IVersion version : Version.Main.values()) {
                    Distribution distribution = new Distribution(version, p, b);
                    assertTrue("Distribution: " + distribution, artifactStore.checkDistribution(distribution));
                }
            }
        }
    }
}
