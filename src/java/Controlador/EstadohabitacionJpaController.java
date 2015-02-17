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
import Entidad.Estado;
import Entidad.Estadohabitacion;
import Entidad.Habitacion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class EstadohabitacionJpaController implements Serializable {

    public EstadohabitacionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estadohabitacion estadohabitacion) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Estado codEst = estadohabitacion.getCodEst();
            if (codEst != null) {
                codEst = em.getReference(codEst.getClass(), codEst.getCodEst());
                estadohabitacion.setCodEst(codEst);
            }
            Habitacion codHab = estadohabitacion.getCodHab();
            if (codHab != null) {
                codHab = em.getReference(codHab.getClass(), codHab.getCodHab());
                estadohabitacion.setCodHab(codHab);
            }
            em.persist(estadohabitacion);
            if (codEst != null) {
                codEst.getEstadohabitacionList().add(estadohabitacion);
                codEst = em.merge(codEst);
            }
            if (codHab != null) {
                codHab.getEstadohabitacionList().add(estadohabitacion);
                codHab = em.merge(codHab);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEstadohabitacion(estadohabitacion.getCodestHab()) != null) {
                throw new PreexistingEntityException("Estadohabitacion " + estadohabitacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estadohabitacion estadohabitacion) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Estadohabitacion persistentEstadohabitacion = em.find(Estadohabitacion.class, estadohabitacion.getCodestHab());
            Estado codEstOld = persistentEstadohabitacion.getCodEst();
            Estado codEstNew = estadohabitacion.getCodEst();
            Habitacion codHabOld = persistentEstadohabitacion.getCodHab();
            Habitacion codHabNew = estadohabitacion.getCodHab();
            if (codEstNew != null) {
                codEstNew = em.getReference(codEstNew.getClass(), codEstNew.getCodEst());
                estadohabitacion.setCodEst(codEstNew);
            }
            if (codHabNew != null) {
                codHabNew = em.getReference(codHabNew.getClass(), codHabNew.getCodHab());
                estadohabitacion.setCodHab(codHabNew);
            }
            estadohabitacion = em.merge(estadohabitacion);
            if (codEstOld != null && !codEstOld.equals(codEstNew)) {
                codEstOld.getEstadohabitacionList().remove(estadohabitacion);
                codEstOld = em.merge(codEstOld);
            }
            if (codEstNew != null && !codEstNew.equals(codEstOld)) {
                codEstNew.getEstadohabitacionList().add(estadohabitacion);
                codEstNew = em.merge(codEstNew);
            }
            if (codHabOld != null && !codHabOld.equals(codHabNew)) {
                codHabOld.getEstadohabitacionList().remove(estadohabitacion);
                codHabOld = em.merge(codHabOld);
            }
            if (codHabNew != null && !codHabNew.equals(codHabOld)) {
                codHabNew.getEstadohabitacionList().add(estadohabitacion);
                codHabNew = em.merge(codHabNew);
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
                Integer id = estadohabitacion.getCodestHab();
                if (findEstadohabitacion(id) == null) {
                    throw new NonexistentEntityException("The estadohabitacion with id " + id + " no longer exists.");
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
            Estadohabitacion estadohabitacion;
            try {
                estadohabitacion = em.getReference(Estadohabitacion.class, id);
                estadohabitacion.getCodestHab();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estadohabitacion with id " + id + " no longer exists.", enfe);
            }
            Estado codEst = estadohabitacion.getCodEst();
            if (codEst != null) {
                codEst.getEstadohabitacionList().remove(estadohabitacion);
                codEst = em.merge(codEst);
            }
            Habitacion codHab = estadohabitacion.getCodHab();
            if (codHab != null) {
                codHab.getEstadohabitacionList().remove(estadohabitacion);
                codHab = em.merge(codHab);
            }
            em.remove(estadohabitacion);
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

    public List<Estadohabitacion> findEstadohabitacionEntities() {
        return findEstadohabitacionEntities(true, -1, -1);
    }

    public List<Estadohabitacion> findEstadohabitacionEntities(int maxResults, int firstResult) {
        return findEstadohabitacionEntities(false, maxResults, firstResult);
    }

    private List<Estadohabitacion> findEstadohabitacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estadohabitacion.class));
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

    public Estadohabitacion findEstadohabitacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estadohabitacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadohabitacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estadohabitacion> rt = cq.from(Estadohabitacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
