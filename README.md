# Video-Converter-Service

## Resumo

Sistema experimental para conversão de vídeos em formatos tradicionais para formatos da web. Atualmente o protocolo suportado é HLS, pelo fato de ter maior compatibilidade com a maioria dos navegadores, sendo eles o Safari. O formato MPEG-DASH será incluído em breve após testes de maior fluidez.

## Java, Spring-boot e AWS

API foi desenvolvida em linguagem Java na versão 8, usando o framwork Spring-boot na versão 2.1.7.
Foi utilizada a integração com os serviços da Amazon Web Services, tais como EC2 para implantação do mesmo, AIM para registro de usuários e S3 para storage no bucket;

## Docker e segurança
Além disso foi utilizado Docker para integrado na EC2. Foi criada uma instancia onde o container foi inicializado somente para uso do serviço.
Maiores informações sobre o processo se encontram no Dockerfile anexado ao projeto.
Para armazenamento das chaves foram utilizadas variáveis de ambiente criadas dentro do servidor, passadas para o container por meio de argumentos e mapeadas dentro do serviço.

## Funcionamento

A API dispôe de dois endpoints para conversão de vidos: Um para conversão por meio de upload de arquivo no front-end e outro para conversão por meio de URL, também passada no front-end.
Para maiores informações acessar a documentação do front-end da aplicação.

## Conversão
Ao entrar com o arquivo do tipo mkv, por exemplo, o mesmo é convertido para o protocolo HLS, usando codec de video H264 e codec de audio ACC. O retorno é um arquivo do tipo m3u8.

## Testes

Também foram implementados testes para melhor garantia do funcionamento da API.

## Implementações futuras
Adicionar mais testes e codecs para permitir maior variedade de conversões.
Implementar conversão de videos para o protocolo MPEG-DASH.
