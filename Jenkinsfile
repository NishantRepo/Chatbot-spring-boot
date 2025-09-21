// Jenkinsfile for a Spring Boot application with Docker deployment

pipeline {
    agent any // Or a specific agent label if you have one configured for Docker

    environment {
        // Customize these variables for your project
        APP_NAME = 'my-spring-boot-app' // Name for your Docker image and container
        DOCKER_IMAGE_TAG = "${APP_NAME}:${env.BUILD_NUMBER}" // Tag with Jenkins build number
        DOCKER_IMAGE_LATEST_TAG = "${APP_NAME}:latest" // Always tag with 'latest'
        SPRING_PROFILE = 'dev' // Or 'dev', 'test', etc. if you use profiles
        MAVEN_PARAMS = "-Dspring.profiles.active=${SPRING_PROFILE}" // Maven parameters
        # If using Gradle, uncomment and adjust:
        # GRADLE_PARAMS = "--profile ${SPRING_PROFILE}"
    }

    stages {
        stage('Checkout Source') {
            steps {
                echo "Cloning repository..."
                // Replace with your actual SCM configuration (e.g., Git)
                git branch: 'main', url: 'https://github.com/NishantRepo/Chatbot-spring-boot.git'
                // For a quick test, you can remove the git step and ensure your project is
                // already in the workspace if running locally.
            }
        }

        stage('Build Spring Boot App') {
            steps {
                echo "Building Spring Boot application..."
                // --- For Maven projects ---
                sh "mvn clean package ${MAVEN_PARAMS}"

                // --- For Gradle projects, uncomment the line below and comment out the mvn line ---
                // sh "gradle clean bootJar ${GRADLE_PARAMS}"

                // Verify JAR exists (adjust path if needed, e.g., build/libs/*.jar for Gradle)
                sh "ls target/*.jar"
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image: ${DOCKER_IMAGE_TAG}"
                // The Dockerfile should be in the root of your project
                // The ARG JAR_FILE in Dockerfile points to where the JAR is after the 'package' command
                sh "docker build -t ${DOCKER_IMAGE_TAG} -t ${DOCKER_IMAGE_LATEST_TAG} ."
                sh "docker images | grep ${APP_NAME}" // Verify image was built
            }
        }

        stage('Deploy Docker Container') {
            steps {
                script {
                    echo "Deploying Docker container: ${APP_NAME}"

                    // Stop and remove existing container to ensure a clean deploy
                    // Check if container exists before trying to stop/remove it
                    def existingContainer = sh(script: "docker ps -a --filter 'name=${APP_NAME}' --format '{{.Names}}'", returnStdout: true).trim()

                    if (existingContainer == APP_NAME) {
                        echo "Stopping and removing existing container: ${APP_NAME}"
                        sh "docker stop ${APP_NAME}"
                        sh "docker rm ${APP_NAME}"
                    } else {
                        echo "No existing container named ${APP_NAME} found. Proceeding with deployment."
                    }

                    // Run the new container
                    // -d: detached mode
                    // -p 8080:8080: map container port 8080 to host port 8080
                    // --name: give the container a specific name
                    // -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}: pass Spring profile as environment variable
                    sh "docker run -d -p 8080:8080 --name ${APP_NAME} ${DOCKER_IMAGE_LATEST_TAG}"

                    echo "Deployment complete! Container ${APP_NAME} is running."
                    // Optional: You might want to add a health check here
                    // sh "curl -f http://localhost:8080/actuator/health || (sleep 10 && curl -f http://localhost:8080/actuator/health)"
                }
            }
        }

        // Optional: Push to Docker Registry
        /*
        stage('Push to Docker Registry') {
            steps {
                script {
                    echo "Logging into Docker Hub..."
                    // Replace 'docker-hub-credentials' with your actual Jenkins Credentials ID for Docker Hub
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo \"${DOCKER_PASSWORD}\" | docker login -u \"${DOCKER_USERNAME}\" --password-stdin"
                    }

                    echo "Pushing Docker image to registry..."
                    // Adjust if your registry requires a prefix (e.g., myregistry.com/${DOCKER_USERNAME}/${APP_NAME})
                    sh "docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_TAG}"
                    sh "docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_LATEST_TAG}"
                    sh "docker logout"
                }
            }
        }
        */
    }

    post {
        always {
            cleanWs() // Clean up the workspace
        }
        success {
            echo "Pipeline finished successfully!"
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}