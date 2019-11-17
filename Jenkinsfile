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
        stage('Minify') {
            steps {
                sh './gradlew minifyJs -b minimize.gradle'
            }
        }
        stage('Zip') {
            steps {
                sh './gradlew zipDist'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'releases/*.zip', fingerprint: true
        }
    }
}
