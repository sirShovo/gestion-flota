document.addEventListener('DOMContentLoaded', () => {
    
    // --- LOGIN LOGIC ---
    const loginForm = document.getElementById('login-form');
    const loginScreen = document.getElementById('login-screen');
    const appScreen = document.getElementById('app');
    const authError = document.getElementById('auth-error');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        
        document.getElementById('email-error').style.display = 'none';
        document.getElementById('password-error').style.display = 'none';
        authError.textContent = '';

        if (!email) {
            document.getElementById('email-error').style.display = 'block';
            return;
        }
        if (!password) {
            document.getElementById('password-error').style.display = 'block';
            return;
        }

        try {
            const res = await fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, passwordHash: password }) // Reusing passwordHash field for plain text transmission (backend handles bcrypt)
            });

            if (res.ok) {
                const user = await res.json();
                loginScreen.classList.add('hidden');
                appScreen.classList.remove('hidden');
                loadDashboard();
            } else {
                authError.textContent = 'Credenciales inválidas o usuario inactivo';
            }
        } catch (error) {
            authError.textContent = 'Error de conexión con el servidor';
        }
    });

    document.getElementById('logout-btn').addEventListener('click', () => {
        appScreen.classList.add('hidden');
        loginScreen.classList.remove('hidden');
        loginForm.reset();
    });

    // --- NAVIGATION LOGIC ---
    const navItems = document.querySelectorAll('.nav-item[data-target]');
    const views = document.querySelectorAll('.view');
    const viewTitle = document.getElementById('view-title');

    navItems.forEach(item => {
        item.addEventListener('click', () => {
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');
            
            views.forEach(view => view.classList.add('hidden'));
            const targetId = item.getAttribute('data-target');
            document.getElementById(targetId).classList.remove('hidden');
            
            viewTitle.textContent = item.textContent.trim();
            
            // Cargar datos según la vista
            switch(targetId) {
                case 'dashboard-view': loadDashboard(); break;
                case 'conductores-view': loadConductores(); break;
                case 'vehiculos-view': loadVehiculos(); break;
                case 'asignaciones-view': loadAsignaciones(); break;
                case 'mantenimientos-view': loadMantenimientos(); break;
                case 'usuarios-view': loadUsuarios(); break;
            }
        });
    });

    // --- DATA LOADING LOGIC ---
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
            const data = await res.json();
            const tbody = document.getElementById('tabla-conductores');
            tbody.innerHTML = '';
            data.forEach(c => {
                tbody.innerHTML += `
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.nombre}</td>
                        <td>${c.cedula}</td>
                        <td>${c.telefono || '-'}</td>
                        <td>${c.licencia || '-'}</td>
                        <td>${c.activo ? 'Activo' : 'Inactivo'}</td>
                    </tr>
                `;
            });
        }
    }

    async function loadVehiculos() {
        const res = await fetch('/api/vehiculos');
        if (res.ok) {
            const data = await res.json();
            const tbody = document.getElementById('tabla-vehiculos');
            tbody.innerHTML = '';
            data.forEach(v => {
                tbody.innerHTML += `
                    <tr>
                        <td>${v.placa}</td>
                        <td>${v.marca}</td>
                        <td>${v.modelo}</td>
                        <td>${v.tipo}</td>
                        <td><span style="font-weight:600; color: ${v.estado === 'DISPONIBLE' ? 'var(--success-color)' : 'var(--text-main)'}">${v.estado}</span></td>
                    </tr>
                `;
            });
        }
    }

    async function loadAsignaciones() {
        const res = await fetch('/api/asignaciones');
        if (res.ok) {
            const data = await res.json();
            const tbody = document.getElementById('tabla-asignaciones');
            tbody.innerHTML = '';
            data.forEach(a => {
                tbody.innerHTML += `
                    <tr>
                        <td>${a.id}</td>
                        <td>${a.conductorId}</td>
                        <td>${a.vehiculoId}</td>
                        <td>${new Date(a.fechaAsignacion).toLocaleString()}</td>
                        <td>${a.estado}</td>
                    </tr>
                `;
            });
        }
    }

    async function loadMantenimientos() {
        const res = await fetch('/api/mantenimientos');
        if (res.ok) {
            const data = await res.json();
            const tbody = document.getElementById('tabla-mantenimientos');
            tbody.innerHTML = '';
            data.forEach(m => {
                tbody.innerHTML += `
                    <tr>
                        <td>${m.id}</td>
                        <td>${m.vehiculoId}</td>
                        <td>${m.tipo}</td>
                        <td>$${(m.costo || 0).toFixed(2)}</td>
                        <td>${m.estado}</td>
                    </tr>
                `;
            });
        }
    }

    async function loadUsuarios() {
        const res = await fetch('/api/usuarios');
        if (res.ok) {
            const data = await res.json();
            const tbody = document.getElementById('tabla-usuarios');
            tbody.innerHTML = '';
            data.forEach(u => {
                tbody.innerHTML += `
                    <tr>
                        <td>${u.id}</td>
                        <td>${u.email}</td>
                        <td>${u.activo ? 'Activo' : 'Inactivo'}</td>
                    </tr>
                `;
            });
        }
    }
});
