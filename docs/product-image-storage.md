# Armazenamento de imagens de produtos

O backend grava as imagens enviadas em `APP_UPLOAD_DIR` e persiste no MySQL somente a URL `/uploads/<nome-gerado>`. Os uploads aceitos são PNG e JPEG válidos, com até 5 MB; o nome do arquivo enviado pelo navegador não é usado no armazenamento.

Os arquivos `docker-compose.yml` e `backend/docker-compose.yml` montam o volume nomeado `pizzaburguer-uploads` em `/app/uploads`, preservando os arquivos quando o container do backend é recriado.

Não há configuração de Render versionada neste repositório. Para manter os uploads entre deploys no Render, crie um Persistent Disk para o serviço do backend e configure `APP_UPLOAD_DIR` com o caminho de montagem escolhido para esse disco (por exemplo, `/var/data/uploads`). Sem esse disco, o filesystem do serviço pode ser efêmero e a persistência de imagens após deploy não está garantida.
