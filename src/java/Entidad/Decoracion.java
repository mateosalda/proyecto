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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "decoracion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Decoracion.findAll", query = "SELECT d FROM Decoracion d"),
    @NamedQuery(name = "Decoracion.findByCodDec", query = "SELECT d FROM Decoracion d WHERE d.codDec = :codDec"),
    @NamedQuery(name = "Decoracion.findByTipodec", query = "SELECT d FROM Decoracion d WHERE d.tipodec = :tipodec"),
    @NamedQuery(name = "Decoracion.findByPrecioDec", query = "SELECT d FROM Decoracion d WHERE d.precioDec = :precioDec")})
public class Decoracion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_dec")
    private Integer codDec;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Tipo_dec")
    private String tipodec;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "precio_dec")
    private String precioDec;
    @JoinTable(name = "decoracionhabitacion", joinColumns = {
        @JoinColumn(name = "cod_dec", referencedColumnName = "cod_dec")}, inverseJoinColumns = {
        @JoinColumn(name = "cod_hab", referencedColumnName = "cod_hab")})
    @ManyToMany
    private List<Habitacion> habitacionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codDec")
    private List<Reserva> reservaList;

    public Decoracion() {
    }

    public Decoracion(Integer codDec) {
        this.codDec = codDec;
    }

    public Decoracion(Integer codDec, String tipodec, String precioDec) {
        this.codDec = codDec;
        this.tipodec = tipodec;
        this.precioDec = precioDec;
    }

    public Integer getCodDec() {
        return codDec;
    }

    public void setCodDec(Integer codDec) {
        this.codDec = codDec;
    }

    public String getTipodec() {
        return tipodec;
    }

    public void setTipodec(String tipodec) {
        this.tipodec = tipodec;
    }

    public String getPrecioDec() {
        return precioDec;
    }

    public void setPrecioDec(String precioDec) {
        this.precioDec = precioDec;
    }

    @XmlTransient
    public List<Habitacion> getHabitacionList() {
        return habitacionList;
    }

    public void setHabitacionList(List<Habitacion> habitacionList) {
        this.habitacionList = habitacionList;
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
        hash += (codDec != null ? codDec.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Decoracion)) {
            return false;
        }
        Decoracion other = (Decoracion) object;
        if ((this.codDec == null && other.codDec != null) || (this.codDec != null && !this.codDec.equals(other.codDec))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Decoracion[ codDec=" + codDec + " ]";
    }
    
}
