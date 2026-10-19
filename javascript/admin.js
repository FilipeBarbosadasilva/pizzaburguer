


		const money = (value) => Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
		const read = (key, fallback = []) => JSON.parse(localStorage.getItem(key) || JSON.stringify(fallback));
		const store = (key, value) => localStorage.setItem(key, JSON.stringify(value));
		const apiFetch = (url, options = {}) => fetch(url, { ...options, credentials: 'include' });
		const redirectToAdminLogin = () => {
			localStorage.removeItem('pizzaburguer-session');
			window.location.assign('login.html?redirect=admin.html&sessao=expirada');
		};
		const escapeHtml = (value) => String(value ?? '').replace(/[&<>"']/g, (character) => ({
			'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
		})[character]);
		let apiOrders = [];
		let registeredCustomers = [];
		let apiProducts = [];
		let refreshingDashboard = false;
		const removedOrdersKey = 'pizzaburguer-admin-removed-orders';
		const currentMenu = () => apiProducts;
		const fallbackDescriptions = new Map([
			['Refrigerante 2 L', 'Escolha entre cola, guaraná ou laranja.'],
			['Suco natural 500 ml', 'Laranja ou limonada, feito na hora.'],
			['Água mineral 500 ml', 'Com ou sem gás, sempre gelada.'],
			['Cerveja long neck', 'Cerveja lager 330 ml. Venda proibida para menores.']
		]);
		const productImageUrl = (image) => image?.startsWith('/') || image?.startsWith('http')
			? image
			: `https://images.unsplash.com/${image || 'photo-1571407970349-bc81e7e96d47'}?auto=format&fit=crop&w=120&q=75`;
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
			const searchTerm = document.querySelector('#product-search').value.trim().toLocaleLowerCase('pt-BR');
			const categoryFilter = document.querySelector('#product-filter-category').value;
			const availabilityFilter = document.querySelector('#product-filter-availability').value;
			const visibleMenu = menu.filter((item) =>
				(!searchTerm || item.name.toLocaleLowerCase('pt-BR').includes(searchTerm))
				&& (!categoryFilter || item.category === categoryFilter)
				&& (availabilityFilter === 'all'
					|| (availabilityFilter === 'available' ? item.available : !item.available)));
			document.querySelector('#menu-table').innerHTML = visibleMenu.length ? visibleMenu.map((item) => `<tr><td><img class="admin-product-thumb" src="${escapeHtml(productImageUrl(item.image))}" alt=""></td><td>${escapeHtml(item.name)}<br><small>${escapeHtml(item.category)}</small></td><td>${money(item.price)}</td><td>${item.available ? 'No cardápio' : 'Removido'}</td><td><button class="table-action" type="button" data-edit="${escapeHtml(item.id)}">Editar</button> <button class="table-action" type="button" data-toggle-availability="${escapeHtml(item.id)}">${item.available ? 'Remover do cardápio' : 'Reativar'}</button></td></tr>`).join('') : `<tr><td colspan="5">${menu.length ? 'Nenhum produto corresponde a esses filtros.' : 'Nenhum item cadastrado.'}</td></tr>`;
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
				const [ordersResponse, customersResponse, pizzasResponse, drinksResponse] = await Promise.all([
					apiFetch('/api/admin/pedidos'),
					apiFetch('/api/admin/clientes'),
					apiFetch('/api/pizzas'),
					apiFetch('/api/bebidas')
				]);
				if ([ordersResponse, customersResponse, pizzasResponse, drinksResponse].some((response) => response.status === 401)) {
					redirectToAdminLogin();
					return;
				}
				if ([ordersResponse, customersResponse].some((response) => response.status === 403)) {
					throw new Error('Sua sessão não está autorizada como administradora. Saia da conta e entre novamente com o usuário administrador.');
				}
				if ([ordersResponse, customersResponse, pizzasResponse, drinksResponse].some((response) => !response.ok)) {
					throw new Error(`Não foi possível atualizar os dados administrativos (HTTP ${ordersResponse.status}/${customersResponse.status}/${pizzasResponse.status}/${drinksResponse.status}).`);
				}
				const [orders, customers, pizzas, drinks] = await Promise.all([
					ordersResponse.json(),
					customersResponse.json(),
					pizzasResponse.json(),
					drinksResponse.json()
				]);
				apiOrders = orders;
				registeredCustomers = customers;
				apiProducts = [
					...pizzas.map((pizza) => ({
						id: `pizza:${pizza.id}`, productType: 'pizza', name: pizza.nome,
						description: pizza.descricao, category: pizza.categoria, price: pizza.preco,
						image: pizza.imagem, available: pizza.disponivel
					})),
					...drinks.map((drink) => ({
						id: `bebida:${drink.id}`, productType: 'bebida', name: drink.nome,
						description: drink.descricao || fallbackDescriptions.get(drink.nome) || '',
						category: 'bebidas', price: drink.preco,
						image: drink.imagem, available: drink.disponivel
					}))
				];
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
		const productForm = document.querySelector('#product-form');
		const imageInput = document.querySelector('#product-image');
		const imagePreview = document.querySelector('#product-image-preview');
		let previewUrl = '';
		const setProductFeedback = (message, isError = false) => {
			const feedback = document.querySelector('#product-feedback');
			feedback.textContent = message;
			feedback.classList.toggle('error', isError);
			feedback.classList.remove('hidden');
		};
		const resetProductForm = () => {
			productForm.reset();
			document.querySelector('#product-id').value = '';
			document.querySelector('#product-form-title').textContent = 'Adicionar item ao cardápio';
			document.querySelector('#product-submit').textContent = 'Adicionar item';
			document.querySelector('#product-cancel-edit').classList.add('hidden');
			imagePreview.classList.add('hidden');
			imagePreview.removeAttribute('src');
			document.querySelector('#product-feedback').classList.add('hidden');
			if (previewUrl) URL.revokeObjectURL(previewUrl);
			previewUrl = '';
		};
		const readApiMessage = async (response) => {
			const responseText = await response.text();
			let body = {};
			try {
				body = responseText ? JSON.parse(responseText) : {};
			} catch {
				if (response.headers.get('content-type')?.includes('text/plain')
					&& responseText.trim() && !responseText.includes('<')) {
					return responseText.trim().slice(0, 240);
				}
			}
			const message = body.detail || body.message || body.erro;
			if (message) return message;
			if (response.status >= 500) {
				return `O servidor não conseguiu concluir a operação (HTTP ${response.status}). Consulte o log do backend para ver a causa.`;
			}
			return `Falha na operação (HTTP ${response.status}).`;
		};
		const uploadProductImage = async (file) => {
			const body = new FormData();
			body.append('file', file);
			const response = await apiFetch('/api/admin/uploads', { method: 'POST', body });
			if (response.status === 401) {
				redirectToAdminLogin();
				throw new Error('Sua sessão expirou.');
			}
			if (!response.ok) throw new Error(`Falha ao enviar a imagem: ${await readApiMessage(response)}`);
			return (await response.json()).url;
		};
		const saveProduct = async (event) => {
			event.preventDefault();
			const button = document.querySelector('#product-submit');
			const form = new FormData(productForm);
			const productId = String(form.get('id') || '');
			const current = apiProducts.find((item) => item.id === productId);
			const category = String(form.get('category'));
			const productType = category === 'bebidas' ? 'bebida' : 'pizza';
			if (current && current.productType !== productType) {
				setProductFeedback('Não é possível mudar um produto de pizza para bebida (ou o contrário). Cadastre um novo produto para essa categoria.', true);
				return;
			}
			button.disabled = true;
			try {
				const selectedFile = imageInput.files[0];
				if (selectedFile && !['image/png', 'image/jpeg'].includes(selectedFile.type)) {
					throw new Error('Selecione uma imagem PNG ou JPEG.');
				}
				if (selectedFile && selectedFile.size > 5 * 1024 * 1024) {
					throw new Error('A imagem deve ter no máximo 5 MB.');
				}
				const image = selectedFile ? await uploadProductImage(selectedFile) : current?.image
					|| (productType === 'bebida' ? 'photo-1622483767028-3f66f32aef97' : 'photo-1571407970349-bc81e7e96d47');
				const payload = {
					nome: String(form.get('name')).trim(),
					descricao: String(form.get('description')).trim(),
					preco: Number(form.get('price')),
					imagem: image,
					disponivel: document.querySelector('#product-available').checked
				};
				if (productType === 'pizza') payload.categoria = category;
				const endpoint = productType === 'bebida' ? 'bebidas' : 'pizzas';
				const apiId = productId ? productId.split(':')[1] : '';
				const response = await apiFetch(`/api/admin/${endpoint}${apiId ? `/${encodeURIComponent(apiId)}` : ''}`, {
					method: apiId ? 'PUT' : 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify(payload)
				});
				if (response.status === 401) {
					redirectToAdminLogin();
					return;
				}
				if (response.status === 403) throw new Error('Sua sessão não está autorizada como administradora.');
				if (!response.ok) {
					throw new Error(`Falha ao ${apiId ? 'atualizar' : 'cadastrar'} produto: ${await readApiMessage(response)}`);
				}
				resetProductForm();
				setProductFeedback(apiId ? 'Produto atualizado.' : 'Produto cadastrado.');
				await refreshDashboard();
			} catch (error) {
				setProductFeedback(error instanceof Error ? error.message : 'Não foi possível salvar o produto.', true);
			} finally {
				button.disabled = false;
			}
		};
		productForm.addEventListener('submit', saveProduct);
		document.querySelector('#product-cancel-edit').addEventListener('click', resetProductForm);
		imageInput.addEventListener('change', () => {
			if (previewUrl) URL.revokeObjectURL(previewUrl);
			previewUrl = imageInput.files[0] ? URL.createObjectURL(imageInput.files[0]) : '';
			if (previewUrl) {
				imagePreview.src = previewUrl;
				imagePreview.classList.remove('hidden');
			} else {
				imagePreview.classList.add('hidden');
				imagePreview.removeAttribute('src');
			}
		});
		document.querySelector('#menu-table').addEventListener('click', (event) => {
			const editButton = event.target.closest('[data-edit]');
			const availabilityButton = event.target.closest('[data-toggle-availability]');
			if (editButton) {
				const item = apiProducts.find((entry) => entry.id === editButton.dataset.edit);
				if (!item) return;
				resetProductForm();
				document.querySelector('#product-id').value = item.id;
				document.querySelector('#product-name').value = item.name;
				document.querySelector('#product-description').value = item.description || '';
				document.querySelector('#product-price').value = item.price;
				document.querySelector('#product-category').value = item.category;
				document.querySelector('#product-available').checked = item.available;
				document.querySelector('#product-form-title').textContent = 'Editar produto';
				document.querySelector('#product-submit').textContent = 'Salvar alterações';
				document.querySelector('#product-cancel-edit').classList.remove('hidden');
				imagePreview.src = productImageUrl(item.image);
				imagePreview.classList.remove('hidden');
				document.querySelector('#product-form').scrollIntoView({ behavior: 'smooth', block: 'start' });
			}
			if (availabilityButton) {
				const item = apiProducts.find((entry) => entry.id === availabilityButton.dataset.toggleAvailability);
				if (!item) return;
				if (item.available && !window.confirm(`Remover ${item.name} do cardápio? O registro e o histórico dos pedidos serão preservados; você poderá reativá-lo depois.`)) return;
				const [type, id] = item.id.split(':');
				const endpoint = `/api/admin/${type === 'bebida' ? 'bebidas' : 'pizzas'}/${encodeURIComponent(id)}`;
				const payload = {
					nome: item.name, descricao: item.description || '',
					preco: item.price, imagem: item.image, disponivel: true
				};
				if (type === 'pizza') payload.categoria = item.category;
				availabilityButton.disabled = true;
				(async () => {
					try {
						const response = item.available
							? await apiFetch(endpoint, { method: 'DELETE' })
							: await apiFetch(endpoint, {
								method: 'PUT',
								headers: { 'Content-Type': 'application/json' },
								body: JSON.stringify(payload)
							});
						if (response.status === 401) {
							redirectToAdminLogin();
							return;
						}
						if (response.status === 403) throw new Error('Sua sessão não está autorizada como administradora.');
						if (!response.ok) throw new Error(await readApiMessage(response));
						setProductFeedback(item.available ? 'Produto removido do cardápio; histórico preservado.' : 'Produto reativado no cardápio.');
						await refreshDashboard();
					} catch (error) {
						setProductFeedback(error instanceof Error ? error.message : 'Não foi possível alterar o cardápio.', true);
					} finally {
						availabilityButton.disabled = false;
					}
				})();
			}
		});
		document.querySelector('#product-search').addEventListener('input', renderDashboard);
		document.querySelector('#product-filter-category').addEventListener('change', renderDashboard);
		document.querySelector('#product-filter-availability').addEventListener('change', renderDashboard);
		document.querySelector('#orders-table').addEventListener('change', (event) => {
			const select = event.target.closest('[data-status]');
			if (!select) return;
			const previousStatus = apiOrders.find((order) => String(order.id) === select.dataset.status)?.status;
			apiFetch(`/api/admin/pedidos/${encodeURIComponent(select.dataset.status)}/status`, {
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ status: select.value })
			}).then(async (response) => {
				if (response.status === 401) {
					redirectToAdminLogin();
					return;
				}
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
				const response = await apiFetch('/api/admin/pedidos', { method: 'DELETE' });
				if (response.status === 401) {
					redirectToAdminLogin();
					return;
				}
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

     