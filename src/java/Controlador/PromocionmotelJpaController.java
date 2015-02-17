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
import Entidad.Promocion;
import Entidad.Motel;
import Entidad.Promocionmotel;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class PromocionmotelJpaController implements Serializable {

    public PromocionmotelJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Promocionmotel promocionmotel) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Promocion codPromo = promocionmotel.getCodPromo();
            if (codPromo != null) {
                codPromo = em.getReference(codPromo.getClass(), codPromo.getCodPromo());
                promocionmotel.setCodPromo(codPromo);
            }
            Motel nitMot = promocionmotel.getNitMot();
            if (nitMot != null) {
                nitMot = em.getReference(nitMot.getClass(), nitMot.getNitMot());
                promocionmotel.setNitMot(nitMot);
            }
            em.persist(promocionmotel);
            if (codPromo != null) {
                codPromo.getPromocionmotelList().add(promocionmotel);
                codPromo = em.merge(codPromo);
            }
            if (nitMot != null) {
                nitMot.getPromocionmotelList().add(promocionmotel);
                nitMot = em.merge(nitMot);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPromocionmotel(promocionmotel.getCodpromoMot()) != null) {
                throw new PreexistingEntityException("Promocionmotel " + promocionmotel + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Promocionmotel promocionmotel) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Promocionmotel persistentPromocionmotel = em.find(Promocionmotel.class, promocionmotel.getCodpromoMot());
            Promocion codPromoOld = persistentPromocionmotel.getCodPromo();
            Promocion codPromoNew = promocionmotel.getCodPromo();
            Motel nitMotOld = persistentPromocionmotel.getNitMot();
            Motel nitMotNew = promocionmotel.getNitMot();
            if (codPromoNew != null) {
                codPromoNew = em.getReference(codPromoNew.getClass(), codPromoNew.getCodPromo());
                promocionmotel.setCodPromo(codPromoNew);
            }
            if (nitMotNew != null) {
                nitMotNew = em.getReference(nitMotNew.getClass(), nitMotNew.getNitMot());
                promocionmotel.setNitMot(nitMotNew);
            }
            promocionmotel = em.merge(promocionmotel);
            if (codPromoOld != null && !codPromoOld.equals(codPromoNew)) {
                codPromoOld.getPromocionmotelList().remove(promocionmotel);
                codPromoOld = em.merge(codPromoOld);
            }
            if (codPromoNew != null && !codPromoNew.equals(codPromoOld)) {
                codPromoNew.getPromocionmotelList().add(promocionmotel);
                codPromoNew = em.merge(codPromoNew);
            }
            if (nitMotOld != null && !nitMotOld.equals(nitMotNew)) {
                nitMotOld.getPromocionmotelList().remove(promocionmotel);
                nitMotOld = em.merge(nitMotOld);
            }
            if (nitMotNew != null && !nitMotNew.equals(nitMotOld)) {
                nitMotNew.getPromocionmotelList().add(promocionmotel);
                nitMotNew = em.merge(nitMotNew);
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
                Integer id = promocionmotel.getCodpromoMot();
                if (findPromocionmotel(id) == null) {
                    throw new NonexistentEntityException("The promocionmotel with id " + id + " no longer exists.");
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
            Promocionmotel promocionmotel;
            try {
                promocionmotel = em.getReference(Promocionmotel.class, id);
                promocionmotel.getCodpromoMot();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The promocionmotel with id " + id + " no longer exists.", enfe);
            }
            Promocion codPromo = promocionmotel.getCodPromo();
            if (codPromo != null) {
                codPromo.getPromocionmotelList().remove(promocionmotel);
                codPromo = em.merge(codPromo);
            }
            Motel nitMot = promocionmotel.getNitMot();
            if (nitMot != null) {
                nitMot.getPromocionmotelList().remove(promocionmotel);
                nitMot = em.merge(nitMot);
            }
            em.remove(promocionmotel);
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

    public List<Promocionmotel> findPromocionmotelEntities() {
        return findPromocionmotelEntities(true, -1, -1);
    }

    public List<Promocionmotel> findPromocionmotelEntities(int maxResults, int firstResult) {
        return findPromocionmotelEntities(false, maxResults, firstResult);
    }

    private List<Promocionmotel> findPromocionmotelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Promocionmotel.class));
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

    public Promocionmotel findPromocionmotel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Promocionmotel.class, id);
        } finally {
            em.close();
        }
    }

    public int getPromocionmotelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Promocionmotel> rt = cq.from(Promocionmotel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
