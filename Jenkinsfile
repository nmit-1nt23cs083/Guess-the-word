
pipeline {
    agent {
        label 'agent-maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean install'
            }
        }
    }

    post {
        success {
            echo 'CI completed successfully! Build and tests passed.'
        }
        failure {
            echo 'CI failed. Check the console output.'
        }
    }
}
