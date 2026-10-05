(() => {
	const sessionKey = 'pizzaburguer-session';
	let user = null;
	try {
		user = JSON.parse(localStorage.getItem(sessionKey) || 'null');
	} catch (error) {
		console.error('Não foi possível ler a sessão salva.', error);
		localStorage.removeItem(sessionKey);
	}

	const page = window.location.pathname.split('/').pop();
	if (page === 'pedido.html' && !user) {
		window.location.replace('login.html?redirect=pedido.html');
		return;
	}
	if (page === 'carrinho.html' && !user) {
		window.location.replace('login.html?redirect=carrinho.html');
		return;
	}
	if (page === 'admin.html' && user?.tipo !== 'ADMIN') {
		window.location.replace('index.html');
		return;
	}

	document.addEventListener('DOMContentLoaded', () => {
		if (user) {
			const header = document.querySelector('.site-header, .admin-header');
			const brand = header?.querySelector('.brand');
			if (header && brand) {
				const greeting = document.createElement('span');
				greeting.className = 'header-greeting';
				greeting.textContent = user.name;
				greeting.setAttribute('aria-label', `Olá, ${user.name}`);
				header.insertBefore(greeting, brand);
			}

			document.querySelectorAll('.header-login').forEach((link) => link.classList.add('hidden'));
			const navigation = document.querySelector('.main-nav');
			if (navigation) {
				const existingOrdersLink = Array.from(navigation.querySelectorAll('a')).find((link) =>
					new URL(link.href, window.location.href).pathname.endsWith('/carrinho.html'));
				if (existingOrdersLink) {
					existingOrdersLink.dataset.ordersLink = '';
				} else {
					const ordersLink = document.createElement('a');
					ordersLink.href = 'carrinho.html';
					ordersLink.dataset.ordersLink = '';
					ordersLink.textContent = 'Meus pedidos';
					navigation.append(ordersLink);
				}
			}
			if (user.tipo === 'ADMIN' && navigation && !navigation.querySelector('[data-admin-link]')) {
				const adminLink = document.createElement('a');
				adminLink.href = 'admin.html';
				adminLink.dataset.adminLink = '';
				adminLink.textContent = 'Admin';
				navigation.append(adminLink);
			}

			const actions = document.querySelector('.header-actions');
			const existingLogout = document.querySelector('#admin-logout');
			if (actions && !actions.querySelector('[data-session-logout]')) {
				const logout = document.createElement('button');
				logout.type = 'button';
				logout.className = 'header-login header-logout';
				logout.dataset.sessionLogout = '';
				logout.textContent = 'Sair';
				actions.append(logout);
			} else if (existingLogout) {
				existingLogout.dataset.sessionLogout = '';
				existingLogout.classList.remove('hidden');
			}
		}

		document.querySelectorAll('[data-session-logout]').forEach((button) => {
			button.addEventListener('click', async () => {
				try {
					const response = await fetch('/api/logout', { method: 'POST' });
					if (!response.ok) throw new Error('O servidor não confirmou o encerramento da sessão.');
				} catch (error) {
					console.error('Falha ao encerrar a sessão no servidor.', error);
					window.alert(error instanceof Error ? error.message : 'Não foi possível encerrar a sessão.');
					return;
				}
				localStorage.removeItem(sessionKey);
				window.location.assign('index.html');
			});
		});

		let cart = [];
		try {
			cart = JSON.parse(localStorage.getItem('pizzaburguer-cart') || '[]');
		} catch (error) {
			console.error('Não foi possível ler o carrinho salvo.', error);
		}
		const count = cart.reduce((sum, item) => sum + (Number(item.quantity) || 0), 0);
		document.querySelectorAll('[data-cart-count]').forEach((element) => {
			element.textContent = count;
		});
	});
})();
