// ============================================================
// CONFIGURACIÓN
// URL base del backend Spring Boot
// ============================================================
const API_BASE_URL = '/api/clima';
const CONSULTAR_N8N_URL = `${API_BASE_URL}/consultar-n8n`;
const HISTORIAL_URL = `${API_BASE_URL}/historial`;

// ============================================================
// FUNCIÓN PRINCIPAL
// Se ejecuta cuando el usuario hace clic en Consultar
// o presiona Enter
// ============================================================
async function consultarClima() {

    const inputCiudad = document.getElementById('inputCiudad');
    const ciudad = inputCiudad.value.trim();

    if (!ciudad) {
        mostrarError('Por favor ingresa el nombre de una ciudad');
        return;
    }

    mostrarCargando(true);
    ocultarResultados();

    try {
        const respuesta = await fetch(CONSULTAR_N8N_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ ciudad: ciudad })
        });

        const datos = await respuesta.json();

        if (respuesta.ok && datos && datos.ciudad) {
            mostrarResultado(datos);
            cargarHistorial();
        } else {
            const mensaje = (datos && datos.mensaje)
                ? datos.mensaje
                : 'No se pudo obtener el clima desde n8n.';
            mostrarError(mensaje);
        }

    } catch (error) {
        mostrarError('Error de conexión con el backend o con n8n.');
        console.error('Error:', error);
    } finally {
        mostrarCargando(false);
    }
}

// ============================================================
// MOSTRAR RESULTADO
// Llena los elementos HTML con los datos del clima
// ============================================================
function mostrarResultado(datos) {

    const temperatura = typeof datos.temperatura === 'number'
        ? datos.temperatura
        : 0;
    const velocidadViento = typeof datos.velocidadViento === 'number'
        ? datos.velocidadViento
        : 0;
    const condicion = obtenerCondicion(datos);
    const humedadTexto = typeof datos.humedad === 'number'
        ? `${datos.humedad}%`
        : '--';
    const hayLluvia = Boolean(datos.hayLluvia);

    // Llenar cada elemento con su dato correspondiente
    document.getElementById('nombreCiudad').textContent =
        datos.ciudad;

    document.getElementById('nombrePais').textContent =
        datos.pais;

    // toFixed(1) muestra un decimal: 14.5 en vez de 14.523
    document.getElementById('temperatura').textContent =
        temperatura.toFixed(1);

    document.getElementById('condicionTexto').textContent =
        condicion;

    // Obtener emoji según el tipo de clima
    document.getElementById('condicionIcono').textContent =
        obtenerIconoClima(condicion, hayLluvia);

    document.getElementById('humedad').textContent =
        humedadTexto;

    document.getElementById('viento').textContent =
        velocidadViento.toFixed(1) + ' km/h';

    document.getElementById('textoRecomendacion').textContent =
        datos.recomendacion;

    // Mostrar la sección de resultado quitando la clase oculto
    document.getElementById('resultado').classList.remove('oculto');
}

function obtenerCondicion(datos) {
    const condicion = (datos.condicionClimatica ||
        datos.descripcion || '').trim();
    return condicion || 'Descripción no disponible';
}

// ============================================================
// OBTENER ÍCONO DEL CLIMA
// Devuelve un emoji según la condición climática
// ============================================================
function obtenerIconoClima(condicion, hayLluvia) {

    // Convertir a minúsculas para comparar sin importar mayúsculas
    const c = condicion.toLowerCase();

    if (hayLluvia || c.includes('lluvia') || c.includes('rain')) {
        return '🌧️';
    } else if (c.includes('tormenta') || c.includes('thunder')) {
        return '⛈️';
    } else if (c.includes('nieve') || c.includes('snow')) {
        return '❄️';
    } else if (c.includes('niebla') || c.includes('fog')) {
        return '🌫️';
    } else if (c.includes('nublado') || c.includes('cloud')) {
        return '☁️';
    } else if (c.includes('parcial') || c.includes('partly')) {
        return '⛅';
    } else {
        return '☀️';
    }
}

// ============================================================
// CARGAR HISTORIAL
// Obtiene y muestra las últimas consultas desde el backend
// ============================================================
async function cargarHistorial() {

    try {
        // Llamar al endpoint GET del historial
        const respuesta = await fetch(HISTORIAL_URL);
        const historial = await respuesta.json();

        const listaHistorial = document.getElementById('listaHistorial');

        // Si no hay consultas mostrar mensaje
        if (!historial || historial.length === 0) {
            listaHistorial.innerHTML =
                '<p class="sin-datos">Aún no hay consultas registradas</p>';
            return;
        }

        // Construir el HTML de cada item del historial
        listaHistorial.innerHTML = historial.map(item => `
            <div class="historial-item">
                <div>
                    <span class="historial-ciudad">
                        ${item.ciudad}, ${item.pais || ''}
                    </span>
                    <br>
                    <span class="historial-condicion">
                        ${item.condicionClimatica || ''}
                    </span>
                </div>
                <span class="historial-temp">
                    ${item.temperatura.toFixed(1)}°C
                </span>
            </div>
        `).join('');

    } catch (error) {
        // Si no se puede cargar el historial no mostramos error
        // para no confundir al usuario
        console.log('No se pudo cargar el historial:', error);
    }
}

// ============================================================
// MOSTRAR U OCULTAR EL SPINNER DE CARGA
// ============================================================
function mostrarCargando(mostrar) {
    const cargando = document.getElementById('cargando');
    if (mostrar) {
        cargando.classList.remove('oculto');
    } else {
        cargando.classList.add('oculto');
    }
}

// ============================================================
// OCULTAR RESULTADO Y ERROR ANTERIORES
// Se llama antes de cada nueva consulta
// ============================================================
function ocultarResultados() {
    document.getElementById('resultado').classList.add('oculto');
    document.getElementById('error').classList.add('oculto');
}

// ============================================================
// MOSTRAR MENSAJE DE ERROR
// ============================================================
function mostrarError(mensaje) {
    document.getElementById('textoError').textContent = mensaje;
    document.getElementById('error').classList.remove('oculto');
}

// ============================================================
// EVENTO: Buscar al presionar la tecla Enter
// ============================================================
document.getElementById('inputCiudad')
    .addEventListener('keypress', function(evento) {
        if (evento.key === 'Enter') {
            consultarClima();
        }
    });

// ============================================================
// EVENTO: Cargar historial cuando la página termina de cargar
// ============================================================
document.addEventListener('DOMContentLoaded', function() {
    cargarHistorial();
});
