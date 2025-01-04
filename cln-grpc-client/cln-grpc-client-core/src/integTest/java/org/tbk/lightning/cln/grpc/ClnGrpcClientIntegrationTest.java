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
     * decode
     **/
    @Test
    void itShouldSuccessfullyInvokeDecodeBlockingBolt11() {
        // taken from https://www.bolt11.org/ on 2023-06-18
        String bolt11 = "lnbc15u1p3xnhl2pp5jptserfk3zk4qy42tlucycrfwxhydvlemu9pqr93tuzlv9cc7g3sdqsvfhkcap3xyhx7un8cqzpgxqzjcsp5f8c52y2stc300gl6s4xswtjpc37hrnnr3c9wvtgjfuvqmpm35evq9qyyssqy4lgd8tj637qcjp05rdpxxykjenthxftej7a2zzmwrmrl70fyj9hvj0rewhzj7jfyuwkwcg9g2jpwtk3wkjtwnkdks84hsnu8xps5vsq4gj5hs";

        DecodeResponse response = clnNodeBlockingStub.decode(DecodeRequest.newBuilder()
                .setString(bolt11)
                .build());

        assertThat(response, is(notNullValue()));
        assertThat(response.getItemType(), is(DecodeResponse.DecodeType.BOLT11_INVOICE));
        assertThat(response.getAmountMsat().getMsat(), is(1_500_000L));
        assertThat(response.getPaymentHash(), equalTo(ByteString.fromHex("90570c8d3688ad5012aa5ff982606971ae46b3f9df0a100cb15f05f61718f223")));
        assertThat(response.getDescription(), is("bolt11.org"));
        assertThat(response.getExpiry(), is(600L));
        assertThat(response.getMinFinalCltvExpiry(), is(40));
        assertThat(response.hasFeatures(), is(true));
    }

    @Test
    void itShouldSuccessfullyInvokeDecodeBlockingBolt12Offer() {
        // taken from https://github.com/lnbc1QWFyb24/bolt12-decoder on 2025-01-03
        String bolt12 = "lno1qgsqvgnwgcg35z6ee2h3yczraddm72xrfua9uve2rlrm9deu7xyfzrc2q42xjurnyyfqys2zzcssx06thlxk00g0epvynxff5vj46p3en8hz8ax9uy4ckyyfuyet8eqg";

        DecodeResponse response = clnNodeBlockingStub.decode(DecodeRequest.newBuilder()
                .setString(bolt12)
                .build());

        assertThat(response, is(notNullValue()));
        assertThat(response.getItemType(), is(DecodeResponse.DecodeType.BOLT12_OFFER));
        assertThat(response.getOfferAmount(), is(0L));
        assertThat(response.getOfferId(), equalTo(ByteString.fromHex("45880e501c65e9060d33128d2de1d23ff52ae768b2bcb62bef262d90b741b8cd")));
        assertThat(response.getOfferDescription(), is("Tips!"));
        assertThat(response.getOfferIssuer(), is("AB"));
        assertThat(response.getOfferIssuerId(), equalTo(ByteString.fromHex("033f4bbfcd67bd0fc858499929a3255d063999ee23f4c5e12b8b1089e132b3e408")));
    }
    /**
     * decode - end
     ************************************************************/

    /************************************************************
     * invoice
     **/
    @Test
    void itShouldSuccessfullyInvokeInvoiceBlocking() {
        InvoiceResponse response = clnNodeBlockingStub.invoice(InvoiceRequest.newBuilder()
                .setAmountMsat(AmountOrAny.newBuilder()
                        .setAmount(Amount.newBuilder().setMsat(21_000L).build())
                        .build())
                .setDescription("The Times 03/Jan/2009 Chancellor on brink of second bailout for banks")
                .build());

        assertThat(response, is(notNullValue()));
        assertThat(response.getBolt11(), startsWith("lnbcrt"));
        assertThat(response.getPaymentHash(), is(notNullValue()));
    }
    /**
     * invoice - end
     ************************************************************/

    /**
     * gRPC StreamObserver to projectreactor Flux adapter
     *
     * @param <T> type of the objects emitted
     */
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