package com.guardia.core.dto.request;

public record LocalizacionRequest(
        String municipio,
        String sector,
        String direccion,
        String referencia,
        Double latitud,
        Double longitud
) {
    public String getMunicipio() { return this.municipio; }
    public String getSector() { return this.sector; }
    public String getDireccion() { return this.direccion; }
    public String getReferencia() { return this.referencia; }
    public Double getLatitud() { return this.latitud; }
    public Double getLongitud() { return this.longitud; }
}
