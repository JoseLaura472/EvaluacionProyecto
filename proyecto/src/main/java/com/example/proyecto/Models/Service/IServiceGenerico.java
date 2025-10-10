package com.example.proyecto.Models.Service;

import java.util.List;

public interface IServiceGenerico<T, K> {
    List<T> findAll();

    T findById(K idEntidad);

    T save(T entidad);

    void deleteById(K idEntidad);
}
