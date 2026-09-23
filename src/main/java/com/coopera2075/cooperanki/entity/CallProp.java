package com.coopera2075.cooperanki.entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity 
@Table (name = "calls_prop")
@Getter 
@Setter 
@NoArgsConstructor 
@ToString 
@EqualsAndHashCode (onlyExplicitlyIncluded = true)
public class CallProp {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank (message = "Title is required")
    @Size (min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    @Column (name = "title", nullable = false)
    private String title;
    @Column (name = "body", nullable = true)
    private String body;
    @Column (name = "deadline", nullable = true)
    private Date deadline;
    @Column (name = "status", nullable = true)
    private String status;

    @Size (min = 0, message = "Total amount must not be 0")
    @Column (name = "total_amount", nullable = true)
    private double totalAmount;

    @ToString.Exclude
    @OneToMany(mappedBy = "callProp", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Investment> investments = new ArrayList<>();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

}
