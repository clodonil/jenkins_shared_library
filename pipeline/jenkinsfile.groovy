@Library('shared')_
pipeline {
  agent any
  environment {
      branch = 'master'
      scmUrl = 'ssh://git@myScmServer.com/repos/myRepo.git'
      serverPort = '8080'
      developmentServer = 'dev-myproject.mycompany.com'
      stagingServer = 'staging-myproject.mycompany.com'
      productionServer = 'production-myproject.mycompany.com'
  }
 
  stages {
    stage('Checkout') { 
      steps {
          git url: 'http://gitlab/root/appdemo.git'

            sh "ls -lat"
           
      }
    }  
    stage('Build') { 
      steps {
        sh 'echo oi'
      }
    }
    stage('FindBug') { 
      steps {
        sh 'ls'
      }
    }
    stage('SonarQube') { 
      steps {
        sh 'echo ola sonar' 
      }
    }
    stage('Deploy') { 
      steps {
        sh 'echo deploy' 
      }
    }
  }
}