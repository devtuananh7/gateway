package bug.creator.simservice1.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientSaved {
    private Long keyId;
    private String aesSecretKey;
    private String rsaPublicKey;
}
