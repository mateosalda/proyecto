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
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByCodUsu", query = "SELECT u FROM Usuario u WHERE u.codUsu = :codUsu"),
    @NamedQuery(name = "Usuario.findByNom1Usu", query = "SELECT u FROM Usuario u WHERE u.nom1Usu = :nom1Usu"),
    @NamedQuery(name = "Usuario.findByNom2Usu", query = "SELECT u FROM Usuario u WHERE u.nom2Usu = :nom2Usu"),
    @NamedQuery(name = "Usuario.findByApe1Usu", query = "SELECT u FROM Usuario u WHERE u.ape1Usu = :ape1Usu"),
    @NamedQuery(name = "Usuario.findByApe2Usu", query = "SELECT u FROM Usuario u WHERE u.ape2Usu = :ape2Usu"),
    @NamedQuery(name = "Usuario.findByEMailUsu", query = "SELECT u FROM Usuario u WHERE u.eMailUsu = :eMailUsu")})
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_usu")
    private Integer codUsu;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "nom1_usu")
    private String nom1Usu;
    @Size(max = 15)
    @Column(name = "nom2_usu")
    private String nom2Usu;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "ape1_usu")
    private String ape1Usu;
    @Size(max = 15)
    @Column(name = "ape2_usu")
    private String ape2Usu;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "e-mail_usu")
    private String eMailUsu;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codUsu")
    private List<Permisousuario> permisousuarioList;
    @JoinColumn(name = "cod_rol", referencedColumnName = "cod_rol")
    @ManyToOne(optional = false)
    private Rol codRol;
    @JoinColumn(name = "cod_carg", referencedColumnName = "cod_carg")
    @ManyToOne(optional = false)
    private Cargo codCarg;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codUsu")
    private List<Motel> motelList;

    public Usuario() {
    }

    public Usuario(Integer codUsu) {
        this.codUsu = codUsu;
    }

    public Usuario(Integer codUsu, String nom1Usu, String ape1Usu, String eMailUsu) {
        this.codUsu = codUsu;
        this.nom1Usu = nom1Usu;
        this.ape1Usu = ape1Usu;
        this.eMailUsu = eMailUsu;
    }

    public Integer getCodUsu() {
        return codUsu;
    }

    public void setCodUsu(Integer codUsu) {
        this.codUsu = codUsu;
    }

    public String getNom1Usu() {
        return nom1Usu;
    }

    public void setNom1Usu(String nom1Usu) {
        this.nom1Usu = nom1Usu;
    }

    public String getNom2Usu() {
        return nom2Usu;
    }

    public void setNom2Usu(String nom2Usu) {
        this.nom2Usu = nom2Usu;
    }

    public String getApe1Usu() {
        return ape1Usu;
    }

    public void setApe1Usu(String ape1Usu) {
        this.ape1Usu = ape1Usu;
    }

    public String getApe2Usu() {
        return ape2Usu;
    }

    public void setApe2Usu(String ape2Usu) {
        this.ape2Usu = ape2Usu;
    }

    public String getEMailUsu() {
        return eMailUsu;
    }

    public void setEMailUsu(String eMailUsu) {
        this.eMailUsu = eMailUsu;
    }

    @XmlTransient
    public List<Permisousuario> getPermisousuarioList() {
        return permisousuarioList;
    }

    public void setPermisousuarioList(List<Permisousuario> permisousuarioList) {
        this.permisousuarioList = permisousuarioList;
    }

    public Rol getCodRol() {
        return codRol;
    }

    public void setCodRol(Rol codRol) {
        this.codRol = codRol;
    }

    public Cargo getCodCarg() {
        return codCarg;
    }

    public void setCodCarg(Cargo codCarg) {
        this.codCarg = codCarg;
    }

    @XmlTransient
    public List<Motel> getMotelList() {
        return motelList;
    }

    public void setMotelList(List<Motel> motelList) {
        this.motelList = motelList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codUsu != null ? codUsu.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.codUsu == null && other.codUsu != null) || (this.codUsu != null && !this.codUsu.equals(other.codUsu))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Usuario[ codUsu=" + codUsu + " ]";
    }
    
}
