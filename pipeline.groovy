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
                        sudo chmod -R 777 /home/ubuntu
                        sudo cd /home/ubuntu/student
                        sudo apt install maven -y
                        sudo mvn clean package
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    git 'https://github.com/AtharvaKeskar2002/docker.git'
                    sh'''
                        sudo cd /home/ubuntu/student
                        sudo mv * docker/studentapp
                        sudo cd ..
                        sudo cd docker
                        sudo cd proxy
                        sudo docker build -t frontendcicdstudent .
                        sudo cd ..
                        sudo cd studentapp
                        sudo docker build -t backendcicdstudent . 
                    '''
                }
            }
        }

        stage ('Build and push to docker hub'){
            steps{
                script{
                    withDockerRegistry(credentialsId: 'docker', toolName: 'docker') {
                        
                        sh "sudo docker tag frontendcicdstudent atharav262002/frontendcicdstudent:latest"
                        sh "sudo docker tag backendcicdstudent atharav262002/backendcicdstudent:latest"
                        sh "sudo docker push atharav262002/frontendcicdstudent:latest"
                        sh "sudo docker push atharav262002/backendcicdstudent:latest"
                   }
                }
            }
        }
    }
}
