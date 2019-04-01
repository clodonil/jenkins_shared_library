@Library('shared')_
pipeline {
  agent any
  environment {
    //Variaveis
    variable
  }
  stages {
    stage('Checkout') { 
      steps {
          // Stage de clone do projeto
          checkout
      }
    }  
    stage('TestUnit') { 
      steps {
        // Stage para executar test unit 
        testunit
      }
    }

    stage('Analysis Sec') { 
      steps {
        // Stage para analise de segurança do código 
        security
      }
    }
    stage('Analysis QA') { 
      steps {
        // Stage para analise de qualidade do código
        qa
      }
    }
    stage('Build') { 
      steps {
        // Build do código
        build
      }
    }
    stage('Publish') { 
      steps {
        // Publicar o build em repositório 
        publish
      }
    }
  }
}
