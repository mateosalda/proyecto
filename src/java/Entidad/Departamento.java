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
@Table(name = "departamento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Departamento.findAll", query = "SELECT d FROM Departamento d"),
    @NamedQuery(name = "Departamento.findByCodDept", query = "SELECT d FROM Departamento d WHERE d.codDept = :codDept"),
    @NamedQuery(name = "Departamento.findByDept", query = "SELECT d FROM Departamento d WHERE d.dept = :dept")})
public class Departamento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod_dept")
    private Integer codDept;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "dept")
    private String dept;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codDept")
    private List<Ciudad> ciudadList;

    public Departamento() {
    }

    public Departamento(Integer codDept) {
        this.codDept = codDept;
    }

    public Departamento(Integer codDept, String dept) {
        this.codDept = codDept;
        this.dept = dept;
    }

    public Integer getCodDept() {
        return codDept;
    }

    public void setCodDept(Integer codDept) {
        this.codDept = codDept;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    @XmlTransient
    public List<Ciudad> getCiudadList() {
        return ciudadList;
    }

    public void setCiudadList(List<Ciudad> ciudadList) {
        this.ciudadList = ciudadList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codDept != null ? codDept.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Departamento)) {
            return false;
        }
        Departamento other = (Departamento) object;
        if ((this.codDept == null && other.codDept != null) || (this.codDept != null && !this.codDept.equals(other.codDept))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidad.Departamento[ codDept=" + codDept + " ]";
    }
    
}
