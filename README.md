# Jenkins shared library: Tutorial `Hands On`

Vamos começar esse tutorial de `Jenkins Shared Library` construindo a infra necessária. Para isso vamos utilizar imagens docker e o [`docker-compose`](https://docs.docker.com/compose/install/) para orquestrar os containers.

O [`docker-compose.yml`](https://github.com/clodonil/jenkins_shared_library/blob/master/docker-compose.yml) que faz a declaração das imagens do `jenkins` e do `gitlab`.


Para começar faça o clone do projeto do git.
```bash
$ git clone https://github.com/clodonil/jenkins_shared_library.git
```

No diretório do projeto, suba os container utilizar o [`docker-compose`](https://docs.docker.com/compose/install/).

```bash
$ docker-compose up
```