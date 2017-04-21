### RAD tools comparison

| Engineering practice                                   | [SaM Java Cloud]                    | [Accenture DevOps Platform]    | [fabric8]                                   |
| ------------------------------------------------------ | ----------------------------------- | ------------------------------ | ------------------------------------------- |
| Version Control Systems                                | Git (file://)), Git (git://)        | git                            | Git (Gogs)                                  |
| Source Code Generation                                 | Spring Boot (via Spring Initializr) | -                              | Various (20+)                               |
| Build Automation                                       | Maven, Gradle                       | maven                          | Maven                                       |
| Continuous integration                                 | Jenkins                             | Jenkins                        | Jenkins                                     |
| Databases (data management)                            | MySQL, MongoDB                      | -                              | -                                           |
| Code Review Systems                                    | -                                   | gerrit                         | Gerrit                                      |
| Testing (TDD, ATDD, BDD, Unittests, Autotests)         | -                                   | unit, automation, performance  | unit, integration, arquillian, chaos monkey |
| Refactoring (automation)                               | -                                   | -                              | -                                           |
| Code Quality Automation                                | -                                   | sonar                          |                                             |
| Deployment Automation                                  | -                                   | minimal only tomcat            |                                             |
| Artifacts Repository Integration                       | -                                   | TODO                           | Nexus, Artifactory                          |
| Continuous Delivery                                    | -                                   | via jenkins pipeline           |                                             |
| Security Management                                    | -                                   | workspaces plugin              |                                             |
| Project Management                                     | -                                   | -                              |                                             |
| Cloud integration and container replication            | Dockerfile (full image)             | docker, aws                    |                                             |
| Logging automation and management                      | -                                   | logstash and elasic            |Elasticsearch + Kibana                       |
| Monitoring, metrics visualization                      | -                                   | kibana                         |Prometheus + Grafana                         |
| Issue Tracking Systems integration                     | -                                   | -                              |                                             |
| Project Lifecycle CRUD                                 | C, R                                | R                              | C, R, D                                     |
| VCS + CI                                               | -                                   | +                              |                                             |
| VCS + Code Review                                      | -                                   | gerrit                         |                                             |
| VCS + Issue Tracking System                            | -                                   | -                              |                                             |





