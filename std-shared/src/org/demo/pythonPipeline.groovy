// /src/org/acme/pythonPipeline.groovy
#!/usr/bin/groovy
package org.acme;

pythonPipeline(pipelineDefinition) {
  // Create a globally accessible variable that makes
  // the YAML pipeline definition available to all scripts
  pd = pipelineDefinition
}

def executePipeline() {
  node {
    if (runTests) {
      stage('Run Tests') {
        sh pd.testCommand
      }
    }

    if (deployUponTestSuccess) {
      stage('Deploy') {
        sh "path/to/a/deploy/bash/script.sh ${pd.deploymentEnvironment}"
      }
    }
  }
}

return this