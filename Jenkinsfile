
pipeline {
    agent none

    stages {
        stage('Checkout') {
            agent {
                label 'agent-maven'
            }
            steps {
                checkout scm
                stash name: 'source-code', includes: '**/*', excludes: '.git/**'
            }
        }

        stage('Build and Test') {
            agent {
                label 'agent-maven'
            }
            steps {
                checkout scm
                sh 'mvn clean install'
            }
        }

        stage('Check Docker') {
            agent {
                label 'docker-agent'
            }
            steps {
                unstash 'source-code'
                sh 'docker --version'
                sh 'docker compose version'
            }
        }

        stage('Build Docker Image') {
            agent {
                label 'docker-agent'
            }
            steps {
                sh 'docker build -t guess-the-word-j:1.0 .'
            }
        }

        stage('Deploy') {
            agent {
                label 'docker-agent'
            }
            steps {
                sh 'docker compose up -d --build'
            }
        }
    }

    post {
        success {
            echo 'CI and CD completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
