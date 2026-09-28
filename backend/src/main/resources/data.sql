INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Administrador', 'admin@pizzaburguer.com', '$2a$10$Uz3eftXoFldWdEx9u7WIPeqMYfkYiga0XClhx3pD878W2vcl8aopi', 'ADMIN'),
('Cliente Demo', 'cliente@pizzaburguer.com', '$2a$10$4G4Bl1uFO6j3uFsUNVwskO86mR7hJ8zR0x9TKIwyvOYBY8A9q2Z/6', 'CLIENTE');

INSERT INTO pizzas (nome, descricao, categoria, preco, imagem, disponivel) VALUES
('Calabresa da casa', 'Calabresa artesanal, cebola roxa e orégano fresco.', 'tradicionais', 42.90, 'photo-1571407970349-bc81e7e96d47', true),
('Muçarela', 'Muçarela derretida, molho da casa e manjericão.', 'tradicionais', 39.90, 'photo-1574071318508-1cdbab80d002', true),
('Portuguesa', 'Presunto, ovo, cebola, azeitona e muçarela.', 'tradicionais', 45.90, 'photo-1579751626657-72bc17010498', true),
('Frango com Catupiry', 'Frango temperado, Catupiry cremoso e milho.', 'especiais', 48.90, 'photo-1593560708920-61dd98c46a4e', true),
('Quatro queijos', 'Muçarela, gorgonzola, parmesão e Catupiry.', 'especiais', 49.90, 'photo-1513104890138-7c749659a591', true),
('Da horta', 'Abobrinha, tomate, cogumelos e rúcula fresca.', 'especiais', 46.90, 'photo-1571407970349-bc81e7e96d47', true),
('Chocolate', 'Chocolate cremoso e raspas de chocolate ao leite.', 'doces', 36.90, 'photo-1579751626657-72bc17010498', true),
('Banana com canela', 'Banana, açúcar, canela e um toque de doce de leite.', 'doces', 34.90, 'photo-1513104890138-7c749659a591', true);

INSERT INTO bebidas (nome, preco, imagem, disponivel) VALUES
('Refrigerante 2 L', 13.90, 'photo-1622483767028-3f66f32aef97', true),
('Suco natural 500 ml', 10.90, 'photo-1600271886742-f049cd451bba', true),
('Água mineral 500 ml', 5.90, 'photo-1616118132534-381148898bb4', true),
('Cerveja long neck', 12.90, 'photo-1608270586620-248524c67de9', true);
