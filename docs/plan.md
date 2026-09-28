# Plano do Projeto — Landing Page App Pizzaria

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
- Página Admin deve ter acesso restrito e visual mais simples/funcional (painel de controle)
