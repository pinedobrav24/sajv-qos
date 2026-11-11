package pe.edu.sajv.sajv_qos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.sajv.sajv_qos.model.QosControlManual;

import java.util.Optional;

public interface QosControlManualRepository
        extends JpaRepository<QosControlManual, Integer> {

    Optional<QosControlManual> findByEquipo_IdAndPolitica_Id(
        Integer equipoId,
        Integer politicaId
    );
}