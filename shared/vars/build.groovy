#!/usr/bin/env groovy 


def call(){
   // Passos para compilar o projeto
   echo "Build."
   sh "${mvnHome}/bin/mvn compile 
}
