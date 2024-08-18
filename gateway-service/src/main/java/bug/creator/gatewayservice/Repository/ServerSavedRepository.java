package bug.creator.gatewayservice.Repository;

import bug.creator.gatewayservice.Entity.ServerSaved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerSavedRepository extends JpaRepository<ServerSaved, Long> {
    ServerSaved findByClientId(String keyId);
}
