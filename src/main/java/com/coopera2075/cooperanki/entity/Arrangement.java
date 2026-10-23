// package com.coopera2075.cooperanki.entity;

// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import lombok.EqualsAndHashCode;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.ToString;

// @Entity 
// @Table (name = "arrangements")
// @Getter 
// @Setter 
// @NoArgsConstructor 
// @ToString 
// @EqualsAndHashCode (onlyExplicitlyIncluded = true)
// public class Arrangement {
//     @Id 
//     @GeneratedValue (strategy = GenerationType.IDENTITY)
//     @EqualsAndHashCode.Include
//     private Long id;

//     @ToString.Exclude
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "manager_id", nullable = false)
//     private Manager manager;

//     @ToString.Exclude
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "project_id", nullable = false)
//     private Project project;
// }
