pipeline {
    agent any
    stages {
        stage('Clean') {
            steps {
                sh './gradlew clean'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('Tar') {
            steps {
                sh 'tar -czf simple-password-manager.tar.gz client/web server/build/distributions'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'simple-password-manager.tar.gz', fingerprint: true
        }
    }
}
