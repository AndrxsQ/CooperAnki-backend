package com.coopera2075.cooperanki.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Convocatoria de financiación publicada por un organismo donante externo. No es lo mismo que CallProp,
// que pertenece a un proyecto y recibe inversiones de los funders. Los índices y restricciones son los de V4.
@Entity
@Table (
    name = "convocatorias",
    uniqueConstraints = @UniqueConstraint (name = "uk_convocatorias_external_id", columnNames = "external_id"),
    indexes = {
        @Index (name = "idx_convocatorias_fecha_cierre", columnList = "fecha_cierre"),
        @Index (name = "idx_convocatorias_organismo_donante", columnList = "organismo_donante"),
        @Index (name = "idx_convocatorias_url_origen", columnList = "url_origen")
    }
)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode (onlyExplicitlyIncluded = true)
public class ConvocatoriaEntity {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank (message = "Title is required")
    @Size (max = 500, message = "Title must not exceed 500 characters")
    @Column (name = "titulo", nullable = false, length = 500)
    private String titulo;

    @NotBlank (message = "Donor organization is required")
    @Size (max = 255, message = "Donor organization must not exceed 255 characters")
    @Column (name = "organismo_donante", nullable = false)
    private String organismoDonante;

    @ToString.Exclude
    @Column (name = "descripcion", nullable = true)
    private String descripcion;

    @Positive (message = "Maximum amount must be greater than 0")
    @Digits (integer = 17, fraction = 2, message = "Maximum amount must have at most 17 integer digits and 2 decimals")
    @Column (name = "monto_maximo", nullable = true, precision = 19, scale = 2)
    private BigDecimal montoMaximo;

    // Código ISO 4217 en mayúsculas (USD, EUR, COP...)
    @Pattern (regexp = "[A-Z]{3}", message = "Currency must be a 3-letter ISO 4217 code in uppercase")
    @Column (name = "moneda", nullable = true, length = 3)
    private String moneda;

    // Vacía en las convocatorias de ventanilla abierta, que no tienen fecha de cierre
    @Column (name = "fecha_cierre", nullable = true)
    private LocalDate fechaCierre;

    @NotBlank (message = "Source URL is required")
    @Size (max = 2048, message = "Source URL must not exceed 2048 characters")
    @URL (regexp = "https?://.*", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Source URL must be a valid http or https URL")
    @Column (name = "url_origen", nullable = false, length = 2048)
    private String urlOrigen;

    // Números de los Objetivos de Desarrollo Sostenible (1 a 17) a los que apunta la convocatoria
    @NotNull (message = "SDG list is required")
    @JdbcTypeCode (SqlTypes.ARRAY)
    @Column (name = "sectores_ods", nullable = false)
    private List<
        @NotNull (message = "SDG numbers must not be null")
        @Range (min = 1, max = 17, message = "SDG numbers must be between 1 and 17")
        Integer> sectoresOds = new ArrayList<>();

    @NotNull (message = "Status is required")
    @Enumerated (EnumType.STRING)
    @Column (name = "estado", nullable = false, length = 20)
    private EstadoConvocatoria estado = EstadoConvocatoria.ABIERTA;

    // Identificador en la fuente de origen. Es único en toda la tabla, así que quien importe de varias fuentes
    // debe evitar colisiones entre ellas (por ejemplo, anteponiendo el nombre de la fuente)
    @Size (max = 255, message = "External id must not exceed 255 characters")
    @Column (name = "external_id", nullable = true)
    private String externalId;

    @AssertTrue (message = "Currency is required when a maximum amount is given")
    public boolean isMonedaInformadaSiHayMonto() {
        return montoMaximo == null || moneda != null;
    }
}
