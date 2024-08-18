package bug.creator.simservice2.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sim2")
public class SimController {

    @RequestMapping("/ping")
    public String getSim() {
        log.info("pong");
        return "pong - sim2";
    }
}
