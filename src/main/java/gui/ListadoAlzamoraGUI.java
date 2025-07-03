package gui;

import modelo.*;
import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListadoAlzamoraGUI extends JFrame {

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private EntityManagerFactory emf;

    public ListadoAlzamoraGUI() {
        setTitle("Listado de Facturas");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        emf = Persistence.createEntityManagerFactory("MinimarketPU");

        modeloTabla = new DefaultTableModel();
        modeloTabla.setColumnIdentifiers(new String[]{
            "Factura N°", "Fecha", "Cliente", "Estado", "Producto", "Cantidad", "Precio", "Subtotal", "Total"
        });

        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);

        add(scroll, BorderLayout.CENTER);

        cargarFacturas();
        setVisible(true);
    }

    private void cargarFacturas() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Factura> facturas = em.createQuery("SELECT f FROM Factura f", Factura.class).getResultList();

            for (Factura f : facturas) {
                String cliente = f.getCliente().getNombre();
                String estado = f.getEstado().toString();
                double total = f.getTotal();
                String fecha = f.getFecha().toString();

                List<DetalleFactura> detalles = em.createQuery(
                    "SELECT d FROM DetalleFactura d WHERE d.factura.nro_factura = :nro",
                    DetalleFactura.class)
                    .setParameter("nro", f.getNro_factura())
                    .getResultList();

                for (DetalleFactura d : detalles) {
                    Producto p = d.getProducto();
                    int cantidad = d.getCantidad();
                    double precio = p.getPrecio();
                    double subtotal = precio * cantidad;

                    modeloTabla.addRow(new Object[]{
                        f.getNro_factura(), fecha, cliente, estado,
                        p.getNom_prod(), cantidad, precio, subtotal, total
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        new ListadoAlzamoraGUI();
    }
}

