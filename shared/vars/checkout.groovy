!/usr/bin/env groovy 


def call(){
   echo "Checkout"
   git url: 'http://gitlab/root/appdemo.git'

}