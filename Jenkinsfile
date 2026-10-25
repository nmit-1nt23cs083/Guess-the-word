
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
        stage('Check Docker') {
    steps {
        sh 'docker --version'
        sh 'docker compose version'
    }
}
     stage('Build Docker Image') {
    steps {
        sh 'docker build -t guess-the-word-j:1.0 .'
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
