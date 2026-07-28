package com.guardia.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Localizacion - Pruebas Unitarias de Modelo")
class LocalizacionTest {

    @Test
    @DisplayName("registrarGPS() debe asignar latitud y longitud")
    void debeRegistrarGPS() {
        Localizacion loc = new Localizacion();
        loc.registrarGPS(10.5, -66.9);
        assertThat(loc.getLatitud()).isEqualTo(10.5);
        assertThat(loc.getLongitud()).isEqualTo(-66.9);
    }

    @Test
    @DisplayName("registrarDireccionManual() debe asignar los cuatro campos de dirección")
    void debeRegistrarDireccionManual() {
        Localizacion loc = new Localizacion();
        loc.registrarDireccionManual("Libertador", "Catia", "Av. Principal", "Cerca de la plaza");

        assertThat(loc.getMunicipio()).isEqualTo("Libertador");
        assertThat(loc.getSector()).isEqualTo("Catia");
        assertThat(loc.getDireccion()).isEqualTo("Av. Principal");
        assertThat(loc.getReferencia()).isEqualTo("Cerca de la plaza");
    }

    @Nested
    @DisplayName("validarUbicacion()")
    class ValidarUbicacion {

        @Test
        @DisplayName("Debe retornar true cuando hay coordenadas GPS completas")
        void debeRetornarTrueConGPS() {
            Localizacion loc = Localizacion.builder().latitud(1.0).longitud(2.0).build();
            assertThat(loc.validarUbicacion()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar true cuando hay una dirección no vacía sin GPS")
        void debeRetornarTrueConDireccion() {
            Localizacion loc = Localizacion.builder().direccion("Calle 1").build();
            assertThat(loc.validarUbicacion()).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false sin GPS ni dirección")
        void debeRetornarFalseSinDatos() {
            Localizacion loc = new Localizacion();
            assertThat(loc.validarUbicacion()).isFalse();
        }
    }

    @Nested
    @DisplayName("obtenerResumenUbicacion()")
    class ObtenerResumenUbicacion {

        @Test
        @DisplayName("Debe construir el resumen a partir de municipio/sector/dirección cuando ambos existen")
        void debeConstruirResumenConMunicipioYSector() {
            Localizacion loc = Localizacion.builder()
                    .municipio("Libertador").sector("Catia").direccion("Av. Principal").build();
            assertThat(loc.obtenerResumenUbicacion()).isEqualTo("Libertador, Catia - Av. Principal");
        }

        @Test
        @DisplayName("Debe construir el resumen GPS cuando no hay municipio/sector pero sí coordenadas")
        void debeConstruirResumenGPS() {
            Localizacion loc = Localizacion.builder().latitud(10.5).longitud(-66.9).build();
            assertThat(loc.obtenerResumenUbicacion()).isEqualTo("GPS: 10.5, -66.9");
        }

        @Test
        @DisplayName("Debe indicar que la ubicación no está especificada cuando no hay ningún dato")
        void debeIndicarUbicacionNoEspecificada() {
            Localizacion loc = new Localizacion();
            assertThat(loc.obtenerResumenUbicacion()).isEqualTo("Ubicación no especificada");
        }
    }
}
