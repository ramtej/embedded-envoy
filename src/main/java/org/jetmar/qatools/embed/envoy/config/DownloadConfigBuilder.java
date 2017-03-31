package org.jetmar.qatools.embed.envoy.config;

import de.flapdoodle.embed.process.config.store.DownloadPath;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import org.jetmar.qatools.embed.envoy.Command;
import org.jetmar.qatools.embed.envoy.PackagePaths;
import org.jetmar.qatools.embed.envoy.ext.SubdirTempDir;


/**
 * Created by jj on 14.03.17.
 */
public class DownloadConfigBuilder extends de.flapdoodle.embed.process.config.store.DownloadConfigBuilder {

    public DownloadConfigBuilder defaultsForCommand(Command command) {
        fileNaming().setDefault(new UUIDTempNaming());
        // I've found the only open and easy to use cross platform binaries
        downloadPath().setDefault(new DownloadPath("https://github.com/ramtej/distributions-envoy/raw/master/downloads/"));
        packageResolver().setDefault(new PackagePaths(command, SubdirTempDir.defaultInstance()));
        artifactStorePath().setDefault(new UserHome(".embedenvoy"));
        downloadPrefix().setDefault(new DownloadPrefix("envoy-download"));
        userAgent().setDefault(new UserAgent("Mozilla/5.0 (compatible; Embedded postgres; +https://github.com/ramtej/embedded-envoy)"));
        progressListener().setDefault(new StandardConsoleProgressListener() {
            @Override
            public void info(String label, String message) {
                if(label.startsWith("Extract")){
                    System.out.print(".");//NOSONAR
                } else {
                    super.info(label, message);//NOSONAR
                }
            }
        });
        return this;
    }
}
