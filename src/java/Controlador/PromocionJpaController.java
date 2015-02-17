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
import Entidad.Promocion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Promocionmotel;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class PromocionJpaController implements Serializable {

    public PromocionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Promocion promocion) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (promocion.getPromocionmotelList() == null) {
            promocion.setPromocionmotelList(new ArrayList<Promocionmotel>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Promocionmotel> attachedPromocionmotelList = new ArrayList<Promocionmotel>();
            for (Promocionmotel promocionmotelListPromocionmotelToAttach : promocion.getPromocionmotelList()) {
                promocionmotelListPromocionmotelToAttach = em.getReference(promocionmotelListPromocionmotelToAttach.getClass(), promocionmotelListPromocionmotelToAttach.getCodpromoMot());
                attachedPromocionmotelList.add(promocionmotelListPromocionmotelToAttach);
            }
            promocion.setPromocionmotelList(attachedPromocionmotelList);
            em.persist(promocion);
            for (Promocionmotel promocionmotelListPromocionmotel : promocion.getPromocionmotelList()) {
                Promocion oldCodPromoOfPromocionmotelListPromocionmotel = promocionmotelListPromocionmotel.getCodPromo();
                promocionmotelListPromocionmotel.setCodPromo(promocion);
                promocionmotelListPromocionmotel = em.merge(promocionmotelListPromocionmotel);
                if (oldCodPromoOfPromocionmotelListPromocionmotel != null) {
                    oldCodPromoOfPromocionmotelListPromocionmotel.getPromocionmotelList().remove(promocionmotelListPromocionmotel);
                    oldCodPromoOfPromocionmotelListPromocionmotel = em.merge(oldCodPromoOfPromocionmotelListPromocionmotel);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPromocion(promocion.getCodPromo()) != null) {
                throw new PreexistingEntityException("Promocion " + promocion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Promocion promocion) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Promocion persistentPromocion = em.find(Promocion.class, promocion.getCodPromo());
            List<Promocionmotel> promocionmotelListOld = persistentPromocion.getPromocionmotelList();
            List<Promocionmotel> promocionmotelListNew = promocion.getPromocionmotelList();
            List<String> illegalOrphanMessages = null;
            for (Promocionmotel promocionmotelListOldPromocionmotel : promocionmotelListOld) {
                if (!promocionmotelListNew.contains(promocionmotelListOldPromocionmotel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Promocionmotel " + promocionmotelListOldPromocionmotel + " since its codPromo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Promocionmotel> attachedPromocionmotelListNew = new ArrayList<Promocionmotel>();
            for (Promocionmotel promocionmotelListNewPromocionmotelToAttach : promocionmotelListNew) {
                promocionmotelListNewPromocionmotelToAttach = em.getReference(promocionmotelListNewPromocionmotelToAttach.getClass(), promocionmotelListNewPromocionmotelToAttach.getCodpromoMot());
                attachedPromocionmotelListNew.add(promocionmotelListNewPromocionmotelToAttach);
            }
            promocionmotelListNew = attachedPromocionmotelListNew;
            promocion.setPromocionmotelList(promocionmotelListNew);
            promocion = em.merge(promocion);
            for (Promocionmotel promocionmotelListNewPromocionmotel : promocionmotelListNew) {
                if (!promocionmotelListOld.contains(promocionmotelListNewPromocionmotel)) {
                    Promocion oldCodPromoOfPromocionmotelListNewPromocionmotel = promocionmotelListNewPromocionmotel.getCodPromo();
                    promocionmotelListNewPromocionmotel.setCodPromo(promocion);
                    promocionmotelListNewPromocionmotel = em.merge(promocionmotelListNewPromocionmotel);
                    if (oldCodPromoOfPromocionmotelListNewPromocionmotel != null && !oldCodPromoOfPromocionmotelListNewPromocionmotel.equals(promocion)) {
                        oldCodPromoOfPromocionmotelListNewPromocionmotel.getPromocionmotelList().remove(promocionmotelListNewPromocionmotel);
                        oldCodPromoOfPromocionmotelListNewPromocionmotel = em.merge(oldCodPromoOfPromocionmotelListNewPromocionmotel);
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
                Integer id = promocion.getCodPromo();
                if (findPromocion(id) == null) {
                    throw new NonexistentEntityException("The promocion with id " + id + " no longer exists.");
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
            Promocion promocion;
            try {
                promocion = em.getReference(Promocion.class, id);
                promocion.getCodPromo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The promocion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Promocionmotel> promocionmotelListOrphanCheck = promocion.getPromocionmotelList();
            for (Promocionmotel promocionmotelListOrphanCheckPromocionmotel : promocionmotelListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Promocion (" + promocion + ") cannot be destroyed since the Promocionmotel " + promocionmotelListOrphanCheckPromocionmotel + " in its promocionmotelList field has a non-nullable codPromo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(promocion);
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

    public List<Promocion> findPromocionEntities() {
        return findPromocionEntities(true, -1, -1);
    }

    public List<Promocion> findPromocionEntities(int maxResults, int firstResult) {
        return findPromocionEntities(false, maxResults, firstResult);
    }

    private List<Promocion> findPromocionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Promocion.class));
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

    public Promocion findPromocion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Promocion.class, id);
        } finally {
            em.close();
        }
    }

    public int getPromocionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Promocion> rt = cq.from(Promocion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
