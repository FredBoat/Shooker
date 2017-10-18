package com.fredboat.shooker;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("shooker")
@Controller
public class Config {

    private final List<Project> sentry = new ArrayList<>();

    public List<Project> getSentry() {
        return sentry;
    }

    public static class Project {

        private String projectName;
        private String webhook;

        public Project() {
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getWebhook() {
            return webhook;
        }

        public void setWebhook(String webhook) {
            this.webhook = webhook;
        }

        @Override
        public String toString() {
            return "Project{" +
                    "projectName='" + projectName + '\'' +
                    ", webhook='" + webhook + '\'' +
                    '}';
        }
    }
}
