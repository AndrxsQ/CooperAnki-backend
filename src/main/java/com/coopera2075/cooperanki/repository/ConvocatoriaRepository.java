package com.coopera2075.cooperanki.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coopera2075.cooperanki.entity.ConvocatoriaEntity;

public interface ConvocatoriaRepository extends JpaRepository<ConvocatoriaEntity, Long> {
    // Para no registrar dos veces la misma convocatoria al importarla desde su fuente de origen
    boolean existsByExternalId(String externalId);

    boolean existsByUrlOrigen(String urlOrigen);
}
