package pe.edu.sajv.sajv_qos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.sajv.sajv_qos.model.Sala;

public interface SalaRepository extends JpaRepository<Sala, Integer> {

    
    // =====================================================
    // LISTADO GENERAL DE SALAS CON SU SEDE
    // =====================================================

    @EntityGraph(attributePaths = {"sede"})
    List<Sala> findAllByOrderByDescripcionAsc();


    // =====================================================
    // LISTAR SALAS ACTIVAS POR SEDE
    // UTILIZADO POR EL MÓDULO DE AUDIENCIAS
    // =====================================================

    List<Sala> findBySede_IdAndEstadoOrderByDescripcionAsc(
        Integer sedeId,
        Integer estado
    );


    // =====================================================
    // VALIDAR QUE UNA SALA PERTENECE A UNA SEDE
    // Y SE ENCUENTRA ACTIVA
    // =====================================================

    boolean existsByIdAndSede_IdAndEstado(
        Integer salaId,
        Integer sedeId,
        Integer estado
    );


    // =====================================================
    // VALIDAR SALAS DUPLICADAS DENTRO DE UNA SEDE
    // =====================================================

    boolean existsBySede_IdAndDescripcionIgnoreCase(
        Integer sedeId,
        String descripcion
    );


    // =====================================================
    // VALIDAR DUPLICIDAD AL EDITAR
    // EXCLUYENDO LA SALA ACTUAL
    // =====================================================

    boolean existsBySede_IdAndDescripcionIgnoreCaseAndIdNot(
        Integer sedeId,
        String descripcion,
        Integer id
    );


    // =====================================================
    // LISTAR TODAS LAS SALAS DE UNA SEDE
    // =====================================================

    List<Sala> findBySede_IdOrderByDescripcionAsc(
        Integer sedeId
    );


    // =====================================================
    // LISTAR SALAS POR ESTADO
    // =====================================================

    List<Sala> findByEstadoOrderByDescripcionAsc(
        Integer estado
    );


    long countByEstado(Integer estado);
}
