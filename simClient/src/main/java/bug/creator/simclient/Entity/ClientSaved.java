package bug.creator.simclient.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "client_saved")
public class ClientSaved {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_id")
    private Long keyId;

    @Column(name = "aes_secret_key")
    private String aesSecretKey;

    @Column(name = "rsa_public_key")
    private String rsaPublicKey;

    @Column(name = "rsa_private_key")
    private String rsaPrivateKey;

}
