package org.jetmar.qatools.embed.envoy.config;

import de.flapdoodle.embed.process.config.ISupportConfig;
import org.jetmar.qatools.embed.envoy.Command;

/**
 * Created by jj on 21.03.17.
 */
public class SupportConfig implements ISupportConfig {
    private final Command command;

    public SupportConfig(Command command) {
        this.command = command;
    }

    @Override
    public String getName() {
        return command.commandName();
    }

    @Override
    public String getSupportUrl() {
        return "https://github.com/yandex-qatools/postgresql-embedded/issues\n";
    }

    @Override
    public String messageOnException(Class<?> context, Exception exception) {
        return null;
    }


}
