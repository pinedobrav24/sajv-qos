package pe.edu.sajv.sajv_qos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.sajv.sajv_qos.model.EquipoRed;

@Repository
public interface EquipoRedRepository extends JpaRepository<EquipoRed, Integer> {

    @EntityGraph(attributePaths = {"sede"})
    List<EquipoRed> findByEstadoOrderByNombreAsc(Integer estado);

    @EntityGraph(attributePaths = {"sede"})
    List<EquipoRed> findBySede_IdAndEstadoOrderByNombreAsc(
            Integer sedeId, Integer estado);

    boolean existsByIpAndPuerto(String ip, Integer puerto);

    long countByEstado(Integer estado);
}