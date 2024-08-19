package bug.creator.simservice1.Controller;

import bug.creator.simservice1.DTO.CalRequest;
import bug.creator.simservice1.DTO.CalResponse;
import bug.creator.simservice1.DTO.DTO;
import bug.creator.simservice1.Entity.ClientSaved;
import bug.creator.simservice1.Entity.ServerSaved;
import bug.creator.simservice1.Repository.ServerSavedRepository;
import bug.creator.simservice1.Service.AESService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/sim1")
@RequiredArgsConstructor
public class SimController {

    private final Gson gson;
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

            Optional<ServerSaved> tempOpt = serverSavedRepository.findById(serverSaved.getId());
            if (tempOpt.isPresent()) {
                serverSaved = tempOpt.get();
                serverSaved.setClientId(serverSaved.getId().toString());
                serverSavedRepository.save(serverSaved);
            }

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
    public CalResponse calculate(@RequestBody CalRequest clientRequest) {
        log.info("calculate");
        log.info("request to controller: {}", gson.toJson(clientRequest));

        CalResponse clientResponse = CalResponse.builder()
                .clientId(clientRequest.getClientId())
                .number1(clientRequest.getNumber1())
                .number2(clientRequest.getNumber2())
                .data(clientRequest.getData())
                .build();

        switch (clientRequest.getData()) {
            case "add" -> clientResponse.setResult(
                    String.valueOf(Long.parseLong(clientRequest.getNumber1()) + Long.parseLong(clientRequest.getNumber2())));
            case "sub" -> clientResponse.setResult(
                    String.valueOf(Long.parseLong(clientRequest.getNumber1()) - Long.parseLong(clientRequest.getNumber2())));
            case "mul" -> clientResponse.setResult(
                    String.valueOf(Long.parseLong(clientRequest.getNumber1()) * Long.parseLong(clientRequest.getNumber2())));
            case "div" -> clientResponse.setResult(
                    String.valueOf(Long.parseLong(clientRequest.getNumber1()) / Long.parseLong(clientRequest.getNumber2())));
            default -> clientResponse.setResult("Invalid operation");
        };

        log.info("response from controller: {}", gson.toJson(clientResponse));
        return clientResponse;
    }
}
