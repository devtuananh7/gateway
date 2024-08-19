package bug.creator.simclient.Controller;

import bug.creator.simclient.DTO.CalResponse;
import bug.creator.simclient.DTO.ClientRequest;
import bug.creator.simclient.DTO.ClientRequestSim;
import bug.creator.simclient.DTO.ClientResponse;
import bug.creator.simclient.Entity.ClientSaved;
import bug.creator.simclient.Repository.ClientSavedRepository;
import bug.creator.simclient.Service.AESService;
import bug.creator.simclient.Service.RSAService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.antlr.v4.runtime.misc.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientSavedRepository clientSavedRepository;
    private final Gson gson;

    @Value("${aes.key.default}")
    private String AES_DEFAULT_KEY;

    @PostMapping("/register")
    public ClientSaved registrationRequestToServer() {
        log.info("Registration request to server");
        Pair<String, String> rsaKeyPair = rsaKeyGen();

        String publicKey = rsaKeyPair.a;
        String privateKey = rsaKeyPair.b;

        ClientSaved clientSaved = new ClientSaved();
        clientSaved.setRsaPublicKey(publicKey);

        try {
            String data = AESService.encrypt(gson.toJson(clientSaved), AES_DEFAULT_KEY);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/sim1/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"data\":\"" + data + "\"}"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String bodyStr = response.body();

            log.info("Response body from server: {}", bodyStr);
            ClientResponse clientResponse  = gson.fromJson(response.body(), ClientResponse.class);

            log.info("Response data from server: {}", clientResponse.getData());

            String dataStr = clientResponse.getData();
            String decryptedData = AESService.decrypt(dataStr, AES_DEFAULT_KEY);

            log.info("Response DECRYPTED from server: {}", decryptedData);

            ClientSaved tempClientSaved = gson.fromJson(decryptedData, ClientSaved.class);

            clientSaved.setKeyId(tempClientSaved.getKeyId());
            clientSaved.setAesSecretKey(tempClientSaved.getAesSecretKey());
            clientSaved.setRsaPrivateKey(privateKey);
            clientSavedRepository.save(clientSaved);
        } catch (Exception e) {
            log.info("Error while sending registration request to server", e);
        }
        return clientSaved;
    }

    @PostMapping("/ping")
    public String ping() {
        log.info("Ping request to server");
        return "pong";
    }

    @PostMapping("/calculate")
    public CalResponse getCalData(@RequestBody ClientRequestSim data) {
        log.info("Sum request to server");
        log.info("Data: {}", gson.toJson(data));
        CalResponse calResponse = new CalResponse();
        ClientSaved clientSaved = clientSavedRepository.findByKeyId(data.getClientId()).orElse(null);
        if (clientSaved == null) {
            log.error("Client key not found");
            return null;
        }

        String encrypt1 = AESService.encrypt(gson.toJson(data), clientSaved.getAesSecretKey());
        ClientRequest clientRequest = new ClientRequest();
        clientRequest.setClientId(data.getClientId());
        clientRequest.setData(encrypt1);

        log.info("before try: {}", gson.toJson(clientRequest));

        try {
            log.info("before encrypt: {}", gson.toJson(clientRequest));
            String encryptedDataStr = AESService.encrypt(gson.toJson(clientRequest), AES_DEFAULT_KEY);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/sim1/calculate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"data\":\"" + encryptedDataStr + "\"}"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String bodyStr = response.body();

            log.info("Response body from cal server: {}", bodyStr);

            Optional<ClientSaved> temp = clientSavedRepository.findByKeyId(data.getClientId());
            if (temp.isEmpty()) {
                log.error("Client key not found");
                return null;
            }

            String aesSecretKey = temp.get().getAesSecretKey();
            log.info("AES Secret Key: {}", aesSecretKey);

            ClientResponse clientResponse = gson.fromJson(bodyStr, ClientResponse.class);
            log.info("Response data from cal server: {}", clientResponse.getData());

            String dataStr = clientResponse.getData();
            String decryptedDataRp = AESService.decrypt(dataStr, aesSecretKey);

            log.info("Response DECRYPTED from cal server: {}", decryptedDataRp);

            calResponse = gson.fromJson(decryptedDataRp, CalResponse.class);
        } catch (Exception e) {
            log.info("Error while sending registration request to server", e);
        }
        return calResponse;
    }



    private Pair<String, String> rsaKeyGen() {
        Pair<String, String> rsaKeyPair = null;
        try {
            KeyPair keyPair = RSAService.keyGen(2048);
            rsaKeyPair = new Pair<>(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()),
                    Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
            log.info("RSA key pair generated successfully");
        } catch (Exception e) {
            log.error("Error while generating RSA key pair", e);
        }
        return rsaKeyPair;
    }
}
