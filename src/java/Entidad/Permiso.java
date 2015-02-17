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
@Table(name = "permiso")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Permiso.findAll", query = "SELECT p FROM Permiso p"),
    @NamedQuery(name = "Permiso.findByCodPermi", query = "SELECT p FROM Permiso p WHERE p.codPermi = :codPermi"),
    @NamedQuery(name = "Permiso.findByPermiso", query = "SELECT p FROM Permiso p WHERE p.permiso = :permiso")})
public class Permiso implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_permi")
    private Integer codPermi;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "permiso")
    private String permiso;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codPermi")
    private List<Permisousuario> permisousuarioList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codPermi")
    private List<Cliente> clienteList;

    public Permiso() {
    }

    public Permiso(Integer codPermi) {
        this.codPermi = codPermi;
    }

    public Permiso(Integer codPermi, String permiso) {
        this.codPermi = codPermi;
        this.permiso = permiso;
    }

    public Integer getCodPermi() {
        return codPermi;
    }

    public void setCodPermi(Integer codPermi) {
        this.codPermi = codPermi;
    }

    public String getPermiso() {
        return permiso;
    }

    public void setPermiso(String permiso) {
        this.permiso = permiso;
    }

    @XmlTransient
    public List<Permisousuario> getPermisousuarioList() {
        return permisousuarioList;
    }

    public void setPermisousuarioList(List<Permisousuario> permisousuarioList) {
        this.permisousuarioList = permisousuarioList;
    }

    @XmlTransient
    public List<Cliente> getClienteList() {
        return clienteList;
    }

    public void setClienteList(List<Cliente> clienteList) {
        this.clienteList = clienteList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codPermi != null ? codPermi.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Permiso)) {
            return false;
        }
        Permiso other = (Permiso) object;
        if ((this.codPermi == null && other.codPermi != null) || (this.codPermi != null && !this.codPermi.equals(other.codPermi))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Permiso[ codPermi=" + codPermi + " ]";
    }
    
}
