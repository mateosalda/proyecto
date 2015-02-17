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
@Table(name = "motel")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Motel.findAll", query = "SELECT m FROM Motel m"),
    @NamedQuery(name = "Motel.findByNitMot", query = "SELECT m FROM Motel m WHERE m.nitMot = :nitMot"),
    @NamedQuery(name = "Motel.findByNomMot", query = "SELECT m FROM Motel m WHERE m.nomMot = :nomMot"),
    @NamedQuery(name = "Motel.findByDirMot", query = "SELECT m FROM Motel m WHERE m.dirMot = :dirMot"),
    @NamedQuery(name = "Motel.findByTelMot", query = "SELECT m FROM Motel m WHERE m.telMot = :telMot"),
    @NamedQuery(name = "Motel.findByEMailMot", query = "SELECT m FROM Motel m WHERE m.eMailMot = :eMailMot")})
public class Motel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "nit_mot")
    private Integer nitMot;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "nom_mot")
    private String nomMot;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "dir_mot")
    private String dirMot;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "tel_mot")
    private String telMot;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "e-mail_mot")
    private String eMailMot;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nitMot")
    private List<Promocionmotel> promocionmotelList;
    @JoinColumn(name = "cod_usu", referencedColumnName = "cod_usu")
    @ManyToOne(optional = false)
    private Usuario codUsu;
    @JoinColumn(name = "cod_ciud", referencedColumnName = "cod_ciud")
    @ManyToOne(optional = false)
    private Ciudad codCiud;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nitMot")
    private List<Habitacion> habitacionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nitMot")
    private List<Reserva> reservaList;

    public Motel() {
    }

    public Motel(Integer nitMot) {
        this.nitMot = nitMot;
    }

    public Motel(Integer nitMot, String nomMot, String dirMot, String telMot, String eMailMot) {
        this.nitMot = nitMot;
        this.nomMot = nomMot;
        this.dirMot = dirMot;
        this.telMot = telMot;
        this.eMailMot = eMailMot;
    }

    public Integer getNitMot() {
        return nitMot;
    }

    public void setNitMot(Integer nitMot) {
        this.nitMot = nitMot;
    }

    public String getNomMot() {
        return nomMot;
    }

    public void setNomMot(String nomMot) {
        this.nomMot = nomMot;
    }

    public String getDirMot() {
        return dirMot;
    }

    public void setDirMot(String dirMot) {
        this.dirMot = dirMot;
    }

    public String getTelMot() {
        return telMot;
    }

    public void setTelMot(String telMot) {
        this.telMot = telMot;
    }

    public String getEMailMot() {
        return eMailMot;
    }

    public void setEMailMot(String eMailMot) {
        this.eMailMot = eMailMot;
    }

    @XmlTransient
    public List<Promocionmotel> getPromocionmotelList() {
        return promocionmotelList;
    }

    public void setPromocionmotelList(List<Promocionmotel> promocionmotelList) {
        this.promocionmotelList = promocionmotelList;
    }

    public Usuario getCodUsu() {
        return codUsu;
    }

    public void setCodUsu(Usuario codUsu) {
        this.codUsu = codUsu;
    }

    public Ciudad getCodCiud() {
        return codCiud;
    }

    public void setCodCiud(Ciudad codCiud) {
        this.codCiud = codCiud;
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
        hash += (nitMot != null ? nitMot.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Motel)) {
            return false;
        }
        Motel other = (Motel) object;
        if ((this.nitMot == null && other.nitMot != null) || (this.nitMot != null && !this.nitMot.equals(other.nitMot))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Motel[ nitMot=" + nitMot + " ]";
    }
    
}
