(() => {
	const orderHistory = document.querySelector('#order-history');
	const feedback = document.querySelector('#orders-feedback');
	const money = (value) => Number(value).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
	const escapeHtml = (value) => String(value ?? '').replace(/[&<>"']/g, (char) => ({
		'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
	})[char]);

	const loadOrders = async () => {
		try {
			const response = await fetch('/api/pedidos/meus');
			if (response.status === 401) {
				window.location.replace('login.html?redirect=carrinho.html');
				return;
			}
			if (!response.ok) throw new Error(`Não foi possível carregar os pedidos (HTTP ${response.status}).`);
			const orders = await response.json();
			renderOrders(orders);
			feedback.classList.add('hidden');
		} catch (error) {
			feedback.textContent = error instanceof Error ? error.message : 'Não foi possível carregar seus pedidos.';
			feedback.classList.remove('hidden');
			orderHistory.replaceChildren();
		}
	};

	const renderOrders = (orders) => {
		if (!orders.length) {
			orderHistory.innerHTML = '<article class="empty-orders"><h2>Você ainda não fez pedidos.</h2><p>Escolha seus sabores favoritos no cardápio e acompanhe tudo por aqui.</p><a class="button button-red" href="cardapio.html">Ver cardápio</a></article>';
			return;
		}

		orderHistory.innerHTML = orders.map((order) => {
			const statusClass = String(order.status).toLowerCase().replaceAll(' ', '-');
			const date = new Date(order.data).toLocaleString('pt-BR');
			const items = (order.itens || []).map((item) =>
				`<li><span>${escapeHtml(item.quantity)} × ${escapeHtml(item.name)}</span><strong>${money(Number(item.price) * Number(item.quantity))}</strong></li>`
			).join('');
			const cancelButton = order.status === 'Pendente'
				? `<button class="button button-small button-cancel" type="button" data-cancel-order="${escapeHtml(order.id)}">Cancelar pedido</button>`
				: '';
			return `<article class="order-card">
				<header class="order-card-heading"><div><p class="eyebrow">Pedido #${escapeHtml(String(order.id).slice(-6))}</p><time>${escapeHtml(date)}</time></div><span class="order-status status-${escapeHtml(statusClass)}">${escapeHtml(order.status)}</span></header>
				<ul class="order-items">${items}</ul>
				<div class="order-details"><span>${escapeHtml(order.formaEntrega)}</span><span>Pagamento: ${escapeHtml(order.formaPagamento)}</span></div>
				${order.endereco ? `<p class="order-note"><strong>Endereço:</strong> ${escapeHtml(order.endereco)}</p>` : ''}
				${order.observacoes ? `<p class="order-note"><strong>Observações:</strong> ${escapeHtml(order.observacoes)}</p>` : ''}
				<footer class="order-card-footer"><strong>Total ${money(order.total)}</strong>${cancelButton}</footer>
			</article>`;
		}).join('');
	};

	document.querySelector('#refresh-orders').addEventListener('click', loadOrders);
	orderHistory.addEventListener('click', async (event) => {
		const button = event.target.closest('[data-cancel-order]');
		if (!button || !window.confirm('Deseja cancelar este pedido? O cancelamento só é possível enquanto estiver Pendente.')) return;

		button.disabled = true;
		try {
			const response = await fetch(`/api/pedidos/${encodeURIComponent(button.dataset.cancelOrder)}`, {
				method: 'DELETE'
			});
			const result = await response.json().catch(() => ({}));
			if (!response.ok) throw new Error(result.message || `Não foi possível cancelar o pedido (HTTP ${response.status}).`);
			await loadOrders();
		} catch (error) {
			feedback.textContent = error instanceof Error ? error.message : 'Não foi possível cancelar o pedido.';
			feedback.classList.remove('hidden');
			button.disabled = false;
		}
	});

	document.querySelector('.menu-toggle').addEventListener('click', (event) => {
		const toggle = event.currentTarget;
		const expanded = toggle.getAttribute('aria-expanded') === 'true';
		toggle.setAttribute('aria-expanded', String(!expanded));
		document.querySelector('.main-nav').classList.toggle('nav-open', !expanded);
	});

	loadOrders();
	window.setInterval(loadOrders, 30000);
})();
