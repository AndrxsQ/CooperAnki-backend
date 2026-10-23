package com.coopera2075.cooperanki.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "economic_units",
        indexes = {
                @Index(name = "idx_economic_units_department_id", columnList = "department_id"),
                @Index(name = "idx_economic_units_municipality_id", columnList = "municipality_id"),
                @Index(name = "idx_economic_units_asociation_id", columnList = "asociation_id"),
                @Index(name = "idx_economic_units_node_id", columnList = "node_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class EconomicUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "doc_number", unique = true)
    private String docNumber;

    private String name;

    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipality_id", nullable = false)
    private Municipality municipality;

    private String address;

    @Column(name = "tel_number")
    private Long telNumber;

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asociation_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "node_id", nullable = false)
    private Node node;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "unit_activities",
            joinColumns = @JoinColumn(name = "unit_id", referencedColumnName = "doc_number"),
            inverseJoinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "ciiu_id")
    )
    @OrderBy("ciiuId ASC")
    private Set<EconomicActivity> activities = new LinkedHashSet<>();
}
