package com.novatech.store.dto;

public class PlantillaRenderResponse {

    private String html;
    private String css;
    private Integer idPlantilla;
    private String tipo;

    public PlantillaRenderResponse() {
    }

    public PlantillaRenderResponse(String html, String css, Integer idPlantilla, String tipo) {
        this.html = html;
        this.css = css;
        this.idPlantilla = idPlantilla;
        this.tipo = tipo;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public Integer getIdPlantilla() {
        return idPlantilla;
    }

    public void setIdPlantilla(Integer idPlantilla) {
        this.idPlantilla = idPlantilla;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
