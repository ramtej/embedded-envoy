package org.jetmar.qatools.embed.envoy.config;

import de.flapdoodle.embed.process.distribution.IVersion;
import org.jetmar.qatools.embed.envoy.Command;

import java.io.IOException;

import static org.jetmar.qatools.embed.envoy.distribution.Version.Main.PRODUCTION;
/**
 * Created by jj on 21.03.17.
 */

public class EnvoyConfig extends AbstractEnvoyConfig<EnvoyConfig> {
    public EnvoyConfig(AbstractEnvoyConfig config, Command command) {
        super(config, command);
    }

    public EnvoyConfig(AbstractEnvoyConfig config) {
        super(config);
    }

    public EnvoyConfig(IVersion version, String dbName) throws IOException {
        this(version, new Net(), new Storage(dbName), new Timeout());
    }

    public EnvoyConfig(IVersion version, String host, int port, String dbName) throws IOException {
        this(version, new Net(host, port), new Storage(dbName), new Timeout());
    }

    public EnvoyConfig(IVersion version, Net network, Storage storage, Timeout timeout, Credentials cred, Command command) {
        super(version, network, storage, timeout, cred, new SupportConfig(command));
    }

    public EnvoyConfig(IVersion version, Net network, Storage storage, Timeout timeout, Credentials cred) {
        this(version, network, storage, timeout, cred, Command.Envoy);
    }

    public EnvoyConfig(IVersion version, Net network, Storage storage, Timeout timeout) {
        super(version, network, storage, timeout);
    }

    public static EnvoyConfig defaultWithDbName(String dbName, String user, String password) throws IOException {
        return new EnvoyConfig(PRODUCTION, new Net(), new Storage(dbName), new Timeout(),
                new Credentials(user, password));
    }

    public static EnvoyConfig defaultWithDbName(String dbName) throws IOException {
        return new EnvoyConfig(PRODUCTION, dbName);
    }

}
