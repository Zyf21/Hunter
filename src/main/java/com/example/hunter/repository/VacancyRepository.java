package com.example.hunter.repository;

import com.example.hunter.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    /**
     * Очень быстрый индексный запрос: SELECT 1 FROM vacancy WHERE vacancy_id = ? LIMIT 1
     */
    boolean existsByVacancyId(String vacancyId);

    /**
     * Если нужно получить всю сущность по внешнему ID
     */
    Optional<Vacancy> findByVacancyId(String vacancyId);
}