pipeline {
    agent any
    stages {
        stage('delete current workspace'){
            steps{
                script{
                    cleanWs()
                }
            }

        }
        stage('git pull') {
            steps {
                script {
                    git 'https://github.com/AtharvaKeskar2002/student.git'
                }
            }
        }

        stage('Build Artifact') {
            steps {
                script {
                    sh '''
                  mvn clean package -f pom.xml
                  cd target && mv studentapp-2.2-SNAPSHOT.war student.war
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    git 'https://github.com/AtharvaKeskar2002/docker.git'
                    sh'''
                        cd ..
                        mv /var/lib/jenkins/workspace/pipeline1/target/student.war /var/lib/jenkins/workspace/pipeline1/docker/studetnapp
                        cd pipeline1/docker
                        cd proxy
                        docker build -t frontendcicdstudent .
                        cd ..
                        cd studetnapp
                        docker build -t backendcicdstudent . 
                    '''
                }
            }
        }

        stage ('Build and push to docker hub'){
            steps{
                script{
                   withDockerRegistry(credentialsId: 'docker') {
                    sh '''
                    docker tag backendcicdstudent atharva262002/backendcicdstudent:latest
                    docker tag frontendcicdstudent atharva262002/frontendcicdstudent:latest
                    docker push atharva262002/backendcicdstudent:latest
                    docker push atharva262002/frontendcicdstudent:latest
                    '''
                    }
                }
            }
        }
    }
}

