package com.novatech.store.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RolPermiso", uniqueConstraints = @UniqueConstraint(columnNames = {"rol_clave", "permiso_clave"}))
public class RolPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_permiso")
    private Integer idRolPermiso;

    @Column(name = "rol_clave")
    private String rolClave;

    @Column(name = "permiso_clave")
    private String permisoClave;

    public RolPermiso() {
    }

    public Integer getIdRolPermiso() {
        return idRolPermiso;
    }

    public void setIdRolPermiso(Integer idRolPermiso) {
        this.idRolPermiso = idRolPermiso;
    }

    public String getRolClave() {
        return rolClave;
    }

    public void setRolClave(String rolClave) {
        this.rolClave = rolClave;
    }

    public String getPermisoClave() {
        return permisoClave;
    }

    public void setPermisoClave(String permisoClave) {
        this.permisoClave = permisoClave;
    }
}
