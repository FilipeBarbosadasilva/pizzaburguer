<!-- <!-- # Plano do Projeto — Landing Page App Pizzaria

## Objetivo
Criar uma landing page para divulgar e operar um app de pizzaria, com no máximo 6 páginas, incluindo uma página administrativa.

## Estrutura do Projeto
Cada página abaixo será um arquivo HTML separado (multi-page), com navegação entre elas — não será uma única página (single page):
- index.html → Home
- cardapio.html → Cardápio
- pedido.html → Pedido / Carrinho
- login.html → Login / Cadastro
- sobre.html → Sobre / Contato
- admin.html → Página Administrativa

## Páginas (máx. 6)

### 1. Home (Landing Principal)
- Banner de destaque com nome da pizzaria e chamada para ação (ex: "Peça agora")
- Apresentação rápida do app/diferenciais (entrega rápida, promoções, pizzas artesanais)
- Botões de acesso: "Ver Cardápio" e "Fazer Pedido"
- Seção de destaques/promoções do dia

### 2. Cardápio
- Lista de pizzas organizadas por categoria (tradicionais, especiais, doces, bebidas)
- Foto, nome, descrição e preço de cada item
- Imagens ilustrativas de cada sabor de pizza (ex: calabresa, mussarela, frango com catupiry, portuguesa, chocolate)
- Imagens das bebidas disponíveis (refrigerantes, sucos, água, cerveja)
- Botão "Adicionar ao Pedido" em cada item

### 3. Pedido / Carrinho
- Resumo dos itens selecionados
- Campo para observações (ex: sem cebola, borda recheada)
- Escolha de forma de entrega (retirada ou entrega)
- Escolha de forma de pagamento (com ícones/imagens: Pix, cartão de crédito, cartão de débito, dinheiro)
- Botão "Finalizar Pedido"

### 4. Login / Cadastro
- Formulário de login (e-mail/senha)
- Opção de cadastro para novos clientes
- Recuperação de senha
- Login social (opcional: Google/Facebook)

### 5. Sobre / Contato
- História da pizzaria
- Endereço, telefone, horário de funcionamento
- Mapa de localização
- Links para redes sociais
- Formulário de contato/dúvidas

### 6. Página Administrativa (Admin)
- Login exclusivo para administrador
- Gerenciamento do cardápio (adicionar, editar, remover pizzas)
- Gerenciamento de pedidos (visualizar status: pendente, em preparo, saiu para entrega, entregue)
- Gerenciamento de clientes cadastrados
- Relatório simples de vendas (quantidade de pedidos, faturamento do dia/mês)

## Funcionalidades JavaScript (Efeitos Dinâmicos)

### Home
- Menu mobile tipo hambúrguer
- Carrossel de promoções/destaques
- Scroll suave entre seções

### Cardápio
- Filtro por categoria (tradicional, especial, doce, bebida)
- Busca por nome da pizza
- Contador de quantidade por item

### Pedido / Carrinho
- Adicionar e remover itens dinamicamente
- Cálculo automático do total do pedido
- Validação dos campos antes de finalizar o pedido

### Login / Cadastro
- Validação de formulário (e-mail, senha)
- Mostrar/ocultar senha
- Mensagens de erro em tempo real

### Admin
- Adicionar, editar e remover pizza sem recarregar a página
- Atualizar status do pedido dinamicamente
- Gráfico simples de vendas (quantidade de pedidos, faturamento)

## Back-end

### Tecnologia
- Linguagem: Java
- Framework sugerido: Spring Boot (facilita API REST, conexão com banco e segurança)
- Banco de dados: MySQL
- Conexão: Spring Data JPA / Hibernate

### Estrutura do Banco de Dados (tabelas principais)
- **usuarios** (id, nome, email, senha, tipo: cliente/admin)
- **pizzas** (id, nome, descricao, categoria, preco, imagem, disponivel)
- **bebidas** (id, nome, preco, imagem, disponivel)
- **pedidos** (id, id_usuario, data, status, forma_pagamento, forma_entrega, total)
- **itens_pedido** (id, id_pedido, id_pizza/bebida, quantidade, observacao)

### Endpoints da API (exemplos)
- **Autenticação**
  - POST /login
  - POST /cadastro
- **Cardápio**
  - GET /pizzas
  - GET /bebidas
- **Pedidos**
  - POST /pedidos (criar pedido)
  - GET /pedidos/{id} (consultar pedido)
  - GET /pedidos/usuario/{id} (histórico do cliente)
- **Admin**
  - POST /admin/pizzas (adicionar pizza)
  - PUT /admin/pizzas/{id} (editar pizza)
  - DELETE /admin/pizzas/{id} (remover pizza)
  - PUT /admin/pedidos/{id}/status (atualizar status do pedido)
  - GET /admin/relatorio (relatório de vendas)

### Segurança
- Senhas com hash (BCrypt)
- Autenticação via sessão ou token (JWT)
- Acesso à área admin restrito por perfil (role-based)

## Observações Gerais
- Design responsivo (mobile first, já que é um app)
- Identidade visual: cores quentes (vermelho, amarelo, marrom) remetendo à pizzaria
- Foco em usabilidade: poucos cliques até finalizar o pedido
- Página Admin deve ter acesso restrito e visual mais simples/funcional (painel de controle) -->


# Plano do Projeto — Landing Page App Pizzaria

## Objetivo
Criar uma landing page para divulgar e operar um app de pizzaria, com no máximo 6 páginas, incluindo uma página administrativa.

## Estrutura do Projeto
Cada página abaixo será um arquivo HTML separado (multi-page), com navegação entre elas — não será uma única página (single page):
- index.html → Home
- cardapio.html → Cardápio
- pedido.html → Pedido / Carrinho
- login.html → Login / Cadastro / Recuperação de senha
- sobre.html → Sobre / Contato
- admin.html → Página Administrativa

## Fluxo do Usuário (NOVO)
1. Visitante acessa o site e clica em **Entrar / Cadastrar** (login.html).
2. Faz o **cadastro** → recebe mensagem de sucesso e é levado ao formulário de **login**.
3. Faz o **login** → o sistema guarda o usuário logado e redireciona para o **Cardápio** (cardapio.html).
4. No Cardápio escolhe os itens e segue para o **Pedido** (pedido.html) e finaliza.
5. Em qualquer página, o **nome do cliente logado aparece no canto esquerdo do header**, com o botão **Sair**.
6. Se esqueceu a senha, usa **"Esqueci minha senha"** no login.html e recebe um link por e-mail para criar uma nova.

Regras de acesso:
- Cardápio e Home podem ser vistos sem login; **adicionar ao pedido e finalizar exigem usuário logado** (sem login, redireciona para login.html).
- admin.html só abre para usuário com tipo ADMIN; qualquer outro é redirecionado para a Home.

## Páginas (máx. 6)

### 1. Home (Landing Principal)
- Banner de destaque com nome da pizzaria e chamada para ação (ex: "Peça agora")
- Apresentação rápida do app/diferenciais (entrega rápida, promoções, pizzas artesanais)
- Botões de acesso: "Ver Cardápio" e "Fazer Pedido"
- Seção de destaques/promoções do dia
- Header com área do usuário (ver "Header com usuário logado")

### 2. Cardápio
- Lista de pizzas organizadas por categoria (tradicionais, especiais, doces, bebidas)
- Foto, nome, descrição e preço de cada item
- Imagens ilustrativas de cada sabor de pizza (ex: calabresa, mussarela, frango com catupiry, portuguesa, chocolate)
- Imagens das bebidas disponíveis (refrigerantes, sucos, água, cerveja)
- Botão "Adicionar ao Pedido" em cada item
- Destino do redirecionamento após o login (NOVO)

### 3. Pedido / Carrinho
- Resumo dos itens selecionados
- Campo para observações (ex: sem cebola, borda recheada)
- Escolha de forma de entrega (retirada ou entrega)
- Escolha de forma de pagamento (com ícones/imagens: Pix, cartão de crédito, cartão de débito, dinheiro)
- Botão "Finalizar Pedido"
- Pedido gravado com o id do usuário logado (NOVO)

### 4. Login / Cadastro / Recuperação de senha
- Formulário de login (e-mail/senha)
- Opção de cadastro para novos clientes
- Recuperação de senha por e-mail (detalhada em "Recuperação de senha por e-mail")
- Login social (opcional: Google/Facebook)
- Esta mesma página tem 4 modos, sem criar novos arquivos: **login**, **cadastro**, **esqueci a senha** e **redefinir senha** (abre quando a URL tem `?token=...`)

### 5. Sobre / Contato
- História da pizzaria
- Endereço, telefone, horário de funcionamento
- Mapa de localização
- Links para redes sociais
- Formulário de contato/dúvidas

### 6. Página Administrativa (Admin)
- Login exclusivo para administrador
- Gerenciamento do cardápio (adicionar, editar, remover pizzas)
- Gerenciamento de pedidos (visualizar status: pendente, em preparo, saiu para entrega, entregue)
- Gerenciamento de clientes cadastrados
- Relatório simples de vendas (quantidade de pedidos, faturamento do dia/mês)

## Header com usuário logado (NOVO)
Header comum a todas as páginas (mesmo trecho de HTML/JS reaproveitado):
- **Canto esquerdo:** nome do cliente logado (ex: "Olá, Felipe") quando houver sessão; quando não houver, mostra o link "Entrar / Cadastrar".
- **Canto direito:** menu de navegação e botão "Sair".
- Se o usuário for ADMIN, aparece também o link para admin.html.
- Em telas pequenas, o nome continua visível no header, ao lado do menu hambúrguer.

## Funcionalidades JavaScript (Efeitos Dinâmicos)

### Home
- Menu mobile tipo hambúrguer
- Carrossel de promoções/destaques
- Scroll suave entre seções

### Cardápio
- Filtro por categoria (tradicional, especial, doce, bebida)
- Busca por nome da pizza
- Contador de quantidade por item

### Pedido / Carrinho
- Adicionar e remover itens dinamicamente
- Cálculo automático do total do pedido
- Validação dos campos antes de finalizar o pedido

### Login / Cadastro
- Validação de formulário (e-mail, senha)
- Mostrar/ocultar senha
- Mensagens de erro em tempo real
- **Sessão do usuário logado (NOVO):**
  - Ao logar com sucesso, o JavaScript salva no navegador (`localStorage`) os dados que a API devolve: id, nome, e-mail e tipo. **A senha nunca é guardada.**
  - Depois de salvar, redireciona para `cardapio.html`.
  - Um script comum (ex: `javascript/sessao.js`), carregado em todas as páginas, lê esses dados, preenche o nome no header e trata o botão "Sair" (apaga os dados e volta para a Home).
  - Páginas protegidas (pedido.html, admin.html) usam o mesmo script para conferir se há usuário logado e o tipo dele antes de mostrar o conteúdo.
- **Cadastro (NOVO):** após cadastrar com sucesso, mostra "Cadastro realizado! Faça seu login" e leva para o modo login.
- **Recuperação de senha (NOVO):**
  - Link "Esqueci minha senha" abre o modo que pede só o e-mail.
  - Mensagem sempre genérica, igual exista ou não o e-mail: "Se o e-mail estiver cadastrado, enviamos um link de recuperação".
  - Com `?token=...` na URL, mostra os campos "Nova senha" e "Confirmar senha", com validação (mínimo de caracteres e senhas iguais).

### Admin
- Adicionar, editar e remover pizza sem recarregar a página
- Atualizar status do pedido dinamicamente
- Gráfico simples de vendas (quantidade de pedidos, faturamento)

## Back-end

### Tecnologia
- Linguagem: Java
- Framework sugerido: Spring Boot (facilita API REST, conexão com banco e segurança)
- Banco de dados: MySQL
- Conexão: Spring Data JPA / Hibernate
- **E-mail (NOVO):** Spring Boot Starter Mail (`spring-boot-starter-mail`) para enviar o link de recuperação

### Estrutura do Banco de Dados (tabelas principais)
- **usuarios** (id, nome, email, senha, tipo: CLIENTE/ADMIN)
- **pizzas** (id, nome, descricao, categoria, preco, imagem, disponivel)
- **bebidas** (id, nome, preco, imagem, disponivel)
- **pedidos** (id, id_usuario, data, status, forma_pagamento, forma_entrega, total)
- **itens_pedido** (id, id_pedido, id_pizza/bebida, quantidade, observacao)
- **tokens_recuperacao (NOVO)** (id, id_usuario, token, expira_em, usado)

### Endpoints da API (exemplos)
- **Autenticação**
  - POST /api/login (devolve id, nome, e-mail e tipo, usados pelo header e pela sessão)
  - POST /api/cadastro
  - **POST /api/recuperar-senha (NOVO)** — recebe `{ email }`, gera o token e envia o e-mail
  - **POST /api/redefinir-senha (NOVO)** — recebe `{ token, novaSenha }` e troca a senha
- **Cardápio**
  - GET /pizzas
  - GET /bebidas
- **Pedidos**
  - POST /pedidos (criar pedido)
  - GET /pedidos/{id} (consultar pedido)
  - GET /pedidos/usuario/{id} (histórico do cliente)
- **Admin**
  - POST /admin/pizzas (adicionar pizza)
  - PUT /admin/pizzas/{id} (editar pizza)
  - DELETE /admin/pizzas/{id} (remover pizza)
  - PUT /admin/pedidos/{id}/status (atualizar status do pedido)
  - GET /admin/relatorio (relatório de vendas)

### Recuperação de senha por e-mail (NOVO)
Fluxo:
1. Cliente informa o e-mail em login.html → `POST /api/recuperar-senha`.
2. Se o e-mail existir, o back-end cria um token aleatório (UUID), salva em `tokens_recuperacao` com validade de 30 minutos e `usado = false`.
3. O sistema envia um e-mail com o link `http://localhost:8080/login.html?token=<TOKEN>`.
4. Cliente abre o link, digita a nova senha → `POST /api/redefinir-senha`.
5. O back-end confere que o token existe, não expirou e não foi usado; grava a nova senha com **BCrypt**; marca o token como usado.
6. Cliente é levado ao modo login com a mensagem "Senha alterada com sucesso".

Regras de segurança:
- Resposta de `/api/recuperar-senha` sempre igual, exista ou não o e-mail (não revela quais e-mails estão cadastrados).
- Token de uso único e com prazo de validade.
- A senha nova segue a mesma regra de tamanho do cadastro e é salva só com hash BCrypt.

Configuração do envio de e-mail (`application-mysql.properties` ou arquivo próprio):
- Servidor SMTP: para testes locais usar o Mailtrap ou uma conta Gmail com **senha de app** (não a senha normal da conta).
- Propriedades `spring.mail.host`, `spring.mail.port`, `spring.mail.username`, `spring.mail.password`.
- Usuário e senha do e-mail lidos de **variáveis de ambiente**, nunca escritos no código nem enviados ao GitHub.
- Em desenvolvimento local, `docker-compose.yml` inicia o Mailpit na porta SMTP `1025`; consulte os e-mails recebidos em `http://localhost:8025`.
- Se o servidor SMTP não estiver disponível, a recuperação informa que o envio falhou em vez de confirmar um e-mail que não foi enviado.

Itens novos no código (só adicionar, sem mexer no que já funciona):
- Dependência `spring-boot-starter-mail` no `pom.xml`
- Entidade e repository `TokenRecuperacao`
- Serviço de envio de e-mail
- Dois novos métodos no `AuthController` (ou um controller novo), sem alterar `/api/login` e `/api/cadastro`
- `javascript/sessao.js` e ajustes nos HTMLs para o header e as páginas protegidas

### Segurança
- Senhas com hash (BCrypt)
- Autenticação via sessão ou token (JWT)
- Acesso à área admin restrito por perfil (role-based)
- Liberar no Spring Security as rotas públicas: `/api/login`, `/api/cadastro`, `/api/recuperar-senha`, `/api/redefinir-senha`, as páginas HTML e os arquivos estáticos

## Observações Gerais
- Design responsivo (mobile first, já que é um app)
- Identidade visual: cores quentes (vermelho, amarelo, marrom) remetendo à pizzaria
- Foco em usabilidade: poucos cliques até finalizar o pedido
- Página Admin deve ter acesso restrito e visual mais simples/funcional (painel de controle)
- O que já funciona (login, cadastro, banco MySQL) não deve ser reescrito: as novidades entram como acréscimos -->



<!-- # Plano do Projeto — Landing Page App Pizzaria

## Objetivo

Criar uma landing page para divulgar e operar um app de pizzaria, com no máximo 6 páginas, incluindo uma página administrativa.

## Estrutura do Projeto

Cada página abaixo será um arquivo HTML separado (multi-page), com navegação entre elas — não será uma única página (single page):

- index.html → Home

- cardapio.html → Cardápio

- pedido.html → Pedido / Carrinho

- login.html → Login / Cadastro

- sobre.html → Sobre / Contato

- admin.html → Página Administrativa

## Páginas (máx. 6)

### 1. Home (Landing Principal)

- Banner de destaque com nome da pizzaria e chamada para ação (ex: "Peça agora")

- Apresentação rápida do app/diferenciais (entrega rápida, promoções, pizzas artesanais)

- Botões de acesso: "Ver Cardápio" e "Fazer Pedido"

- Seção de destaques/promoções do dia

### 2. Cardápio

- Lista de pizzas organizadas por categoria (tradicionais, especiais, doces, bebidas)

- Foto, nome, descrição e preço de cada item

- Imagens ilustrativas de cada sabor de pizza (ex: calabresa, mussarela, frango com catupiry, portuguesa, chocolate)

- Imagens das bebidas disponíveis (refrigerantes, sucos, água, cerveja)

- Botão "Adicionar ao Pedido" em cada item

### 3. Pedido / Carrinho

- Resumo dos itens selecionados

- Campo para observações (ex: sem cebola, borda recheada)

- Escolha de forma de entrega (retirada ou entrega)

- Escolha de forma de pagamento (com ícones/imagens: Pix, cartão de crédito, cartão de débito, dinheiro)

- Botão "Finalizar Pedido"

### 4. Login / Cadastro

- Formulário de login (e-mail/senha)

- Opção de cadastro para novos clientes

- Recuperação de senha

- Login social (opcional: Google/Facebook)

### 5. Sobre / Contato

- História da pizzaria

- Endereço, telefone, horário de funcionamento

- Mapa de localização

- Links para redes sociais

- Formulário de contato/dúvidas

### 6. Página Administrativa (Admin)

- Login exclusivo para administrador

- Gerenciamento do cardápio (adicionar, editar, remover pizzas)

- Gerenciamento de pedidos (visualizar status: pendente, em preparo, saiu para entrega, entregue)

- Gerenciamento de clientes cadastrados

- Relatório simples de vendas (quantidade de pedidos, faturamento do dia/mês)

## Funcionalidades JavaScript (Efeitos Dinâmicos)

### Home

- Menu mobile tipo hambúrguer

- Carrossel de promoções/destaques

- Scroll suave entre seções

### Cardápio

- Filtro por categoria (tradicional, especial, doce, bebida)

- Busca por nome da pizza

- Contador de quantidade por item

### Pedido / Carrinho

- Adicionar e remover itens dinamicamente

- Cálculo automático do total do pedido

- Validação dos campos antes de finalizar o pedido

### Login / Cadastro

- Validação de formulário (e-mail, senha)

- Mostrar/ocultar senha

- Mensagens de erro em tempo real

### Admin

- Adicionar, editar e remover pizza sem recarregar a página

- Atualizar status do pedido dinamicamente

- Gráfico simples de vendas (quantidade de pedidos, faturamento)

## Back-end

### Tecnologia

- Linguagem: Java

- Framework sugerido: Spring Boot (facilita API REST, conexão com banco e segurança)

- Banco de dados: MySQL

- Conexão: Spring Data JPA / Hibernate

### Estrutura do Banco de Dados (tabelas principais)

- **usuarios** (id, nome, email, senha, tipo: cliente/admin)

- **pizzas** (id, nome, descricao, categoria, preco, imagem, disponivel)

- **bebidas** (id, nome, preco, imagem, disponivel)

- **pedidos** (id, id_usuario, data, status, forma_pagamento, forma_entrega, total)

- **itens_pedido** (id, id_pedido, id_pizza/bebida, quantidade, observacao)

### Endpoints da API (exemplos)

- **Autenticação**

  - POST /login

  - POST /cadastro

- **Cardápio**

  - GET /pizzas

  - GET /bebidas

- **Pedidos**

  - POST /pedidos (criar pedido)

  - GET /pedidos/{id} (consultar pedido)

  - GET /pedidos/usuario/{id} (histórico do cliente)

- **Admin**

  - POST /admin/pizzas (adicionar pizza)

  - PUT /admin/pizzas/{id} (editar pizza)

  - DELETE /admin/pizzas/{id} (remover pizza)

  - PUT /admin/pedidos/{id}/status (atualizar status do pedido)

  - GET /admin/relatorio (relatório de vendas)

### Segurança

- Senhas com hash (BCrypt)

- Autenticação via sessão ou token (JWT)

- Acesso à área admin restrito por perfil (role-based)

## Observações Gerais

- Design responsivo (mobile first, já que é um app)

- Identidade visual: cores quentes (vermelho, amarelo, marrom) remetendo à pizzaria

- Foco em usabilidade: poucos cliques até finalizar o pedido

- Página Admin deve ter acesso restrito e visual mais simples/funcional (painel de controle) -->

# Plano do Projeto — Landing Page App Pizzaria

## Objetivo

Criar uma landing page para divulgar e operar um app de pizzaria, com no máximo 6 páginas, incluindo uma página administrativa.

## Estrutura do Projeto

Cada página abaixo será um arquivo HTML separado (multi-page), com navegação entre elas — não será uma única página (single page):

- index.html → Home

- cardapio.html → Cardápio

- pedido.html → Pedido / Carrinho

- login.html → Login / Cadastro / Recuperação de senha

- sobre.html → Sobre / Contato

- admin.html → Página Administrativa

## Fluxo do Usuário (NOVO)

1. Visitante acessa o site e clica em **Entrar / Cadastrar** (login.html).

2. Faz o **cadastro** → recebe mensagem de sucesso e é levado ao formulário de **login**.

3. Faz o **login** → o sistema guarda o usuário logado e redireciona para o **Cardápio** (cardapio.html).

4. No Cardápio escolhe os itens e segue para o **Pedido** (pedido.html) e finaliza.

5. Em qualquer página, o **nome do cliente logado aparece no canto esquerdo do header**, com o botão **Sair**.

6. Se esqueceu a senha, usa **"Esqueci minha senha"** no login.html e recebe um link por e-mail para criar uma nova.

Regras de acesso:

- Cardápio e Home podem ser vistos sem login; **adicionar ao pedido e finalizar exigem usuário logado** (sem login, redireciona para login.html).

- admin.html só abre para usuário com tipo ADMIN; qualquer outro é redirecionado para a Home.

## Páginas (máx. 6)

### 1. Home (Landing Principal)

- Banner de destaque com nome da pizzaria e chamada para ação (ex: "Peça agora")

- Apresentação rápida do app/diferenciais (entrega rápida, promoções, pizzas artesanais)

- Botões de acesso: "Ver Cardápio" e "Fazer Pedido"

- Seção de destaques/promoções do dia

- Header com área do usuário (ver "Header com usuário logado")

### 2. Cardápio

- Lista de pizzas organizadas por categoria (tradicionais, especiais, doces, bebidas)

- Foto, nome, descrição e preço de cada item

- Imagens ilustrativas de cada sabor de pizza (ex: calabresa, mussarela, frango com catupiry, portuguesa, chocolate)

- Imagens das bebidas disponíveis (refrigerantes, sucos, água, cerveja)

- Botão "Adicionar ao Pedido" em cada item

- Destino do redirecionamento após o login (NOVO)

### 3. Pedido / Carrinho

- Resumo dos itens selecionados

- Campo para observações (ex: sem cebola, borda recheada)

- Escolha de forma de entrega (retirada ou entrega)

- Escolha de forma de pagamento (com ícones/imagens: Pix, cartão de crédito, cartão de débito, dinheiro)

- Botão "Finalizar Pedido"

- Pedido gravado com o id do usuário logado (NOVO)

### 4. Login / Cadastro / Recuperação de senha

- Formulário de login (e-mail/senha)

- Opção de cadastro para novos clientes

- Recuperação de senha por e-mail (detalhada em "Recuperação de senha por e-mail")

- Login social (opcional: Google/Facebook)

- Esta mesma página tem 4 modos, sem criar novos arquivos: **login**, **cadastro**, **esqueci a senha** e **redefinir senha** (abre quando a URL tem `?token=...`)

### 5. Sobre / Contato

- História da pizzaria

- Endereço, telefone, horário de funcionamento

- Mapa de localização

- Links para redes sociais

- Formulário de contato/dúvidas

### 6. Página Administrativa (Admin)

- Login exclusivo para administrador

- Gerenciamento do cardápio (adicionar, editar, remover pizzas)

- Gerenciamento de pedidos (visualizar status: pendente, em preparo, saiu para entrega, entregue)

- Gerenciamento de clientes cadastrados

- Relatório simples de vendas (quantidade de pedidos, faturamento do dia/mês)

## Header com usuário logado (NOVO)

Header comum a todas as páginas (mesmo trecho de HTML/JS reaproveitado):

- **Canto esquerdo:** nome do cliente logado (ex: "Olá, Felipe") quando houver sessão; quando não houver, mostra o link "Entrar / Cadastrar".

- **Canto direito:** menu de navegação e botão "Sair".

- Se o usuário for ADMIN, aparece também o link para admin.html.

- Em telas pequenas, o nome continua visível no header, ao lado do menu hambúrguer.

## Funcionalidades JavaScript (Efeitos Dinâmicos)

### Home

- Menu mobile tipo hambúrguer

- Carrossel de promoções/destaques

- Scroll suave entre seções

### Cardápio

- Filtro por categoria (tradicional, especial, doce, bebida)

- Busca por nome da pizza

- Contador de quantidade por item

### Pedido / Carrinho

- Adicionar e remover itens dinamicamente

- Cálculo automático do total do pedido

- Validação dos campos antes de finalizar o pedido

### Login / Cadastro

- Validação de formulário (e-mail, senha)

- Mostrar/ocultar senha

- Mensagens de erro em tempo real

- **Sessão do usuário logado (NOVO):**

  - Ao logar com sucesso, o JavaScript salva no navegador (`localStorage`) os dados que a API devolve: id, nome, e-mail e tipo. **A senha nunca é guardada.**

  - Depois de salvar, redireciona para `cardapio.html`.

  - Um script comum (ex: `javascript/sessao.js`), carregado em todas as páginas, lê esses dados, preenche o nome no header e trata o botão "Sair" (apaga os dados e volta para a Home).

  - Páginas protegidas (pedido.html, admin.html) usam o mesmo script para conferir se há usuário logado e o tipo dele antes de mostrar o conteúdo.

- **Cadastro (NOVO):** após cadastrar com sucesso, mostra "Cadastro realizado! Faça seu login" e leva para o modo login.

- **Recuperação de senha (NOVO):**

  - Link "Esqueci minha senha" abre o modo que pede só o e-mail.

  - Mensagem sempre genérica, igual exista ou não o e-mail: "Se o e-mail estiver cadastrado, enviamos um link de recuperação".

  - Com `?token=...` na URL, mostra os campos "Nova senha" e "Confirmar senha", com validação (mínimo de caracteres e senhas iguais).

### Admin

- Adicionar, editar e remover pizza sem recarregar a página

- Atualizar status do pedido dinamicamente

- Gráfico simples de vendas (quantidade de pedidos, faturamento)

## Back-end

### Tecnologia

- Linguagem: Java

- Framework sugerido: Spring Boot (facilita API REST, conexão com banco e segurança)

- Banco de dados: MySQL

- Conexão: Spring Data JPA / Hibernate

- **E-mail (NOVO):** Spring Boot Starter Mail (`spring-boot-starter-mail`) para enviar o link de recuperação

### Estrutura do Banco de Dados (tabelas principais)

- **usuarios** (id, nome, email, senha, tipo: CLIENTE/ADMIN)

- **pizzas** (id, nome, descricao, categoria, preco, imagem, disponivel)

- **bebidas** (id, nome, preco, imagem, disponivel)

- **pedidos** (id, id_usuario, data, status, forma_pagamento, forma_entrega, total)

- **itens_pedido** (id, id_pedido, id_pizza/bebida, quantidade, observacao)

- **tokens_recuperacao (NOVO)** (id, id_usuario, token, expira_em, usado)

### Endpoints da API (exemplos)

- **Autenticação**

  - POST /api/login (devolve id, nome, e-mail e tipo, usados pelo header e pela sessão)

  - POST /api/cadastro

  - **POST /api/recuperar-senha (NOVO)** — recebe `{ email }`, gera o token e envia o e-mail

  - **POST /api/redefinir-senha (NOVO)** — recebe `{ token, novaSenha }` e troca a senha

- **Cardápio**

  - GET /pizzas

  - GET /bebidas

- **Pedidos**

  - POST /pedidos (criar pedido)

  - GET /pedidos/{id} (consultar pedido)

  - GET /pedidos/usuario/{id} (histórico do cliente)

- **Admin**

  - POST /admin/pizzas (adicionar pizza)

  - PUT /admin/pizzas/{id} (editar pizza)

  - DELETE /admin/pizzas/{id} (remover pizza)

  - PUT /admin/pedidos/{id}/status (atualizar status do pedido)

  - GET /admin/relatorio (relatório de vendas)

### Recuperação de senha por e-mail (NOVO)

Fluxo:

1. Cliente informa o e-mail em login.html → `POST /api/recuperar-senha`.

2. Se o e-mail existir, o back-end cria um token aleatório (UUID), salva em `tokens_recuperacao` com validade de 30 minutos e `usado = false`.

3. O sistema envia um e-mail com o link `http://localhost:8080/login.html?token=<TOKEN>`.

4. Cliente abre o link, digita a nova senha → `POST /api/redefinir-senha`.

5. O back-end confere que o token existe, não expirou e não foi usado; grava a nova senha com **BCrypt**; marca o token como usado.

6. Cliente é levado ao modo login com a mensagem "Senha alterada com sucesso".

Regras de segurança:

- Resposta de `/api/recuperar-senha` sempre igual, exista ou não o e-mail (não revela quais e-mails estão cadastrados).

- Token de uso único e com prazo de validade.

- A senha nova segue a mesma regra de tamanho do cadastro e é salva só com hash BCrypt.

Configuração do envio de e-mail (`application-mysql.properties` ou arquivo próprio):

- Servidor SMTP: para testes locais usar o Mailtrap ou uma conta Gmail com **senha de app** (não a senha normal da conta).

- Propriedades `spring.mail.host`, `spring.mail.port`, `spring.mail.username`, `spring.mail.password`.

- Usuário e senha do e-mail lidos de **variáveis de ambiente**, nunca escritos no código nem enviados ao GitHub.

- Em desenvolvimento local, `docker-compose.yml` inicia o Mailpit na porta SMTP `1025`; consulte os e-mails recebidos em `http://localhost:8025`.

- Se o servidor SMTP não estiver disponível, a recuperação informa que o envio falhou em vez de confirmar um e-mail que não foi enviado.

Itens novos no código (só adicionar, sem mexer no que já funciona):

- Dependência `spring-boot-starter-mail` no `pom.xml`

- Entidade e repository `TokenRecuperacao`

- Serviço de envio de e-mail

- Dois novos métodos no `AuthController` (ou um controller novo), sem alterar `/api/login` e `/api/cadastro`

- `javascript/sessao.js` e ajustes nos HTMLs para o header e as páginas protegidas

### Segurança

- Senhas com hash (BCrypt)

- Autenticação via sessão ou token (JWT)

- Acesso à área admin restrito por perfil (role-based)

- Liberar no Spring Security as rotas públicas: `/api/login`, `/api/cadastro`, `/api/recuperar-senha`, `/api/redefinir-senha`, as páginas HTML e os arquivos estáticos

## Observações Gerais

- Design responsivo (mobile first, já que é um app)

- Identidade visual: cores quentes (vermelho, amarelo, marrom) remetendo à pizzaria

- Foco em usabilidade: poucos cliques até finalizar o pedido

- Página Admin deve ter acesso restrito e visual mais simples/funcional (painel de controle)

- O que já funciona (login, cadastro, banco MySQL) não deve ser reescrito: as novidades entram como acréscimos
ADENDO AO PLAN.MD — GERENCIAMENTO DE PRODUTOS E IMAGENS NO ADMIN
Importante: Este adendo apenas acrescenta requisitos ao plano existente. Não substituir, apagar, reorganizar ou reescrever os requisitos originais. A Home deve permanecer como está. Não implementar banners promocionais, carrosséis ou novas seções promocionais na Home como parte desta tarefa.

Objetivo desta melhoria
Permitir que um administrador autorizado gerencie produtos e envie imagens próprias pelo painel admin.html, sem precisar editar o código-fonte a cada alteração. As mudanças devem aproveitar a estrutura, as APIs, as entidades e o banco MySQL que já existem no projeto Pizzaburguer.
Funcionalidades exclusivas do painel administrativo
Cadastro de produtos
- Permitir ao administrador cadastrar pizzas, bebidas e outras categorias já suportadas pelo sistema.
- Campos do produto: nome, descrição, categoria, preço, imagem e disponibilidade, respeitando os campos e padrões existentes no projeto.
- Permitir selecionar uma imagem do computador por um campo de envio de arquivo.
- Exibir pré-visualização da imagem antes de salvar.
- Validar campos obrigatórios, preço válido, formato do arquivo e tamanho máximo configurado.
- Informar claramente se o cadastro foi concluído ou se ocorreu um erro.
Edição e remoção
- Permitir editar os dados de um produto existente.
- Permitir substituir a imagem de um produto sem alterar seu identificador nem criar um produto duplicado.
- Se nenhuma nova imagem for enviada durante a edição, manter a imagem atual.
- Permitir excluir ou desativar produtos conforme as regras já adotadas pelo sistema e preservar a integridade dos pedidos antigos.
- Pedir confirmação antes de excluir um produto.
- Atualizar a listagem após as operações, sem recarregar a página quando isso for compatível com a implementação atual.
Listagem e experiência de uso
- Exibir produtos cadastrados com foto, nome, categoria, preço e disponibilidade.
- Incluir busca e filtro por categoria se forem compatíveis com a interface existente.
- Exibir estado vazio quando não houver produtos e mensagens úteis em caso de falha na API.
- Manter o painel responsivo para celular e computador, respeitando o estilo visual atual.
Requisitos de backend e banco de dados
- Antes de programar, inspecionar o código atual para identificar as entidades, DTOs, controllers, services, repositories, endpoints e regras de segurança já existentes para produtos, pizzas e bebidas.
- Reutilizar os endpoints e modelos existentes sempre que possível; não criar recursos duplicados sem necessidade.
- Caso falte suporte ao envio de imagens, acrescentá-lo de forma compatível com a arquitetura atual do Spring Boot.
- Persistir os dados do produto no MySQL. Armazenar no banco o caminho ou identificador da imagem, evitando salvar arquivos grandes diretamente no banco, salvo se o projeto já usar deliberadamente essa abordagem.
- Garantir que o endpoint de upload aceite apenas formatos de imagem permitidos, valide o tamanho do arquivo, gere nomes seguros e não confie no nome ou no MIME type informado pelo navegador.
- Não permitir que arquivos enviados sejam executados como código.
- Proteger todas as operações de criar, editar, excluir e enviar imagens com autorização real no backend para o perfil ADMIN. Esconder botões no frontend não é uma medida de segurança suficiente.
- Não confiar apenas em localStorage ou em verificações JavaScript para conceder acesso administrativo. Manter compatibilidade com o mecanismo de autenticação e autorização que o projeto já utiliza.
Armazenamento de imagens e Render
- Não depender de armazenamento temporário do container para imagens que precisam continuar disponíveis após reinicializações ou novos deploys.
- Verificar a configuração de hospedagem atual antes de escolher a solução. Usar um volume persistente do Render ou armazenamento de objetos compatível, conforme os recursos e a configuração existentes.
- Guardar no banco apenas a referência persistente da imagem e servir as imagens por uma URL ou rota apropriada.
- Não remover, substituir ou migrar imagens existentes sem necessidade e sem plano de compatibilidade.
- Configurar limites de upload e tratar falhas de armazenamento com mensagens claras.
Integração com o cardápio existente
- Os produtos cadastrados pelo administrador devem aparecer no cardápio existente usando os mecanismos de consulta atuais.
- Produtos editados devem refletir seus dados atualizados nas telas que já os utilizam, sem quebrar o carrinho nem o fluxo de pedidos.
- Produtos indisponíveis não devem poder ser adicionados a novos pedidos, de acordo com as regras atuais do sistema.
- Preservar o preço e a descrição registrados nos pedidos antigos conforme o modelo de dados existente; não alterar o histórico de pedidos automaticamente ao editar um produto.
- Manter compatibilidade com os nomes de campos, formatos JSON e rotas já consumidos pelo frontend.
Limites explícitos desta tarefa
- Não alterar o layout, o conteúdo ou o comportamento da Home (index.html).
- Não adicionar banners promocionais, carrosséis promocionais ou novas seções à Home.
- Não alterar login, cadastro, recuperação de senha, checkout, pagamento, entrega, relatórios ou regras de pedidos, exceto ajustes estritamente necessários para compatibilidade com o gerenciamento de produtos.
- Não recriar nem apagar tabelas, não limpar dados e não executar migrações destrutivas.
- Não substituir a arquitetura existente nem reescrever funcionalidades que já funcionam.
- Não fazer deploy automaticamente. Preparar e executar testes antes de qualquer publicação.
Plano de implementação seguro
1. Analisar a estrutura real do projeto e identificar como produtos, pizzas, bebidas e imagens são tratados atualmente.
2. Registrar quais arquivos serão alterados e explicar por que cada alteração é necessária.
3. Implementar o cadastro, a edição, a remoção ou desativação e o envio de imagens aproveitando o código existente.
4. Implementar armazenamento persistente adequado ao ambiente de produção no Render.
5. Verificar autorização ADMIN no backend para todas as operações administrativas.
6. Testar cadastro com e sem imagem, pré-visualização, edição com troca de imagem, edição sem troca de imagem, validações, exclusão/desativação, acesso de cliente comum e persistência após reinicialização.
7. Testar se o cardápio, o carrinho e os pedidos continuam funcionando e se a Home permanece inalterada.
8. Apresentar um resumo dos arquivos modificados, variáveis de ambiente necessárias e resultados dos testes. Informar explicitamente qualquer etapa que não pôde ser validada.
Critérios de conclusão
- Um administrador autenticado consegue cadastrar produtos e enviar imagens próprias.
- O administrador consegue editar dados e trocar imagens sem duplicar produtos.
- A imagem continua disponível após reinicialização/deploy, conforme a solução persistente configurada.
- Usuários sem perfil ADMIN não conseguem chamar as operações administrativas diretamente pela API.
- Os produtos aparecem no cardápio e respeitam a disponibilidade.
- O banco e o histórico de pedidos são preservados.
- A Home continua visual e funcionalmente inalterada