


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
		const money = (value) => Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
		const read = (key, fallback = []) => JSON.parse(localStorage.getItem(key) || JSON.stringify(fallback));
		const store = (key, value) => localStorage.setItem(key, JSON.stringify(value));
		const escapeHtml = (value) => String(value ?? '').replace(/[&<>"']/g, (character) => ({
			'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
		})[character]);
		let apiOrders = [];
		let registeredCustomers = [];
		let refreshingDashboard = false;
		const removedOrdersKey = 'pizzaburguer-admin-removed-orders';
		const currentMenu = () => {
			const removed = read('pizzaburguer-menu-removed');
			const overrides = read('pizzaburguer-menu-overrides', {});
			return [...defaults.filter((item) => !removed.includes(item.id)).map((item) => ({ ...item, ...overrides[item.id] })), ...read('pizzaburguer-menu')];
		};
		const getPopularItems = (orders) => {
			const quantities = new Map();
			orders.filter((order) => order.status !== 'Cancelado').forEach((order) => {
				(order.itens || []).forEach((item) => {
					const quantity = Number(item.quantity);
					if (typeof item.name !== 'string' || !item.name.trim() || !Number.isFinite(quantity) || quantity <= 0) return;
					quantities.set(item.name, (quantities.get(item.name) || 0) + quantity);
				});
			});
			return Array.from(quantities, ([nome, quantidade]) => ({ nome, quantidade }))
				.sort((left, right) => right.quantidade - left.quantidade || left.nome.localeCompare(right.nome, 'pt-BR'));
		};
		const renderDashboard = () => {
			const menu = currentMenu();
			const removedOrderIds = new Set(read(removedOrdersKey).map(String));
			const orders = apiOrders.filter((order) => !removedOrderIds.has(String(order.id)));
			const users = registeredCustomers;
			const now = new Date();
			const activeOrders = orders.filter((order) => order.status !== 'Cancelado');
			const todayOrders = activeOrders.filter((order) => new Date(order.data).toDateString() === now.toDateString());
			const monthOrders = activeOrders.filter((order) => { const date = new Date(order.data); return date.getMonth() === now.getMonth() && date.getFullYear() === now.getFullYear(); });
			document.querySelector('#stat-orders').textContent = todayOrders.length;
			document.querySelector('#stat-day').textContent = money(todayOrders.reduce((sum, order) => sum + Number(order.total), 0));
			document.querySelector('#stat-month').textContent = money(monthOrders.reduce((sum, order) => sum + Number(order.total), 0));
			document.querySelector('#menu-table').innerHTML = menu.length ? menu.map((item) => `<tr><td>${escapeHtml(item.name)}<br><small>${escapeHtml(item.category)}</small></td><td>${money(item.price)}</td><td><button class="table-action" type="button" data-edit="${escapeHtml(item.id)}">Editar</button> <button class="table-action" type="button" data-delete="${escapeHtml(item.id)}">Remover</button></td></tr>`).join('') : '<tr><td colspan="3">Nenhum item cadastrado.</td></tr>';
			document.querySelector('#orders-table').innerHTML = orders.length ? orders.map((order) => {
				const phone = String(order.telefone || '').replace(/\D/g, '');
				const whatsappNumber = phone.length === 10 || phone.length === 11 ? `55${phone}` : phone;
				const whatsapp = phone ? `<a href="https://wa.me/${whatsappNumber}" target="_blank" rel="noreferrer">${escapeHtml(order.telefone)}</a>` : '—';
				const summary = (order.itens || []).map((item) => `${item.quantity}× ${item.name}`).join(', ');
				const removeAction = order.status === 'Cancelado'
					? `<button class="table-action" type="button" data-remove-order="${order.id}" aria-label="Remover pedido cancelado ${order.id}">Remover</button>`
					: '—';
				return `<tr><td>#${String(order.id).slice(-6)}</td><td>${escapeHtml(order.nome)}</td><td>${escapeHtml(summary)}</td><td>${whatsapp}</td><td>${money(order.total)}</td><td><select data-status="${order.id}" aria-label="Status do pedido de ${escapeHtml(order.nome)}"><option ${order.status === 'Pendente' ? 'selected' : ''}>Pendente</option><option ${order.status === 'Em preparo' ? 'selected' : ''}>Em preparo</option><option ${order.status === 'Saiu para entrega' ? 'selected' : ''}>Saiu para entrega</option><option ${order.status === 'Entregue' ? 'selected' : ''}>Entregue</option><option ${order.status === 'Cancelado' ? 'selected' : ''} disabled>Cancelado</option></select></td><td>${removeAction}</td></tr>`;
			}).join('') : '<tr><td colspan="7">Nenhum pedido recebido.</td></tr>';
			const popularItems = getPopularItems(apiOrders);
			document.querySelector('#popular-items-table').innerHTML = popularItems.length
				? popularItems.map((item, index) => `<tr><td>${index + 1}</td><td>${escapeHtml(item.nome)}</td><td>${item.quantidade}</td></tr>`).join('')
				: '<tr><td colspan="3">Ainda não há itens pedidos.</td></tr>';
			document.querySelector('#customers-table').innerHTML = users.length ? users.map((user) => {
				const phone = String(user.telefone || '').replace(/\D/g, '');
				const whatsappNumber = phone.length === 10 || phone.length === 11 ? `55${phone}` : phone;
				const whatsapp = phone ? `<a href="https://wa.me/${whatsappNumber}" target="_blank" rel="noreferrer">${escapeHtml(user.telefone)}</a>` : 'Não informado';
				return `<tr><td>${escapeHtml(user.nome)}</td><td>${escapeHtml(user.email)}</td><td>${whatsapp}</td></tr>`;
			}).join('') : '<tr><td colspan="3">Nenhum cliente cadastrado.</td></tr>';
		};
		const refreshDashboard = async () => {
			if (refreshingDashboard) return;
			refreshingDashboard = true;
			try {
				const [ordersResponse, customersResponse] = await Promise.all([
					fetch('/api/admin/pedidos'),
					fetch('/api/admin/clientes')
				]);
				if (ordersResponse.status === 403 || customersResponse.status === 403) {
					throw new Error('Sua sessão não está autorizada como administradora. Saia da conta e entre novamente com o usuário administrador.');
				}
				if (!ordersResponse.ok || !customersResponse.ok) {
					throw new Error(`Não foi possível atualizar pedidos e clientes (HTTP ${ordersResponse.status}/${customersResponse.status}).`);
				}
				[apiOrders, registeredCustomers] = await Promise.all([
					ordersResponse.json(),
					customersResponse.json()
				]);
				document.querySelector('#admin-data-error').classList.add('hidden');
				renderDashboard();
			} catch (error) {
				const feedback = document.querySelector('#admin-data-error');
				feedback.textContent = error instanceof Error ? error.message : 'Não foi possível carregar os dados administrativos.';
				feedback.classList.remove('hidden');
			} finally {
				refreshingDashboard = false;
			}
		};
		const showDashboard = () => {
			document.querySelector('#admin-dashboard').classList.remove('hidden');
			refreshDashboard();
		};
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
			const previousStatus = apiOrders.find((order) => String(order.id) === select.dataset.status)?.status;
			fetch(`/api/admin/pedidos/${encodeURIComponent(select.dataset.status)}/status`, {
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ status: select.value })
			}).then(async (response) => {
				if (!response.ok) {
					const result = await response.json().catch(() => ({}));
					throw new Error(result.message || `Não foi possível atualizar o status (HTTP ${response.status}).`);
				}
				await refreshDashboard();
			}).catch((error) => {
				window.alert(error instanceof Error ? error.message : 'Não foi possível atualizar o status.');
				select.value = previousStatus || 'Pendente';
			});
		});
		document.querySelector('#orders-table').addEventListener('click', (event) => {
			const button = event.target.closest('[data-remove-order]');
			if (!button) return;
			const order = apiOrders.find((entry) => String(entry.id) === button.dataset.removeOrder);
			if (!order || order.status !== 'Cancelado') return;
			if (!window.confirm(`Remover o pedido cancelado #${String(order.id).slice(-6)} desta lista? O histórico do cliente será mantido.`)) return;
			const removedOrderIds = read(removedOrdersKey).map(String);
			if (!removedOrderIds.includes(String(order.id))) removedOrderIds.push(String(order.id));
			store(removedOrdersKey, removedOrderIds);
			renderDashboard();
		});
		document.querySelector('#refresh-dashboard').addEventListener('click', refreshDashboard);
		document.querySelector('#clear-orders').addEventListener('click', async (event) => {
			if (!window.confirm('ATENÇÃO: isso apagará permanentemente TODOS os pedidos (pendentes, em preparo, entregues e cancelados), inclusive do histórico dos clientes. Esta ação não pode ser desfeita. Deseja continuar?')) return;
			const button = event.currentTarget;
			button.disabled = true;
			try {
				const response = await fetch('/api/admin/pedidos', { method: 'DELETE' });
				if (response.status === 403) {
					throw new Error('Sua sessão não está autorizada como administradora. Saia da conta e entre novamente com o usuário administrador.');
				}
				if (response.status === 405) {
					throw new Error('O backend em execução ainda não reconhece a limpeza de pedidos. Salve seu trabalho e reinicie o Spring Boot pelo VS Code.');
				}
				if (!response.ok) throw new Error(`Não foi possível apagar os pedidos (HTTP ${response.status}).`);
				const result = await response.json();
				if (result.quantidadeRestante !== 0) {
					throw new Error(`A limpeza terminou com ${result.quantidadeRestante} pedido(s) ainda no banco.`);
				}
				store(removedOrdersKey, []);
				apiOrders = [];
				renderDashboard();
				document.querySelector('#admin-data-error').classList.add('hidden');
				await refreshDashboard();
				window.alert(`${result.quantidadeRemovida} pedido(s) apagado(s).`);
			} catch (error) {
				const feedback = document.querySelector('#admin-data-error');
				feedback.textContent = error instanceof Error ? error.message : 'Não foi possível apagar os pedidos.';
				feedback.classList.remove('hidden');
			} finally {
				button.disabled = false;
			}
		});
		document.querySelector('#print-daily-report').addEventListener('click', () => {
			const today = new Date();
			const todayOrders = apiOrders.filter((order) => {
				const orderDate = new Date(order.data);
				return orderDate.getFullYear() === today.getFullYear()
					&& orderDate.getMonth() === today.getMonth()
					&& orderDate.getDate() === today.getDate();
			});
			const activeOrders = todayOrders.filter((order) => order.status !== 'Cancelado');
			const dailyTotal = activeOrders.reduce((sum, order) => sum + Number(order.total), 0);
			const reportWindow = window.open('', '_blank');
			if (!reportWindow) {
				window.alert('O navegador bloqueou a janela do relatório. Permita pop-ups para imprimir.');
				return;
			}
			const rows = todayOrders.length ? todayOrders.map((order) => {
				const items = (order.itens || []).map((item) =>
					`${Number(item.quantity)}× ${escapeHtml(item.name)}`
				).join(', ');
				return `<tr><td>#${escapeHtml(String(order.id).slice(-6))}</td><td>${escapeHtml(order.nome)}</td><td>${escapeHtml(items)}</td><td>${escapeHtml(order.status)}</td><td>${money(order.total)}</td></tr>`;
			}).join('') : '<tr><td colspan="5">Nenhum pedido registrado hoje.</td></tr>';
			const reportDate = today.toLocaleDateString('pt-BR');
			reportWindow.document.write(`<!doctype html><html lang="pt-BR"><head><meta charset="utf-8"><title>Relatório diário - ${reportDate}</title><style>body{font:14px Arial,sans-serif;color:#222;margin:32px}h1{margin-bottom:4px}p{margin:6px 0}.summary{display:flex;gap:32px;margin:24px 0}table{width:100%;border-collapse:collapse}th,td{text-align:left;padding:9px;border:1px solid #ccc}th{background:#f1f1f1}@media print{body{margin:12mm}button{display:none}}</style></head><body><h1>Pizzaburguer — Relatório diário</h1><p>Data: ${reportDate}</p><div class="summary"><p>Pedidos válidos: <strong>${activeOrders.length}</strong></p><p>Pedidos cancelados: <strong>${todayOrders.length - activeOrders.length}</strong></p><p>Total vendido: <strong>${money(dailyTotal)}</strong></p></div><p>Pedidos cancelados não entram no total vendido.</p><table><thead><tr><th>Pedido</th><th>Cliente</th><th>Itens</th><th>Status</th><th>Total</th></tr></thead><tbody>${rows}</tbody></table></body></html>`);
			reportWindow.document.close();
			reportWindow.focus();
			window.setTimeout(() => reportWindow.print(), 250);
		});
		const currentUser = JSON.parse(localStorage.getItem('pizzaburguer-session') || 'null');
		if (currentUser?.tipo === 'ADMIN') {
			showDashboard();
			window.setInterval(refreshDashboard, 30000);
		}

     