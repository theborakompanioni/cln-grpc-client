package org.tbk.lightning.cln.grpc.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.Duration;
import java.util.Objects;

@ConfigurationProperties(
        prefix = "org.tbk.lightning.cln.grpc",
        ignoreUnknownFields = false
)
@Getter
@AllArgsConstructor(onConstructor = @__(@ConstructorBinding))
public class ClnClientAutoConfigProperties {
    private static final Duration DEFAULT_SHUTDOWN_TIMEOUT = Duration.ofSeconds(10);

    private String host;
    private int port;
    private String caCertFilePath;
    private String clientCertFilePath;
    private String clientKeyFilePath;
    private Duration shutdownTimeout;

    public Duration getShutdownTimeout() {
        return Objects.requireNonNullElse(shutdownTimeout, DEFAULT_SHUTDOWN_TIMEOUT);
    }
}