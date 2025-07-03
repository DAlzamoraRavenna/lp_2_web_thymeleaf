package aplicacion;

import modelo.*;
import javax.persistence.*;
import java.util.*;

public class RegistroAlzamora {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MinimarketPU");
        EntityManager em = emf.createEntityManager();

        try {
            // Datos simulados (puedes reemplazar por Scanner si deseas)
            int idCliente = 1; // Asegúrate de que exista en la BD

            // Cargar cliente
            Cliente cliente = em.find(Cliente.class, idCliente);
            if (cliente == null) {
                System.out.println("❌ Cliente no encontrado.");
                return;
            }

            // Preparar productos comprados (ID + cantidad)
            Map<Integer, Integer> productosComprados = new HashMap<>();
            productosComprados.put(101, 2); // 2 unidades del producto 101
            productosComprados.put(102, 1); // 1 unidad del producto 102

            // Iniciar transacción
            em.getTransaction().begin();

            // Crear nueva factura
            Factura factura = new Factura();
            factura.setCliente(cliente);
            factura.setFecha(new Date());
            factura.setEstado(EstadoFactura.Emitido);

            double total = 0.0;
            List<DetalleFactura> detalles = new ArrayList<>();

            for (Map.Entry<Integer, Integer> entry : productosComprados.entrySet()) {
                int idProducto = entry.getKey();
                int cantidad = entry.getValue();

                Producto producto = em.find(Producto.class, idProducto);
                if (producto == null) {
                    System.out.println("❌ Producto con ID " + idProducto + " no encontrado.");
                    continue;
                }

                // Crear detalle
                DetalleFactura detalle = new DetalleFactura();
                DetalleFacturaId detId = new DetalleFacturaId();
                detalle.setId(detId);
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setFactura(factura);

                detalles.add(detalle);

                // Calcular subtotal
                total += producto.getPrecio() * cantidad;
            }

            factura.setTotal(total);

            // Persistir factura (esto genera el nro_factura automáticamente)
            em.persist(factura);

            // Asignar el ID a cada detalle y persistir
            for (DetalleFactura d : detalles) {
                DetalleFacturaId id = new DetalleFacturaId(factura.getNro_factura(), d.getProducto().getId_prod());
                d.setId(id);
                em.persist(d);
            }

            em.getTransaction().commit();

            System.out.println("✅ Factura registrada con éxito. Total: S/ " + total);

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
