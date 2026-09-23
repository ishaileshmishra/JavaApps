package com.shaileshmishra.app.health.dto;

public class HealthResponseDTO {

    private String status;
    private String service;
    private String timestamp;

    public HealthResponseDTO() {
    }

    public HealthResponseDTO(String status, String service, String timestamp) {
        this.status = status;
        this.service = service;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
