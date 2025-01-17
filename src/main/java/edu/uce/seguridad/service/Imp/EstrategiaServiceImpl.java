package edu.uce.seguridad.service.Imp;

import edu.uce.seguridad.exception.EliminacionException;
import edu.uce.seguridad.exception.NoEncontradoExcepcion;
import edu.uce.seguridad.model.EstrategiasContinuidad;
import edu.uce.seguridad.model.RecursoPrioridad;
import edu.uce.seguridad.model.ResumenDeEstrategias;
import edu.uce.seguridad.repository.EstrategiaRepository;
import edu.uce.seguridad.repository.EstrategiasContinuidadRepository;
import edu.uce.seguridad.service.service.EstrategiaService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class EstrategiaServiceImpl implements EstrategiaService {

    private EstrategiaRepository estrategiaRepository;

    private EstrategiasContinuidadRepository estrategiasContinuidadRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ResumenDeEstrategias> buscarTodos() throws NoEncontradoExcepcion {
        List<ResumenDeEstrategias> listas = this.estrategiaRepository.findAll();
        if (listas.isEmpty()) {
            throw new NoEncontradoExcepcion("respuesta", "No se encuentran datos");
        }
        return listas;
    }

    @Override
    @Transactional
    public ResumenDeEstrategias agregar(ResumenDeEstrategias pojo) throws DataAccessException {
        // Por cada estrategia se crea un form 7.2
        generarEstrategiasContinuidad(pojo, false);
        return this.estrategiaRepository.insert(pojo);
    }

    @Override
    @Transactional
    public ResumenDeEstrategias actualizar(ResumenDeEstrategias pojo) throws DataAccessException {
        this.buscaPorId(pojo.get_id());
        generarEstrategiasContinuidad(pojo, true);
        return this.estrategiaRepository.save(pojo);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenDeEstrategias buscaPorId(String identificador) throws NoEncontradoExcepcion {
        ResumenDeEstrategias resumenDeEstrategias = this.estrategiaRepository.findById(identificador).orElse(null);
        if (resumenDeEstrategias == null) {
            throw new NoEncontradoExcepcion("respuesta", "No se encontro registro para el id: ".concat(identificador));
        }
        return resumenDeEstrategias;
    }

    @Override
    public void eliminarDocumento(String identificador) throws EliminacionException {
        ResumenDeEstrategias resumenDeEstrategias = this.buscaPorId(identificador);
        if (resumenDeEstrategias == null) {
            throw new NoEncontradoExcepcion("respuesta", "No se encuentra el registro a eliminar");
        }
        this.estrategiaRepository.delete(resumenDeEstrategias);
    }

    @Override
    public Optional<ResumenDeEstrategias> buscarporUsuario(String usuario) {
        Optional<ResumenDeEstrategias> resumenDeEstrategias = this.estrategiaRepository.findByUsuario(usuario);
        if (resumenDeEstrategias.isPresent()) {
            return resumenDeEstrategias;
        }
        throw new NoEncontradoExcepcion("resumen", "No se encontro el registro para el usuario:".concat(usuario));
    }

    @Override
    public void eliminarConUsuario(String usuario) {
        Optional<ResumenDeEstrategias>  resumenDeEstrategias = this.buscarporUsuario(usuario);
        resumenDeEstrategias.ifPresent(deEstrategias -> this.estrategiaRepository.delete(deEstrategias));
    }

    // By Erick
    @Transactional
    public void generarEstrategiasContinuidad(ResumenDeEstrategias pojo, boolean bandera) {
        String[] categoria = {"recursosInternos", "serviciosPublicos", "sociosExternos"};

        if(bandera) { // Si actualiza el form 7.1 el 7.2 desaparece y toma los nuevos datos del 7.1
            List<EstrategiasContinuidad> list = this.estrategiasContinuidadRepository.findByUsuario(pojo.getUsuario());
            this.estrategiasContinuidadRepository.deleteAll(list);
        }

        RecursoPrioridad recursoPrioridad = new RecursoPrioridad();
        recursoPrioridad.setRecurso("");
        recursoPrioridad.setQueProcede("");
        recursoPrioridad.setDetalleMedida("");
        recursoPrioridad.setPeriodo("Mediano Plazo");
        recursoPrioridad.setDeparEncargado("");

        HashMap<String, List<RecursoPrioridad>> hashMap = new HashMap<>();

        pojo.getEstrategia().forEach((llave, valor) -> {

            EstrategiasContinuidad estrategiasContinuidad = new EstrategiasContinuidad(); // Se crea otra instancia porque al persistir setea al objeto con el id
            estrategiasContinuidad.setUsuario(pojo.getUsuario());

            valor.forEach(estrategia -> {  // Hubiera sido mejor separar las estrategias en documentos diferentes y no una lista por que no son categorias X_X =(
                estrategiasContinuidad.setActividadPrioritaria(estrategia.getNombreEstrategia());
            });

            hashMap.put(categoria[0], Arrays.asList(recursoPrioridad, recursoPrioridad));
            hashMap.put(categoria[1], Arrays.asList(recursoPrioridad, recursoPrioridad));
            hashMap.put(categoria[2], Arrays.asList(recursoPrioridad, recursoPrioridad));

            estrategiasContinuidad.setCategorias(hashMap);
            this.estrategiasContinuidadRepository.insert(estrategiasContinuidad);

        });
    }
}
