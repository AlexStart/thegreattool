### TODO 

| User story                                             | Tasks                                           | Estimation, MD                 | 
| ------------------------------------------------------ | ----------------------------------------------- | ------------------------------ |
| 1. Separate containers using Docker Compose/Swarm      | > apache2 container                             | 1                              |
|                                                        | > webconsole container                          | 5                              |
|                                                        | > jenkins container                             | 1                              |
|                                                        | > mysql container                               | 1                              |
|                                                        | > mongodb container                             | 1                              |
|                                                        | > composing                                     | 5                              |
|                                                        | > cluster via swarm                             | 5                              |
|                                                        | > testing                                       | 1                              |
|                                                        | >                                               |                                |
| 2. Security and user/team management setup             | > Jenkins auto-config                           | 3                              |
|                                                        | > MySQL auto-config                             | 2                              |
|                                                        | > MongoDB auto-config                           | 2                              |
|                                                        | > Gitlab auto-config                            | 3                              |
|                                                        | > SonarQube auto-config                         | 3                              |
|                                                        | > Nexus auto-config                             | 2                              |
|                                                        | > Deployments security                          | 2                              |
|                                                        | > Apache2 configuration                         | 3                              |
|                                                        | >                                               |                                |
| 3. Integration with GitLab                             | > GitLab Docker container                       | 1                              |
|                                                        | > GitLab provider impl                          | 5                              |
|                                                        | > Jenkins job customization                     | 1                              |
|                                                        | >                                               |                                |
| 4. SonarQube integration                               | > SonarQube Docker container                    | 1                              |
|                                                        | > SonarQube provider impl                       | 7                              |
|                                                        | > Jenkins job customization                     | 2                              |
|                                                        | > Integration with GitLab                       | 2                              |
|                                                        | >                                               |                                |
| 5. Nexus integration                                   | > Nexus Docker container                        | 1                              |
|                                                        | > Nexus provider impl                           | 5                              |
|                                                        | > Jenkins job customization                     | 1                              |
|                                                        | >                                               |                                |
| 6. Minimal deployment automation                       | > pure oracle java8 container                   | 1                              |
|                                                        | > Jenkins pipelines                             | 10                             |
|                                                        | >                                               |                                |
| 7. Code generation                                     | > DTO+Services+Transactions                     | 2                              |
|                                                        | > REST support                                  | 2                              |
|                                                        | > logback+redis+logstash                        | 3                              |
|                                                        | > Unit and integration tests                    | 3                              |
|                                                        | > Minimal UI (SpringFox) and Swagger for API    | 1                              |
| TOTAL                                                  |                                                 | 88                             |

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
