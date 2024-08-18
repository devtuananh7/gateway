package bug.creator.simclient.Repository;

import bug.creator.simclient.Entity.ClientSaved;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientSavedRepository extends JpaRepository<ClientSaved, Long> {
    Optional<ClientSaved> findByKeyId(Long keyId);
}
