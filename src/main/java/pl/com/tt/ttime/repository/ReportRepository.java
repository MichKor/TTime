package pl.com.tt.ttime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.tt.ttime.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
