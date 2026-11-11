
package pe.edu.sajv.sajv_qos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.sajv.sajv_qos.model.QosOperacion;

@Repository
public interface QosOperacionRepository extends JpaRepository<QosOperacion, Integer> {

    List<QosOperacion> findTop100ByOrderByFechaInicioDesc();

    List<QosOperacion> findByAudiencia_IdOrderByFechaInicioDesc(
            Integer audienciaId);

    List<QosOperacion> findByEquipo_IdOrderByFechaInicioDesc(
            Integer equipoId);

    Optional<QosOperacion>
    findTopByAudiencia_IdAndEquipo_IdAndPolitica_IdAndResultadoOrderByFechaInicioDescIdDesc(
            Integer audienciaId,
            Integer equipoId,
            Integer politicaId,
            Integer resultado);

        // Última operación exitosa por router y política.
    Optional<QosOperacion>
    findTopByEquipo_IdAndPolitica_IdAndResultadoOrderByFechaInicioDescIdDesc(
            Integer equipoId,
            Integer politicaId,
            Integer resultado);

    long countByResultadoAndFechaInicioGreaterThanEqual(Integer resultado, java.time.LocalDateTime desde);
}
