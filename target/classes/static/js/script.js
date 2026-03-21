document.addEventListener('DOMContentLoaded', () => {
    const themeToggle = document.getElementById('theme-toggle');
    const rootEl = document.documentElement;

    // Sync UI icon with current effective theme
    const currentEffectiveTheme = rootEl.getAttribute('data-theme') || localStorage.getItem('theme') || 'dark';
    rootEl.setAttribute('data-theme', currentEffectiveTheme);
    themeToggle.textContent = currentEffectiveTheme === 'dark' ? '☀️' : '🌙';

    themeToggle.addEventListener('click', () => {
        const currentTheme = rootEl.getAttribute('data-theme');
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';

        rootEl.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        themeToggle.textContent = newTheme === 'dark' ? '☀️' : '🌙';

        // Feedback animation
        const cards = document.querySelectorAll('.game-card, .auth-card, .game-area');
        cards.forEach(card => {
            card.style.transform = 'scale(0.99)';
            setTimeout(() => card.style.transform = '', 150);
        });
    });
});
