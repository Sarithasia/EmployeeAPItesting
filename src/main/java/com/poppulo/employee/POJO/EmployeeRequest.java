package com.poppulo.employee.POJO;

public class EmployeeRequest {
	private String name;
	private String job;

	public EmployeeRequest(String name, String job) {
		this.name = name;
		this.job = job;
	}

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
}
