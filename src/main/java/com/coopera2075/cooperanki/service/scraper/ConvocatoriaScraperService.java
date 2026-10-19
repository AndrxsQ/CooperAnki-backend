// package com.coopera2075.cooperanki.service.scraper;

// import java.util.List;

// import com.coopera2075.cooperanki.dto.ConvocatoriaExtraida;

// // Contrato común de las fuentes externas de convocatorias. Cada fuente (AECID, organismos multilaterales...)
// // es un bean que implementa esta interfaz, así que quien las use puede inyectar List<ConvocatoriaScraperService>
// // y recorrerlas todas sin conocer cada implementación
// public interface ConvocatoriaScraperService {
//     // Nombre corto de la fuente (por ejemplo "AECID"), para los logs y para identificar el origen de los datos
//     String getFuente();

//     // Descarga y analiza las convocatorias publicadas en la fuente. No guarda nada: quien llama decide qué persistir
//     // y comprueba con ConvocatoriaRepository (existsByExternalId, existsByUrlOrigen) si ya estaban registradas.
//     // Lanza ScraperException si la fuente no responde o su estructura ya no es la esperada
//     List<ConvocatoriaExtraida> extraerConvocatorias();
// }
