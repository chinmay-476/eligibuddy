const heroCopy = document.getElementById("hero-copy");
const publishCard = document.getElementById("publish-card");
const quickFacts = document.getElementById("quick-facts");
const sectionNav = document.getElementById("section-nav");
const docSections = document.getElementById("doc-sections");
const searchInput = document.getElementById("doc-search");
const repoLink = document.getElementById("repo-link");

const state = { rawSections: [], renderedSections: [] };

fetch("./data/developer-guide.json")
    .then((response) => {
        if (!response.ok) {
            throw new Error(`Failed to load documentation data: ${response.status}`);
        }
        return response.json();
    })
    .then((data) => {
        renderHero(data);
        renderNavigation(data.sections);
        renderSections(data.sections);
        setupSearch();
        setupReveal();
        setupActiveNav();
    })
    .catch((error) => {
        docSections.innerHTML = `
            <section class="empty-state">
                <p>Could not load the developer guide.</p>
                <p class="muted-note">${escapeHtml(error.message)}</p>
            </section>
        `;
    });

function renderHero(data) {
    document.title = `${data.meta.title} - Eligibuddy`;
    repoLink.href = data.meta.repoUrl;
    heroCopy.innerHTML = `
        <div>
            <p class="section-eyebrow">${escapeHtml(data.meta.eyebrow)}</p>
            <h1>${escapeHtml(data.meta.title)}</h1>
            <p>${escapeHtml(data.meta.summary)}</p>
            <div class="hero-meta">
                ${data.meta.highlights.map((item) => `<span class="hero-chip">${escapeHtml(item)}</span>`).join("")}
            </div>
        </div>
        <div>
            <div class="hero-actions">
                <a class="primary-button" href="#section-setup">${escapeHtml(data.meta.primaryActionLabel)}</a>
                <a class="ghost-button" href="#section-publish">${escapeHtml(data.meta.secondaryActionLabel)}</a>
            </div>
        </div>
    `;
    publishCard.innerHTML = `
        <p class="section-eyebrow">Public docs</p>
        <h3>${escapeHtml(data.publish.title)}</h3>
        <p>${escapeHtml(data.publish.description)}</p>
        <ul class="bullet-list">
            ${data.publish.points.map((point) => `<li>${escapeHtml(point)}</li>`).join("")}
        </ul>
        <p class="muted-note">Expected URL after GitHub Pages is enabled: <span class="inline-code">${escapeHtml(data.publish.expectedUrl)}</span></p>
    `;
    quickFacts.innerHTML = `
        <p class="section-eyebrow">Quick facts</p>
        <h3>Verified snapshot</h3>
        <div class="quick-facts-grid">
            ${data.quickFacts.map((fact) => `
                <div class="fact-row">
                    <strong>${escapeHtml(fact.value)}</strong>
                    <span>${escapeHtml(fact.label)}</span>
                </div>
            `).join("")}
        </div>
    `;
}

function renderNavigation(sections) {
    sectionNav.innerHTML = sections.map((section) => `<a href="#section-${escapeHtml(section.id)}">${escapeHtml(section.title)}</a>`).join("");
}

function renderSections(sections) {
    state.rawSections = sections;
    docSections.innerHTML = sections.map((section) => buildSection(section)).join("");
    state.renderedSections = Array.from(docSections.querySelectorAll(".section-card"));
    docSections.querySelectorAll("[data-copy]").forEach((button) => {
        button.addEventListener("click", () => copyCode(button));
    });
}

function buildSection(section) {
    return `
        <section class="section-card" id="section-${escapeHtml(section.id)}" data-searchable="${escapeHtml(getSearchContent(section))}">
            <div class="section-header">
                <div class="section-title">
                    <p class="section-eyebrow">${escapeHtml(section.eyebrow)}</p>
                    <h2>${escapeHtml(section.title)}</h2>
                    <p class="section-intro">${escapeHtml(section.intro)}</p>
                </div>
                ${section.tags?.length ? `
                    <div class="section-tags">
                        ${section.tags.map((tag) => `<span class="section-tag">${escapeHtml(tag)}</span>`).join("")}
                    </div>
                ` : ""}
            </div>
            ${section.cards?.length ? buildCards(section.cards) : ""}
            ${section.steps?.length ? buildSteps(section.steps) : ""}
            ${section.table ? buildTable(section.table) : ""}
            ${section.apiGroups?.length ? buildApiGroups(section.apiGroups) : ""}
            ${section.codeSamples?.length ? buildCodeSamples(section.codeSamples) : ""}
            ${section.checklist?.length ? buildChecklist(section.checklist) : ""}
            ${section.notes?.length ? buildNotes(section.notes) : ""}
        </section>
    `;
}

function buildCards(cards) {
    return `
        <div class="cards-grid">
            ${cards.map((card) => `
                <article class="info-card">
                    <h3>${escapeHtml(card.title)}</h3>
                    <p>${escapeHtml(card.body)}</p>
                    ${card.bullets?.length ? `<ul>${card.bullets.map((item) => `<li>${escapeHtml(item)}</li>`).join("")}</ul>` : ""}
                </article>
            `).join("")}
        </div>
    `;
}

function buildSteps(steps) {
    return `
        <div class="checklist-shell">
            <p class="table-label">Recommended flow</p>
            <div class="step-list">
                ${steps.map((step, index) => `
                    <div class="step-item">
                        <div class="step-index">${index + 1}</div>
                        <h3>${escapeHtml(step.title)}</h3>
                        <p>${escapeHtml(step.body)}</p>
                        ${step.note ? `<p class="muted-note">${escapeHtml(step.note)}</p>` : ""}
                    </div>
                `).join("")}
            </div>
        </div>
    `;
}

function buildTable(table) {
    return `
        <div class="table-shell">
            <p class="table-label">${escapeHtml(table.label || "Reference")}</p>
            <h3>${escapeHtml(table.title)}</h3>
            ${table.note ? `<p class="table-note">${escapeHtml(table.note)}</p>` : ""}
            <div class="table-scroll">
                <table>
                    <thead>
                        <tr>${table.columns.map((column) => `<th>${escapeHtml(column)}</th>`).join("")}</tr>
                    </thead>
                    <tbody>
                        ${table.rows.map((row) => `<tr>${row.map((cell) => `<td>${formatCell(cell)}</td>`).join("")}</tr>`).join("")}
                    </tbody>
                </table>
            </div>
        </div>
    `;
}

function buildApiGroups(apiGroups) {
    return apiGroups.map((group) => `
        <div class="table-shell">
            <p class="table-label">${escapeHtml(group.label)}</p>
            <h3>${escapeHtml(group.title)}</h3>
            <p class="table-note">${escapeHtml(group.intro)}</p>
            <div class="table-scroll">
                <table>
                    <thead>
                        <tr><th>Method</th><th>Path</th><th>Access</th><th>Purpose</th></tr>
                    </thead>
                    <tbody>
                        ${group.endpoints.map((endpoint) => `
                            <tr>
                                <td><span class="method-pill method-${endpoint.method.toLowerCase()}">${escapeHtml(endpoint.method)}</span></td>
                                <td><code>${escapeHtml(endpoint.path)}</code></td>
                                <td>${escapeHtml(endpoint.access)}</td>
                                <td>${escapeHtml(endpoint.purpose)}</td>
                            </tr>
                        `).join("")}
                    </tbody>
                </table>
            </div>
        </div>
    `).join("");
}

function buildCodeSamples(samples) {
    return samples.map((sample) => `
        <div class="code-shell">
            <div class="code-head">
                <div>
                    <p class="table-label">${escapeHtml(sample.label)}</p>
                    <h3>${escapeHtml(sample.title)}</h3>
                </div>
                <button type="button" class="copy-button" data-copy="${escapeHtml(sample.code)}">Copy</button>
            </div>
            ${sample.note ? `<p class="table-note">${escapeHtml(sample.note)}</p>` : ""}
            <pre><code>${escapeHtml(sample.code)}</code></pre>
        </div>
    `).join("");
}

function buildChecklist(items) {
    return `
        <div class="checklist-shell">
            <p class="table-label">Checklist</p>
            <ul class="checklist">${items.map((item) => `<li>${escapeHtml(item)}</li>`).join("")}</ul>
        </div>
    `;
}

function buildNotes(notes) {
    return `
        <div class="checklist-shell">
            <p class="table-label">Notes</p>
            <ul class="bullet-list">${notes.map((note) => `<li>${escapeHtml(note)}</li>`).join("")}</ul>
        </div>
    `;
}

function setupSearch() {
    searchInput.addEventListener("input", () => {
        const query = searchInput.value.trim().toLowerCase();
        let visibleCount = 0;
        state.renderedSections.forEach((section) => {
            const haystack = section.dataset.searchable.toLowerCase();
            const matches = !query || haystack.includes(query);
            section.hidden = !matches;
            if (matches) {
                visibleCount += 1;
            }
        });
        const existingEmpty = document.querySelector(".empty-state[data-generated='search']");
        if (existingEmpty) {
            existingEmpty.remove();
        }
        if (!visibleCount) {
            const empty = document.createElement("section");
            empty.className = "empty-state";
            empty.dataset.generated = "search";
            empty.innerHTML = `
                <p>No documentation blocks match <strong>${escapeHtml(query)}</strong>.</p>
                <p class="muted-note">Try terms like <span class="inline-code">mongodb</span>, <span class="inline-code">ollama</span>, <span class="inline-code">/api/jobs</span>, or <span class="inline-code">manage-exams</span>.</p>
            `;
            docSections.appendChild(empty);
        }
    });
}

function setupReveal() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add("is-visible");
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.12 });
    state.renderedSections.forEach((section) => observer.observe(section));
}

function setupActiveNav() {
    const links = Array.from(sectionNav.querySelectorAll("a"));
    const byId = new Map(links.map((link) => [link.getAttribute("href").slice(1), link]));
    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) {
                return;
            }
            links.forEach((link) => link.classList.remove("active"));
            const link = byId.get(entry.target.id);
            if (link) {
                link.classList.add("active");
            }
        });
    }, { rootMargin: "-35% 0px -55% 0px", threshold: 0 });
    state.renderedSections.forEach((section) => observer.observe(section));
}

function copyCode(button) {
    const text = button.getAttribute("data-copy") || "";
    navigator.clipboard.writeText(text).then(() => {
        const original = button.textContent;
        button.textContent = "Copied";
        setTimeout(() => { button.textContent = original; }, 1400);
    });
}

function getSearchContent(section) {
    return [
        section.eyebrow,
        section.title,
        section.intro,
        JSON.stringify(section.cards || []),
        JSON.stringify(section.steps || []),
        JSON.stringify(section.table || {}),
        JSON.stringify(section.apiGroups || []),
        JSON.stringify(section.codeSamples || []),
        JSON.stringify(section.checklist || []),
        JSON.stringify(section.notes || [])
    ].join(" ");
}

function formatCell(cell) {
    if (typeof cell !== "string") {
        return escapeHtml(String(cell));
    }
    if (cell.startsWith("code:")) {
        return `<code>${escapeHtml(cell.slice(5))}</code>`;
    }
    return escapeHtml(cell);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}
