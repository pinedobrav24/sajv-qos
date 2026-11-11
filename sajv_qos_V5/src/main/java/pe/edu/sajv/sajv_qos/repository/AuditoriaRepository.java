package pe.edu.sajv.sajv_qos.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.edu.sajv.sajv_qos.model.Auditoria;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    @Query("""
            SELECT a FROM Auditoria a
            WHERE (:usuario IS NULL OR LOWER(a.usuario) LIKE LOWER(CONCAT('%', :usuario, '%')))
              AND (:modulo IS NULL OR a.modulo = :modulo)
              AND (:accion IS NULL OR a.accion = :accion)
              AND (:desde IS NULL OR a.fecha >= :desde)
              AND (:hasta IS NULL OR a.fecha <= :hasta)
            """)
    Page<Auditoria> buscarConFiltros(
            @Param("usuario") String usuario,
            @Param("modulo") String modulo,
            @Param("accion") String accion,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);
}
