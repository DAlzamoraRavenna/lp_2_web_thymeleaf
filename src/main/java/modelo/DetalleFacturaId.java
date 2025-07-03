package modelo;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Embeddable
public class DetalleFacturaId implements Serializable {

    private int nro_factura;
    private int id_prod;

    public DetalleFacturaId() {}

    public DetalleFacturaId(int nro_factura, int id_prod) {
        this.nro_factura = nro_factura;
        this.id_prod = id_prod;
    }

    // Getters y Setters
    public int getNro_factura() {
        return nro_factura;
    }

    public void setNro_factura(int nro_factura) {
        this.nro_factura = nro_factura;
    }

    public int getId_prod() {
        return id_prod;
    }

    public void setId_prod(int id_prod) {
        this.id_prod = id_prod;
    }

    // hashCode y equals (obligatorio en claves compuestas)
    @Override
    public int hashCode() {
        return Objects.hash(nro_factura, id_prod);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DetalleFacturaId)) return false;
        DetalleFacturaId that = (DetalleFacturaId) obj;
        return nro_factura == that.nro_factura && id_prod == that.id_prod;
    }
}
