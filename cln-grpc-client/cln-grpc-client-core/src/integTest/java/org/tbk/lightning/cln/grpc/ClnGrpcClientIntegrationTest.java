package org.tbk.lightning.cln.grpc;

import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.tbk.lightning.cln.grpc.client.GetinfoRequest;
import org.tbk.lightning.cln.grpc.client.GetinfoResponse;
import org.tbk.lightning.cln.grpc.client.NodeGrpc;
import org.tbk.lightning.cln.grpc.config.ClnContainerRpcClientAutoConfiguration;
import org.tbk.spring.testcontainer.bitcoind.config.BitcoindContainerAutoConfiguration;
import org.tbk.spring.testcontainer.cln.ClnContainer;
import org.tbk.spring.testcontainer.cln.config.ClnContainerAutoConfiguration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(classes = {
        BitcoindContainerAutoConfiguration.class,
        ClnContainerAutoConfiguration.class,
        ClnContainerRpcClientAutoConfiguration.class
})
@ActiveProfiles("test")
class ClnGrpcClientIntegrationTest {

    @Autowired(required = false)
    private ClnContainer<?> clnContainer;


    @Autowired(required = false)
    private NodeGrpc.NodeBlockingStub clnNodeBlockingStub;

    @Autowired(required = false)
    private NodeGrpc.NodeStub clnNodeStub;

    @Autowired(required = false)
    private NodeGrpc.NodeFutureStub clnNodeFutureStub;

    @Test
    void contextLoads() {
        assertThat(clnContainer, is(notNullValue()));
        assertThat(clnContainer.isRunning(), is(true));

        assertThat(clnNodeBlockingStub, is(notNullValue()));
        assertThat(clnNodeStub, is(notNullValue()));
        assertThat(clnNodeFutureStub, is(notNullValue()));
    }


    @Test
    void itShouldSuccessfullyInvokeGetInfoBlocking() {
        GetinfoResponse response = clnNodeBlockingStub.getinfo(GetinfoRequest.newBuilder().build());

        assertThat(response, is(notNullValue()));
        assertThat(response.getAlias(), is("tbk-cln-grpc-client-test"));
        assertThat(response.getNetwork(), is("regtest"));
    }

    @Test
    void itShouldSuccessfullyInvokeGetInfoFuture() throws ExecutionException, InterruptedException, TimeoutException {
        GetinfoResponse response = clnNodeFutureStub.getinfo(GetinfoRequest.newBuilder().build())
                .get(5, TimeUnit.SECONDS);

        assertThat(response, is(notNullValue()));
        assertThat(response.getAlias(), is("tbk-cln-grpc-client-test"));
        assertThat(response.getNetwork(), is("regtest"));
    }

    @Test
    void itShouldSuccessfullyInvokeGetInfo() {
        GetinfoResponse response = Flux.<GetinfoResponse>create(emitter -> {
            try {
                clnNodeStub.getinfo(GetinfoRequest.newBuilder().build(), new EmittingStreamObserver<>(emitter));
            } catch (Exception e) {
                emitter.error(e);
            }
        }).blockFirst(Duration.ofSeconds(5));

        assertThat(response, is(notNullValue()));
        assertThat(response.getAlias(), is("tbk-cln-grpc-client-test"));
        assertThat(response.getNetwork(), is("regtest"));
    }

    @RequiredArgsConstructor
    static class EmittingStreamObserver<T> implements StreamObserver<T> {

        @NonNull
        private final FluxSink<T> emitter;

        @Override
        public void onNext(T value) {
            emitter.next(value);
        }

        @Override
        public void onError(Throwable t) {
            emitter.error(t);
        }

        @Override
        public void onCompleted() {
            emitter.complete();
        }
    }

}