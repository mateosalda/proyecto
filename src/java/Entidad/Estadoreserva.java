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
@Table(name = "estadoreserva")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estadoreserva.findAll", query = "SELECT e FROM Estadoreserva e"),
    @NamedQuery(name = "Estadoreserva.findByCodestRes", query = "SELECT e FROM Estadoreserva e WHERE e.codestRes = :codestRes"),
    @NamedQuery(name = "Estadoreserva.findByEstadoReserva", query = "SELECT e FROM Estadoreserva e WHERE e.estadoReserva = :estadoReserva")})
public class Estadoreserva implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "Cod_estRes")
    private Integer codestRes;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "estadoReserva")
    private String estadoReserva;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codestRes")
    private List<Reserva> reservaList;

    public Estadoreserva() {
    }

    public Estadoreserva(Integer codestRes) {
        this.codestRes = codestRes;
    }

    public Estadoreserva(Integer codestRes, String estadoReserva) {
        this.codestRes = codestRes;
        this.estadoReserva = estadoReserva;
    }

    public Integer getCodestRes() {
        return codestRes;
    }

    public void setCodestRes(Integer codestRes) {
        this.codestRes = codestRes;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    @XmlTransient
    public List<Reserva> getReservaList() {
        return reservaList;
    }

    public void setReservaList(List<Reserva> reservaList) {
        this.reservaList = reservaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codestRes != null ? codestRes.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estadoreserva)) {
            return false;
        }
        Estadoreserva other = (Estadoreserva) object;
        if ((this.codestRes == null && other.codestRes != null) || (this.codestRes != null && !this.codestRes.equals(other.codestRes))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Estadoreserva[ codestRes=" + codestRes + " ]";
    }
    
}
