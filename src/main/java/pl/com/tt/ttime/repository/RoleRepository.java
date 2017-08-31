package pl.com.tt.ttime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.tt.ttime.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
