pipeline {
    agent any
    stages {
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
                        cd /home/ubuntu/student
                        sudo apt install maven -y
                        mvn clean package
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    git 'https://github.com/AtharvaKeskar2002/docker.git'
                    sh'''
                        cd /home/ubuntu/student
                        mv * docker/studentapp
                        cd ..
                        cd docker
                        cd proxy
                        docker build -t frontendcicdstudent .
                        cd ..
                        cd studentapp
                        docker build -t backendcicdstudent . 
                    '''
                }
            }
        }

        stage ('Build and push to docker hub'){
            steps{
                script{
                    withDockerRegistry(credentialsId: 'docker', toolName: 'docker') {
                        
                        sh "docker tag frontendcicdstudent atharav262002/frontendcicdstudent:latest"
                        sh "docker tag backendcicdstudent atharav262002/backendcicdstudent:latest"
                        sh "docker push atharav262002/frontendcicdstudent:latest"
                        sh "docker push atharav262002/backendcicdstudent:latest"
                   }
                }
            }
        }
    }
}
