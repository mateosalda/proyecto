/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Entidad;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author cpe
 */
@Entity
@Table(name = "promocion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Promocion.findAll", query = "SELECT p FROM Promocion p"),
    @NamedQuery(name = "Promocion.findByCodPromo", query = "SELECT p FROM Promocion p WHERE p.codPromo = :codPromo"),
    @NamedQuery(name = "Promocion.findByTipoPromo", query = "SELECT p FROM Promocion p WHERE p.tipoPromo = :tipoPromo"),
    @NamedQuery(name = "Promocion.findByDescuento", query = "SELECT p FROM Promocion p WHERE p.descuento = :descuento")})
public class Promocion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_promo")
    private Integer codPromo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "tipo_promo")
    private String tipoPromo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "descuento")
    private double descuento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codPromo")
    private List<Promocionmotel> promocionmotelList;

    public Promocion() {
    }

    public Promocion(Integer codPromo) {
        this.codPromo = codPromo;
    }

    public Promocion(Integer codPromo, String tipoPromo, double descuento) {
        this.codPromo = codPromo;
        this.tipoPromo = tipoPromo;
        this.descuento = descuento;
    }

    public Integer getCodPromo() {
        return codPromo;
    }

    public void setCodPromo(Integer codPromo) {
        this.codPromo = codPromo;
    }

    public String getTipoPromo() {
        return tipoPromo;
    }

    public void setTipoPromo(String tipoPromo) {
        this.tipoPromo = tipoPromo;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    @XmlTransient
    public List<Promocionmotel> getPromocionmotelList() {
        return promocionmotelList;
    }

    public void setPromocionmotelList(List<Promocionmotel> promocionmotelList) {
        this.promocionmotelList = promocionmotelList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codPromo != null ? codPromo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Promocion)) {
            return false;
        }
        Promocion other = (Promocion) object;
        if ((this.codPromo == null && other.codPromo != null) || (this.codPromo != null && !this.codPromo.equals(other.codPromo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Promocion[ codPromo=" + codPromo + " ]";
    }
    
}
