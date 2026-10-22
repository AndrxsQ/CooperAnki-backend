package com.coopera2075.cooperanki.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "economic_activities")
@Getter
@Setter
@NoArgsConstructor
public class EconomicActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ciiu_id", nullable = false, unique = true, length = 4)
    private String ciiuId;

    @Column(nullable = false)
    private String name;
}
