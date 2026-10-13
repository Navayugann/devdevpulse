package com.devpulse.devpulse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProjectRepository projectRepository;

    public DataInitializer(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void run(String... args) {
        if (projectRepository.count() == 0) {
            projectRepository.save(new Project("DevPulse Backend", "Core Spring Boot REST API and database layer", "In Progress"));
            projectRepository.save(new Project("Health API", "Application uptime and health monitoring endpoints", "Completed"));
            projectRepository.save(new Project("Project Dashboard", "Web UI for tracking project metrics and tasks", "Planned"));
        }
    }
}
