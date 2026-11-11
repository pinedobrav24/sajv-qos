
package pe.edu.sajv.sajv_qos.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.model.Usuario;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioDetalleService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Usuario no encontrado: " + username
                    )
                );

        if (usuario.getEstado() == null || usuario.getEstado() != 1) {
            throw new UsernameNotFoundException(
                "Usuario inactivo: " + username
            );
        }

        Integer codigoRol = usuario.getRol() != null
                ? usuario.getRol()
                : 0;

        String rol = switch (codigoRol) {
            case 1 -> "ADMINISTRADOR";
            case 2 -> "OPERADOR";
            case 3 -> "CONSULTA";
            default -> throw new UsernameNotFoundException(
                "Rol no válido para el usuario: " + username
            );
        };

        return User.withUsername(usuario.getUsuario())
                .password(usuario.getContrasena())
                .roles(rol)
                .build();
    }
}
