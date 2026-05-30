let currentUser = null;
let currentCrudEntity = null;
let currentCrudId = null;

// Caché de datos para fácil edición
const dataCache = {
    conductores: [],
    vehiculos: [],
    usuarios: [],
    asignaciones: [],
    mantenimientos: []
};

document.addEventListener('DOMContentLoaded', () => {
    
    // --- INICIALIZACIÓN DE TEMA ---
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.body.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);

    // --- LOGIN LOGIC ---
    const loginForm = document.getElementById('login-form');
    
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const authError = document.getElementById('auth-error');
        
        document.getElementById('email-error').style.display = 'none';
        document.getElementById('password-error').style.display = 'none';
        authError.textContent = '';

        if (!email) { document.getElementById('email-error').style.display = 'block'; return; }
        if (!password) { document.getElementById('password-error').style.display = 'block'; return; }

        try {
            const res = await fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, passwordHash: password }) 
            });

            if (res.ok) {
                currentUser = await res.json();
                document.getElementById('login-screen').classList.add('hidden');
                document.getElementById('app').classList.remove('hidden');
                
                // Set topbar user info
                document.getElementById('topbar-username').textContent = currentUser.nombre;
                document.getElementById('topbar-avatar').textContent = currentUser.nombre.charAt(0).toUpperCase();
                
                loadDashboard();
            } else {
                authError.textContent = 'Credenciales inválidas o usuario inactivo';
            }
        } catch (error) {
            authError.textContent = 'Error de conexión con el servidor';
        }
    });

    // --- USER DROPDOWN & LOGOUT ---
    const userMenuBtn = document.getElementById('user-menu-btn');
    const userDropdown = document.getElementById('user-dropdown');
    
    userMenuBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        userDropdown.classList.toggle('show');
    });
    
    document.addEventListener('click', () => userDropdown.classList.remove('show'));

    document.getElementById('logout-btn').addEventListener('click', () => {
        currentUser = null;
        document.getElementById('app').classList.add('hidden');
        document.getElementById('login-screen').classList.remove('hidden');
        loginForm.reset();
    });

    document.getElementById('btn-mi-perfil').addEventListener('click', () => {
        switchView('perfil-view');
        document.getElementById('perfil-nombre').value = currentUser.nombre;
        document.getElementById('perfil-email').value = currentUser.email;
    });

    // --- TEMA OSCURO ---
    document.getElementById('theme-toggle').addEventListener('click', () => {
        const current = document.body.getAttribute('data-theme');
        const next = current === 'light' ? 'dark' : 'light';
        document.body.setAttribute('data-theme', next);
        localStorage.setItem('theme', next);
        updateThemeIcon(next);
    });

    function updateThemeIcon(theme) {
        if (theme === 'dark') {
            document.getElementById('moon-icon').classList.add('hidden');
            document.getElementById('sun-icon').classList.remove('hidden');
        } else {
            document.getElementById('moon-icon').classList.remove('hidden');
            document.getElementById('sun-icon').classList.add('hidden');
        }
    }

    // --- BUSCADOR GLOBAL ---
    const searchInput = document.getElementById('global-search');
    const searchResults = document.getElementById('search-results');
    let searchTimeout;

    searchInput.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        const q = e.target.value;
        if (q.length < 2) {
            searchResults.style.display = 'none';
            return;
        }
        
        searchTimeout = setTimeout(async () => {
            const res = await fetch('/api/search?q=' + encodeURIComponent(q));
            if (res.ok) {
                const results = await res.json();
                searchResults.innerHTML = '';
                if (results.length === 0) {
                    searchResults.innerHTML = '<div class="search-result-item" style="color:var(--text-muted); cursor:default">No se encontraron resultados</div>';
                } else {
                    results.forEach(r => {
                        const div = document.createElement('div');
                        div.className = 'search-result-item';
                        div.innerHTML = `
                            <span class="search-result-type">${r.type}</span>
                            <div class="search-result-title">${r.title}</div>
                            <div class="search-result-subtitle">${r.subtitle}</div>
                        `;
                        div.addEventListener('click', () => {
                            searchResults.style.display = 'none';
                            searchInput.value = '';
                            switchView(r.target);
                        });
                        searchResults.appendChild(div);
                    });
                }
                searchResults.style.display = 'block';
            }
        }, 300);
    });
    
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.search-container')) {
            searchResults.style.display = 'none';
        }
    });

    // --- NAVEGACIÓN ---
    const navItems = document.querySelectorAll('.nav-item[data-target]');
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            switchView(item.getAttribute('data-target'));
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
        });
    });

    // --- PERFIL LOGIC ---
    document.getElementById('perfil-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const pass1 = document.getElementById('perfil-pass1').value;
        const pass2 = document.getElementById('perfil-pass2').value;
        
        document.getElementById('perfil-pass-error').style.display = 'none';
        
        if (pass1 || pass2) {
            if (pass1 !== pass2) {
                document.getElementById('perfil-pass-error').style.display = 'block';
                return;
            }
            // Cambiar contraseña
            await fetch(`/api/usuarios/${currentUser.id}/password`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ password: pass1 })
            });
        }
        
        // Cambiar datos
        const payload = {
            id: currentUser.id,
            nombre: document.getElementById('perfil-nombre').value,
            email: document.getElementById('perfil-email').value,
            passwordHash: '' // Not used for update info
        };
        const res = await fetch('/api/usuarios', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        
        if (res.ok) {
            alert('Perfil actualizado correctamente');
            currentUser.nombre = payload.nombre;
            currentUser.email = payload.email;
            document.getElementById('topbar-username').textContent = currentUser.nombre;
            document.getElementById('topbar-avatar').textContent = currentUser.nombre.charAt(0).toUpperCase();
        }
    });

});

// --- FUNCIONES GLOBALES DE NAVEGACIÓN ---
function switchView(targetId) {
    document.querySelectorAll('.view').forEach(view => view.classList.add('hidden'));
    document.getElementById(targetId).classList.remove('hidden');
    
    switch(targetId) {
        case 'dashboard-view': loadDashboard(); break;
        case 'conductores-view': loadConductores(); break;
        case 'vehiculos-view': loadVehiculos(); break;
        case 'asignaciones-view': loadAsignaciones(); break;
        case 'mantenimientos-view': loadMantenimientos(); break;
        case 'usuarios-view': loadUsuarios(); break;
    }
}

// --- CARGA DE DATOS (READ) ---
async function loadDashboard() {
    const res = await fetch('/api/dashboard/stats');
    if (res.ok) {
        const data = await res.json();
        document.getElementById('stat-asignaciones').textContent = data.asignacionesActivas || 0;
        document.getElementById('stat-costos').textContent = `$${(data.totalMantenimiento || 0).toFixed(2)}`;
    }
}

async function loadConductores() {
    const res = await fetch('/api/conductores');
    if (res.ok) {
        dataCache.conductores = await res.json();
        const tbody = document.getElementById('tabla-conductores');
        tbody.innerHTML = '';
        dataCache.conductores.forEach(c => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${c.id}</td>
                <td>${c.nombre}</td>
                <td>${c.cedula}</td>
                <td>${c.telefono || '-'}</td>
                <td>${c.licencia || '-'}</td>
                <td><span class="user-card-status ${c.activo ? 'status-active' : 'status-inactive'}">${c.activo ? 'Activo' : 'Inactivo'}</span></td>
            `;
            tr.onclick = () => showModalDetails('conductor', c);
            tbody.appendChild(tr);
        });
    }
}

async function loadVehiculos() {
    const res = await fetch('/api/vehiculos');
    if (res.ok) {
        dataCache.vehiculos = await res.json();
        const tbody = document.getElementById('tabla-vehiculos');
        tbody.innerHTML = '';
        dataCache.vehiculos.forEach(v => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${v.placa}</td>
                <td>${v.marca}</td>
                <td>${v.modelo}</td>
                <td>${v.tipo}</td>
                <td><span style="font-weight:600; color: ${v.estado === 'DISPONIBLE' ? 'var(--success-color)' : 'var(--text-main)'}">${v.estado}</span></td>
            `;
            tr.onclick = () => showModalDetails('vehiculo', v);
            tbody.appendChild(tr);
        });
    }
}

async function loadAsignaciones() {
    const res = await fetch('/api/asignaciones');
    if (res.ok) {
        dataCache.asignaciones = await res.json();
        const tbody = document.getElementById('tabla-asignaciones');
        tbody.innerHTML = '';
        dataCache.asignaciones.forEach(a => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${a.id}</td>
                <td>${a.conductorNombre}</td>
                <td>${a.vehiculoPlaca}</td>
                <td>${new Date(a.fechaAsignacion).toLocaleString()}</td>
                <td><span style="font-weight:600">${a.estado}</span></td>
            `;
            tr.onclick = () => showModalDetails('asignacion', a);
            tbody.appendChild(tr);
        });
    }
}

async function loadMantenimientos() {
    const res = await fetch('/api/mantenimientos');
    if (res.ok) {
        dataCache.mantenimientos = await res.json();
        const tbody = document.getElementById('tabla-mantenimientos');
        tbody.innerHTML = '';
        dataCache.mantenimientos.forEach(m => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${m.id}</td>
                <td>${m.vehiculoPlaca}</td>
                <td>${m.tipo}</td>
                <td>$${(m.costo || 0).toFixed(2)}</td>
                <td><span style="font-weight:600">${m.estado}</span></td>
            `;
            tr.onclick = () => showModalDetails('mantenimiento', m);
            tbody.appendChild(tr);
        });
    }
}

async function loadUsuarios() {
    const res = await fetch('/api/usuarios');
    if (res.ok) {
        dataCache.usuarios = await res.json();
        const grid = document.getElementById('grid-usuarios');
        grid.innerHTML = '';
        dataCache.usuarios.forEach(u => {
            const div = document.createElement('div');
            div.className = 'user-card';
            div.innerHTML = `
                <div class="user-card-avatar">${u.nombre.charAt(0).toUpperCase()}</div>
                <div class="user-card-name">${u.nombre}</div>
                <div class="user-card-email">${u.email}</div>
                <span class="user-card-status ${u.activo ? 'status-active' : 'status-inactive'}">${u.activo ? 'Activo' : 'Inactivo'}</span>
            `;
            div.onclick = () => showModalDetails('usuario', u);
            grid.appendChild(div);
        });
    }
}

// --- LOGICA MODAL Y CRUD DINÁMICO ---
const modalConfig = {
    conductor: [
        { id: 'id', label: 'ID', type: 'text', readOnly: true, hiddenOnCreate: true },
        { id: 'nombre', label: 'Nombre', type: 'text', required: true },
        { id: 'cedula', label: 'Cédula', type: 'text', required: true },
        { id: 'telefono', label: 'Teléfono', type: 'text' },
        { id: 'licencia', label: 'Licencia', type: 'text' }
    ],
    vehiculo: [
        { id: 'id', label: 'ID', type: 'text', readOnly: true, hiddenOnCreate: true },
        { id: 'placa', label: 'Placa', type: 'text', required: true },
        { id: 'marca', label: 'Marca', type: 'text', required: true },
        { id: 'modelo', label: 'Modelo', type: 'text', required: true },
        { id: 'tipo', label: 'Tipo', type: 'text', required: true },
        { id: 'kilometraje', label: 'Kilometraje', type: 'number' },
        { id: 'estado', label: 'Estado', type: 'text', readOnly: true, hiddenOnCreate: true }
    ],
    usuario: [
        { id: 'id', label: 'ID', type: 'text', readOnly: true, hiddenOnCreate: true },
        { id: 'nombre', label: 'Nombre', type: 'text', required: true },
        { id: 'email', label: 'Correo', type: 'email', required: true },
        { id: 'passwordHash', label: 'Contraseña (solo al crear)', type: 'password', requiredOnCreate: true, hiddenOnEdit: true }
    ],
    asignacion: [
        { id: 'id', label: 'ID', type: 'text', readOnly: true, hiddenOnCreate: true },
        { id: 'conductorId', label: 'ID Conductor', type: 'number', required: true },
        { id: 'vehiculoId', label: 'ID Vehículo', type: 'number', required: true },
        { id: 'estado', label: 'Estado', type: 'text', readOnly: true, hiddenOnCreate: true }
    ],
    mantenimiento: [
        { id: 'id', label: 'ID', type: 'text', readOnly: true, hiddenOnCreate: true },
        { id: 'vehiculoId', label: 'ID Vehículo', type: 'number', required: true },
        { id: 'tipo', label: 'Tipo', type: 'text', required: true },
        { id: 'costo', label: 'Costo', type: 'number' },
        { id: 'descripcion', label: 'Descripción', type: 'textarea' },
        { id: 'estado', label: 'Estado', type: 'text', readOnly: true, hiddenOnCreate: true }
    ]
};

function openModal(entityType) {
    currentCrudEntity = entityType;
    currentCrudId = null;
    document.getElementById('modal-title').textContent = 'Nuevo ' + entityType.charAt(0).toUpperCase() + entityType.slice(1);
    
    const content = document.getElementById('modal-form-content');
    content.innerHTML = '';
    
    modalConfig[entityType].forEach(field => {
        if (field.hiddenOnCreate) return;
        
        const div = document.createElement('div');
        div.className = 'form-group';
        
        if (field.type === 'textarea') {
            div.innerHTML = `<label>${field.label}</label><textarea id="crud-${field.id}" ${field.required ? 'required' : ''}></textarea>`;
        } else {
            div.innerHTML = `<label>${field.label}</label><input type="${field.type}" id="crud-${field.id}" ${field.required || field.requiredOnCreate ? 'required' : ''}>`;
        }
        content.appendChild(div);
    });

    document.getElementById('modal-btn-delete').classList.add('hidden');
    document.getElementById('modal-btn-edit').classList.add('hidden');
    document.getElementById('modal-btn-save').classList.remove('hidden');

    document.getElementById('crud-modal').classList.add('show');
}

function showModalDetails(entityType, data) {
    currentCrudEntity = entityType;
    currentCrudId = data.id;
    document.getElementById('modal-title').textContent = 'Detalle de ' + entityType.charAt(0).toUpperCase() + entityType.slice(1);
    
    const content = document.getElementById('modal-form-content');
    content.innerHTML = '';
    
    modalConfig[entityType].forEach(field => {
        if (field.hiddenOnEdit) return;
        
        const div = document.createElement('div');
        div.className = 'form-group';
        
        if (field.type === 'textarea') {
            div.innerHTML = `<label>${field.label}</label><textarea id="crud-${field.id}" disabled>${data[field.id] || ''}</textarea>`;
        } else {
            div.innerHTML = `<label>${field.label}</label><input type="${field.type}" id="crud-${field.id}" value="${data[field.id] || ''}" disabled>`;
        }
        content.appendChild(div);
    });

    document.getElementById('modal-btn-delete').classList.remove('hidden');
    document.getElementById('modal-btn-delete').onclick = () => deleteEntity(entityType, data.id);
    
    const editBtn = document.getElementById('modal-btn-edit');
    editBtn.classList.remove('hidden');
    editBtn.onclick = () => enableEditMode();
    
    document.getElementById('modal-btn-save').classList.add('hidden');

    document.getElementById('crud-modal').classList.add('show');
}

function enableEditMode() {
    const inputs = document.querySelectorAll('#modal-form-content input, #modal-form-content textarea');
    inputs.forEach(input => {
        // No habilitar campos de solo lectura
        const fieldConfig = modalConfig[currentCrudEntity].find(f => `crud-${f.id}` === input.id);
        if (fieldConfig && !fieldConfig.readOnly) {
            input.removeAttribute('disabled');
        }
    });
    document.getElementById('modal-btn-edit').classList.add('hidden');
    document.getElementById('modal-btn-save').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('crud-modal').classList.remove('show');
}

async function submitCrudForm() {
    // Basic validation
    const form = document.getElementById('crud-form');
    if (!form.reportValidity()) return;

    const payload = {};
    modalConfig[currentCrudEntity].forEach(field => {
        const el = document.getElementById(`crud-${field.id}`);
        if (el) {
            payload[field.id] = el.value;
        }
    });

    if (currentCrudId) {
        payload.id = currentCrudId;
    }

    const method = currentCrudId ? 'PUT' : 'POST';
    // Pluralize for endpoint
    let endpoint = `/api/${currentCrudEntity}s`;
    if (currentCrudEntity === 'asignacion') endpoint = '/api/asignaciones';

    const res = await fetch(endpoint, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        closeModal();
        // Reload current view
        if (currentCrudEntity === 'conductor') loadConductores();
        if (currentCrudEntity === 'vehiculo') loadVehiculos();
        if (currentCrudEntity === 'asignacion') loadAsignaciones();
        if (currentCrudEntity === 'mantenimiento') loadMantenimientos();
        if (currentCrudEntity === 'usuario') loadUsuarios();
    } else {
        alert('Ocurrió un error al guardar los datos.');
    }
}

async function deleteEntity(entityType, id) {
    if(!confirm('¿Está seguro que desea borrar/desactivar este registro?')) return;
    
    let endpoint = `/api/${entityType}s/${id}`;
    if (entityType === 'asignacion') endpoint = `/api/asignaciones/${id}`;
    
    const res = await fetch(endpoint, { method: 'DELETE' });
    if(res.ok) {
        closeModal();
        if (entityType === 'conductor') loadConductores();
        if (entityType === 'vehiculo') loadVehiculos();
        if (entityType === 'asignacion') loadAsignaciones();
        if (entityType === 'mantenimiento') loadMantenimientos();
        if (entityType === 'usuario') loadUsuarios();
    } else {
        alert('Error al intentar eliminar.');
    }
}
