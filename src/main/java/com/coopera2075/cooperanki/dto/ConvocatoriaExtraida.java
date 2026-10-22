// package com.coopera2075.cooperanki.dto;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.List;

// import com.coopera2075.cooperanki.entity.ConvocatoriaEntity;
// import com.coopera2075.cooperanki.entity.EstadoConvocatoria;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// // Convocatoria tal como la devuelve un ConvocatoriaScraperService, antes de guardarla.
// // Tiene los mismos campos que ConvocatoriaEntity salvo el id
// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class ConvocatoriaExtraida {
//     private String titulo;
//     private String organismoDonante;
//     private String descripcion;
//     private BigDecimal montoMaximo;
//     private String moneda;
//     private LocalDate fechaCierre;
//     private String urlOrigen;

//     @Builder.Default
//     private List<Integer> sectoresOds = new ArrayList<>();

//     private EstadoConvocatoria estado;
//     private String externalId;

//     public ConvocatoriaEntity toEntity() {
//         ConvocatoriaEntity convocatoria = new ConvocatoriaEntity();
//         convocatoria.setTitulo(titulo);
//         convocatoria.setOrganismoDonante(organismoDonante);
//         convocatoria.setDescripcion(descripcion);
//         convocatoria.setMontoMaximo(montoMaximo);
//         convocatoria.setMoneda(moneda);
//         convocatoria.setFechaCierre(fechaCierre);
//         convocatoria.setUrlOrigen(urlOrigen);
//         convocatoria.setSectoresOds(sectoresOds == null ? new ArrayList<>() : new ArrayList<>(sectoresOds));
//         // Sin estado se conserva el valor por defecto de la entidad (ABIERTA)
//         if (estado != null) {
//             convocatoria.setEstado(estado);
//         }
//         convocatoria.setExternalId(externalId);
//         return convocatoria;
//     }
// }
