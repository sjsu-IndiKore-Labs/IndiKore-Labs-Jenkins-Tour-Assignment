pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from Git repository...'
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                echo 'Compiling Java sources...'
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo 'Running JUnit 5 test suite...'
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Package Artifact') {
            steps {
                echo 'Packaging application into executable JAR...'
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
        }

        stage('Smoke Test') {
            steps {
                echo 'Executing smoke test with packaged JAR...'
                script {
                    if (isUnix()) {
                        sh 'java -jar target/indikore-calculator-toolkit-1.0.0.jar --cli'
                    } else {
                        bat 'java -jar target/indikore-calculator-toolkit-1.0.0.jar --cli'
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully! All tests passed.'
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
        }
        failure {
            echo 'Pipeline failed. Check test reports and build logs for details.'
        }
    }
}
