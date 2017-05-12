### TODO 

| User story                                             | Tasks                               | Estimation, MD                 | 
| ------------------------------------------------------ | ----------------------------------- | ------------------------------ |
| 1. Separate containers using Docker Compose/Swarm      | > apache2 container                 |                                |
|                                                        | > webconsole container              |                                |
|                                                        | > jenkins container                 |                                |
|                                                        | > mysql container                   |                                |
|                                                        | > mongodb container                 |                                |
|                                                        | > composing                         |                                |
|                                                        | > cluster via swarm                 |                                |
|                                                        | > testing                           |                                |
|                                                        | >                                   |                                |
| 2. Security and user/team management setup             | > Jenkins auto-config               | 3                              |
|                                                        | > MySQL auto-config                 | 2                              |
|                                                        | > MongoDB auto-config               | 2                              |
|                                                        | > Gitlab auto-config                | ?                              |
|                                                        | > SonarQube auto-config             |                                |
|                                                        | > Nexus auto-config                 |                                |
|                                                        | > Deployments security              | 2                              |
|                                                        | > Apache2 configuration             | 3                              |
|                                                        | >                                   |                                |
| 3. Integration with GitLab                             | > GitLab Docker container           |                                |
|                                                        | > GitLab provider impl              |                                |
|                                                        | > Jenkins job customization         |                                |
|                                                        | >                                   |                                |
| 4. SonarQube integration                               | > SonarQube Docker container        |                                |
|                                                        | > SonarQube provider impl           |                                |
|                                                        | > Jenkins job customization         |                                |
|                                                        | >                                   |                                |
| 5. Nexus integration                                   | > Nexus Docker container            |                                |
|                                                        | > Nexus provider impl               |                                |
|                                                        | > Jenkins job customization         |                                |
|                                                        | >                                   |                                |
| 6. Minimal deployment automation                       | > pure oracle java8 container       |                                |
|                                                        | > Jenkins job customization         |                                |
|                                                        | >                                   |                                |
| 7. Code generation                                     | > DTO+Services+Transactions         |                                |
|                                                        | > REST support                      |                                |
|                                                        | > logback+redis+logstash            |                                |
|                                                        | > Unit and integration tests        |                                |
|                                                        | > Minimal UI and Swagger for API    |                                |

# Minimal viable product description:
SaM Java Cloud will be a platform, which consists of at least 9 following linked Docker containers:
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

Thus, GitLab playes 4 roles: code repository, code review system, issue tracking system and PM tool.

Release management is done by means of Jenkins and Nexus.
