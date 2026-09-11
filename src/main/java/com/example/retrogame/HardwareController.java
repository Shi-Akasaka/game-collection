package com.example.retrogame;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hardwares")
public class HardwareController {

    private final HardwareRepository repository;

    public HardwareController(HardwareRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Hardware> getHardwares() {
    	return repository.findAllByActiveTrueOrderBySortOrderAsc();
    }
}
