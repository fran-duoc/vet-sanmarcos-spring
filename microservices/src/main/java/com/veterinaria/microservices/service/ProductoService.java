package com.veterinaria.microservices.service;

import com.veterinaria.microservices.model.Producto;
import com.veterinaria.microservices.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;


    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }


    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }


    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }


    public Producto actualizar(Integer id, Producto producto) {
        producto.setId(id);
        return productoRepository.save(producto);
    }


    public void eliminarPorId(Integer id) {
        productoRepository.deleteById(id);
    }

}
