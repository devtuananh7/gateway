package bug.creator.simservice1.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "server_saved")
public class ServerSaved {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_rsa_public_key")
    private String rsaPublicKey;

    @Column(name = "client_aes_secret_key")
    private String aesSecretKey;
}
