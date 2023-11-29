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
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    git 'https://github.com/AtharvaKeskar2002/docker.git'
                    sh'''
                        mv * docker/studentapp
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

        stage('Push to Docker Hub') {
            steps {
                script {
                      sh''' docker tag frontendcicdstudent atharva262002/frontendcicdstudent
                            docker tag backendcicdstudent atharva262002/backendcicdstudent
                            sh "docker push atharva262002/frontendcicdstudent"
                            sh "docker push atharva262002/backendcicdstudent"
                      '''
                    }
                }

            }
    }
}
