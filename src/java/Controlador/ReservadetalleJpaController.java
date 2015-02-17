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
import Entidad.Reserva;
import Entidad.Reservadetalle;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class ReservadetalleJpaController implements Serializable {

    public ReservadetalleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Reservadetalle reservadetalle) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Reserva codRes = reservadetalle.getCodRes();
            if (codRes != null) {
                codRes = em.getReference(codRes.getClass(), codRes.getCodRes());
                reservadetalle.setCodRes(codRes);
            }
            em.persist(reservadetalle);
            if (codRes != null) {
                codRes.getReservadetalleList().add(reservadetalle);
                codRes = em.merge(codRes);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findReservadetalle(reservadetalle.getCodresDet()) != null) {
                throw new PreexistingEntityException("Reservadetalle " + reservadetalle + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Reservadetalle reservadetalle) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Reservadetalle persistentReservadetalle = em.find(Reservadetalle.class, reservadetalle.getCodresDet());
            Reserva codResOld = persistentReservadetalle.getCodRes();
            Reserva codResNew = reservadetalle.getCodRes();
            if (codResNew != null) {
                codResNew = em.getReference(codResNew.getClass(), codResNew.getCodRes());
                reservadetalle.setCodRes(codResNew);
            }
            reservadetalle = em.merge(reservadetalle);
            if (codResOld != null && !codResOld.equals(codResNew)) {
                codResOld.getReservadetalleList().remove(reservadetalle);
                codResOld = em.merge(codResOld);
            }
            if (codResNew != null && !codResNew.equals(codResOld)) {
                codResNew.getReservadetalleList().add(reservadetalle);
                codResNew = em.merge(codResNew);
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
                Integer id = reservadetalle.getCodresDet();
                if (findReservadetalle(id) == null) {
                    throw new NonexistentEntityException("The reservadetalle with id " + id + " no longer exists.");
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
            Reservadetalle reservadetalle;
            try {
                reservadetalle = em.getReference(Reservadetalle.class, id);
                reservadetalle.getCodresDet();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reservadetalle with id " + id + " no longer exists.", enfe);
            }
            Reserva codRes = reservadetalle.getCodRes();
            if (codRes != null) {
                codRes.getReservadetalleList().remove(reservadetalle);
                codRes = em.merge(codRes);
            }
            em.remove(reservadetalle);
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

    public List<Reservadetalle> findReservadetalleEntities() {
        return findReservadetalleEntities(true, -1, -1);
    }

    public List<Reservadetalle> findReservadetalleEntities(int maxResults, int firstResult) {
        return findReservadetalleEntities(false, maxResults, firstResult);
    }

    private List<Reservadetalle> findReservadetalleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Reservadetalle.class));
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

    public Reservadetalle findReservadetalle(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Reservadetalle.class, id);
        } finally {
            em.close();
        }
    }

    public int getReservadetalleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Reservadetalle> rt = cq.from(Reservadetalle.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
