package com.coopera2075.cooperanki.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table (name = "projects")
@Getter 
@Setter 
@NoArgsConstructor 
@ToString 
@EqualsAndHashCode (onlyExplicitlyIncluded = true)
public class Project {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Size (min = 2, max = 100, message = "Project name must be between 2 and 100 characters")
    @Column (name = "name", nullable = false)
    private String name;
    
    @Size (max = 255, message = "Project description must not exceed 255 characters")
    @Column (name = "description", nullable = true)
    private String description;

    @ToString.Exclude
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Arrangement> arrangements = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CallProp> callProps = new ArrayList<>();
}
