
package pe.edu.sajv.sajv_qos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.sajv.sajv_qos.model.PoliticaQos;

@Repository
public interface PoliticaQosRepository extends JpaRepository<PoliticaQos, Integer> {

    List<PoliticaQos> findByEstadoOrderByNombreAsc(Integer estado);

    boolean existsByCodigo(String codigo);

    long countByEstado(Integer estado);
}
