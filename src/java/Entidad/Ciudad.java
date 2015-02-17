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
@Table(name = "ciudad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ciudad.findAll", query = "SELECT c FROM Ciudad c"),
    @NamedQuery(name = "Ciudad.findByCodCiud", query = "SELECT c FROM Ciudad c WHERE c.codCiud = :codCiud"),
    @NamedQuery(name = "Ciudad.findByCiudad", query = "SELECT c FROM Ciudad c WHERE c.ciudad = :ciudad")})
public class Ciudad implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_ciud")
    private Integer codCiud;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "Ciudad")
    private String ciudad;
    @JoinColumn(name = "cod_dept", referencedColumnName = "cod_dept")
    @ManyToOne(optional = false)
    private Departamento codDept;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codCiud")
    private List<Motel> motelList;

    public Ciudad() {
    }

    public Ciudad(Integer codCiud) {
        this.codCiud = codCiud;
    }

    public Ciudad(Integer codCiud, String ciudad) {
        this.codCiud = codCiud;
        this.ciudad = ciudad;
    }

    public Integer getCodCiud() {
        return codCiud;
    }

    public void setCodCiud(Integer codCiud) {
        this.codCiud = codCiud;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Departamento getCodDept() {
        return codDept;
    }

    public void setCodDept(Departamento codDept) {
        this.codDept = codDept;
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
        hash += (codCiud != null ? codCiud.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ciudad)) {
            return false;
        }
        Ciudad other = (Ciudad) object;
        if ((this.codCiud == null && other.codCiud != null) || (this.codCiud != null && !this.codCiud.equals(other.codCiud))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Ciudad[ codCiud=" + codCiud + " ]";
    }
    
}
