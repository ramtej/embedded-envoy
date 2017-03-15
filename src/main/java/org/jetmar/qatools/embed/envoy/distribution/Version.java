package org.jetmar.qatools.embed.envoy.distribution;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * Created by jj on 14.03.17.
 */
public enum Version implements IVersion {

    V1_2_0("1.2.0"),
    V1_1_0("1.2.0"),
    ;


    private final String specificVersion;

    Version(String vName) {
        this.specificVersion = vName;
    }

    @Override
    public String asInDownloadPath() {
        return specificVersion;
    }

    @Override
    public String toString() {
        return "Version{" + specificVersion + '}';
    }

    public enum Main implements IVersion {
        // @Deprecated V9_1(V9_1_24),
        // @Deprecated V9_2(V9_2_19),

        V1_2(V1_2_0),
        V1_1(V1_1_0),
        PRODUCTION(V1_2);

        private final IVersion _latest;

        Main(IVersion latest) {
            _latest = latest;
        }

        @Override
        public String asInDownloadPath() {
            return _latest.asInDownloadPath();
        }
    }
}
