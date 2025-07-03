package aplicacion;

import modelo.*;

import javax.persistence.*;
import java.util.List;

public class ListadoAlzamora {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MinimarketPU");
        EntityManager em = emf.createEntityManager();

        try {
            // Obtener todas las facturas
            List<Factura> facturas = em.createQuery("SELECT f FROM Factura f", Factura.class).getResultList();

            if (facturas.isEmpty()) {
                System.out.println("📭 No hay facturas registradas.");
                return;
            }

            for (Factura f : facturas) {
                System.out.println("--------------------------------------------------");
                System.out.println("🧾 N° Factura: " + f.getNro_factura());
                System.out.println("📅 Fecha: " + f.getFecha());
                System.out.println("👤 Cliente: " + f.getCliente().getNombre());
                System.out.println("📦 Estado: " + f.getEstado());

                // Consultar productos relacionados a esta factura
                List<DetalleFactura> detalles = em.createQuery(
                        "SELECT d FROM DetalleFactura d WHERE d.factura.nro_factura = :nro",
                        DetalleFactura.class)
                        .setParameter("nro", f.getNro_factura())
                        .getResultList();

                System.out.println("🛒 Productos:");
                for (DetalleFactura d : detalles) {
                    System.out.println("  - " + d.getProducto().getNom_prod() +
                            " | Cantidad: " + d.getCantidad() +
                            " | Precio Unit: S/ " + d.getProducto().getPrecio());
                }

                System.out.println("💰 Total: S/ " + f.getTotal());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
