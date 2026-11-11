package pe.edu.sajv.sajv_qos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.sajv.sajv_qos.model.Sede;

public interface SedeRepository extends JpaRepository<Sede, Integer> {
    
    boolean existsByDescripcionIgnoreCase(String descripcion);

    boolean existsByDescripcionIgnoreCaseAndIdNot(String descripcion, Integer id);

    long countByEstado(Integer estado);
}
