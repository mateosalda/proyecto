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
@Table(name = "permisousuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Permisousuario.findAll", query = "SELECT p FROM Permisousuario p"),
    @NamedQuery(name = "Permisousuario.findByCodpermiUsu", query = "SELECT p FROM Permisousuario p WHERE p.codpermiUsu = :codpermiUsu")})
public class Permisousuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_permiUsu")
    private Integer codpermiUsu;
    @JoinColumn(name = "cod_permi", referencedColumnName = "cod_permi")
    @ManyToOne(optional = false)
    private Permiso codPermi;
    @JoinColumn(name = "cod_usu", referencedColumnName = "cod_usu")
    @ManyToOne(optional = false)
    private Usuario codUsu;

    public Permisousuario() {
    }

    public Permisousuario(Integer codpermiUsu) {
        this.codpermiUsu = codpermiUsu;
    }

    public Integer getCodpermiUsu() {
        return codpermiUsu;
    }

    public void setCodpermiUsu(Integer codpermiUsu) {
        this.codpermiUsu = codpermiUsu;
    }

    public Permiso getCodPermi() {
        return codPermi;
    }

    public void setCodPermi(Permiso codPermi) {
        this.codPermi = codPermi;
    }

    public Usuario getCodUsu() {
        return codUsu;
    }

    public void setCodUsu(Usuario codUsu) {
        this.codUsu = codUsu;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codpermiUsu != null ? codpermiUsu.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Permisousuario)) {
            return false;
        }
        Permisousuario other = (Permisousuario) object;
        if ((this.codpermiUsu == null && other.codpermiUsu != null) || (this.codpermiUsu != null && !this.codpermiUsu.equals(other.codpermiUsu))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Permisousuario[ codpermiUsu=" + codpermiUsu + " ]";
    }
    
}
