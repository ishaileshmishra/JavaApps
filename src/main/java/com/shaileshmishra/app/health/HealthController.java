package com.shaileshmishra.app.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shaileshmishra.app.common.util.UtcTimestamp;
import com.shaileshmishra.app.health.dto.HealthResponseDTO;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<HealthResponseDTO> checkHealth() {
        return ResponseEntity.ok(new HealthResponseDTO("UP", "JavaApps", UtcTimestamp.now()));
    }
}
