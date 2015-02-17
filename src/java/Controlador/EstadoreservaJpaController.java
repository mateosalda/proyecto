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
import Entidad.Estadoreserva;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Reserva;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class EstadoreservaJpaController implements Serializable {

    public EstadoreservaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estadoreserva estadoreserva) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (estadoreserva.getReservaList() == null) {
            estadoreserva.setReservaList(new ArrayList<Reserva>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Reserva> attachedReservaList = new ArrayList<Reserva>();
            for (Reserva reservaListReservaToAttach : estadoreserva.getReservaList()) {
                reservaListReservaToAttach = em.getReference(reservaListReservaToAttach.getClass(), reservaListReservaToAttach.getCodRes());
                attachedReservaList.add(reservaListReservaToAttach);
            }
            estadoreserva.setReservaList(attachedReservaList);
            em.persist(estadoreserva);
            for (Reserva reservaListReserva : estadoreserva.getReservaList()) {
                Estadoreserva oldCodestResOfReservaListReserva = reservaListReserva.getCodestRes();
                reservaListReserva.setCodestRes(estadoreserva);
                reservaListReserva = em.merge(reservaListReserva);
                if (oldCodestResOfReservaListReserva != null) {
                    oldCodestResOfReservaListReserva.getReservaList().remove(reservaListReserva);
                    oldCodestResOfReservaListReserva = em.merge(oldCodestResOfReservaListReserva);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEstadoreserva(estadoreserva.getCodestRes()) != null) {
                throw new PreexistingEntityException("Estadoreserva " + estadoreserva + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estadoreserva estadoreserva) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Estadoreserva persistentEstadoreserva = em.find(Estadoreserva.class, estadoreserva.getCodestRes());
            List<Reserva> reservaListOld = persistentEstadoreserva.getReservaList();
            List<Reserva> reservaListNew = estadoreserva.getReservaList();
            List<String> illegalOrphanMessages = null;
            for (Reserva reservaListOldReserva : reservaListOld) {
                if (!reservaListNew.contains(reservaListOldReserva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reserva " + reservaListOldReserva + " since its codestRes field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Reserva> attachedReservaListNew = new ArrayList<Reserva>();
            for (Reserva reservaListNewReservaToAttach : reservaListNew) {
                reservaListNewReservaToAttach = em.getReference(reservaListNewReservaToAttach.getClass(), reservaListNewReservaToAttach.getCodRes());
                attachedReservaListNew.add(reservaListNewReservaToAttach);
            }
            reservaListNew = attachedReservaListNew;
            estadoreserva.setReservaList(reservaListNew);
            estadoreserva = em.merge(estadoreserva);
            for (Reserva reservaListNewReserva : reservaListNew) {
                if (!reservaListOld.contains(reservaListNewReserva)) {
                    Estadoreserva oldCodestResOfReservaListNewReserva = reservaListNewReserva.getCodestRes();
                    reservaListNewReserva.setCodestRes(estadoreserva);
                    reservaListNewReserva = em.merge(reservaListNewReserva);
                    if (oldCodestResOfReservaListNewReserva != null && !oldCodestResOfReservaListNewReserva.equals(estadoreserva)) {
                        oldCodestResOfReservaListNewReserva.getReservaList().remove(reservaListNewReserva);
                        oldCodestResOfReservaListNewReserva = em.merge(oldCodestResOfReservaListNewReserva);
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
                Integer id = estadoreserva.getCodestRes();
                if (findEstadoreserva(id) == null) {
                    throw new NonexistentEntityException("The estadoreserva with id " + id + " no longer exists.");
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
            Estadoreserva estadoreserva;
            try {
                estadoreserva = em.getReference(Estadoreserva.class, id);
                estadoreserva.getCodestRes();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estadoreserva with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Reserva> reservaListOrphanCheck = estadoreserva.getReservaList();
            for (Reserva reservaListOrphanCheckReserva : reservaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estadoreserva (" + estadoreserva + ") cannot be destroyed since the Reserva " + reservaListOrphanCheckReserva + " in its reservaList field has a non-nullable codestRes field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(estadoreserva);
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

    public List<Estadoreserva> findEstadoreservaEntities() {
        return findEstadoreservaEntities(true, -1, -1);
    }

    public List<Estadoreserva> findEstadoreservaEntities(int maxResults, int firstResult) {
        return findEstadoreservaEntities(false, maxResults, firstResult);
    }

    private List<Estadoreserva> findEstadoreservaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estadoreserva.class));
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

    public Estadoreserva findEstadoreserva(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estadoreserva.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadoreservaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estadoreserva> rt = cq.from(Estadoreserva.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
