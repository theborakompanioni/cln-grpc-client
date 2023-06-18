package org.tbk.lightning.cln.grpc;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.tbk.lightning.cln.grpc.client.*;
import org.tbk.lightning.cln.grpc.config.ClnContainerRpcClientAutoConfiguration;
import org.tbk.spring.testcontainer.bitcoind.config.BitcoindContainerAutoConfiguration;
import org.tbk.spring.testcontainer.cln.ClnContainer;
import org.tbk.spring.testcontainer.cln.config.ClnContainerAutoConfiguration;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.Hex;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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


    /************************************************************
     * getinfo
     **/
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
    /**
     * getinfo - end
     ************************************************************/

    /************************************************************
     * listpays
     **/
    @Test
    void itShouldSuccessfullyInvokeListPaysBlocking() {
        ListpaysResponse response = clnNodeBlockingStub.listPays(ListpaysRequest.newBuilder().build());

        assertThat(response, is(notNullValue()));
        assertThat(response.getPaysCount(), is(0));
        assertThat(response.getPaysList(), hasSize(0));
    }
    /**
     * listpays - end
     ************************************************************/

    /************************************************************
     * listpays
     **/
    @Test
    void itShouldSuccessfullyInvokeDecodePayBlocking() {
        // taken from https://www.bolt11.org/ on 2023-06-18
        String bolt11 = "lnbc15u1p3xnhl2pp5jptserfk3zk4qy42tlucycrfwxhydvlemu9pqr93tuzlv9cc7g3sdqsvfhkcap3xyhx7un8cqzpgxqzjcsp5f8c52y2stc300gl6s4xswtjpc37hrnnr3c9wvtgjfuvqmpm35evq9qyyssqy4lgd8tj637qcjp05rdpxxykjenthxftej7a2zzmwrmrl70fyj9hvj0rewhzj7jfyuwkwcg9g2jpwtk3wkjtwnkdks84hsnu8xps5vsq4gj5hs";

        DecodepayResponse response = clnNodeBlockingStub.decodePay(DecodepayRequest.newBuilder()
                .setBolt11(bolt11)
                .build());

        assertThat(response, is(notNullValue()));
        assertThat(response.getAmountMsat().getMsat(), is(1500000L));
        assertThat(response.getPaymentHash(), equalTo(ByteString.fromHex("90570c8d3688ad5012aa5ff982606971ae46b3f9df0a100cb15f05f61718f223")));
        assertThat(response.getDescription(), is("bolt11.org"));
        assertThat(response.getExpiry(), is(600L));
        assertThat(response.getMinFinalCltvExpiry(), is(40));
        assertThat(response.hasFeatures(), is(true));
    }

    /**
     * listpays - end
     ************************************************************/

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