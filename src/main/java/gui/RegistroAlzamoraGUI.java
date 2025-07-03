package gui;

import modelo.*;
import javax.persistence.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


public class RegistroAlzamoraGUI extends JFrame {

    private JComboBox<String> comboClientes, comboProductos;
    private JTextField txtCantidad;
    private JTextArea txtArea;
    private JButton btnAgregar, btnRegistrar;

    private Map<Integer, Producto> mapaProductos = new HashMap<>();
    private Map<Integer, Integer> productosAgregados = new HashMap<>();
    private double totalVenta = 0.0;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MinimarketPU");

    public RegistroAlzamoraGUI() {
        setTitle("Registro de Facturas - Minimarket");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de Factura"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espacio entre componentes

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        comboClientes = new JComboBox<>();
        cargarClientes();
        panelFormulario.add(comboClientes, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Producto:"), gbc);

        gbc.gridx = 1;
        comboProductos = new JComboBox<>();
        cargarProductos();
        panelFormulario.add(comboProductos, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Cantidad:"), gbc);

        gbc.gridx = 1;
        txtCantidad = new JTextField(10);
        panelFormulario.add(txtCantidad, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        btnAgregar = new JButton("Agregar Producto");
        panelFormulario.add(btnAgregar, gbc);

        gbc.gridx = 1;
        btnRegistrar = new JButton("Registrar Factura");
        panelFormulario.add(btnRegistrar, gbc);

        txtArea = new JTextArea(10, 40);
        txtArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtArea);

        add(panelFormulario, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Eventos
        btnAgregar.addActionListener(e -> agregarProducto());
        btnRegistrar.addActionListener(e -> registrarFactura());

        setVisible(true);
    }

    private void cargarClientes() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
            for (Cliente c : clientes) {
                comboClientes.addItem(c.getId_cliente() + " - " + c.getNombre());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void cargarProductos() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Producto> productos = em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();
            for (Producto p : productos) {
                comboProductos.addItem(p.getId_prod() + " - " + p.getNom_prod());
                mapaProductos.put(p.getId_prod(), p);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void agregarProducto() {
        try {
            String seleccion = comboProductos.getSelectedItem().toString();
            int id = Integer.parseInt(seleccion.split(" - ")[0]);
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            Producto producto = mapaProductos.get(id);
            if (producto == null) {
                JOptionPane.showMessageDialog(this, "❌ Producto no encontrado.");
                return;
            }

            double subtotal = producto.getPrecio() * cantidad;
            totalVenta += subtotal;
            productosAgregados.put(id, productosAgregados.getOrDefault(id, 0) + cantidad);

            txtArea.append("🛒 " + producto.getNom_prod() + " x" + cantidad +
                    " → Subtotal: S/ " + String.format("%.2f", subtotal) + "\n");
            txtArea.append("💰 Total acumulado: S/ " + String.format("%.2f", totalVenta) + "\n\n");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Datos inválidos.");
        }
    }

    private void registrarFactura() {
        EntityManager em = emf.createEntityManager();

        try {
            String[] clienteSplit = comboClientes.getSelectedItem().toString().split(" - ");
            int idCliente = Integer.parseInt(clienteSplit[0]);
            Cliente cliente = em.find(Cliente.class, idCliente);

            em.getTransaction().begin();

            Factura factura = new Factura();
            factura.setCliente(cliente);
            factura.setFecha(new Date());
            factura.setEstado(EstadoFactura.Emitido);

            double total = 0.0;
            List<DetalleFactura> detalles = new ArrayList<>();

            for (Map.Entry<Integer, Integer> entry : productosAgregados.entrySet()) {
                int idProd = entry.getKey();
                int cantidad = entry.getValue();

                Producto prod = em.find(Producto.class, idProd);
                if (prod == null) continue;

                DetalleFactura detalle = new DetalleFactura();
                detalle.setProducto(prod);
                detalle.setCantidad(cantidad);
                detalle.setFactura(factura);
                detalles.add(detalle);
                total += prod.getPrecio() * cantidad;
            }

            factura.setTotal(total);
            em.persist(factura);

            for (DetalleFactura d : detalles) {
                DetalleFacturaId id = new DetalleFacturaId(factura.getNro_factura(), d.getProducto().getId_prod());
                d.setId(id);
                em.persist(d);
            }

            em.getTransaction().commit();
            JOptionPane.showMessageDialog(this, "✅ Factura registrada correctamente.\nTotal: S/ " + total);

            productosAgregados.clear();
            txtArea.setText("");
            txtCantidad.setText("");
            totalVenta = 0.0;

        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error al registrar.");
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        new RegistroAlzamoraGUI();
    }
}


