package bug.creator.simservice1.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "simulation_client_secret")
@Getter
@Setter
public class SimulationClientSecretEntity {

    @Id
    @Column(name = "client_id")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_public")
    private String clientPublic;

}
