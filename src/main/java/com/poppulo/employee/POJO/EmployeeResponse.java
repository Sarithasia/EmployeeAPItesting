package com.poppulo.employee.POJO;

public class EmployeeResponse {
    private String name;
    private String job;
    private String updatedAt;

    // Getters and setters (or use Lombok annotations for brevity)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
