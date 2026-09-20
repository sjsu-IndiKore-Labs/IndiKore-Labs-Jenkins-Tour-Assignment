pipeline {
    agent {
        docker {
            image 'maven:3.9.16-eclipse-temurin-17'
    	}    
    }

    environment {
        APP_NAME = 'indikore-calculator-toolkit'
        APP_VERSION = '1.0.0'
        JAR_NAME = 'indikore-calculator-toolkit-1.0.0.jar'
        DEPLOY_DIR = 'deployment'
    }

    options {
        skipStagesAfterUnstable()
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from GitHub...'
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
                echo 'Running JUnit 5 tests...'
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
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        stage('Package Artifact') {
            steps {
                echo "Packaging ${APP_NAME} ${APP_VERSION}..."
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                echo "Deploying ${JAR_NAME} to the staging directory..."
                script {
                    if (isUnix()) {
                        sh '''
                            mkdir -p "$DEPLOY_DIR"
                            cp "target/$JAR_NAME" "$DEPLOY_DIR/"
                        '''
                    } else {
                        bat '''
                            if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
                            copy /Y "target\\%JAR_NAME%" "%DEPLOY_DIR%\\"
                        '''
                    }
                }
            }
        }

        stage('Health Check / Smoke Test') {
            steps {
                echo 'Running the deployed application health check...'
                script {
                    if (isUnix()) {
                        sh 'java -jar "$DEPLOY_DIR/$JAR_NAME" --cli'
                    } else {
                        bat 'java -jar "%DEPLOY_DIR%\\%JAR_NAME%" --cli'
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully! All tests passed.'
            archiveArtifacts artifacts: "target/${env.JAR_NAME}",
                             fingerprint: true,
                             allowEmptyArchive: false
        }

        failure {
            echo 'Pipeline failed. Check the test reports and build logs.'
        }

        always {
            echo "Finished Jenkins build ${env.BUILD_NUMBER}."
        }

        cleanup {
            echo 'Cleaning the Jenkins workspace...'
            deleteDir()
        }
    }
}
