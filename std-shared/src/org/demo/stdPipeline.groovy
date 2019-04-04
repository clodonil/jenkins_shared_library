#!/bin/groovy
package org.demo;

def execute() {

  node {

    stage('Initialize') {
      checkout scm
      echo 'Loading pipeline definition'
      Yaml parser = new Yaml()
      Map pipelineDefinition = parser.load(new File(pwd() + '/pipeline.yml').text)
    }

    switch(pipelineDefinition.pipelineType) {
      case 'python':
        new pythonPipeline(pipelineDefinition).executePipeline()
      case 'nodejs':
        new nodeJSPipeline(pipelineDefinition).executePipeline()
    }

  }

}