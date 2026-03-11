package com.vacacionesclima.infrastructure.adapter.out.api;

import com.vacacionesclima.domain.model.DatosClima;
import com.vacacionesclima.domain.port.out.ClimaApiPort;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

// @Component le dice a Spring que esta clase
// es un componente que debe gestionar
@Component

// Esta clase implementa ClimaApiPort del dominio
// Es el PUENTE entre el dominio y OpenWeatherMap
// Si mañana cambiamos de API, solo cambia esta clase
// el dominio y la aplicacion no se tocan
public class OpenWeatherMapAdapter implements ClimaApiPort {

    // @Value lee el valor desde application.properties
    // ${openweather.api.key} busca esa propiedad en el archivo
    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.url}")
    private String apiUrl;

    // RestTemplate es la clase de Spring para hacer
    // peticiones HTTP a APIs externas
    private final RestTemplate restTemplate = new RestTemplate();

    // =========================================================
    // METODO PRINCIPAL
    // Consulta el clima de una ciudad en OpenWeatherMap
    // y retorna un objeto DatosClima con los datos parseados
    // =========================================================
    @Override
    public DatosClima obtenerDatosClima(String ciudad) {

        try {
            // PASO 1: Construir la URL completa de la API
            // units=metric  → temperatura en Celsius
            // lang=es        → descripcion del clima en español
            String url = String.format(
                "%s?q=%s&appid=%s&units=metric&lang=es",
                apiUrl, ciudad, apiKey
            );

            // PASO 2: Llamar a OpenWeatherMap y obtener el JSON
            // getForObject() hace el GET y convierte a String
            String jsonRespuesta = restTemplate
                    .getForObject(url, String.class);

            // PASO 3: Parsear el JSON de respuesta
            // Estructura del JSON de OpenWeatherMap:
            // {
            //   "name": "Bogota",
            //   "sys": { "country": "CO" },
            //   "main": {
            //     "temp": 14.5,
            //     "humidity": 80
            //   },
            //   "weather": [{
            //     "id": 803,
            //     "description": "muy nuboso"
            //   }],
            //   "wind": { "speed": 4.1 }
            // }
            JSONObject json = new JSONObject(jsonRespuesta);

            // Extraer objeto "main" que tiene temperatura y humedad
            JSONObject main = json.getJSONObject("main");

            // Extraer el primer elemento del array "weather"
            JSONArray weatherArray = json.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);

            // Extraer objeto "wind" que tiene velocidad del viento
            JSONObject wind = json.getJSONObject("wind");

            // Extraer objeto "sys" que tiene el país
            JSONObject sys = json.getJSONObject("sys");

            // PASO 4: Obtener los valores individuales del JSON
            // Nombre real de la ciudad según OpenWeatherMap
            String nombreCiudad = json.getString("name");

            // Código del país: CO, US, MX, ES, etc.
            String pais = sys.getString("country");

            // Temperatura en Celsius (por units=metric)
            Double temperatura = main.getDouble("temp");

            // Porcentaje de humedad
            Integer humedad = main.getInt("humidity");

            // Descripción del clima en español (por lang=es)
            // Ejemplos: "lluvia ligera", "cielo despejado"
            String descripcion = weather.getString("description");

            // ID numérico del clima en OpenWeatherMap
            // Se usa para detectar si hay lluvia
            int weatherId = weather.getInt("id");

            // Velocidad del viento en m/s
            // Multiplicamos por 3.6 para convertir a km/h
            Double velocidadViento = wind.getDouble("speed") * 3.6;

            // PASO 5: Detectar si hay lluvia
            // OpenWeatherMap usa estos rangos de ID:
            // 200-299 → Tormenta eléctrica
            // 300-399 → Llovizna
            // 500-599 → Lluvia
            // 600-699 → Nieve
            // Todos estos se consideran "hay lluvia"
            Boolean hayLluvia = (weatherId >= 200 && weatherId < 700);

            // PASO 6: Retornar el Value Object DatosClima
            return new DatosClima(
                    nombreCiudad,
                    pais,
                    temperatura,
                    descripcion,
                    humedad,
                    velocidadViento,
                    hayLluvia
            );

        } catch (HttpClientErrorException.NotFound e) {
            // Error 404: la ciudad no existe en OpenWeatherMap
            throw new RuntimeException(
                "Ciudad no encontrada: " + ciudad +
                ". Verifica que el nombre sea correcto."
            );

        } catch (HttpClientErrorException.Unauthorized e) {
            // Error 401: la API Key no es válida
            throw new RuntimeException(
                "API Key de OpenWeatherMap no valida. " +
                "Verifica la configuracion en application.properties."
            );

        } catch (Exception e) {
            // Cualquier otro error inesperado
            throw new RuntimeException(
                "Error al consultar el clima de: " + ciudad +
                ". Detalle: " + e.getMessage()
            );
        }
    }
}