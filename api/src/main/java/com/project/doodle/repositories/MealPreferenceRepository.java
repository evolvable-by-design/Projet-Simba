package com.project.doodle.repositories;

import com.project.doodle.models.MealPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealPreferenceRepository extends JpaRepository<MealPreference, Long> {
}
