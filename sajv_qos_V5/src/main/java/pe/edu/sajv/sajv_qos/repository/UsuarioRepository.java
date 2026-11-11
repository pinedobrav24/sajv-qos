package pe.edu.sajv.sajv_qos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.edu.sajv.sajv_qos.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @EntityGraph(attributePaths = {"sede"})
    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByUsuarioIgnoreCase(String usuario);

    boolean existsByUsuarioIgnoreCaseAndIdNot(String usuario, Integer id);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Integer id);

    @EntityGraph(attributePaths = {"sede"})
    List<Usuario> findAllByOrderByApellidosAscNombreAsc();
}
