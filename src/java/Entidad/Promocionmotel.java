/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Entidad;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author cpe
 */
@Entity
@Table(name = "promocionmotel")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Promocionmotel.findAll", query = "SELECT p FROM Promocionmotel p"),
    @NamedQuery(name = "Promocionmotel.findByCodpromoMot", query = "SELECT p FROM Promocionmotel p WHERE p.codpromoMot = :codpromoMot")})
public class Promocionmotel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_promoMot")
    private Integer codpromoMot;
    @JoinColumn(name = "cod_promo", referencedColumnName = "cod_promo")
    @ManyToOne(optional = false)
    private Promocion codPromo;
    @JoinColumn(name = "nit_mot", referencedColumnName = "nit_mot")
    @ManyToOne(optional = false)
    private Motel nitMot;

    public Promocionmotel() {
    }

    public Promocionmotel(Integer codpromoMot) {
        this.codpromoMot = codpromoMot;
    }

    public Integer getCodpromoMot() {
        return codpromoMot;
    }

    public void setCodpromoMot(Integer codpromoMot) {
        this.codpromoMot = codpromoMot;
    }

    public Promocion getCodPromo() {
        return codPromo;
    }

    public void setCodPromo(Promocion codPromo) {
        this.codPromo = codPromo;
    }

    public Motel getNitMot() {
        return nitMot;
    }

    public void setNitMot(Motel nitMot) {
        this.nitMot = nitMot;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codpromoMot != null ? codpromoMot.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Promocionmotel)) {
            return false;
        }
        Promocionmotel other = (Promocionmotel) object;
        if ((this.codpromoMot == null && other.codpromoMot != null) || (this.codpromoMot != null && !this.codpromoMot.equals(other.codpromoMot))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Promocionmotel[ codpromoMot=" + codpromoMot + " ]";
    }
    
}
