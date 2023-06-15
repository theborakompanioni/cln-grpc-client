package org.tbk.lightning.cln.grpc.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tbk.lightning.cln.grpc.ClnRpcConfig;
import org.tbk.lightning.cln.grpc.ClnRpcConfigImpl;
import org.tbk.lightning.cln.grpc.client.NodeGrpc;
import org.tbk.spring.testcontainer.cln.ClnContainer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ClnClientAutoConfigProperties.class)
@RequiredArgsConstructor
public class ClnContainerRpcClientAutoConfiguration {

    @NonNull
    private final ClnClientAutoConfigProperties properties;

    @Bean("clnRpcSslContext")
    public SslContext clnRpcSslContext(ClnContainer<?> clnContainer) {
        return clnContainer.copyFileFromContainer(properties.getClientCertFilePath(), certStream -> {
            return clnContainer.copyFileFromContainer(properties.getClientKeyFilePath(), keyStream -> {
                return clnContainer.copyFileFromContainer(properties.getCaCertFilePath(), caStream -> {
                    return GrpcSslContexts.configure(SslContextBuilder.forClient(), SslProvider.OPENSSL)
                            .keyManager(certStream, keyStream)
                            .trustManager(caStream)
                            .build();
                });
            });
        });
    }

    @Bean("clnRpcConfig")
    public ClnRpcConfig clnRpcConfig(ClnContainer<?> clnContainer,
                                     @Qualifier("clnRpcSslContext") SslContext clnRpcSslContext) {
        String host = clnContainer.getHost();
        Integer mappedPort = clnContainer.getMappedPort(properties.getPort());

        return ClnRpcConfigImpl.builder()
                .host(host)
                .port(mappedPort)
                .sslContext(clnRpcSslContext)
                .build();
    }

    @Bean(name = "clnChannelBuilder")
    public ManagedChannelBuilder<?> clnChannelBuilder(ClnRpcConfig rpcConfig) {
        return NettyChannelBuilder.forAddress(rpcConfig.getHost(), rpcConfig.getPort())
                .sslContext(rpcConfig.getSslContext());
    }

    @Bean(name = "clnChannel")
    public ManagedChannel clnChannel(@Qualifier("clnChannelBuilder") ManagedChannelBuilder<?> clnChannelBuilder) {
        // From https://github.com/grpc/grpc-java/issues/3268#issuecomment-317484178:
        // > Channels are expensive to create, and the general recommendation is to use one per application,
        // > shared among the service stubs.
        return clnChannelBuilder.build();
    }

    @Bean(name = "clnChannelShutdownHook")
    public DisposableBean clnChannelShutdownHook(@Qualifier("clnChannel") ManagedChannel clnChannel) {
        return () -> {
            Duration timeout = properties.getShutdownTimeout();
            try {
                log.debug("Closing grpc managed channel {} ...", clnChannel);
                try {
                    clnChannel.shutdown().awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
                    log.debug("Closed grpc managed channel {}", clnChannel);
                } catch (io.grpc.StatusRuntimeException e) {
                    log.error("Error occurred closing managed grpc channel: " + e.getStatus(), e);
                    clnChannel.shutdownNow().awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    log.error("Thread interrupted: " + e.getMessage(), e);
                    clnChannel.shutdownNow().awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
                }
            } catch (Exception e) {
                log.error("Grpc managed channel did not shutdown cleanly", e);
            }
        };
    }

    @Bean(name = "clnNodeBlockingStub")
    public NodeGrpc.NodeBlockingStub clnNodeBlockingStub(@Qualifier("clnChannel") ManagedChannel clnChannel) {
        return NodeGrpc.newBlockingStub(clnChannel);
    }

    @Bean(name = "clnNodeStub")
    public NodeGrpc.NodeStub clnNodeStub(@Qualifier("clnChannel") ManagedChannel clnChannel) {
        return NodeGrpc.newStub(clnChannel);
    }


    @Bean(name = "clnNodeFutureStub")
    public NodeGrpc.NodeFutureStub clnNodeFutureStub(@Qualifier("clnChannel") ManagedChannel clnChannel) {
        return NodeGrpc.newFutureStub(clnChannel);
    }
}