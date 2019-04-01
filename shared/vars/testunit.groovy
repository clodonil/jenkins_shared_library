!/usr/bin/env groovy 


def call(){
   echo "TestUnit"
   // Coloque aqui os passos para o testUnit Junit
   sh "${mvnHome}/bin/mvn --batch-mode -V -U -e clean test -Dsurefire.useFile=false"
   junit testResults: '**/target/surefire-reports/TEST-*.xml'
}