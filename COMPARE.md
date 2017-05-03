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
| Code Quality Automation                                | -                                   | sonar                          | +- (SonarQube) fabric8-devops/sonarqube     |
| Deployment Automation                                  | -                                   | minimal only tomcat            | Testing, Staging, Production                |
| Artifacts Repository Integration                       | -                                   | Nexus                          | Nexus, Artifactory                          |
| Continuous Delivery                                    | -                                   | via jenkins pipeline           | via jenkins pipeline                        |
| Security Management                                    | -                                   | workspaces plugin              | -                                           |
| Project Management                                     | -                                   | -                              | Taiga                                       |
| Cloud integration and container replication            | Dockerfile (full image)             | docker, aws                    | Kubernetes cluster over Docker, AWS, GCE    |
| Logging automation and management                      | -                                   | logstash and elasic            | Elasticsearch + Logstash + Kibana           |
| Monitoring, metrics visualization                      | -                                   | kibana                         | Prometheus + Grafana                        |
| Issue Tracking Systems integration                     | -                                   | -                              | +-                                          |
| Project Lifecycle CRUD                                 | C, R                                | C,R                            | C, R, D                                     |
| VCS + CI                                               | -                                   | +                              | +                                           |
| VCS + Code Review                                      | -                                   | gerrit                         | Gerrit                                      |
| VCS + Issue Tracking System                            | -                                   | -                              | Taiga                                       |




Accenture полностью сделана на Jenkins. Поэтому это смесь конфигурации Jenkins, groovy, bash, docker. Существующие pipeline работают, но если нужно что-то поменять,
конфигурационно это не сделаешь, придется менять код. Т.е получается очень универсально, но и пользоватся этим сможет только подготовленный человек.

Fabric8 – платформа «для всего». Использует Kubernetes который использует Docker-контейнеры с custom-ными image-ами от fabric8. Порог входа туда достаточно большой, 
надо быть хорогим DevOps-ом. Отличается нестабильностью (последний релиз к примеру нерабочий), некоторые плагины (как Тайга или Gerrit) – не ставятся. 
Работает ооочень медленно (на VirtualBox-e на 6GB). Периодически перестартовывает контейнеры, по своему желанию. Также как и Accenture использует jenkins pipelines, 
но более развернуто применяет их для CI/CD. Напихали туда все что можно. В связи с этим системы между собой кое-как сынтегрированы, но бывает, что интеграции отваливаются. 
Активно еще разрабатывается и пока сыровата на мой взгляд. И сложновата. Для работы с ней нужно уметь разбираться в ее исходниках – впрочем, также как и в Accenture.