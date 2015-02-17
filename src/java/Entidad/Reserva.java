/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Entidad;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author cpe
 */
@Entity
@Table(name = "reserva")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reserva.findAll", query = "SELECT r FROM Reserva r"),
    @NamedQuery(name = "Reserva.findByCodRes", query = "SELECT r FROM Reserva r WHERE r.codRes = :codRes"),
    @NamedQuery(name = "Reserva.findByPrecio", query = "SELECT r FROM Reserva r WHERE r.precio = :precio"),
    @NamedQuery(name = "Reserva.findByHora", query = "SELECT r FROM Reserva r WHERE r.hora = :hora"),
    @NamedQuery(name = "Reserva.findByFecha", query = "SELECT r FROM Reserva r WHERE r.fecha = :fecha")})
public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_res")
    private Integer codRes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio")
    private double precio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hora")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codRes")
    private List<Reservadetalle> reservadetalleList;
    @JoinColumn(name = "cod_dec", referencedColumnName = "cod_dec")
    @ManyToOne(optional = false)
    private Decoracion codDec;
    @JoinColumn(name = "cod_hab", referencedColumnName = "cod_hab")
    @ManyToOne(optional = false)
    private Habitacion codHab;
    @JoinColumn(name = "Cod_estRes", referencedColumnName = "Cod_estRes")
    @ManyToOne(optional = false)
    private Estadoreserva codestRes;
    @JoinColumn(name = "pin_cli", referencedColumnName = "pin_cli")
    @ManyToOne(optional = false)
    private Cliente pinCli;
    @JoinColumn(name = "nit_mot", referencedColumnName = "nit_mot")
    @ManyToOne(optional = false)
    private Motel nitMot;

    public Reserva() {
    }

    public Reserva(Integer codRes) {
        this.codRes = codRes;
    }

    public Reserva(Integer codRes, double precio, Date hora, Date fecha) {
        this.codRes = codRes;
        this.precio = precio;
        this.hora = hora;
        this.fecha = fecha;
    }

    public Integer getCodRes() {
        return codRes;
    }

    public void setCodRes(Integer codRes) {
        this.codRes = codRes;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @XmlTransient
    public List<Reservadetalle> getReservadetalleList() {
        return reservadetalleList;
    }

    public void setReservadetalleList(List<Reservadetalle> reservadetalleList) {
        this.reservadetalleList = reservadetalleList;
    }

    public Decoracion getCodDec() {
        return codDec;
    }

    public void setCodDec(Decoracion codDec) {
        this.codDec = codDec;
    }

    public Habitacion getCodHab() {
        return codHab;
    }

    public void setCodHab(Habitacion codHab) {
        this.codHab = codHab;
    }

    public Estadoreserva getCodestRes() {
        return codestRes;
    }

    public void setCodestRes(Estadoreserva codestRes) {
        this.codestRes = codestRes;
    }

    public Cliente getPinCli() {
        return pinCli;
    }

    public void setPinCli(Cliente pinCli) {
        this.pinCli = pinCli;
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
        hash += (codRes != null ? codRes.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reserva)) {
            return false;
        }
        Reserva other = (Reserva) object;
        if ((this.codRes == null && other.codRes != null) || (this.codRes != null && !this.codRes.equals(other.codRes))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Reserva[ codRes=" + codRes + " ]";
    }
    
}
