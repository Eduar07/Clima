// ============================================================
// CONFIGURACIÓN
// URL base del backend Spring Boot
// ============================================================
const BACKEND_URL = 'http://localhost:5678/webhook/consultar-clima';

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
        const respuesta = await fetch(BACKEND_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ ciudad: ciudad })
        });

        const datos = await respuesta.json();

        // N8N responde directamente con los datos procesados
        if (datos.ciudad) {
            mostrarResultadoN8N(datos);
        } else {
            mostrarError('No se pudo obtener el clima');
        }

    } catch (error) {
        mostrarError('Error de conexión con N8N en puerto 5678.');
        console.error('Error:', error);
    } finally {
        mostrarCargando(false);
    }
}

// Función para mostrar resultado con la estructura que devuelve N8N
function mostrarResultadoN8N(datos) {

    document.getElementById('nombreCiudad').textContent =
        datos.ciudad;

    document.getElementById('nombrePais').textContent =
        datos.pais;

    document.getElementById('temperatura').textContent =
        datos.temperatura.toFixed(1);

    document.getElementById('condicionTexto').textContent =
        datos.descripcion;

    document.getElementById('condicionIcono').textContent =
        obtenerIconoClima(datos.descripcion, datos.hayLluvia);

    document.getElementById('humedad').textContent =
        datos.humedad + '%';

    document.getElementById('viento').textContent =
        datos.velocidadViento.toFixed(1) + ' km/h';

    document.getElementById('textoRecomendacion').textContent =
        datos.recomendacion;

    document.getElementById('resultado').classList.remove('oculto');
}

// ============================================================
// MOSTRAR RESULTADO
// Llena los elementos HTML con los datos del clima
// ============================================================
function mostrarResultado(datos) {

    // Llenar cada elemento con su dato correspondiente
    document.getElementById('nombreCiudad').textContent =
        datos.ciudad;

    document.getElementById('nombrePais').textContent =
        datos.pais;

    // toFixed(1) muestra un decimal: 14.5 en vez de 14.523
    document.getElementById('temperatura').textContent =
        datos.temperatura.toFixed(1);

    document.getElementById('condicionTexto').textContent =
        datos.condicionClimatica;

    // Obtener emoji según el tipo de clima
    document.getElementById('condicionIcono').textContent =
        obtenerIconoClima(datos.condicionClimatica, datos.hayLluvia);

    document.getElementById('humedad').textContent =
        datos.humedad + '%';

    document.getElementById('viento').textContent =
        datos.velocidadViento.toFixed(1) + ' km/h';

    document.getElementById('textoRecomendacion').textContent =
        datos.recomendacion;

    // Mostrar la sección de resultado quitando la clase oculto
    document.getElementById('resultado').classList.remove('oculto');
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
        const respuesta = await fetch(`${BACKEND_URL}/historial`);
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