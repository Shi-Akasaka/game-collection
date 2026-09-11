package com.example.retrogame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameRepository repository;
    private final HardwareRepository hardwareRepository;
    private final UserRepository userRepository;

    public GameController(
            GameRepository repository,
            HardwareRepository hardwareRepository,
            UserRepository userRepository) {

        this.repository = repository;
        this.hardwareRepository = hardwareRepository;
        this.userRepository = userRepository;
    }

   @GetMapping
    public List<Game> getGames(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) Long hardwareId,
            @RequestParam(required = false, defaultValue = "") String maker,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return List.of();
        }

        return repository.search(
                keyword.trim(),
                hardwareId,
                maker,
                userId);
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        long totalGames = repository.count();
        long boxCount = repository.countByBoxTrue();
        long manualCount = repository.countByManualTrue();
        long totalPrice = repository.sumPrice() == null ? 0L : repository.sumPrice();

        Map<Long, Long> countByHardwareId = new HashMap<>();
        for (Object[] row : repository.countGroupByHardware()) {
            countByHardwareId.put((Long) row[0], ((Number) row[1]).longValue());
        }

        List<Map<String, Object>> hardwareCounts = new ArrayList<>();
        for (Hardware hardware : hardwareRepository.findAllByActiveTrueOrderBySortOrderAsc()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", hardware.getId());
            item.put("name", hardware.getName());
            item.put("count", countByHardwareId.getOrDefault(hardware.getId(), 0L));
            hardwareCounts.add(item);
        }

        hardwareCounts.sort(Comparator
                .comparing((Map<String, Object> item) -> ((Number) item.get("count")).longValue())
                .reversed()
                .thenComparing(item -> String.valueOf(item.get("name"))));

        Map<String, Object> result = new HashMap<>();
        result.put("totalGames", totalGames);
        result.put("totalPrice", totalPrice);
        result.put("boxCount", boxCount);
        result.put("manualCount", manualCount);
        result.put("boxRate", totalGames == 0 ? 0 : Math.round(boxCount * 1000.0 / totalGames) / 10.0);
        result.put("manualRate", totalGames == 0 ? 0 : Math.round(manualCount * 1000.0 / totalGames) / 10.0);
        result.put("hardwareCounts", hardwareCounts);
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Game> createGame(
            @RequestParam String title,
            @RequestParam(required = false) Long hardwareId,
            @RequestParam(required = false) String maker,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String purchaseDate,
            @RequestParam(defaultValue = "false") boolean box,
            @RequestParam(defaultValue = "false") boolean manual,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) MultipartFile image,
            HttpSession session) {

        Game game = new Game();
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        game.setUser(user);

        game.setTitle(title);
        game.setHardware(findHardware(hardwareId));
        game.setMaker(maker);
        game.setGenre(genre);
        game.setPrice(price);
        game.setBox(box);
        game.setManual(manual);
        game.setRemarks(remarks);

        if (releaseDate != null && !releaseDate.isBlank()) {
            game.setReleaseDate(java.time.LocalDate.parse(releaseDate));
        }

        if (purchaseDate != null && !purchaseDate.isBlank()) {
            game.setPurchaseDate(java.time.LocalDate.parse(purchaseDate));
        }

        try {
            if (image != null && !image.isEmpty()) {
                game.setImageData(image.getBytes());
                game.setImageContentType(image.getContentType());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(repository.save(game));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Game> updateGame(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) Long hardwareId,
            @RequestParam(required = false) String maker,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String purchaseDate,
            @RequestParam(defaultValue = "false") boolean box,
            @RequestParam(defaultValue = "false") boolean manual,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) MultipartFile image) {

        return repository.findById(id).map(game -> {
            game.setTitle(title);
            game.setHardware(findHardware(hardwareId));
            game.setMaker(maker);
            game.setGenre(genre);
            game.setPrice(price);
            game.setBox(box);
            game.setManual(manual);
            game.setRemarks(remarks);

            game.setReleaseDate(
                    releaseDate == null || releaseDate.isBlank()
                            ? null : java.time.LocalDate.parse(releaseDate));

            game.setPurchaseDate(
                    purchaseDate == null || purchaseDate.isBlank()
                            ? null : java.time.LocalDate.parse(purchaseDate));

            try {
                if (image != null && !image.isEmpty()) {
                    game.setImageData(image.getBytes());
                    game.setImageContentType(image.getContentType());
                }
            } catch (Exception e) {
                return null;
            }

            return repository.save(game);
        }).map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        return repository.findById(id)
                .filter(game -> game.getImageData() != null)
                .map(game -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(
                                game.getImageContentType() != null
                                        ? game.getImageContentType()
                                        : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                        .body(game.getImageData()))
                .orElse(ResponseEntity.notFound().build());
    }

    private Hardware findHardware(Long hardwareId) {
        if (hardwareId == null) {
            return null;
        }
        return hardwareRepository.findById(hardwareId).orElse(null);
    }
}
