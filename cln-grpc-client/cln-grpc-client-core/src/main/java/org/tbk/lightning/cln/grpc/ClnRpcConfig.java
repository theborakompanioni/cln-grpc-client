package org.tbk.lightning.cln.grpc;

import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

public interface ClnRpcConfig {

    /**
     * IP address or hostname where cln grpc api is reachable.
     * e.g. localhost, 192.168.0.1, etc.
     *
     * @return IP address or hostname where cln grpc api is reachable.
     */
    String getHost();

    /**
     * Port where cln grpc api is listening.
     *
     * @return Port where cln grpc api is listening.
     */
    Integer getPort();

    /**
     * The {@link SslContext} to access the cln api.
     *
     * @return The {@link SslContext} to access the cln api.
     */
    SslContext getSslContext();
}
