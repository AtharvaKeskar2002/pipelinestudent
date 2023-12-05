pipeline {
    agent any
    stages {
        stage('git pull') {
            steps {
                script {
                    sh '''
                    echo "root" | su -c "apt update -y"
                    cd /var/lib/jenkins/workspace
                    rm -rf *
                    '''
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
                        mv /var/lib/jenkins/workspace/pipeline1/target/student.war docker/studentapp
                        cd docker
                        cd proxy
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
