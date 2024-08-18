package bug.creator.gatewayservice.Repository;

import bug.creator.gatewayservice.Entity.SimulationClientSecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientSecretRepository extends JpaRepository<SimulationClientSecretEntity, Long> {
}
