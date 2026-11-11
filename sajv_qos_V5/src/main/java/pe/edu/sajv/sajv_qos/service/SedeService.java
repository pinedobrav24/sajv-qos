package pe.edu.sajv.sajv_qos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.model.Sede;
import pe.edu.sajv.sajv_qos.repository.SedeRepository;

@Service
@RequiredArgsConstructor  
public class SedeService {

    
    private final SedeRepository sedeRepository;
    private final AuditoriaService auditoriaService;

    //LISTAR SEDES

    public List<Sede>SedeSel(){
        return sedeRepository.findAll();
    }

    //BUSCAR SEDES POR ID
    @Transactional(readOnly = true)
    public Sede BuscarPorId(Integer id){
        return sedeRepository.findById(id)
            .orElseThrow(() -> 
                new IllegalArgumentException(
                    "No se encontró la sede con ID: " + id)
                );
    }

    //REGISTRAR SEDES
    @Transactional
    public Sede registrar(Sede sede){

        validarDatos(sede);

        String descripcion = sede.getDescripcion().trim();
        
        if (sedeRepository.existsByDescripcionIgnoreCase(descripcion)) {
            throw new IllegalArgumentException(
            "Ya existe una sede con la descripción: " + descripcion);
        }

        sede.setDescripcion(descripcion);

        if(sede.getDireccion() != null){
            sede.setDireccion(sede.getDireccion().trim());
        }

        //Nueva sede activa
        sede.setEstado(1);

        Sede guardada = sedeRepository.save(sede);
        auditoriaService.registrar("SEDES", "CREAR",
            "Se registró la sede " + guardada.getDescripcion()
                + " (ID " + guardada.getId() + ").");
        return guardada;
    }

    //EDITAR SEDES
    @Transactional
    public Sede editar(Integer id, Sede datos){
        Sede sede=BuscarPorId(id);
        validarDatos(datos);

        String descripcion = datos.getDescripcion().trim();

        if(sedeRepository.existsByDescripcionIgnoreCaseAndIdNot(descripcion, id)) {
            throw new IllegalArgumentException(
            "Ya existe otra sede con la descripción: " + descripcion);
        }

        sede.setDescripcion(descripcion);
        sede.setDireccion(datos.getDireccion() != null 
            ? datos.getDireccion().trim() : null);

        //no se permite cambiar el estado al editar, se mantiene el estado actual
        Sede actualizada = sedeRepository.save(sede);
        auditoriaService.registrar("SEDES", "ACTUALIZAR",
            "Se actualizaron los datos de la sede "
                + actualizada.getDescripcion() + " (ID "
                + actualizada.getId() + ").");
        return actualizada;

    }

    //DESACTIVAR SEDES

    @Transactional
    public void desactivar(Integer id){
        Sede sede=BuscarPorId(id);

        if(Integer.valueOf(0).equals(sede.getEstado())){
            throw new IllegalArgumentException(
                "La sede con ID: " + id + " ya está desactivada");
        }
        sede.setEstado(0);
        sedeRepository.save(sede);
        auditoriaService.registrar("SEDES", "DESACTIVAR",
            "Se desactivó la sede " + sede.getDescripcion()
                + " (ID " + sede.getId() + ").");
    }

    //ACTIVAR SEDES
    @Transactional 
    public void activar(Integer id){
        Sede sede=BuscarPorId(id);

        if(Integer.valueOf(1).equals(sede.getEstado())){
            throw new IllegalArgumentException(
                "La sede con ID: " + id + " ya está activada");
        }
        sede.setEstado(1);
        sedeRepository.save(sede);
        auditoriaService.registrar("SEDES", "ACTIVAR",
            "Se activó la sede " + sede.getDescripcion()
                + " (ID " + sede.getId() + ").");
    }

    //VALIDAR DATOS
    private void validarDatos(Sede sede){
        if(sede.getDescripcion() == null 
            || sede.getDescripcion().trim().isBlank()){
            throw new IllegalArgumentException(
                "La descripción de la sede no puede estar vacía");
        }

        if(sede.getDescripcion().trim().length() > 100){
            throw new IllegalArgumentException(
                "La descripción de la sede no puede exceder los 100 caracteres");
        }

        if(sede.getDireccion() != null 
            && sede.getDireccion().trim().length() > 200){
            throw new IllegalArgumentException(
                "La dirección de la sede no puede exceder los 200 caracteres");
        }
    }
}

