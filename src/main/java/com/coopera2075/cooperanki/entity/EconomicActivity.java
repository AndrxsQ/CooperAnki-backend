package com.coopera2075.cooperanki.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "ciiu_id", nullable = false, length = 4)
    private String ciiuId;

    @Column(nullable = false)
    private String name;
}
