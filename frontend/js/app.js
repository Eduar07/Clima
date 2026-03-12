// ============================================================
// CONFIGURACIÓN
// URL base del backend Spring Boot
// ============================================================
const BACKEND_URL = 'http://localhost:8080/api/clima';

// ============================================================
// FUNCIÓN PRINCIPAL
// Se ejecuta cuando el usuario hace clic en Consultar
// o presiona Enter
// ============================================================
async function consultarClima() {

    // 1. Leer la ciudad que escribió el usuario
    const inputCiudad = document.getElementById('inputCiudad');
    const ciudad = inputCiudad.value.trim();

    // 2. Validar que no esté vacío
    if (!ciudad) {
        mostrarError('Por favor ingresa el nombre de una ciudad');
        return;
    }

    // 3. Preparar la pantalla para la consulta
    mostrarCargando(true);
    ocultarResultados();

    try {
        // 4. Llamar al backend con fetch
        // fetch() hace peticiones HTTP desde JavaScript
        const respuesta = await fetch(`${BACKEND_URL}/consultar`, {
            method: 'POST',
            headers: {
                // Le decimos al backend que enviamos JSON
                'Content-Type': 'application/json'
            },
            // Convertir el objeto JavaScript a texto JSON
            // { ciudad: "Bogota" } → '{"ciudad":"Bogota"}'
            body: JSON.stringify({ ciudad: ciudad })
        });

        // 5. Convertir la respuesta de texto JSON a objeto JavaScript
        const datos = await respuesta.json();

        // 6. Verificar si la consulta fue exitosa
        if (datos.exitoso) {
            mostrarResultado(datos);
            cargarHistorial();
        } else {
            mostrarError(
                datos.mensaje ||
                'No se pudo obtener el clima de la ciudad indicada'
            );
        }

    } catch (error) {
        // Si el backend no está corriendo o hay error de red
        mostrarError(
            'Error de conexión. Verifica que el servidor ' +
            'esté corriendo en el puerto 8080.'
        );
        console.error('Error:', error);

    } finally {
        // Esto se ejecuta SIEMPRE al terminar
        // con éxito o con error
        mostrarCargando(false);
    }
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