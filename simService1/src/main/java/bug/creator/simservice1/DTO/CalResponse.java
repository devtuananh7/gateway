package bug.creator.simservice1.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalResponse {
    private Long clientId;
    private String data;
    private String number1;
    private String number2;
    private String result;
}
