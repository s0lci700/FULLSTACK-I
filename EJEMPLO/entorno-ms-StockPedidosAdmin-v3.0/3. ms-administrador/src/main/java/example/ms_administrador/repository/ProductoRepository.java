package example.ms_administrador.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import example.ms_administrador.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{

}
