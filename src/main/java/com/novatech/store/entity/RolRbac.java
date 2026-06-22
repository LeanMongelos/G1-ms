package com.novatech.store.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RolRbac")
public class RolRbac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    /** SUPERADMIN, GERENTE, VENDEDOR, CLIENTE */
    @Column(name = "clave", unique = true)
    private String clave;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    /** No se puede eliminar ni renombrar clave */
    @Column(name = "es_sistema")
    private Boolean esSistema;

    /** Wildcard: todos los permisos */
    @Column(name = "acceso_total")
    private Boolean accesoTotal;

    /** Puede entrar al panel /admin */
    @Column(name = "acceso_panel")
    private Boolean accesoPanel;

    public RolRbac() {
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsSistema() {
        return esSistema;
    }

    public void setEsSistema(Boolean esSistema) {
        this.esSistema = esSistema;
    }

    public Boolean getAccesoTotal() {
        return accesoTotal;
    }

    public void setAccesoTotal(Boolean accesoTotal) {
        this.accesoTotal = accesoTotal;
    }

    public Boolean getAccesoPanel() {
        return accesoPanel;
    }

    public void setAccesoPanel(Boolean accesoPanel) {
        this.accesoPanel = accesoPanel;
    }
}
