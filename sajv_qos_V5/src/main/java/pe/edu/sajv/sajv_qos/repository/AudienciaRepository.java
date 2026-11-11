package pe.edu.sajv.sajv_qos.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.sajv.sajv_qos.model.Audiencia;

@Repository 
public interface AudienciaRepository extends JpaRepository<Audiencia, Integer> {
    //Listar audiencias por estado ordenadas por fecha y hora de inicio
    List<Audiencia> findByEstadoOrderByFechaAscHoraInicioAsc(Integer estado);

    //validar  cruce de horarios en una misma sala
     boolean existsBySala_IdAndFechaAndEstadoAndHoraInicioLessThanAndHoraFinGreaterThan(
        Integer salaId,
        LocalDate fecha,
        Integer estado,
        LocalTime horaFin,
        LocalTime horaInicio
    );

    // Audiencias de una sede específica
    List<Audiencia> findBySala_Sede_IdAndEstadoOrderByFechaAscHoraInicioAsc(
    Integer sedeId,
    Integer estado
    );

    boolean existsBySala_IdAndEstado(
    Integer salaId,
    Integer estado
);

    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM Audiencia a
    WHERE a.sala.id = :salaId
      AND a.fecha = :fecha
      AND a.estado = 1
      AND a.situacion <> 4
      AND a.horaInicio < :horaFin
      AND a.horaFin > :horaInicio
      AND a.id <> :audienciaId
        """)
    boolean existeCruceAlEditar(
        @Param("salaId") Integer salaId,
        @Param("fecha") LocalDate fecha,
        @Param("horaFin") LocalTime horaFin,
        @Param("horaInicio") LocalTime horaInicio,
        @Param("audienciaId") Integer audienciaId
    );


    @Query("""
    SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
    FROM Audiencia a
    WHERE a.sala.id = :salaId
      AND a.fecha = :fecha
      AND a.estado = 1
      AND a.situacion <> 4
      AND a.horaInicio < :horaFin
      AND a.horaFin > :horaInicio
    """)
    boolean existeCruce(
        @Param("salaId") Integer salaId,
        @Param("fecha") LocalDate fecha,
        @Param("horaFin") LocalTime horaFin,
        @Param("horaInicio") LocalTime horaInicio
    );

    // Validar si una sala tiene audiencias pendientes o en curso
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Audiencia a
        WHERE a.sala.id = :salaId
        AND a.estado = 1
        AND a.situacion IN (1, 2, 5)
        AND (
            a.fecha > :fechaActual
            OR (
                a.fecha = :fechaActual
                AND a.horaFin > :horaActual
            )
        )
        """)
    boolean tieneAudienciasPendientes(
        @Param("salaId") Integer salaId,
        @Param("fechaActual") LocalDate fechaActual,
        @Param("horaActual") LocalTime horaActual
    );

    long countByEstadoAndFecha(Integer estado, LocalDate fecha);

    long countByEstadoAndSituacionAndFecha(Integer estado, Integer situacion, LocalDate fecha);

    long countByEstadoAndSituacionAndFechaGreaterThanEqual(Integer estado, Integer situacion, LocalDate fecha);

   @Query("""
        SELECT a FROM Audiencia a
        WHERE a.estado = :estado
        AND a.situacion = 1
        AND (
            a.fecha > :fecha
            OR (
                a.fecha = :fecha
                AND a.horaInicio > :hora
            )
        )
        ORDER BY a.fecha ASC, a.horaInicio ASC
    """)
    @EntityGraph(attributePaths = {"sala", "sala.sede"})
    List<Audiencia> buscarProximasAudiencias(
        @Param("estado") Integer estado,
        @Param("fecha") LocalDate fecha,
        @Param("hora") LocalTime hora
    );

    // Audiencias habilitadas para automatización QoS
    @Query("""
            SELECT a
            FROM Audiencia a
            WHERE a.estado = 1
            AND a.situacion IN (1, 2)
            AND a.fecha = :fecha
            AND a.horaInicio IS NOT NULL
            AND a.horaFin IS NOT NULL
            ORDER BY a.horaInicio ASC
            """)
        List<Audiencia> buscarAudienciasParaQos(
            @Param("fecha") LocalDate fecha
        );

        @Modifying
    @Query("""
        UPDATE Audiencia a
        SET a.situacion = 3
        WHERE a.estado = 1
        AND a.situacion IN (1, 2)
        AND (
            a.fecha < :fechaActual
            OR (
                a.fecha = :fechaActual
                AND a.horaFin <= :horaActual
            )
        )
    """)
    int actualizarAudienciasFinalizadas(
        @Param("fechaActual") LocalDate fechaActual,
        @Param("horaActual") LocalTime horaActual
    );

    @Modifying
    @Query("""
        UPDATE Audiencia a
        SET a.situacion = 2
        WHERE a.estado = 1
        AND a.situacion = 1
        AND a.fecha = :fechaActual
        AND a.horaInicio <= :horaActual
        AND a.horaFin > :horaActual
    """)
    int actualizarAudienciasEnCurso(
        @Param("fechaActual") LocalDate fechaActual,
        @Param("horaActual") LocalTime horaActual
    );

    
    @Query("""
        SELECT COUNT(a)
        FROM Audiencia a
        WHERE a.estado = :estado
        AND a.situacion = 1
        AND (
            a.fecha > :fecha
            OR (
                a.fecha = :fecha
                AND a.horaInicio > :hora
            )
        )
    """)
    long contarAudienciasProgramadas(
        @Param("estado") Integer estado,
        @Param("fecha") LocalDate fecha,
        @Param("hora") LocalTime hora
    );

}
