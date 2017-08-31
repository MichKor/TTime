package pl.com.tt.ttime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.tt.ttime.model.Template;

public interface TemplateRepository extends JpaRepository<Template, Long> {
}
