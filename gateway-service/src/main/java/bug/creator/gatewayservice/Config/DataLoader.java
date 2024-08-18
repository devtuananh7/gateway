package bug.creator.gatewayservice.Config;

import bug.creator.gatewayservice.Repository.ServerSavedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
//@Component
@RequiredArgsConstructor
public class DataLoader {

    private static Map<String, String> dataMap = new HashMap<>();

    private final ServerSavedRepository dataRepository; // Repository hoặc service để lấy dữ liệu

    @PostConstruct
    public void init() {
        log.info("Loading data from database");
        loadDataFromDatabase();
        log.info("Data loaded successfully");
    }

    public static Map<String, String> getDataMap() {
        return dataMap;
    }

    private void loadDataFromDatabase() {
        // Giả sử bạn có một phương thức trong repository để lấy tất cả dữ liệu
        dataRepository.findAll().forEach(entry -> {
            dataMap.put(entry.getClientId(), entry.getAesSecretKey());
            log.info("Data loaded: {} - {}", entry.getClientId(), entry.getAesSecretKey());
        });
    }
}
