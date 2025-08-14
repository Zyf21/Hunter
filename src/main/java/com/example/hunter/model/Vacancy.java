package com.example.hunter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "vacancy",
        uniqueConstraints = @UniqueConstraint(columnNames = "vacancy_id")
)
@Getter
@Setter
@NoArgsConstructor
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Внешний ID вакансии из HH.ru */
    @Column(name = "vacancy_id", nullable = false, unique = true)
    private String vacancyId;

    @Column(nullable = false)
    private String name;

    private String employer;

    private String workType;

    private String schedule;

    @Column(length = 5000)
    private String description;
}
