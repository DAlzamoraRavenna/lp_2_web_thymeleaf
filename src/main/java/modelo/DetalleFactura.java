package modelo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "detallefact")
public class DetalleFactura implements Serializable {

    @EmbeddedId
    private DetalleFacturaId id;

    @ManyToOne
    @MapsId("nro_factura")
    @JoinColumn(name = "nro_factura")
    private Factura factura;

    @ManyToOne
    @MapsId("id_prod")
    @JoinColumn(name = "id_prod")
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    // Getters y Setters
    public DetalleFacturaId getId() {
        return id;
    }

    public void setId(DetalleFacturaId id) {
        this.id = id;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
