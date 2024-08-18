package bug.creator.simservice1.Controller;

import bug.creator.simservice1.DTO.ClientRequest;
import bug.creator.simservice1.DTO.ClientRequestSim;
import bug.creator.simservice1.DTO.ClientResponse;
import bug.creator.simservice1.DTO.DTO;
import bug.creator.simservice1.Entity.ClientSaved;
import bug.creator.simservice1.Entity.ServerSaved;
import bug.creator.simservice1.Repository.ClientSecretRepository;
import bug.creator.simservice1.Repository.ServerSavedRepository;
import bug.creator.simservice1.Service.AESService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sim1")
@RequiredArgsConstructor
public class SimController {

    private final Gson gson;
    private final ClientSecretRepository clientSecretRepository;
    private final ServerSavedRepository serverSavedRepository;

    @RequestMapping("/ping")
    public String getSim(@RequestBody DTO dto) {
        log.info("pong");
        log.info("controller: {}", gson.toJson(dto));
        return "pong - sim1";
    }

    @PostMapping("/register")
    public ClientSaved registration(@RequestBody ClientSaved clientRequest) {
        log.info("Register: {}", gson.toJson(clientRequest));
        ServerSaved serverSaved = new ServerSaved();
        ClientSaved clientSaved = new ClientSaved();

        serverSaved.setRsaPublicKey(clientRequest.getRsaPublicKey());
        try {
            String aesKey = AESService.keyGen(128);

            serverSaved.setAesSecretKey(aesKey);
            serverSavedRepository.save(serverSaved);

            clientSaved.setKeyId(serverSaved.getId());
            clientSaved.setRsaPublicKey(clientRequest.getRsaPublicKey());
            clientSaved.setAesSecretKey(aesKey);
            log.info("ClientSaved: {}", gson.toJson(clientSaved));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        return clientSaved;
    }

    @PostMapping("/calculate")
    public String calculate(@RequestBody ClientRequestSim clientRequest) {
        log.info("calculate");
        log.info("request to controller: {}", gson.toJson(clientRequest));
        return switch (clientRequest.getData()) {
            case "add" ->
                    String.valueOf(Long.parseLong(clientRequest.getNumber1()) + Long.parseLong(clientRequest.getNumber2()));
            case "sub" ->
                    String.valueOf(Long.parseLong(clientRequest.getNumber1()) - Long.parseLong(clientRequest.getNumber2()));
            case "mul" ->
                    String.valueOf(Long.parseLong(clientRequest.getNumber1()) * Long.parseLong(clientRequest.getNumber2()));
            case "div" ->
                    String.valueOf(Long.parseLong(clientRequest.getNumber1()) / Long.parseLong(clientRequest.getNumber2()));
            default -> "Invalid operation";
        };
    }
}
