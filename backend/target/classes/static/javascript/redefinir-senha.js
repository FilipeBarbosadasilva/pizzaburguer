document.querySelector('#reset-form').addEventListener('submit', async (event) => {
	event.preventDefault();
	const form = event.currentTarget;
	const feedback = document.querySelector('#reset-feedback');
	const button = form.querySelector('button[type="submit"]');
	const values = new FormData(form);
	const password = String(values.get('password'));
	const confirmPassword = String(values.get('confirmPassword'));
	const token = new URLSearchParams(window.location.search).get('token');

	if (!token) {
		feedback.textContent = 'Link de recuperação inválido ou expirado.';
		feedback.classList.remove('hidden');
		return;
	}
	if (password !== confirmPassword) {
		feedback.textContent = 'As senhas não coincidem.';
		feedback.classList.remove('hidden');
		return;
	}

	button.disabled = true;
	feedback.textContent = 'Atualizando sua senha...';
	feedback.classList.remove('hidden');
	try {
		const response = await fetch('/api/redefinir-senha', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ token, novaSenha: password })
		});
		const result = await response.json();
		if (!response.ok) throw new Error(result.erro || 'Não foi possível alterar a senha.');
		window.location.assign('login.html?senhaAtualizada=1');
	} catch (error) {
		feedback.textContent = error instanceof Error
			? error.message
			: 'Não foi possível alterar a senha. Tente novamente.';
	} finally {
		button.disabled = false;
	}
});
