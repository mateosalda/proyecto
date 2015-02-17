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
import Entidad.Decoracion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Habitacion;
import java.util.ArrayList;
import java.util.List;
import Entidad.Reserva;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class DecoracionJpaController implements Serializable {

    public DecoracionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Decoracion decoracion) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (decoracion.getHabitacionList() == null) {
            decoracion.setHabitacionList(new ArrayList<Habitacion>());
        }
        if (decoracion.getReservaList() == null) {
            decoracion.setReservaList(new ArrayList<Reserva>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Habitacion> attachedHabitacionList = new ArrayList<Habitacion>();
            for (Habitacion habitacionListHabitacionToAttach : decoracion.getHabitacionList()) {
                habitacionListHabitacionToAttach = em.getReference(habitacionListHabitacionToAttach.getClass(), habitacionListHabitacionToAttach.getCodHab());
                attachedHabitacionList.add(habitacionListHabitacionToAttach);
            }
            decoracion.setHabitacionList(attachedHabitacionList);
            List<Reserva> attachedReservaList = new ArrayList<Reserva>();
            for (Reserva reservaListReservaToAttach : decoracion.getReservaList()) {
                reservaListReservaToAttach = em.getReference(reservaListReservaToAttach.getClass(), reservaListReservaToAttach.getCodRes());
                attachedReservaList.add(reservaListReservaToAttach);
            }
            decoracion.setReservaList(attachedReservaList);
            em.persist(decoracion);
            for (Habitacion habitacionListHabitacion : decoracion.getHabitacionList()) {
                habitacionListHabitacion.getDecoracionList().add(decoracion);
                habitacionListHabitacion = em.merge(habitacionListHabitacion);
            }
            for (Reserva reservaListReserva : decoracion.getReservaList()) {
                Decoracion oldCodDecOfReservaListReserva = reservaListReserva.getCodDec();
                reservaListReserva.setCodDec(decoracion);
                reservaListReserva = em.merge(reservaListReserva);
                if (oldCodDecOfReservaListReserva != null) {
                    oldCodDecOfReservaListReserva.getReservaList().remove(reservaListReserva);
                    oldCodDecOfReservaListReserva = em.merge(oldCodDecOfReservaListReserva);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDecoracion(decoracion.getCodDec()) != null) {
                throw new PreexistingEntityException("Decoracion " + decoracion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Decoracion decoracion) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Decoracion persistentDecoracion = em.find(Decoracion.class, decoracion.getCodDec());
            List<Habitacion> habitacionListOld = persistentDecoracion.getHabitacionList();
            List<Habitacion> habitacionListNew = decoracion.getHabitacionList();
            List<Reserva> reservaListOld = persistentDecoracion.getReservaList();
            List<Reserva> reservaListNew = decoracion.getReservaList();
            List<String> illegalOrphanMessages = null;
            for (Reserva reservaListOldReserva : reservaListOld) {
                if (!reservaListNew.contains(reservaListOldReserva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reserva " + reservaListOldReserva + " since its codDec field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Habitacion> attachedHabitacionListNew = new ArrayList<Habitacion>();
            for (Habitacion habitacionListNewHabitacionToAttach : habitacionListNew) {
                habitacionListNewHabitacionToAttach = em.getReference(habitacionListNewHabitacionToAttach.getClass(), habitacionListNewHabitacionToAttach.getCodHab());
                attachedHabitacionListNew.add(habitacionListNewHabitacionToAttach);
            }
            habitacionListNew = attachedHabitacionListNew;
            decoracion.setHabitacionList(habitacionListNew);
            List<Reserva> attachedReservaListNew = new ArrayList<Reserva>();
            for (Reserva reservaListNewReservaToAttach : reservaListNew) {
                reservaListNewReservaToAttach = em.getReference(reservaListNewReservaToAttach.getClass(), reservaListNewReservaToAttach.getCodRes());
                attachedReservaListNew.add(reservaListNewReservaToAttach);
            }
            reservaListNew = attachedReservaListNew;
            decoracion.setReservaList(reservaListNew);
            decoracion = em.merge(decoracion);
            for (Habitacion habitacionListOldHabitacion : habitacionListOld) {
                if (!habitacionListNew.contains(habitacionListOldHabitacion)) {
                    habitacionListOldHabitacion.getDecoracionList().remove(decoracion);
                    habitacionListOldHabitacion = em.merge(habitacionListOldHabitacion);
                }
            }
            for (Habitacion habitacionListNewHabitacion : habitacionListNew) {
                if (!habitacionListOld.contains(habitacionListNewHabitacion)) {
                    habitacionListNewHabitacion.getDecoracionList().add(decoracion);
                    habitacionListNewHabitacion = em.merge(habitacionListNewHabitacion);
                }
            }
            for (Reserva reservaListNewReserva : reservaListNew) {
                if (!reservaListOld.contains(reservaListNewReserva)) {
                    Decoracion oldCodDecOfReservaListNewReserva = reservaListNewReserva.getCodDec();
                    reservaListNewReserva.setCodDec(decoracion);
                    reservaListNewReserva = em.merge(reservaListNewReserva);
                    if (oldCodDecOfReservaListNewReserva != null && !oldCodDecOfReservaListNewReserva.equals(decoracion)) {
                        oldCodDecOfReservaListNewReserva.getReservaList().remove(reservaListNewReserva);
                        oldCodDecOfReservaListNewReserva = em.merge(oldCodDecOfReservaListNewReserva);
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
                Integer id = decoracion.getCodDec();
                if (findDecoracion(id) == null) {
                    throw new NonexistentEntityException("The decoracion with id " + id + " no longer exists.");
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
            Decoracion decoracion;
            try {
                decoracion = em.getReference(Decoracion.class, id);
                decoracion.getCodDec();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The decoracion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Reserva> reservaListOrphanCheck = decoracion.getReservaList();
            for (Reserva reservaListOrphanCheckReserva : reservaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Decoracion (" + decoracion + ") cannot be destroyed since the Reserva " + reservaListOrphanCheckReserva + " in its reservaList field has a non-nullable codDec field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Habitacion> habitacionList = decoracion.getHabitacionList();
            for (Habitacion habitacionListHabitacion : habitacionList) {
                habitacionListHabitacion.getDecoracionList().remove(decoracion);
                habitacionListHabitacion = em.merge(habitacionListHabitacion);
            }
            em.remove(decoracion);
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

    public List<Decoracion> findDecoracionEntities() {
        return findDecoracionEntities(true, -1, -1);
    }

    public List<Decoracion> findDecoracionEntities(int maxResults, int firstResult) {
        return findDecoracionEntities(false, maxResults, firstResult);
    }

    private List<Decoracion> findDecoracionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Decoracion.class));
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

    public Decoracion findDecoracion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Decoracion.class, id);
        } finally {
            em.close();
        }
    }

    public int getDecoracionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Decoracion> rt = cq.from(Decoracion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
