
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
        stage('Deploy') {
    steps {
        sh 'docker compose up -d --build'
    }
}
    }

    post {
        success {
            echo 'Build and tests passed!'
        }
        failure {
            echo 'Build failed. Check the console output.'
        }
    }
}
