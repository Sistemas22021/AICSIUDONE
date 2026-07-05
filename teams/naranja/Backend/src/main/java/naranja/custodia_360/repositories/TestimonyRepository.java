package naranja.custodia_360.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import naranja.custodia_360.models.Testimony;

@Repository
public interface TestimonyRepository extends JpaRepository<Testimony, String> {
}