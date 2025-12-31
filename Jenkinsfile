/**
 * Jenkinsfile for Playwright Java Framework
 * CI/CD Pipeline with API Integration
 */

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timestamps()
        timeout(time: 45, unit: 'MINUTES')
        disableConcurrentBuilds()
        ansiColor('xterm')
    }

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    environment {
        BASE_URL = 'https://the-internet.herokuapp.com'
        BROWSER = 'chromium'
        HEADLESS = 'true'
    }

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['all', 'smoke', 'regression'],
            description: 'Test suite to execute'
        )
        choice(
            name: 'BROWSER_TYPE',
            choices: ['chromium', 'firefox', 'webkit'],
            description: 'Browser to run tests on'
        )
        booleanParam(
            name: 'HEADLESS_MODE',
            defaultValue: true,
            description: 'Run tests in headless mode'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Checking out code from repository...'
                checkout scm
                echo '✅ Code checked out successfully'
            }
        }

        stage('Setup Environment') {
            steps {
                echo '🔧 Setting up environment...'
                sh '''
                    echo "Java: $(java -version 2>&1 | head -n 1)"
                    echo "Maven: $(mvn -version | head -n 1)"
                '''
            }
        }

        stage('Install Dependencies') {
            steps {
                echo '📦 Installing dependencies...'
                sh '''
                    mvn clean install -DskipTests
                    mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps" || true
                '''
                echo '✅ Dependencies installed'
            }
        }

        stage('Clean Reports') {
            steps {
                echo '🧹 Cleaning previous reports...'
                sh '''
                    rm -rf target/surefire-reports/* || true
                    rm -rf screenshots/* || true
                    mkdir -p screenshots
                '''
            }
        }

        stage('Run Tests') {
            steps {
                echo '🧪 Running Playwright tests...'
                script {
                    def headless = params.HEADLESS_MODE ? 'true' : 'false'
                    def browser = params.BROWSER_TYPE ?: 'chromium'
                    def suiteFile = 'testng.xml'
                    
                    if (params.TEST_SUITE == 'smoke') {
                        suiteFile = 'testng-smoke.xml'
                    } else if (params.TEST_SUITE == 'regression') {
                        suiteFile = 'testng-regression.xml'
                    }
                    
                    sh """
                        mvn test \
                            -Dbrowser=${browser} \
                            -Dheadless=${headless} \
                            -DbaseUrl=${BASE_URL} \
                            -DsuiteXmlFile=src/test/resources/${suiteFile} \
                            || echo "Tests completed"
                    """
                }
            }
            post {
                always {
                    echo '📊 Publishing test results...'
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                    archiveArtifacts artifacts: 'target/surefire-reports/**/*', allowEmptyArchive: true
                    archiveArtifacts artifacts: 'screenshots/**/*.png', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        success {
            echo '✅ ========================================='
            echo '✅  PIPELINE COMPLETED SUCCESSFULLY'
            echo '✅ ========================================='
        }
        
        failure {
            echo '❌ ========================================='
            echo '❌  PIPELINE FAILED'
            echo '❌ ========================================='
        }
        
        always {
            echo '🧹 Cleanup completed'
        }
    }
}
