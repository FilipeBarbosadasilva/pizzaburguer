document.querySelector('#recovery-form').addEventListener('submit', async (event) => {
	event.preventDefault();
	const form = event.currentTarget;
	const feedback = document.querySelector('#recovery-feedback');
	const button = form.querySelector('button[type="submit"]');
	const email = String(new FormData(form).get('email')).trim().toLowerCase();

	button.disabled = true;
	feedback.textContent = 'Enviando solicitação...';
	feedback.classList.remove('hidden');
	try {
		const response = await fetch('/api/recuperar-senha', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ email })
		});
		const result = await response.json();
		if (!response.ok) throw new Error(result.erro || 'Não foi possível solicitar a recuperação.');
		feedback.textContent = result.mensagem;
	} catch (error) {
		feedback.textContent = error instanceof Error
			? error.message
			: 'Não foi possível solicitar a recuperação. Tente novamente.';
	} finally {
		button.disabled = false;
	}
});
