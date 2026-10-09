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
