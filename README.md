# Jenkins shared library: Tutorial `Hands-On`

Vamos partir do ponto que você já conhece o `Jenkins` e já tenha alguns pipelines construídos. Se você nunca fez nenhum pipeline, mais esta estudando a melhor forma de desenvolver, esse tutorial pode contribuir com a estruturação do seu projeto.

Em ambientes com muitos pipelines para projetos diferentes tornam a manutenção e administração das pipelines custosas e complexas. Para esses ambientes como eliminar a repetição de código no desenvolvimento de diferentes pipeline?

Copiar e colar partes de uma pipelines como forma de reutilizar código em diferentes `Jenkins` pode rapidamente se tornar uma dor de cabeça de manutenção.

Então, para evitar isso, você armazena seus "código de stage" em uma biblioteca compartilhada (`shared library`) no `Jenkins`. Você só precisa escrever o código uma vez e em seguida, pode referenciar o mesmo código em todos os seus pipelines.

Dessa forma, melhorias aplicadas em um `stage` são automaticamente replicadas para todos os pipelines que tem esse `stage` referenciado.

Nesse tutorial vamos estudar sobre o `shared library` na prática e para isso vamos precisar de uma infra-estrutura com `jenkins` e `gitlab`. Também vamos precisar de um AppDemo para usar na nossa pipeline.

O tutorial foi estruturado da seguinte forma:

1. Provisionar um infra-estrutura básica para o `Hands-On`;
2. Criar uma pipeline com um template predefinido, mais os stages como `shared library`; e
3. Criar uma pipeline standard, tendo todas as definições no `shared library`. Permitindo uma escalabilidade muito maior que a pipeline tradicional.

## Infra-estrutura

Vamos começar esse tutorial de `Jenkins Shared Library` construindo a infra necessária. Para isso vamos utilizar imagens docker e o [`docker-compose`](https://docs.docker.com/compose/install/) para orquestrar os containers.

O [`docker-compose.yml`](https://github.com/clodonil/jenkins_shared_library/blob/master/docker-compose.yml) que faz a declaração das imagens do `jenkins` e do `gitlab`.

Para começar faça o clone do projeto do git.
```bash
$ git clone https://github.com/clodonil/jenkins_shared_library.git
```
No diretório do projeto, inicie os container utilizar o [`docker-compose`](https://docs.docker.com/compose/install/).

```bash
$ docker-compose up
```

### Configuração do Jenkins e Gitlab

Como as imagens em execução, podemos acessar o `jenkins` pela url `http://localhost:8080` e o `gitab` pela url `http://localhost`.

Na configuração do jenkins, é necessário recuperar o token que está no diretório `/var/jenkins_home/secrets/initialAdminPassword` para seguir a instalação.

Esse processo pode ser feito conectando diretamente no container como mostra a imagem a seguir.

![img1](https://github.com/clodonil/jenkins_shared_library/blob/master/imgs/img1.png)

Também é possível listar os logs do container para obter esse token. Veja qual o processo que fica mais fácil para você.

```bash 
$ docker logs jenkins_shared_library_jenkins_1


*************************************************************

ce83575d55d2418fafc98f86f1151cdb

This may also be found at: /var/jenkins_home/secrets/initialAdminPassword

*************************************************************
```

As outras configurações necessárias é apenas a criação do usuário no `jenkins` e no `gitlab`.

![img1](https://github.com/clodonil/jenkins_shared_library/blob/master/imgs/img2.png)

Agora temos o `jenkins` e o `gitlab` configurado corretamente.

## Template de Pipeline 

Vamos utilizar como base a pipeline abaixo. Ela têm 5 stages.

- **Checkout**: Realiza o clone do código fonte;
- **TestUnit**: Roda o teste unitário;
- **Analysis Sec**: Realiza análise de segurança do código;  
- **Analysis QA**: Realiza análise de qualidade do código;
- **Build**: Compila o código fonte para gerar os artefatos; 
- **Publish**: Publica os artefatos em um repositório;

Vamos escrever a [pipeline](https://github.com/clodonil/jenkins_shared_library/blob/master/pipeline/jenkinsfile.groovy) em `groovy`. 

> Conheça mais sobre [Groovy](http://groovy-lang.org/learn.html)

Em cada stage foi criado as seguintes chamadas `variable`,`checkout`,`testunit`, `security`, `qa`,  `build`e `publish`. Essa chamadas são funções importadas de um branch do git.

Crie no jenkins um projeto (`job`) do tipo pipeline e utilize o código abaixo como template.

```python
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

```

Antes de começar a escrever as funções da pipeline é necessário criar um ambiente para o desenvolvimento, teste e validação antes de colocar em produção.

Estamos falando em escrever código, portanto vamos utilizar o `gitlab` para realizar o controle do código. Como exemplo, criaremos as branch de desenvolvimento e a branch de produção. Primeiramente o código é desenvolvido na branch de desenvolvimento e após os testes, realizamos um `Merge Request` para a branch de produção.

> Qualquer um que tem acesso ao repositório pode enviar o código para ser executado no seu Jenkins, um repositório privado é o melhor solução.

Dessa forma matemos os ambientes controlados e a evolução da pipeline de Jenkins é feita como qualquer outro código.

![branch](https://github.com/clodonil/jenkins_shared_library/blob/master/imgs/branch.png)

Portanto crie no `gitlab` um repositório (criei com o nome shared) e as duas branch. Os códigos de exemplo estão no diretório [shared](https://github.com/clodonil/jenkins_shared_library/tree/master/shared/), envie para a branch de desenvolvimento.


## Shared Library

Com o repositório criado, precisamos adicionar esse repositório ao Jenkins em Gerenciar bibliotecas globais de pipeline do Jenkins,Configure System. Você precisará de acesso administrativo a Jenkins. Como há um possível risco de segurança. 

> `Manage Jenkins » Configure System » Global Pipeline Libraries`

![jenkins](https://github.com/clodonil/jenkins_shared_library/blob/master/imgs/jenkins-shared.png)

A escolha da opção *'Load implicitl'* significa que você não precisa usar a tag `@Library` em seus pipelines para acessar sua biblioteca, mas também significa que sua biblioteca será carregada em cada pipeline, quer você queira ou não. Portanto deixamos essa opção desabilitada, e explicitamente declaramos nas pipelines, conforme abaixo.

```
@Library('shared')_
```

Perceba `_` no final da declaração.

Na opção *'Default version'* define a branch padrão que vai ser utilizado as bibliotecas. Nesse caso estamos utilizando a branch `production`. 

A opção *'Allow default version to be overridden'* é útil, permite especificar uma branch `library "my-shared-library@$BRANCH_NAME"` (ou tag ou outro identificador) diferente dos padrões para que você possa experimentar as alterações em um único local antes de colocar em produção, o que é importante quando qualquer alteração feita na biblioteca tem a chance de afetar todos que a usam.

## Estrutura da Library

O repositório do `shared library` segue o seguinte estrutura:

- **/src/[io|org|com|other tld]/companyname/**: Local para as classes/métodos em `java` e os arquivos devem ter a extensão `.groovy`.
- **/vars**: - Aqui ficam as variáveis globais e funções. Em nosso exemplo de `jenkinsfile` declarativo vamos colocar nossas funções nesse diretório.

* **/resources/**: São para seus arquivos `não-Groovy`, tais como `txt`,`json` ou qualquer outro.

As bibliotecas como exemplos são esses:

- [shared/vars/variable.groovy](https://github.com/clodonil/jenkins_shared_library/blob/master/shared/vars/variable.groovy)
- [shared/vars/checkout.groovy](https://github.com/clodonil/jenkins_shared_library/blob/master/shared/vars/checkout.groovy)
- [shared/vars/testunit.groovy](https://github.com/clodonil/jenkins_shared_library/blob/master/shared/vars/testunit.groovy)
- [shared/vars/security.groovy](https://github.com/clodonil/jenkins_shared_library/blob/master/shared/vars/security.groovy)
- [shared/vars/qa.groovy](https://github.com/clodonil/jenkins_shared_library/blob/master/shared/vars/qa.groovy)
- [shared/vars/build.groovy](https://github.com/clodonil/jenkins_shared_library/blob/master/shared/vars/build.groovy)
- [shared/vars/publish.groovy](https://github.com/clodonil/jenkins_shared_library/blob/master/shared/vars/publish.groovy)


![jenkinsExec](https://github.com/clodonil/jenkins_shared_library/blob/master/imgs/jenkins-exec.png)


## Multi-Tecnologia (Pipeline Standard)

A abordagem até aqui seguiu o modelo tradicional de pipeline, porém nem sempre essa é a melhor escolha. Agora vamos seguir uma nova abordagem, removendo todos os scripts do `Jenkinsfile` para que todos os pipelines do Jenkins estejam em conformidade com um processo específico, que será definido no código-fonte da biblioteca compartilhada.

Por exemplo, se você estiver continuamente testando e entregando aplicativos `Java` e aplicativos `Python`, desejaria ter um "pipeline Java" padrão e um "pipeline Python" padrão com etapas semelhantes.

> O Fluxo de entrada de novas tecnologias é conhecida e o desenvolvimento é simples e estruturado.

No entanto, você pode dar ao desenvolvedor a capacidade de informar que o pipeline deve se comportar de maneira diferente por meio de entradas no `Jenkinsfile`.

Como por exemplo adicionando o seguinte código no seu repositório.

```yaml
pipelinetype: python
testCommand: "pytest test.py"
```

O arquivo de `Jenkinsfile` deve ser o mais simples possível, e único para todas as tecnologias, e tudo que deve ter é uma chamada para o método `stdPipeline`.

Todos os códigos desse módulo estão no diretório [`std-shared`](https://github.com/clodonil/jenkins_shared_library/blob/master/std-shared)

```java
#!/bin/groovy
@Library('std-shared') _
import org.demo.*
new stdPipeline().execute()
```

O método `stdPipeline` utiliza o arquivo `pipeline.yml` para definir a tecnologia que será chamada. E a lib `stdPipeline().execute()` é versionado no git.

```java
// /src/org/demo/stdPipeline.groovy
#!/bin/groovy
package org.demo;

def execute() {
  node {
    stage('Inicializando') {
      checkout scm
      echo 'load file pipeline.yml'
      Yaml parser = new Yaml()
      Map pipelineDefinition = parser.load(new File(pwd() + '/pipeline.yml').text)
    }
    switch(pipelineDefinition.pipelineType) {
      case 'python':
        new pythonPipeline(pipelineDefinition).executePipeline()
      case 'java':
        new javaPipeline(pipelineDefinition).executePipeline()
    }
  }
}
```
Em caso de novas tecnologias, basta adicionar uma nova entrada no `stdPipeline.groovy` e validar no Jenkins de desenvolvimento.

```java
// /src/org/demo/javaPipeline.groovy
#!/usr/bin/groovy
package org.acme;

javaPipeline(pipelineDefinition) {
  pd = pipelineDefinition
}
def executePipeline() {
  node {
      stage('Run Tests') {
        sh pd.testCommand
      }
      stage('Analysis Sec') { 
        // Stage para analise de segurança do código 
        security
      }
      stage('Analysis QA') { 
        // Stage para analise de qualidade do código
        qa
      }
      stage('Build') { 
        // Build do código
        java/build
      }
      stage('Publish') { 
        // Publicar o build em repositório 
        java/publish
      }
   }
}
return this
```
Dessa forma podemos criar pipelines totalmente versionadas.

Referências:

- [shared-library](jenkins-pipeline-global-shared-library-best-practices.html)
- [Ippon](https://blog.ippon.tech/setting-up-a-shared-library-and-seed-job-in-jenkins-part-2/)
- [automatingguy](https://automatingguy.com/2017/12/29/jenkins-pipelines-shared-libraries/)
- [medium](https://medium.com/devopslinks/a-hacky-hackers-guide-to-jenkins-scripted-pipelines-part-4-dd49fcb0d62)
- [Jenkins](https://jenkins.io/doc/book/pipeline/shared-libraries/)
- [Fabric8](https://github.com/fabric8io/fabric8-pipeline-library)
- [aimtheory](http://www.aimtheory.com/jenkins/pipeline/continuous-delivery/2017/12/02/jenkins-pipeline-global-shared-library-best-practices.html)
