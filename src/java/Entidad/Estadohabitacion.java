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
@Table(name = "estadohabitacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estadohabitacion.findAll", query = "SELECT e FROM Estadohabitacion e"),
    @NamedQuery(name = "Estadohabitacion.findByCodestHab", query = "SELECT e FROM Estadohabitacion e WHERE e.codestHab = :codestHab")})
public class Estadohabitacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_estHab")
    private Integer codestHab;
    @JoinColumn(name = "cod_est", referencedColumnName = "cod_est")
    @ManyToOne(optional = false)
    private Estado codEst;
    @JoinColumn(name = "cod_hab", referencedColumnName = "cod_hab")
    @ManyToOne(optional = false)
    private Habitacion codHab;

    public Estadohabitacion() {
    }

    public Estadohabitacion(Integer codestHab) {
        this.codestHab = codestHab;
    }

    public Integer getCodestHab() {
        return codestHab;
    }

    public void setCodestHab(Integer codestHab) {
        this.codestHab = codestHab;
    }

    public Estado getCodEst() {
        return codEst;
    }

    public void setCodEst(Estado codEst) {
        this.codEst = codEst;
    }

    public Habitacion getCodHab() {
        return codHab;
    }

    public void setCodHab(Habitacion codHab) {
        this.codHab = codHab;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codestHab != null ? codestHab.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estadohabitacion)) {
            return false;
        }
        Estadohabitacion other = (Estadohabitacion) object;
        if ((this.codestHab == null && other.codestHab != null) || (this.codestHab != null && !this.codestHab.equals(other.codestHab))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Estadohabitacion[ codestHab=" + codestHab + " ]";
    }
    
}
