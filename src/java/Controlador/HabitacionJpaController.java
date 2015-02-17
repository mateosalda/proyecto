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
import Entidad.Motel;
import Entidad.Decoracion;
import java.util.ArrayList;
import java.util.List;
import Entidad.Estadohabitacion;
import Entidad.Habitacion;
import Entidad.Reserva;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class HabitacionJpaController implements Serializable {

    public HabitacionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Habitacion habitacion) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (habitacion.getDecoracionList() == null) {
            habitacion.setDecoracionList(new ArrayList<Decoracion>());
        }
        if (habitacion.getEstadohabitacionList() == null) {
            habitacion.setEstadohabitacionList(new ArrayList<Estadohabitacion>());
        }
        if (habitacion.getReservaList() == null) {
            habitacion.setReservaList(new ArrayList<Reserva>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Motel nitMot = habitacion.getNitMot();
            if (nitMot != null) {
                nitMot = em.getReference(nitMot.getClass(), nitMot.getNitMot());
                habitacion.setNitMot(nitMot);
            }
            List<Decoracion> attachedDecoracionList = new ArrayList<Decoracion>();
            for (Decoracion decoracionListDecoracionToAttach : habitacion.getDecoracionList()) {
                decoracionListDecoracionToAttach = em.getReference(decoracionListDecoracionToAttach.getClass(), decoracionListDecoracionToAttach.getCodDec());
                attachedDecoracionList.add(decoracionListDecoracionToAttach);
            }
            habitacion.setDecoracionList(attachedDecoracionList);
            List<Estadohabitacion> attachedEstadohabitacionList = new ArrayList<Estadohabitacion>();
            for (Estadohabitacion estadohabitacionListEstadohabitacionToAttach : habitacion.getEstadohabitacionList()) {
                estadohabitacionListEstadohabitacionToAttach = em.getReference(estadohabitacionListEstadohabitacionToAttach.getClass(), estadohabitacionListEstadohabitacionToAttach.getCodestHab());
                attachedEstadohabitacionList.add(estadohabitacionListEstadohabitacionToAttach);
            }
            habitacion.setEstadohabitacionList(attachedEstadohabitacionList);
            List<Reserva> attachedReservaList = new ArrayList<Reserva>();
            for (Reserva reservaListReservaToAttach : habitacion.getReservaList()) {
                reservaListReservaToAttach = em.getReference(reservaListReservaToAttach.getClass(), reservaListReservaToAttach.getCodRes());
                attachedReservaList.add(reservaListReservaToAttach);
            }
            habitacion.setReservaList(attachedReservaList);
            em.persist(habitacion);
            if (nitMot != null) {
                nitMot.getHabitacionList().add(habitacion);
                nitMot = em.merge(nitMot);
            }
            for (Decoracion decoracionListDecoracion : habitacion.getDecoracionList()) {
                decoracionListDecoracion.getHabitacionList().add(habitacion);
                decoracionListDecoracion = em.merge(decoracionListDecoracion);
            }
            for (Estadohabitacion estadohabitacionListEstadohabitacion : habitacion.getEstadohabitacionList()) {
                Habitacion oldCodHabOfEstadohabitacionListEstadohabitacion = estadohabitacionListEstadohabitacion.getCodHab();
                estadohabitacionListEstadohabitacion.setCodHab(habitacion);
                estadohabitacionListEstadohabitacion = em.merge(estadohabitacionListEstadohabitacion);
                if (oldCodHabOfEstadohabitacionListEstadohabitacion != null) {
                    oldCodHabOfEstadohabitacionListEstadohabitacion.getEstadohabitacionList().remove(estadohabitacionListEstadohabitacion);
                    oldCodHabOfEstadohabitacionListEstadohabitacion = em.merge(oldCodHabOfEstadohabitacionListEstadohabitacion);
                }
            }
            for (Reserva reservaListReserva : habitacion.getReservaList()) {
                Habitacion oldCodHabOfReservaListReserva = reservaListReserva.getCodHab();
                reservaListReserva.setCodHab(habitacion);
                reservaListReserva = em.merge(reservaListReserva);
                if (oldCodHabOfReservaListReserva != null) {
                    oldCodHabOfReservaListReserva.getReservaList().remove(reservaListReserva);
                    oldCodHabOfReservaListReserva = em.merge(oldCodHabOfReservaListReserva);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findHabitacion(habitacion.getCodHab()) != null) {
                throw new PreexistingEntityException("Habitacion " + habitacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Habitacion habitacion) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Habitacion persistentHabitacion = em.find(Habitacion.class, habitacion.getCodHab());
            Motel nitMotOld = persistentHabitacion.getNitMot();
            Motel nitMotNew = habitacion.getNitMot();
            List<Decoracion> decoracionListOld = persistentHabitacion.getDecoracionList();
            List<Decoracion> decoracionListNew = habitacion.getDecoracionList();
            List<Estadohabitacion> estadohabitacionListOld = persistentHabitacion.getEstadohabitacionList();
            List<Estadohabitacion> estadohabitacionListNew = habitacion.getEstadohabitacionList();
            List<Reserva> reservaListOld = persistentHabitacion.getReservaList();
            List<Reserva> reservaListNew = habitacion.getReservaList();
            List<String> illegalOrphanMessages = null;
            for (Estadohabitacion estadohabitacionListOldEstadohabitacion : estadohabitacionListOld) {
                if (!estadohabitacionListNew.contains(estadohabitacionListOldEstadohabitacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Estadohabitacion " + estadohabitacionListOldEstadohabitacion + " since its codHab field is not nullable.");
                }
            }
            for (Reserva reservaListOldReserva : reservaListOld) {
                if (!reservaListNew.contains(reservaListOldReserva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reserva " + reservaListOldReserva + " since its codHab field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (nitMotNew != null) {
                nitMotNew = em.getReference(nitMotNew.getClass(), nitMotNew.getNitMot());
                habitacion.setNitMot(nitMotNew);
            }
            List<Decoracion> attachedDecoracionListNew = new ArrayList<Decoracion>();
            for (Decoracion decoracionListNewDecoracionToAttach : decoracionListNew) {
                decoracionListNewDecoracionToAttach = em.getReference(decoracionListNewDecoracionToAttach.getClass(), decoracionListNewDecoracionToAttach.getCodDec());
                attachedDecoracionListNew.add(decoracionListNewDecoracionToAttach);
            }
            decoracionListNew = attachedDecoracionListNew;
            habitacion.setDecoracionList(decoracionListNew);
            List<Estadohabitacion> attachedEstadohabitacionListNew = new ArrayList<Estadohabitacion>();
            for (Estadohabitacion estadohabitacionListNewEstadohabitacionToAttach : estadohabitacionListNew) {
                estadohabitacionListNewEstadohabitacionToAttach = em.getReference(estadohabitacionListNewEstadohabitacionToAttach.getClass(), estadohabitacionListNewEstadohabitacionToAttach.getCodestHab());
                attachedEstadohabitacionListNew.add(estadohabitacionListNewEstadohabitacionToAttach);
            }
            estadohabitacionListNew = attachedEstadohabitacionListNew;
            habitacion.setEstadohabitacionList(estadohabitacionListNew);
            List<Reserva> attachedReservaListNew = new ArrayList<Reserva>();
            for (Reserva reservaListNewReservaToAttach : reservaListNew) {
                reservaListNewReservaToAttach = em.getReference(reservaListNewReservaToAttach.getClass(), reservaListNewReservaToAttach.getCodRes());
                attachedReservaListNew.add(reservaListNewReservaToAttach);
            }
            reservaListNew = attachedReservaListNew;
            habitacion.setReservaList(reservaListNew);
            habitacion = em.merge(habitacion);
            if (nitMotOld != null && !nitMotOld.equals(nitMotNew)) {
                nitMotOld.getHabitacionList().remove(habitacion);
                nitMotOld = em.merge(nitMotOld);
            }
            if (nitMotNew != null && !nitMotNew.equals(nitMotOld)) {
                nitMotNew.getHabitacionList().add(habitacion);
                nitMotNew = em.merge(nitMotNew);
            }
            for (Decoracion decoracionListOldDecoracion : decoracionListOld) {
                if (!decoracionListNew.contains(decoracionListOldDecoracion)) {
                    decoracionListOldDecoracion.getHabitacionList().remove(habitacion);
                    decoracionListOldDecoracion = em.merge(decoracionListOldDecoracion);
                }
            }
            for (Decoracion decoracionListNewDecoracion : decoracionListNew) {
                if (!decoracionListOld.contains(decoracionListNewDecoracion)) {
                    decoracionListNewDecoracion.getHabitacionList().add(habitacion);
                    decoracionListNewDecoracion = em.merge(decoracionListNewDecoracion);
                }
            }
            for (Estadohabitacion estadohabitacionListNewEstadohabitacion : estadohabitacionListNew) {
                if (!estadohabitacionListOld.contains(estadohabitacionListNewEstadohabitacion)) {
                    Habitacion oldCodHabOfEstadohabitacionListNewEstadohabitacion = estadohabitacionListNewEstadohabitacion.getCodHab();
                    estadohabitacionListNewEstadohabitacion.setCodHab(habitacion);
                    estadohabitacionListNewEstadohabitacion = em.merge(estadohabitacionListNewEstadohabitacion);
                    if (oldCodHabOfEstadohabitacionListNewEstadohabitacion != null && !oldCodHabOfEstadohabitacionListNewEstadohabitacion.equals(habitacion)) {
                        oldCodHabOfEstadohabitacionListNewEstadohabitacion.getEstadohabitacionList().remove(estadohabitacionListNewEstadohabitacion);
                        oldCodHabOfEstadohabitacionListNewEstadohabitacion = em.merge(oldCodHabOfEstadohabitacionListNewEstadohabitacion);
                    }
                }
            }
            for (Reserva reservaListNewReserva : reservaListNew) {
                if (!reservaListOld.contains(reservaListNewReserva)) {
                    Habitacion oldCodHabOfReservaListNewReserva = reservaListNewReserva.getCodHab();
                    reservaListNewReserva.setCodHab(habitacion);
                    reservaListNewReserva = em.merge(reservaListNewReserva);
                    if (oldCodHabOfReservaListNewReserva != null && !oldCodHabOfReservaListNewReserva.equals(habitacion)) {
                        oldCodHabOfReservaListNewReserva.getReservaList().remove(reservaListNewReserva);
                        oldCodHabOfReservaListNewReserva = em.merge(oldCodHabOfReservaListNewReserva);
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
                Integer id = habitacion.getCodHab();
                if (findHabitacion(id) == null) {
                    throw new NonexistentEntityException("The habitacion with id " + id + " no longer exists.");
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
            Habitacion habitacion;
            try {
                habitacion = em.getReference(Habitacion.class, id);
                habitacion.getCodHab();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The habitacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Estadohabitacion> estadohabitacionListOrphanCheck = habitacion.getEstadohabitacionList();
            for (Estadohabitacion estadohabitacionListOrphanCheckEstadohabitacion : estadohabitacionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Habitacion (" + habitacion + ") cannot be destroyed since the Estadohabitacion " + estadohabitacionListOrphanCheckEstadohabitacion + " in its estadohabitacionList field has a non-nullable codHab field.");
            }
            List<Reserva> reservaListOrphanCheck = habitacion.getReservaList();
            for (Reserva reservaListOrphanCheckReserva : reservaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Habitacion (" + habitacion + ") cannot be destroyed since the Reserva " + reservaListOrphanCheckReserva + " in its reservaList field has a non-nullable codHab field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Motel nitMot = habitacion.getNitMot();
            if (nitMot != null) {
                nitMot.getHabitacionList().remove(habitacion);
                nitMot = em.merge(nitMot);
            }
            List<Decoracion> decoracionList = habitacion.getDecoracionList();
            for (Decoracion decoracionListDecoracion : decoracionList) {
                decoracionListDecoracion.getHabitacionList().remove(habitacion);
                decoracionListDecoracion = em.merge(decoracionListDecoracion);
            }
            em.remove(habitacion);
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

    public List<Habitacion> findHabitacionEntities() {
        return findHabitacionEntities(true, -1, -1);
    }

    public List<Habitacion> findHabitacionEntities(int maxResults, int firstResult) {
        return findHabitacionEntities(false, maxResults, firstResult);
    }

    private List<Habitacion> findHabitacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Habitacion.class));
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

    public Habitacion findHabitacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Habitacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getHabitacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Habitacion> rt = cq.from(Habitacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
