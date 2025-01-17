package edu.uce.seguridad.service.Imp;

import edu.uce.seguridad.exception.EliminacionException;
import edu.uce.seguridad.exception.NoEncontradoExcepcion;
import edu.uce.seguridad.model.PeriodoTolerable;
import edu.uce.seguridad.repository.PeriodoTolerableRepository;
import edu.uce.seguridad.service.service.PeriodoTolerableService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PeriodoTolerableServiceImpl implements PeriodoTolerableService {

    private PeriodoTolerableRepository repository;

    @Override
    public List<PeriodoTolerable> buscarTodos() throws NoEncontradoExcepcion {
        return this.repository.findAll();
    }

    @Override
    public PeriodoTolerable agregar(PeriodoTolerable pojo) throws DataAccessException {
        return repository.insert(pojo);
    }

    @Override
    public PeriodoTolerable actualizar(PeriodoTolerable pojo) throws DataAccessException {
        return this.repository.save(pojo);
    }

    @Override
    public PeriodoTolerable buscaPorId(String identificador) throws NoEncontradoExcepcion {
        return this.repository.findById(identificador).orElse(null);
    }

    @Override
    public void eliminarDocumento(String identificador) throws EliminacionException {
        PeriodoTolerable periodo = this.buscaPorId(identificador);
        if (periodo != null) {
            this.repository.delete(periodo);
        }
    }

    @Override
    public List<PeriodoTolerable> buscarPeriodosPorUsuario(String nombreUsuario) throws NoEncontradoExcepcion {
        return this.repository.findPeriodosByUsuario(nombreUsuario);
    }
}
