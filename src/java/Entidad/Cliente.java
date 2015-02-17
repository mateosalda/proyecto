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
@Table(name = "cliente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c"),
    @NamedQuery(name = "Cliente.findByPinCli", query = "SELECT c FROM Cliente c WHERE c.pinCli = :pinCli"),
    @NamedQuery(name = "Cliente.findByNicknameCli", query = "SELECT c FROM Cliente c WHERE c.nicknameCli = :nicknameCli"),
    @NamedQuery(name = "Cliente.findByEMailCli", query = "SELECT c FROM Cliente c WHERE c.eMailCli = :eMailCli")})
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "pin_cli")
    private Integer pinCli;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "nickname_cli")
    private String nicknameCli;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "e-mail_cli")
    private String eMailCli;
    @JoinColumn(name = "cod_permi", referencedColumnName = "cod_permi")
    @ManyToOne(optional = false)
    private Permiso codPermi;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pinCli")
    private List<Reserva> reservaList;

    public Cliente() {
    }

    public Cliente(Integer pinCli) {
        this.pinCli = pinCli;
    }

    public Cliente(Integer pinCli, String nicknameCli, String eMailCli) {
        this.pinCli = pinCli;
        this.nicknameCli = nicknameCli;
        this.eMailCli = eMailCli;
    }

    public Integer getPinCli() {
        return pinCli;
    }

    public void setPinCli(Integer pinCli) {
        this.pinCli = pinCli;
    }

    public String getNicknameCli() {
        return nicknameCli;
    }

    public void setNicknameCli(String nicknameCli) {
        this.nicknameCli = nicknameCli;
    }

    public String getEMailCli() {
        return eMailCli;
    }

    public void setEMailCli(String eMailCli) {
        this.eMailCli = eMailCli;
    }

    public Permiso getCodPermi() {
        return codPermi;
    }

    public void setCodPermi(Permiso codPermi) {
        this.codPermi = codPermi;
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
        hash += (pinCli != null ? pinCli.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.pinCli == null && other.pinCli != null) || (this.pinCli != null && !this.pinCli.equals(other.pinCli))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Cliente[ pinCli=" + pinCli + " ]";
    }
    
}
