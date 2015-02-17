/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.IllegalOrphanException;
import Controlador.exceptions.NonexistentEntityException;
import Controlador.exceptions.PreexistingEntityException;
import Controlador.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Rol;
import Entidad.Cargo;
import Entidad.Permisousuario;
import java.util.ArrayList;
import java.util.List;
import Entidad.Motel;
import Entidad.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (usuario.getPermisousuarioList() == null) {
            usuario.setPermisousuarioList(new ArrayList<Permisousuario>());
        }
        if (usuario.getMotelList() == null) {
            usuario.setMotelList(new ArrayList<Motel>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Rol codRol = usuario.getCodRol();
            if (codRol != null) {
                codRol = em.getReference(codRol.getClass(), codRol.getCodRol());
                usuario.setCodRol(codRol);
            }
            Cargo codCarg = usuario.getCodCarg();
            if (codCarg != null) {
                codCarg = em.getReference(codCarg.getClass(), codCarg.getCodCarg());
                usuario.setCodCarg(codCarg);
            }
            List<Permisousuario> attachedPermisousuarioList = new ArrayList<Permisousuario>();
            for (Permisousuario permisousuarioListPermisousuarioToAttach : usuario.getPermisousuarioList()) {
                permisousuarioListPermisousuarioToAttach = em.getReference(permisousuarioListPermisousuarioToAttach.getClass(), permisousuarioListPermisousuarioToAttach.getCodpermiUsu());
                attachedPermisousuarioList.add(permisousuarioListPermisousuarioToAttach);
            }
            usuario.setPermisousuarioList(attachedPermisousuarioList);
            List<Motel> attachedMotelList = new ArrayList<Motel>();
            for (Motel motelListMotelToAttach : usuario.getMotelList()) {
                motelListMotelToAttach = em.getReference(motelListMotelToAttach.getClass(), motelListMotelToAttach.getNitMot());
                attachedMotelList.add(motelListMotelToAttach);
            }
            usuario.setMotelList(attachedMotelList);
            em.persist(usuario);
            if (codRol != null) {
                codRol.getUsuarioList().add(usuario);
                codRol = em.merge(codRol);
            }
            if (codCarg != null) {
                codCarg.getUsuarioList().add(usuario);
                codCarg = em.merge(codCarg);
            }
            for (Permisousuario permisousuarioListPermisousuario : usuario.getPermisousuarioList()) {
                Usuario oldCodUsuOfPermisousuarioListPermisousuario = permisousuarioListPermisousuario.getCodUsu();
                permisousuarioListPermisousuario.setCodUsu(usuario);
                permisousuarioListPermisousuario = em.merge(permisousuarioListPermisousuario);
                if (oldCodUsuOfPermisousuarioListPermisousuario != null) {
                    oldCodUsuOfPermisousuarioListPermisousuario.getPermisousuarioList().remove(permisousuarioListPermisousuario);
                    oldCodUsuOfPermisousuarioListPermisousuario = em.merge(oldCodUsuOfPermisousuarioListPermisousuario);
                }
            }
            for (Motel motelListMotel : usuario.getMotelList()) {
                Usuario oldCodUsuOfMotelListMotel = motelListMotel.getCodUsu();
                motelListMotel.setCodUsu(usuario);
                motelListMotel = em.merge(motelListMotel);
                if (oldCodUsuOfMotelListMotel != null) {
                    oldCodUsuOfMotelListMotel.getMotelList().remove(motelListMotel);
                    oldCodUsuOfMotelListMotel = em.merge(oldCodUsuOfMotelListMotel);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUsuario(usuario.getCodUsu()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getCodUsu());
            Rol codRolOld = persistentUsuario.getCodRol();
            Rol codRolNew = usuario.getCodRol();
            Cargo codCargOld = persistentUsuario.getCodCarg();
            Cargo codCargNew = usuario.getCodCarg();
            List<Permisousuario> permisousuarioListOld = persistentUsuario.getPermisousuarioList();
            List<Permisousuario> permisousuarioListNew = usuario.getPermisousuarioList();
            List<Motel> motelListOld = persistentUsuario.getMotelList();
            List<Motel> motelListNew = usuario.getMotelList();
            List<String> illegalOrphanMessages = null;
            for (Permisousuario permisousuarioListOldPermisousuario : permisousuarioListOld) {
                if (!permisousuarioListNew.contains(permisousuarioListOldPermisousuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Permisousuario " + permisousuarioListOldPermisousuario + " since its codUsu field is not nullable.");
                }
            }
            for (Motel motelListOldMotel : motelListOld) {
                if (!motelListNew.contains(motelListOldMotel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Motel " + motelListOldMotel + " since its codUsu field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codRolNew != null) {
                codRolNew = em.getReference(codRolNew.getClass(), codRolNew.getCodRol());
                usuario.setCodRol(codRolNew);
            }
            if (codCargNew != null) {
                codCargNew = em.getReference(codCargNew.getClass(), codCargNew.getCodCarg());
                usuario.setCodCarg(codCargNew);
            }
            List<Permisousuario> attachedPermisousuarioListNew = new ArrayList<Permisousuario>();
            for (Permisousuario permisousuarioListNewPermisousuarioToAttach : permisousuarioListNew) {
                permisousuarioListNewPermisousuarioToAttach = em.getReference(permisousuarioListNewPermisousuarioToAttach.getClass(), permisousuarioListNewPermisousuarioToAttach.getCodpermiUsu());
                attachedPermisousuarioListNew.add(permisousuarioListNewPermisousuarioToAttach);
            }
            permisousuarioListNew = attachedPermisousuarioListNew;
            usuario.setPermisousuarioList(permisousuarioListNew);
            List<Motel> attachedMotelListNew = new ArrayList<Motel>();
            for (Motel motelListNewMotelToAttach : motelListNew) {
                motelListNewMotelToAttach = em.getReference(motelListNewMotelToAttach.getClass(), motelListNewMotelToAttach.getNitMot());
                attachedMotelListNew.add(motelListNewMotelToAttach);
            }
            motelListNew = attachedMotelListNew;
            usuario.setMotelList(motelListNew);
            usuario = em.merge(usuario);
            if (codRolOld != null && !codRolOld.equals(codRolNew)) {
                codRolOld.getUsuarioList().remove(usuario);
                codRolOld = em.merge(codRolOld);
            }
            if (codRolNew != null && !codRolNew.equals(codRolOld)) {
                codRolNew.getUsuarioList().add(usuario);
                codRolNew = em.merge(codRolNew);
            }
            if (codCargOld != null && !codCargOld.equals(codCargNew)) {
                codCargOld.getUsuarioList().remove(usuario);
                codCargOld = em.merge(codCargOld);
            }
            if (codCargNew != null && !codCargNew.equals(codCargOld)) {
                codCargNew.getUsuarioList().add(usuario);
                codCargNew = em.merge(codCargNew);
            }
            for (Permisousuario permisousuarioListNewPermisousuario : permisousuarioListNew) {
                if (!permisousuarioListOld.contains(permisousuarioListNewPermisousuario)) {
                    Usuario oldCodUsuOfPermisousuarioListNewPermisousuario = permisousuarioListNewPermisousuario.getCodUsu();
                    permisousuarioListNewPermisousuario.setCodUsu(usuario);
                    permisousuarioListNewPermisousuario = em.merge(permisousuarioListNewPermisousuario);
                    if (oldCodUsuOfPermisousuarioListNewPermisousuario != null && !oldCodUsuOfPermisousuarioListNewPermisousuario.equals(usuario)) {
                        oldCodUsuOfPermisousuarioListNewPermisousuario.getPermisousuarioList().remove(permisousuarioListNewPermisousuario);
                        oldCodUsuOfPermisousuarioListNewPermisousuario = em.merge(oldCodUsuOfPermisousuarioListNewPermisousuario);
                    }
                }
            }
            for (Motel motelListNewMotel : motelListNew) {
                if (!motelListOld.contains(motelListNewMotel)) {
                    Usuario oldCodUsuOfMotelListNewMotel = motelListNewMotel.getCodUsu();
                    motelListNewMotel.setCodUsu(usuario);
                    motelListNewMotel = em.merge(motelListNewMotel);
                    if (oldCodUsuOfMotelListNewMotel != null && !oldCodUsuOfMotelListNewMotel.equals(usuario)) {
                        oldCodUsuOfMotelListNewMotel.getMotelList().remove(motelListNewMotel);
                        oldCodUsuOfMotelListNewMotel = em.merge(oldCodUsuOfMotelListNewMotel);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getCodUsu();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getCodUsu();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Permisousuario> permisousuarioListOrphanCheck = usuario.getPermisousuarioList();
            for (Permisousuario permisousuarioListOrphanCheckPermisousuario : permisousuarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Permisousuario " + permisousuarioListOrphanCheckPermisousuario + " in its permisousuarioList field has a non-nullable codUsu field.");
            }
            List<Motel> motelListOrphanCheck = usuario.getMotelList();
            for (Motel motelListOrphanCheckMotel : motelListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Motel " + motelListOrphanCheckMotel + " in its motelList field has a non-nullable codUsu field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Rol codRol = usuario.getCodRol();
            if (codRol != null) {
                codRol.getUsuarioList().remove(usuario);
                codRol = em.merge(codRol);
            }
            Cargo codCarg = usuario.getCodCarg();
            if (codCarg != null) {
                codCarg.getUsuarioList().remove(usuario);
                codCarg = em.merge(codCarg);
            }
            em.remove(usuario);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
