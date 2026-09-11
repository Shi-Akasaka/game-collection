package com.example.retrogame;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {

	@Query("""
	        SELECT g FROM Game g
	        WHERE (
	            :keyword = ''
	            OR LOWER(g.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
	            OR LOWER(COALESCE(g.hardware.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	            OR LOWER(COALESCE(g.maker, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	            OR LOWER(COALESCE(g.genre, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	        )
	        AND (:hardwareId IS NULL OR g.hardware.id = :hardwareId)
	        AND (:maker = '' OR g.maker = :maker)
	        AND g.user.id = :userId
	        ORDER BY g.id DESC
	        """)
	
	List<Game> search(
	        @Param("keyword") String keyword,
	        @Param("hardwareId") Long hardwareId,
	        @Param("maker") String maker,
	        @Param("userId") Long userId);

	long countByBoxTrue();
	long countByManualTrue();

	@Query("SELECT COALESCE(SUM(g.price), 0) FROM Game g")
	Long sumPrice();

	@Query("SELECT g.hardware.id, COUNT(g.id) FROM Game g WHERE g.hardware IS NOT NULL GROUP BY g.hardware.id")
	List<Object[]> countGroupByHardware();
}
