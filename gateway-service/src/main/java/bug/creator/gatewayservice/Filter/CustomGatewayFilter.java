package bug.creator.gatewayservice.Filter;

import bug.creator.gatewayservice.DTO.ClientRequest;
import bug.creator.gatewayservice.DTO.ClientResponse;
import bug.creator.gatewayservice.Repository.ServerSavedRepository;
import bug.creator.gatewayservice.Service.AESService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.logging.log4j.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;


@Slf4j
@Component
@RequiredArgsConstructor
@Order(-2)
public class CustomGatewayFilter implements GlobalFilter {

    private final ServerSavedRepository serverSavedRepository;
    private final Gson gson;

    @Value("${aes.key.default}")
    private String AES_SECRET;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        log.info("Request URL: {}", request.getURI());

//        Map<String, String> dataMap = DataLoader.getDataMap();

        // Cache request body
        return DataBufferUtils.join(request.getBody())
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.boundedElastic())
                .flatMap(dataBuffer -> {
                    byte[] bodyBytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bodyBytes);
                    DataBufferUtils.release(dataBuffer);

                    String cachedBody = new String(bodyBytes, StandardCharsets.UTF_8);
                    log.info("Cached Request Body: {}", cachedBody);

                    ClientRequest clientRequest = gson.fromJson(cachedBody, ClientRequest.class);

                    String data = clientRequest.getData();

                    // Giải mã dữ liệu với AES
                    String decryptedBody = AESService.decrypt(data, AES_SECRET);

                    log.info("Decrypted Data: {}", decryptedBody);

                    if (request.getURI().toString().contains("register")) {
                        log.info("Register request");
                        // Sửa đổi và tái sử dụng request body
                        ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(request) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
                                DataBuffer buffer = bufferFactory.wrap(decryptedBody.getBytes(StandardCharsets.UTF_8));
                                return Flux.just(buffer);
                            }
                        };
                        ServerHttpResponseDecorator decoratedResponse =
                                getDecoratedResponse(response, new DefaultDataBufferFactory(), AES_SECRET);

                        return chain.filter(exchange.mutate()
                                .request(decoratedRequest)
                                .response(decoratedResponse)
                                .build()
                        );
                    } else {
                        JsonObject jsonObject = gson.fromJson(decryptedBody, JsonObject.class);
                        String clientId;
                        String aesSecretKey;
                        if (!jsonObject.has("clientId")) {
                            return Mono.error(new RuntimeException("Client ID is required"));
                        } else {
                            clientId = jsonObject.get("clientId").getAsString();
                            log.info("Client ID: {}", clientId);
                        }

                        if (Strings.isBlank(clientId)) {
                            return Mono.error(new RuntimeException("Client ID is required"));
                        } else {
                            aesSecretKey = serverSavedRepository.findByClientId(clientId).getAesSecretKey();
                            log.info("AES Secret Key: {}", aesSecretKey);
                            if (Strings.isBlank(aesSecretKey)) {
                                return Mono.error(new RuntimeException("AES Secret Key is not found"));
                            }
                            String tempData = jsonObject.get("data").getAsString();
                            String tempDecryptedBody = AESService.decrypt(tempData, aesSecretKey);
                            log.info("Decrypted Data: {}", tempDecryptedBody);

                            // Sửa đổi và tái sử dụng request body
                            ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(request) {
                                @Override
                                public Flux<DataBuffer> getBody() {
                                    DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
                                    DataBuffer buffer = bufferFactory.wrap(tempDecryptedBody.getBytes(StandardCharsets.UTF_8));
                                    return Flux.just(buffer);
                                }
                            };
                            ServerHttpResponseDecorator decoratedResponse = getDecoratedResponse(response,
                                    new DefaultDataBufferFactory(),
                                    aesSecretKey);

                            return chain.filter(exchange.mutate()
                                    .request(decoratedRequest)
                                    .response(decoratedResponse)
                                    .build()
                            );
                        }


                    }
                });
    }


    private ServerHttpResponseDecorator getDecoratedResponse(ServerHttpResponse response, DataBufferFactory dataBufferFactory, String aesSecretKey) {
        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {

                if (body instanceof Flux) {

                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

                        DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
                        byte[] content = new byte[joinedBuffers.readableByteCount()];
                        joinedBuffers.read(content);
                        String responseBody = new String(content, StandardCharsets.UTF_8);

                        log.info("Original Response Body: {}", responseBody);
                        log.info("AES Secret Key: {}", aesSecretKey);

                        String encryptedBody = AESService.encrypt(responseBody, aesSecretKey);

                        ClientResponse clientResponse = new ClientResponse();
                        clientResponse.setData(encryptedBody);

                        log.info("Encrypted Response: {}", gson.toJson(clientResponse));

                        return dataBufferFactory.wrap(gson.toJson(clientResponse).getBytes(StandardCharsets.UTF_8));
                    })).onErrorResume(err -> {

                        log.error("error while decorating Response: {}",err.getMessage());
                        return Mono.empty();
                    });

                }
                return super.writeWith(body);
            }
        };
    }
}
