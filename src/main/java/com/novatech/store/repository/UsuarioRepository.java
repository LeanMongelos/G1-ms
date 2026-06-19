package com.novatech.store.repository;

import com.novatech.store.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Un "repositorio" es la clase que habla con la base de datos.
// Al extender JpaRepository ya tenemos GRATIS los metodos basicos:
// findAll() (traer todos), findById() (traer uno por id), save() (guardar), deleteById() (borrar), etc.
// JpaRepository<Usuario, Integer>: trabaja con la entidad Usuario y su id es de tipo Integer.
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Metodo extra: buscar un usuario por su email.
    // Spring entiende el nombre "findByEmail" y arma la consulta solo.
    // Optional significa que el resultado puede existir o no.
    Optional<Usuario> findByEmail(String email);
}
