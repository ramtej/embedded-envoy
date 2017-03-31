package org.jetmar.qatools.embed.envoy.main;

import org.jetmar.qatools.embed.envoy.EnvoyProcess;
import org.jetmar.qatools.embed.envoy.EnvoyStarter;
import org.jetmar.qatools.embed.envoy.config.EnvoyConfig;
import org.jetmar.qatools.embed.envoy.distribution.Version;

/**
 * Created by jj on 23.03.17.
 */
public class TestMain {

    public static void main(String[] args) {
        System.out.println("Hello World!"); // Display the string.
        try {
                test1();

            // process.stop();
        } catch(Exception _ex)
        {
            _ex.printStackTrace();
        }
        }

        private static void test1() throws Exception
        {
            EnvoyProcess process = EnvoyStarter.getDefaultInstance()
                    .prepare(new EnvoyConfig(Version.Main.PRODUCTION,
                    "test-db"))
                    .start();

        }
}
