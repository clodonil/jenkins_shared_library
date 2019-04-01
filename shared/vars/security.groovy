!/usr/bin/env groovy 


def call(){
   echo "Teste de Seguranca"
   // Coloque aqui os passos para o testUnit Junit
   sh "${mvnHome}/bin/mvn -batch-mode -V -U -e checkstyle:checkstyle pmd:pmd pmd:cpd findbugs:findbugs spotbugs:spotbugs"
}