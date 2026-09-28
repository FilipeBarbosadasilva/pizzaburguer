


const defaults = [
			{ id: 'calabresa', name: 'Calabresa da casa', description: 'Calabresa artesanal, cebola roxa e orégano fresco.', price: 42.9, category: 'tradicionais', image: 'photo-1571407970349-bc81e7e96d47' },
			{ id: 'mussarela', name: 'Muçarela', description: 'Muçarela derretida, molho da casa e manjericão.', price: 39.9, category: 'tradicionais', image: 'photo-1574071318508-1cdbab80d002' },
			{ id: 'portuguesa', name: 'Portuguesa', description: 'Presunto, ovo, cebola, azeitona e muçarela.', price: 45.9, category: 'tradicionais', image: 'photo-1579751626657-72bc17010498' },
			{ id: 'frango-catupiry', name: 'Frango com Catupiry', description: 'Frango temperado, Catupiry cremoso e milho.', price: 48.9, category: 'especiais', image: 'photo-1593560708920-61dd98c46a4e' },
			{ id: 'quatro-queijos', name: 'Quatro queijos', description: 'Muçarela, gorgonzola, parmesão e Catupiry.', price: 49.9, category: 'especiais', image: 'photo-1513104890138-7c749659a591' },
			{ id: 'vegetariana', name: 'Da horta', description: 'Abobrinha, tomate, cogumelos e rúcula fresca.', price: 46.9, category: 'especiais', image: 'photo-1571407970349-bc81e7e96d47' },
			{ id: 'chocolate', name: 'Chocolate', description: 'Chocolate cremoso e raspas de chocolate ao leite.', price: 36.9, category: 'doces', image: 'photo-1579751626657-72bc17010498' },
			{ id: 'banana-canela', name: 'Banana com canela', description: 'Banana, açúcar, canela e um toque de doce de leite.', price: 34.9, category: 'doces', image: 'photo-1513104890138-7c749659a591' },
			{ id: 'refrigerante', name: 'Refrigerante 2 L', description: 'Escolha entre cola, guaraná ou laranja.', price: 13.9, category: 'bebidas', image: 'photo-1622483767028-3f66f32aef97' },
			{ id: 'suco', name: 'Suco natural 500 ml', description: 'Laranja ou limonada, feito na hora.', price: 10.9, category: 'bebidas', image: 'photo-1600271886742-f049cd451bba' },
			{ id: 'agua', name: 'Água mineral 500 ml', description: 'Com ou sem gás, sempre gelada.', price: 5.9, category: 'bebidas', image: 'photo-1616118132534-381148898bb4' },
			{ id: 'cerveja', name: 'Cerveja long neck', description: 'Cerveja lager 330 ml.', price: 12.9, category: 'bebidas', image: 'photo-1608270586620-248524c67de9' }
		];
		const escapeHtml = (value) => String(value).replace(/[&<>"']/g, (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
		const money = (value) => Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
		const read = (key, fallback = []) => JSON.parse(localStorage.getItem(key) || JSON.stringify(fallback));
		const store = (key, value) => localStorage.setItem(key, JSON.stringify(value));
		const currentMenu = () => {
			const removed = read('pizzaburguer-menu-removed');
			const overrides = read('pizzaburguer-menu-overrides', {});
			return [...defaults.filter((item) => !removed.includes(item.id)).map((item) => ({ ...item, ...overrides[item.id] })), ...read('pizzaburguer-menu')];
		};
		const renderDashboard = () => {
			const menu = currentMenu();
			const orders = read('pizzaburguer-orders');
			const users = read('pizzaburguer-users');
			const now = new Date();
			const todayOrders = orders.filter((order) => new Date(order.createdAt).toDateString() === now.toDateString());
			const monthOrders = orders.filter((order) => { const date = new Date(order.createdAt); return date.getMonth() === now.getMonth() && date.getFullYear() === now.getFullYear(); });
			document.querySelector('#stat-orders').textContent = todayOrders.length;
			document.querySelector('#stat-day').textContent = money(todayOrders.reduce((sum, order) => sum + order.total, 0));
			document.querySelector('#stat-month').textContent = money(monthOrders.reduce((sum, order) => sum + order.total, 0));
			document.querySelector('#menu-table').innerHTML = menu.length ? menu.map((item) => `<tr><td>${escapeHtml(item.name)}<br><small>${escapeHtml(item.category)}</small></td><td>${money(item.price)}</td><td><button class="table-action" type="button" data-edit="${escapeHtml(item.id)}">Editar</button> <button class="table-action" type="button" data-delete="${escapeHtml(item.id)}">Remover</button></td></tr>`).join('') : '<tr><td colspan="3">Nenhum item cadastrado.</td></tr>';
			document.querySelector('#orders-table').innerHTML = orders.length ? orders.map((order) => `<tr><td>#${String(order.id).slice(-6)}</td><td>${escapeHtml(order.name)}</td><td>${money(order.total)}</td><td><select data-status="${order.id}" aria-label="Status do pedido de ${escapeHtml(order.name)}"><option ${order.status === 'Pendente' ? 'selected' : ''}>Pendente</option><option ${order.status === 'Em preparo' ? 'selected' : ''}>Em preparo</option><option ${order.status === 'Saiu para entrega' ? 'selected' : ''}>Saiu para entrega</option><option ${order.status === 'Entregue' ? 'selected' : ''}>Entregue</option></select></td></tr>`).join('') : '<tr><td colspan="4">Nenhum pedido recebido.</td></tr>';
			document.querySelector('#customers-table').innerHTML = users.length ? users.map((user) => `<tr><td>${escapeHtml(user.name)}</td><td>${escapeHtml(user.email)}</td></tr>`).join('') : '<tr><td colspan="2">Nenhum cliente cadastrado.</td></tr>';
		};
		const showDashboard = () => {
			document.querySelector('#admin-login').classList.add('hidden');
			document.querySelector('#admin-dashboard').classList.remove('hidden');
			document.querySelector('#admin-logout').classList.remove('hidden');
			renderDashboard();
		};
		document.querySelector('#admin-login-form').addEventListener('submit', async (event) => {
			event.preventDefault();
			const form = new FormData(event.currentTarget);
			const email = String(form.get('email')).trim().toLowerCase();
			const password = String(form.get('password'));
			const feedback = document.querySelector('#admin-login-feedback');
			try {
				const response = await fetch('http://localhost:8080/api/login', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ email, senha: password })
				});
				const result = await response.json();
				if (!response.ok || result.tipo !== 'ADMIN') throw new Error('Credenciais de administrador inválidas.');
				sessionStorage.setItem('pizzaburguer-admin-auth', 'true');
				showDashboard();
			} catch (error) {
				feedback.textContent = error.message || 'Senha incorreta.';
				feedback.classList.remove('hidden');
			}
		});
		document.querySelector('#admin-logout').addEventListener('click', () => {
			sessionStorage.removeItem('pizzaburguer-admin-auth');
			document.querySelector('#admin-dashboard').classList.add('hidden');
			document.querySelector('#admin-login').classList.remove('hidden');
			document.querySelector('#admin-logout').classList.add('hidden');
		});
		document.querySelector('#product-form').addEventListener('submit', (event) => {
			event.preventDefault();
			const form = new FormData(event.currentTarget);
			const name = String(form.get('name')).trim();
			const products = read('pizzaburguer-menu');
			const id = `${name.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-z0-9]+/g, '-')}-${Date.now()}`;
			products.push({ id, name, description: String(form.get('description')).trim(), price: Number(form.get('price')), category: form.get('category'), image: form.get('image') });
			store('pizzaburguer-menu', products);
			event.currentTarget.reset();
			document.querySelector('#product-feedback').textContent = 'Item adicionado ao cardápio.';
			document.querySelector('#product-feedback').classList.remove('hidden');
			renderDashboard();
		});
		document.querySelector('#menu-table').addEventListener('click', (event) => {
			const editButton = event.target.closest('[data-edit]');
			const deleteButton = event.target.closest('[data-delete]');
			let menu = currentMenu();
			if (editButton) {
				const item = menu.find((entry) => entry.id === editButton.dataset.edit);
				const name = window.prompt('Nome do item:', item.name);
				if (name === null || !name.trim()) return;
				const price = window.prompt('Preço (R$):', item.price);
				if (price === null || !Number.isFinite(Number(price)) || Number(price) <= 0) return;
				const description = window.prompt('Descrição:', item.description);
				if (description === null) return;
				const updated = { ...item, name: name.trim(), price: Number(price), description: description.trim() };
				if (defaults.some((entry) => entry.id === item.id)) {
					const overrides = read('pizzaburguer-menu-overrides', {});
					overrides[item.id] = updated;
					store('pizzaburguer-menu-overrides', overrides);
				} else store('pizzaburguer-menu', read('pizzaburguer-menu').map((entry) => entry.id === item.id ? updated : entry));
			}
			if (deleteButton) {
				const item = menu.find((entry) => entry.id === deleteButton.dataset.delete);
				if (!window.confirm(`Remover ${item.name} do cardápio?`)) return;
				if (defaults.some((entry) => entry.id === item.id)) store('pizzaburguer-menu-removed', [...read('pizzaburguer-menu-removed'), item.id]);
				else store('pizzaburguer-menu', read('pizzaburguer-menu').filter((entry) => entry.id !== item.id));
			}
			renderDashboard();
		});
		document.querySelector('#orders-table').addEventListener('change', (event) => {
			const select = event.target.closest('[data-status]');
			if (!select) return;
			store('pizzaburguer-orders', read('pizzaburguer-orders').map((order) => String(order.id) === select.dataset.status ? { ...order, status: select.value } : order));
		});
		if (sessionStorage.getItem('pizzaburguer-admin-auth') === 'true') showDashboard();

     