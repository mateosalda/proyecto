/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Entidad;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author cpe
 */
@Entity
@Table(name = "reservadetalle")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reservadetalle.findAll", query = "SELECT r FROM Reservadetalle r"),
    @NamedQuery(name = "Reservadetalle.findByCodresDet", query = "SELECT r FROM Reservadetalle r WHERE r.codresDet = :codresDet"),
    @NamedQuery(name = "Reservadetalle.findByPinCli", query = "SELECT r FROM Reservadetalle r WHERE r.pinCli = :pinCli"),
    @NamedQuery(name = "Reservadetalle.findByNitMot", query = "SELECT r FROM Reservadetalle r WHERE r.nitMot = :nitMot"),
    @NamedQuery(name = "Reservadetalle.findByCodHab", query = "SELECT r FROM Reservadetalle r WHERE r.codHab = :codHab"),
    @NamedQuery(name = "Reservadetalle.findByCodDec", query = "SELECT r FROM Reservadetalle r WHERE r.codDec = :codDec"),
    @NamedQuery(name = "Reservadetalle.findByCodestRes", query = "SELECT r FROM Reservadetalle r WHERE r.codestRes = :codestRes"),
    @NamedQuery(name = "Reservadetalle.findByFecha", query = "SELECT r FROM Reservadetalle r WHERE r.fecha = :fecha"),
    @NamedQuery(name = "Reservadetalle.findByPrecio", query = "SELECT r FROM Reservadetalle r WHERE r.precio = :precio")})
public class Reservadetalle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_resDet")
    private Integer codresDet;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pin_cli")
    private int pinCli;
    @Basic(optional = false)
    @NotNull
    @Column(name = "nit_mot")
    private int nitMot;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_hab")
    private int codHab;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_dec")
    private int codDec;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Cod_estRes")
    private int codestRes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Precio")
    private double precio;
    @JoinColumn(name = "cod_res", referencedColumnName = "cod_res")
    @ManyToOne(optional = false)
    private Reserva codRes;

    public Reservadetalle() {
    }

    public Reservadetalle(Integer codresDet) {
        this.codresDet = codresDet;
    }

    public Reservadetalle(Integer codresDet, int pinCli, int nitMot, int codHab, int codDec, int codestRes, Date fecha, double precio) {
        this.codresDet = codresDet;
        this.pinCli = pinCli;
        this.nitMot = nitMot;
        this.codHab = codHab;
        this.codDec = codDec;
        this.codestRes = codestRes;
        this.fecha = fecha;
        this.precio = precio;
    }

    public Integer getCodresDet() {
        return codresDet;
    }

    public void setCodresDet(Integer codresDet) {
        this.codresDet = codresDet;
    }

    public int getPinCli() {
        return pinCli;
    }

    public void setPinCli(int pinCli) {
        this.pinCli = pinCli;
    }

    public int getNitMot() {
        return nitMot;
    }

    public void setNitMot(int nitMot) {
        this.nitMot = nitMot;
    }

    public int getCodHab() {
        return codHab;
    }

    public void setCodHab(int codHab) {
        this.codHab = codHab;
    }

    public int getCodDec() {
        return codDec;
    }

    public void setCodDec(int codDec) {
        this.codDec = codDec;
    }

    public int getCodestRes() {
        return codestRes;
    }

    public void setCodestRes(int codestRes) {
        this.codestRes = codestRes;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Reserva getCodRes() {
        return codRes;
    }

    public void setCodRes(Reserva codRes) {
        this.codRes = codRes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codresDet != null ? codresDet.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservadetalle)) {
            return false;
        }
        Reservadetalle other = (Reservadetalle) object;
        if ((this.codresDet == null && other.codresDet != null) || (this.codresDet != null && !this.codresDet.equals(other.codresDet))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Reservadetalle[ codresDet=" + codresDet + " ]";
    }
    
}
