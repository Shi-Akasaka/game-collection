package com.example.retrogame;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HardwareRepository extends JpaRepository<Hardware, Long> {

	List<Hardware> findAllByActiveTrueOrderBySortOrderAsc();

    Optional<Hardware> findByName(String name);

}