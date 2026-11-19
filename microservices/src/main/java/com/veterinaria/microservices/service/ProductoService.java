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
        if (producto.getActivo() == null) {
            producto.setActivo(true);
        }
        if (producto.getFechaIngreso() == null) {
            producto.setFechaIngreso(java.time.LocalDate.now());
        }
        if (producto.getStock() == null) {
            producto.setStock(0);
        }
        return productoRepository.save(producto);
    }


    public Producto actualizar(Integer id, Producto producto) {
        Optional<Producto> existenteOpt = productoRepository.findById(id);
        if (existenteOpt.isPresent()) {
            Producto existente = existenteOpt.get();
            if (producto.getNombre() != null) existente.setNombre(producto.getNombre());
            if (producto.getDescripcion() != null) existente.setDescripcion(producto.getDescripcion());
            if (producto.getPrecio() != null) existente.setPrecio(producto.getPrecio());
            if (producto.getStock() != null) existente.setStock(producto.getStock());
            if (producto.getActivo() != null) existente.setActivo(producto.getActivo());
            if (producto.getFechaIngreso() != null) existente.setFechaIngreso(producto.getFechaIngreso());
            return productoRepository.save(existente);
        }
        producto.setId(id);
        return productoRepository.save(producto);
    }


    public void eliminarPorId(Integer id) {
        productoRepository.deleteById(id);
    }

}
