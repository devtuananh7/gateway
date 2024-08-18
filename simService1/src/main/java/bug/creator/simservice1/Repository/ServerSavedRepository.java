package bug.creator.simservice1.Repository;


import bug.creator.simservice1.Entity.ServerSaved;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerSavedRepository extends JpaRepository<ServerSaved, Long> {
}
