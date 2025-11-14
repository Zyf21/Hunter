// pipeline {
//   agent any
//
//   environment {
//     IMAGE_NAME = "myapp"
//     COMPOSE_FILE = "docker-compose.yml"
//     GRADLE_CACHE = "/home/gradle/.gradle"
//   }
//
//   stages {
//     stage('Checkout') {
//       steps {
//         checkout scm
//       }
//     }
//
//     stage('Build Jar') {
//         steps {
//             sh 'chmod +x ./gradlew'
//             sh './gradlew clean bootJar -x test'
//         }
//     }
//
//
//     stage('Docker Build') {
//       steps {
//         script {
//           def commitShort = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
//           sh "docker build --cache-from ${IMAGE_NAME}:latest -t ${IMAGE_NAME}:${commitShort} ."
//           sh "docker tag ${IMAGE_NAME}:${commitShort} ${IMAGE_NAME}:latest || true"
//         }
//       }
//     }
//
//     stage('Deploy') {
//       steps {
//         sh "docker-compose -f ${COMPOSE_FILE} up -d --build app"
//       }
//     }
//   }
//
//   post {
//     success {
//       echo "Deploy succeeded"
//     }
//     failure {
//       echo "Deploy failed"
//     }
//   }
// }
pipeline {
  agent any
  environment {
    IMAGE_NAME = "myapp"
    COMPOSE_FILE = "docker-compose.yml"
  }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Build Jar') {
      steps {
        sh 'chmod +x ./gradlew'
        sh './gradlew clean bootJar -x test'
      }
    }
    stage('Docker Build') {
      steps {
        script {
          def commitShort = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
          sh "docker build -t ${IMAGE_NAME}:${commitShort} -t ${IMAGE_NAME}:latest ."
        }
      }
    }
    stage('Deploy') {
      steps {
        sh "docker compose -f ${COMPOSE_FILE} up -d --no-build app"
      }
    }
  }
  post {
    success { echo "Deploy succeeded" }
    failure { echo "Deploy failed" }
  }
}

