package edu.uce.seguridad.repository;

import edu.uce.seguridad.model.ImpactoNegocio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ImpactoRepository extends MongoRepository<ImpactoNegocio, String> {
    @Query("{'usuario.nombreUsuario': ?0}")
    ImpactoNegocio findImpactoByUsuario(String nombreUsuario);
}
