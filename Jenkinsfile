pipeline {
    agent any

    environment {
        IMAGE_NAME = "spring-boot-sso-keycloak"
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout') {
            steps {
                git 'https://github.com/NishantRepo/Chatbot-spring-boot.git'
            }
        }

        stage('Build') {
            steps {
                // Example for Java Spring Boot using Maven
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t $IMAGE_NAME:$IMAGE_TAG .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                // Optional: push to Docker Hub or private registry
                // docker login -u $DOCKER_USER -p $DOCKER_PASS
                // docker tag $IMAGE_NAME:$IMAGE_TAG yourdockerhub/$IMAGE_NAME:$IMAGE_TAG
                // docker push yourdockerhub/$IMAGE_NAME:$IMAGE_TAG
                echo 'Skipping push (optional)'
            }
        }

        stage('Deploy Docker Container') {
            steps {
                // Stop old container if exists
                sh "docker stop $IMAGE_NAME || true"
                sh "docker rm $IMAGE_NAME || true"

                // Run new container
                sh "docker run -d --name $IMAGE_NAME -p 9091:9091 $IMAGE_NAME:$IMAGE_TAG"
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished'
        }
    }
}
