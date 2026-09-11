let currentGames = [];
let currentSort = "asc";

async function loadHardwares() {
    const response = await fetch("/api/hardwares");
    const hardwares = await response.json();
    const select = document.getElementById("hardwareFilter");

    let currentCategory = "";
    hardwares.forEach(hardware => {
        if (hardware.category !== currentCategory) {
            currentCategory = hardware.category;
        }

        const option = document.createElement("option");
        option.value = hardware.id;
        option.textContent = hardware.name;
        select.appendChild(option);
    });
}

async function loadMakers() {
    const response = await fetch("/api/games");
    const games = await response.json();
    const select = document.getElementById("makerFilter");
    const makers = [...new Set(
        games
            .map(game => game.maker)
            .filter(maker => maker && maker.trim())
    )].sort();
    makers.forEach(maker => {
        const option = document.createElement("option");
        option.value = maker;
        option.textContent = maker;
        select.appendChild(option);
    });
}

async function loadGames(keyword = "") {
    const hardwareId = document.getElementById("hardwareFilter").value;
    const maker = document.getElementById("makerFilter").value;
    const params = new URLSearchParams();

    if (keyword.trim()) {
        params.set("keyword", keyword.trim());
    }
	if (hardwareId) {
	    params.set("hardwareId", hardwareId);
	}

	if (maker) {
	    params.set("maker", maker);
	}

    const query = params.toString();
    const url = query ? "/api/games?" + query : "/api/games";

    const response = await fetch(url);
    const games = await response.json();
	
	currentGames = games;
	
	games.sort((a, b) => {
	    const result = (a.title || "").localeCompare(b.title || "", "ja");
	    return currentSort === "asc" ? result : -result;
	});

//    document.getElementById("count").textContent =
//        games.length + "本のゲームが登録されています";
	document.getElementById("count").textContent =
    "登録ゲーム ： " + games.length + "本";

    const list = document.getElementById("game-list");

    if (games.length === 0) {
        list.innerHTML = "<p>該当するゲームはありません。</p>";
        return;
    }

    list.innerHTML = `
        <div class="table-wrapper">
            <table class="game-table">
                <thead>
                    <tr>
                        <th>画像</th>
                        <th>ゲーム名</th>
                        <th>ハード</th>
                        <th>メーカー</th>
                        <th>箱</th>
                        <th>説明書</th>
                        <th>価格</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    ${games.map(game => {
                        const image = game.imageData
                            ? `<img src="/api/games/${game.id}/image" alt="${escapeHtml(game.title)}">`
                            : `<div class="table-no-image">画像なし</div>`;

                        return `
                            <tr>
                                <td class="game-table-image">
                                    <a href="/detail.html?id=${game.id}" class="image-link">
                                        ${image}
                                    </a>
                                </td>
                                <td>${escapeHtml(game.title)}</td>
                                <td>${escapeHtml(game.hardware ? game.hardware.name : "-")}</td>
                                <td>${escapeHtml(game.maker || "-")}</td>
                                <td class="mark">${game.box ? "○" : "×"}</td>
                                <td class="mark">${game.manual ? "○" : "×"}</td>
                                <td class="price">${game.price != null ? Number(game.price).toLocaleString() + "円" : "-"}</td>
                                <td>
                                    <a class="detail-button" href="/detail.html?id=${game.id}">詳細</a>
                                </td>
                            </tr>
                        `;
                    }).join("")}
                </tbody>
            </table>
        </div>
    `;
}

function searchGames() {
    const keyword = document.getElementById("keyword").value.trim();
    loadGames(keyword);
}

function clearFilters() {
    document.getElementById("keyword").value = "";
    document.getElementById("hardwareFilter").value = "";
    document.getElementById("makerFilter").value = "";
    loadGames();
}

document.getElementById("keyword").addEventListener("keydown", function(e) {
    if (e.key === "Enter") {
        searchGames();
    }
});

document.getElementById("hardwareFilter").addEventListener("change", function() {
    searchGames();
});

document.getElementById("makerFilter").addEventListener("change", function() {
    searchGames();
});

function sortGames(order) {
    currentSort = order;
    loadGames(document.getElementById("keyword").value.trim());
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

(async function init() {
    await loadHardwares();
    await loadMakers();
    const urlParams = new URLSearchParams(window.location.search);
    const hardwareId = urlParams.get("hardwareId");
    const maker = urlParams.get("maker") || "";
    const keyword = urlParams.get("keyword") || "";

	if (hardwareId) {
        document.getElementById("hardwareFilter").value = hardwareId;
    }

    if (maker) {
        document.getElementById("makerFilter").value = maker;
    }

    if (keyword) {
        document.getElementById("keyword").value = keyword;
    }

    await loadGames(keyword);

})();
