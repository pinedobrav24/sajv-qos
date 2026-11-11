package pe.edu.sajv.sajv_qos.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.dto.UsuarioForm;
import pe.edu.sajv.sajv_qos.model.Sede;
import pe.edu.sajv.sajv_qos.model.Usuario;
import pe.edu.sajv.sajv_qos.repository.SedeRepository;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private static final int ACTIVO = 1;
    // Contraseña temporal estándar solicitada para restablecimientos administrativos.
    // El usuario deberá cambiarla obligatoriamente en su siguiente inicio de sesión.
    private static final String CONTRASENA_TEMPORAL = "11111111Aa";
    private static final int INACTIVO = 0;
    private final UsuarioRepository usuarioRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByApellidosAscNombreAsc();
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el usuario indicado."));
    }

    @Transactional
    public Usuario crear(UsuarioForm form) {
        validarFormulario(form, null, true);
        Usuario usuario = new Usuario();
        copiarDatos(form, usuario);
        usuario.setContrasena(passwordEncoder.encode(form.getContrasena().trim()));
        usuario.setEstado(ACTIVO);
        usuario.setDebeCambiarContrasena(1);
        Usuario guardado = usuarioRepository.save(usuario);
        auditoriaService.registrar("USUARIOS", "CREAR",
                "Se creó el usuario " + guardado.getUsuario() + ".");
        return guardado;
    }

    @Transactional
    public Usuario actualizar(Integer id, UsuarioForm form) {
        Usuario usuario = buscarPorId(id);
        validarFormulario(form, id, false);
        copiarDatos(form, usuario);
        if (form.getContrasena() != null && !form.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(form.getContrasena().trim()));
            usuario.setDebeCambiarContrasena(1);
        }
        Usuario guardado = usuarioRepository.save(usuario);
        auditoriaService.registrar("USUARIOS", "ACTUALIZAR",
                "Se actualizaron los datos del usuario " + guardado.getUsuario() + ".");
        return guardado;
    }

    @Transactional
    public void cambiarEstado(Integer id, boolean activar) {
        Usuario usuario = buscarPorId(id);
        int nuevoEstado = activar ? ACTIVO : INACTIVO;
        if (usuario.getEstado() != null && usuario.getEstado() == nuevoEstado) {
            throw new IllegalArgumentException(activar
                    ? "El usuario ya se encuentra activo."
                    : "El usuario ya se encuentra inactivo.");
        }
        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);
        auditoriaService.registrar("USUARIOS", activar ? "ACTIVAR" : "DESACTIVAR",
                "Se " + (activar ? "activó" : "desactivó") + " el usuario " + usuario.getUsuario() + ".");
    }

    @Transactional
    public String restablecerContrasena(Integer id) {
        Usuario usuario = buscarPorId(id);
        usuario.setContrasena(passwordEncoder.encode(CONTRASENA_TEMPORAL));
        usuario.setDebeCambiarContrasena(1);
        usuarioRepository.save(usuario);
        auditoriaService.registrar("USUARIOS", "RESTABLECER_CONTRASENA",
                "Se restableció la contraseña del usuario " + usuario.getUsuario()
                        + " y se solicitó el cambio en el siguiente ingreso.");
        // Se devuelve para mostrarla al administrador después del restablecimiento.
        // Nunca almacenar la contraseña en texto plano ni escribirla en logs.
        return CONTRASENA_TEMPORAL;
    }

    @Transactional
    public void cambiarMiContrasena(String nombreUsuario, String actual, String nueva, String confirmacion) {
        Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        if (actual == null || !passwordEncoder.matches(actual, usuario.getContrasena())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }
        if (nueva == null || nueva.length() < 10 || nueva.length() > 100) {
            throw new IllegalArgumentException("La nueva contraseña debe tener entre 10 y 100 caracteres.");
        }
        if (!nueva.equals(confirmacion)) {
            throw new IllegalArgumentException("La confirmación de la contraseña no coincide.");
        }
        if (passwordEncoder.matches(nueva, usuario.getContrasena())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual.");
        }
        usuario.setContrasena(passwordEncoder.encode(nueva));
        usuario.setDebeCambiarContrasena(0);
        usuarioRepository.save(usuario);
        auditoriaService.registrar("SEGURIDAD", "CAMBIAR_CONTRASENA",
                "El usuario actualizó su contraseña.");
    }

    private void validarFormulario(UsuarioForm form, Integer id, boolean esNuevo) {
        String username = form.getUsuario().trim();
        String correo = form.getCorreo().trim();
        if (id == null ? usuarioRepository.existsByUsuarioIgnoreCase(username)
                : usuarioRepository.existsByUsuarioIgnoreCaseAndIdNot(username, id)) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }
        if (id == null ? usuarioRepository.existsByCorreoIgnoreCase(correo)
                : usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        if (esNuevo && (form.getContrasena() == null || form.getContrasena().isBlank())) {
            throw new IllegalArgumentException("La contraseña es obligatoria para crear el usuario.");
        }
        if (form.getContrasena() != null && !form.getContrasena().isBlank()
                && form.getContrasena().trim().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (form.getRol() == null || form.getRol() < 1 || form.getRol() > 3) {
            throw new IllegalArgumentException("El perfil seleccionado no es válido.");
        }
        // Solo el perfil Operador (2) exige sede; Administrador y Consulta son globales/opcionales.
        if (form.getRol() == 2 && form.getSedeId() == null) {
            throw new IllegalArgumentException("Debe asignar una sede al usuario con perfil Operador.");
        }
        if (form.getSedeId() != null) {
            Sede sede = sedeRepository.findById(form.getSedeId())
                    .orElseThrow(() -> new IllegalArgumentException("La sede seleccionada no existe."));
            if (!Integer.valueOf(ACTIVO).equals(sede.getEstado())) {
                throw new IllegalArgumentException("No se puede asignar una sede inactiva.");
            }
        }
    }

    private void copiarDatos(UsuarioForm form, Usuario usuario) {
        usuario.setNombre(form.getNombre().trim());
        usuario.setApellidos(form.getApellidos().trim());
        usuario.setUsuario(form.getUsuario().trim());
        usuario.setCorreo(form.getCorreo().trim());
        usuario.setRol(form.getRol());
        usuario.setSede(form.getSedeId() == null ? null : sedeRepository.findById(form.getSedeId())
                .orElseThrow(() -> new IllegalArgumentException("La sede seleccionada no existe.")));
    }
}
