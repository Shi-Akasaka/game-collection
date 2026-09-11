async function loadStats() {
    const response = await fetch("/api/games/stats");

    if (!response.ok) {
        document.getElementById("hardwareStats").innerHTML =
            "<p>集計データを取得できませんでした。</p>";
        return;
    }

    const stats = await response.json();

    document.getElementById("totalGames").textContent = stats.totalGames;
    document.getElementById("totalPrice").textContent =
        Number(stats.totalPrice).toLocaleString("ja-JP");
    document.getElementById("boxRate").textContent = stats.boxRate;
    document.getElementById("manualRate").textContent = stats.manualRate;

    const list = document.getElementById("hardwareStats");

    if (!stats.hardwareCounts || stats.hardwareCounts.length === 0) {
        list.innerHTML = "<p>ハード情報がありません。</p>";
        return;
    }

    const maxCount = Math.max(
        ...stats.hardwareCounts.map(item => Number(item.count)),
        1
    );

    list.innerHTML = stats.hardwareCounts.map(item => {
        const count = Number(item.count);
        const width = Math.round((count / maxCount) * 100);

        return `
            <a class="hardware-stat-row hardware-stat-link"
               href="/index.html?hardwareId=${encodeURIComponent(item.id)}"
               title="${escapeHtml(item.name)}のゲーム一覧を見る">
                <div class="hardware-stat-name">
                    <span>${escapeHtml(item.name)}</span>
                    <small>${escapeHtml(item.category)}</small>
                </div>
                <div class="hardware-stat-bar-wrap">
                    <div class="hardware-stat-bar" style="width:${width}%"></div>
                </div>
                <strong class="hardware-stat-count">${count}本</strong>
            </a>
        `;
    }).join("");
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

loadStats();
