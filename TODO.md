### TODO 

| User story                                             | Tasks                               | Estimation, MD                 | 
| ------------------------------------------------------ | ----------------------------------- | ------------------------------ |
| 1. Separate containers using Docker Compose/Swarm      |                                     |                                |
| 2. Security and user/team management setup             |                                     |                                |
| 3. Integration with GitLab                             |                                     |                                |
| 4. SonarQube integration                               |                                     |                                |
| 5. Nexus integration                                   |                                     |                                |
| 6. Minimal deployment automation                       |                                     |                                |
| 7. Logging automation and management                   |                                     |                                |
| 8. Code generation                                     |                                     |                                |


# Minimal viable product description:
SaM Java Cloud will be a platform, which consists of at least 8 following linked Docker containers:
- apache2 (proxy)
- webconsole(central mgmt dashboard)
- gitlab
- jenkins
- mysql
- mongodb
- sonarqube
- nexus
- deployments (stage)

When a user creates a project, it is possibile to link it to CI (Jenkins), code repository (Gitlab), and a database. Also code quality (SonarQube) and artifactory (Nexus) 
automatically link to the project.

Development lifecycle:
Code change (Gitlab) -> Build is triggered with tests (Jenkins) -> Code Review (Gitlab) -> Code checks (SonarQube) -> Deployment on stage (Jenkins) -> Issue mgmt (Gitlab)

Thus, GitLab playes 3 roles: code repository, code review system and issue tracking system

Release management is done by means of Jenkins and Nexus.
