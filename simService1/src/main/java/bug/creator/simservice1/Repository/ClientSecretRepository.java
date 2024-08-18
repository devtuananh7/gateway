package bug.creator.simservice1.Repository;


import bug.creator.simservice1.Entity.SimulationClientSecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientSecretRepository extends JpaRepository<SimulationClientSecretEntity, Long> {
}
