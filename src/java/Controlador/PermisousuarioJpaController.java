/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import Controlador.exceptions.NonexistentEntityException;
import Controlador.exceptions.PreexistingEntityException;
import Controlador.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Permiso;
import Entidad.Permisousuario;
import Entidad.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class PermisousuarioJpaController implements Serializable {

    public PermisousuarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Permisousuario permisousuario) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Permiso codPermi = permisousuario.getCodPermi();
            if (codPermi != null) {
                codPermi = em.getReference(codPermi.getClass(), codPermi.getCodPermi());
                permisousuario.setCodPermi(codPermi);
            }
            Usuario codUsu = permisousuario.getCodUsu();
            if (codUsu != null) {
                codUsu = em.getReference(codUsu.getClass(), codUsu.getCodUsu());
                permisousuario.setCodUsu(codUsu);
            }
            em.persist(permisousuario);
            if (codPermi != null) {
                codPermi.getPermisousuarioList().add(permisousuario);
                codPermi = em.merge(codPermi);
            }
            if (codUsu != null) {
                codUsu.getPermisousuarioList().add(permisousuario);
                codUsu = em.merge(codUsu);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPermisousuario(permisousuario.getCodpermiUsu()) != null) {
                throw new PreexistingEntityException("Permisousuario " + permisousuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Permisousuario permisousuario) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Permisousuario persistentPermisousuario = em.find(Permisousuario.class, permisousuario.getCodpermiUsu());
            Permiso codPermiOld = persistentPermisousuario.getCodPermi();
            Permiso codPermiNew = permisousuario.getCodPermi();
            Usuario codUsuOld = persistentPermisousuario.getCodUsu();
            Usuario codUsuNew = permisousuario.getCodUsu();
            if (codPermiNew != null) {
                codPermiNew = em.getReference(codPermiNew.getClass(), codPermiNew.getCodPermi());
                permisousuario.setCodPermi(codPermiNew);
            }
            if (codUsuNew != null) {
                codUsuNew = em.getReference(codUsuNew.getClass(), codUsuNew.getCodUsu());
                permisousuario.setCodUsu(codUsuNew);
            }
            permisousuario = em.merge(permisousuario);
            if (codPermiOld != null && !codPermiOld.equals(codPermiNew)) {
                codPermiOld.getPermisousuarioList().remove(permisousuario);
                codPermiOld = em.merge(codPermiOld);
            }
            if (codPermiNew != null && !codPermiNew.equals(codPermiOld)) {
                codPermiNew.getPermisousuarioList().add(permisousuario);
                codPermiNew = em.merge(codPermiNew);
            }
            if (codUsuOld != null && !codUsuOld.equals(codUsuNew)) {
                codUsuOld.getPermisousuarioList().remove(permisousuario);
                codUsuOld = em.merge(codUsuOld);
            }
            if (codUsuNew != null && !codUsuNew.equals(codUsuOld)) {
                codUsuNew.getPermisousuarioList().add(permisousuario);
                codUsuNew = em.merge(codUsuNew);
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
                Integer id = permisousuario.getCodpermiUsu();
                if (findPermisousuario(id) == null) {
                    throw new NonexistentEntityException("The permisousuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Permisousuario permisousuario;
            try {
                permisousuario = em.getReference(Permisousuario.class, id);
                permisousuario.getCodpermiUsu();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The permisousuario with id " + id + " no longer exists.", enfe);
            }
            Permiso codPermi = permisousuario.getCodPermi();
            if (codPermi != null) {
                codPermi.getPermisousuarioList().remove(permisousuario);
                codPermi = em.merge(codPermi);
            }
            Usuario codUsu = permisousuario.getCodUsu();
            if (codUsu != null) {
                codUsu.getPermisousuarioList().remove(permisousuario);
                codUsu = em.merge(codUsu);
            }
            em.remove(permisousuario);
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

    public List<Permisousuario> findPermisousuarioEntities() {
        return findPermisousuarioEntities(true, -1, -1);
    }

    public List<Permisousuario> findPermisousuarioEntities(int maxResults, int firstResult) {
        return findPermisousuarioEntities(false, maxResults, firstResult);
    }

    private List<Permisousuario> findPermisousuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Permisousuario.class));
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

    public Permisousuario findPermisousuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Permisousuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getPermisousuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Permisousuario> rt = cq.from(Permisousuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
