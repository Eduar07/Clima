package com.vacacionesclima.application.usecase;

import com.vacacionesclima.domain.model.ConsultaClima;
import com.vacacionesclima.domain.model.DatosClima;
import com.vacacionesclima.domain.port.in.ConsultarClimaUseCase;
import com.vacacionesclima.domain.port.out.ClimaApiPort;
import com.vacacionesclima.domain.port.out.ClimaRepository;
import java.time.LocalDateTime;
import java.util.List;

// Esta clase implementa el puerto de entrada ConsultarClimaUseCase
// Orquesta el proceso completo: consulta la API, genera
// la recomendacion y guarda el resultado en la base de datos
//
// IMPORTANTE: esta clase NO tiene anotaciones de Spring
// como @Service ni @Component. Spring la conocera
// a traves de BeanConfiguration que crearemos mas adelante
public class ConsultarClimaUseCaseImpl implements ConsultarClimaUseCase {

    // Puertos de salida
    // Se inyectan por constructor, no con @Autowired
    private final ClimaApiPort climaApiPort;
    private final ClimaRepository climaRepository;

    // Constructor: Spring inyectara las implementaciones reales
    // cuando creemos BeanConfiguration
    public ConsultarClimaUseCaseImpl(ClimaApiPort climaApiPort,
                                     ClimaRepository climaRepository) {
        this.climaApiPort = climaApiPort;
        this.climaRepository = climaRepository;
    }

    // =========================================================
    // METODO PRINCIPAL
    // Orquesta todo el proceso de consulta del clima
    // =========================================================
    @Override
    public ConsultaClima consultarClima(String ciudad) {

        // PASO 1: Obtener datos reales del clima desde la API
        // climaApiPort no sabe si es OpenWeatherMap u otra API
        // solo llama al metodo del puerto
        DatosClima datos = climaApiPort.obtenerDatosClima(ciudad);

        // PASO 2: Aplicar las reglas de negocio para
        // generar la recomendacion de vestimenta
        String recomendacion = generarRecomendacion(
                datos.getTemperatura(),
                datos.getHayLluvia()
        );

        // PASO 3: Crear la entidad del dominio con todos los datos
        ConsultaClima consulta = new ConsultaClima(
                null,                        // ID lo asigna MySQL automaticamente
                datos.getCiudad(),
                datos.getPais(),
                datos.getTemperatura(),
                datos.getDescripcion(),
                datos.getHumedad(),
                datos.getVelocidadViento(),
                recomendacion,
                datos.getHayLluvia(),
                LocalDateTime.now()          // Fecha y hora actual
        );

        // PASO 4: Guardar en la base de datos a traves del puerto
        // climaRepository no sabe si es MySQL, MongoDB u otro
        return climaRepository.guardar(consulta);
    }

    // =========================================================
    // OBTENER HISTORIAL
    // Retorna las ultimas 10 consultas realizadas
    // =========================================================
    @Override
    public List<ConsultaClima> obtenerHistorial() {
        return climaRepository.obtenerUltimas(10);
    }

    // =========================================================
    // BUSCAR POR CIUDAD
    // Retorna todas las consultas de una ciudad especifica
    // =========================================================
    @Override
    public List<ConsultaClima> buscarPorCiudad(String ciudad) {
        return climaRepository.buscarPorCiudad(ciudad);
    }

    // =========================================================
    // LOGICA DE RECOMENDACION DE VESTIMENTA
    // Esta es la regla de negocio principal del proyecto
    // =========================================================
    private String generarRecomendacion(Double temperatura,
                                         Boolean hayLluvia) {

        StringBuilder recomendacion = new StringBuilder();

        // Reglas segun la temperatura
        if (temperatura < 10) {
            recomendacion.append(
                "Usa abrigo grueso, hace mucho frio. "
            );
        } else if (temperatura >= 10 && temperatura <= 17) {
            recomendacion.append(
                "Usa chaqueta o sueter. "
            );
        } else if (temperatura >= 18 && temperatura <= 25) {
            recomendacion.append(
                "Usa ropa comoda. "
            );
        } else {
            recomendacion.append(
                "Usa ropa ligera, hace calor. "
            );
        }

        // Regla adicional si hay lluvia
        // Se agrega independientemente de la temperatura
        if (Boolean.TRUE.equals(hayLluvia)) {
            recomendacion.append(
                "Lleva paraguas o impermeable."
            );
        }

        return recomendacion.toString();
    }
}