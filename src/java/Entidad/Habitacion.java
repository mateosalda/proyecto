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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "habitacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Habitacion.findAll", query = "SELECT h FROM Habitacion h"),
    @NamedQuery(name = "Habitacion.findByCodHab", query = "SELECT h FROM Habitacion h WHERE h.codHab = :codHab"),
    @NamedQuery(name = "Habitacion.findByTipoHab", query = "SELECT h FROM Habitacion h WHERE h.tipoHab = :tipoHab"),
    @NamedQuery(name = "Habitacion.findByDescripHab", query = "SELECT h FROM Habitacion h WHERE h.descripHab = :descripHab"),
    @NamedQuery(name = "Habitacion.findByPreciHab", query = "SELECT h FROM Habitacion h WHERE h.preciHab = :preciHab")})
public class Habitacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_hab")
    private Integer codHab;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "tipo_hab")
    private String tipoHab;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "descrip_hab")
    private String descripHab;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "preci_hab")
    private String preciHab;
    @ManyToMany(mappedBy = "habitacionList")
    private List<Decoracion> decoracionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codHab")
    private List<Estadohabitacion> estadohabitacionList;
    @JoinColumn(name = "nit_mot", referencedColumnName = "nit_mot")
    @ManyToOne(optional = false)
    private Motel nitMot;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codHab")
    private List<Reserva> reservaList;

    public Habitacion() {
    }

    public Habitacion(Integer codHab) {
        this.codHab = codHab;
    }

    public Habitacion(Integer codHab, String tipoHab, String descripHab, String preciHab) {
        this.codHab = codHab;
        this.tipoHab = tipoHab;
        this.descripHab = descripHab;
        this.preciHab = preciHab;
    }

    public Integer getCodHab() {
        return codHab;
    }

    public void setCodHab(Integer codHab) {
        this.codHab = codHab;
    }

    public String getTipoHab() {
        return tipoHab;
    }

    public void setTipoHab(String tipoHab) {
        this.tipoHab = tipoHab;
    }

    public String getDescripHab() {
        return descripHab;
    }

    public void setDescripHab(String descripHab) {
        this.descripHab = descripHab;
    }

    public String getPreciHab() {
        return preciHab;
    }

    public void setPreciHab(String preciHab) {
        this.preciHab = preciHab;
    }

    @XmlTransient
    public List<Decoracion> getDecoracionList() {
        return decoracionList;
    }

    public void setDecoracionList(List<Decoracion> decoracionList) {
        this.decoracionList = decoracionList;
    }

    @XmlTransient
    public List<Estadohabitacion> getEstadohabitacionList() {
        return estadohabitacionList;
    }

    public void setEstadohabitacionList(List<Estadohabitacion> estadohabitacionList) {
        this.estadohabitacionList = estadohabitacionList;
    }

    public Motel getNitMot() {
        return nitMot;
    }

    public void setNitMot(Motel nitMot) {
        this.nitMot = nitMot;
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
        hash += (codHab != null ? codHab.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Habitacion)) {
            return false;
        }
        Habitacion other = (Habitacion) object;
        if ((this.codHab == null && other.codHab != null) || (this.codHab != null && !this.codHab.equals(other.codHab))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Habitacion[ codHab=" + codHab + " ]";
    }
    
}
