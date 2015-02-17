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
import Entidad.Usuario;
import Entidad.Ciudad;
import Entidad.Promocionmotel;
import java.util.ArrayList;
import java.util.List;
import Entidad.Habitacion;
import Entidad.Motel;
import Entidad.Reserva;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class MotelJpaController implements Serializable {

    public MotelJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Motel motel) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (motel.getPromocionmotelList() == null) {
            motel.setPromocionmotelList(new ArrayList<Promocionmotel>());
        }
        if (motel.getHabitacionList() == null) {
            motel.setHabitacionList(new ArrayList<Habitacion>());
        }
        if (motel.getReservaList() == null) {
            motel.setReservaList(new ArrayList<Reserva>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario codUsu = motel.getCodUsu();
            if (codUsu != null) {
                codUsu = em.getReference(codUsu.getClass(), codUsu.getCodUsu());
                motel.setCodUsu(codUsu);
            }
            Ciudad codCiud = motel.getCodCiud();
            if (codCiud != null) {
                codCiud = em.getReference(codCiud.getClass(), codCiud.getCodCiud());
                motel.setCodCiud(codCiud);
            }
            List<Promocionmotel> attachedPromocionmotelList = new ArrayList<Promocionmotel>();
            for (Promocionmotel promocionmotelListPromocionmotelToAttach : motel.getPromocionmotelList()) {
                promocionmotelListPromocionmotelToAttach = em.getReference(promocionmotelListPromocionmotelToAttach.getClass(), promocionmotelListPromocionmotelToAttach.getCodpromoMot());
                attachedPromocionmotelList.add(promocionmotelListPromocionmotelToAttach);
            }
            motel.setPromocionmotelList(attachedPromocionmotelList);
            List<Habitacion> attachedHabitacionList = new ArrayList<Habitacion>();
            for (Habitacion habitacionListHabitacionToAttach : motel.getHabitacionList()) {
                habitacionListHabitacionToAttach = em.getReference(habitacionListHabitacionToAttach.getClass(), habitacionListHabitacionToAttach.getCodHab());
                attachedHabitacionList.add(habitacionListHabitacionToAttach);
            }
            motel.setHabitacionList(attachedHabitacionList);
            List<Reserva> attachedReservaList = new ArrayList<Reserva>();
            for (Reserva reservaListReservaToAttach : motel.getReservaList()) {
                reservaListReservaToAttach = em.getReference(reservaListReservaToAttach.getClass(), reservaListReservaToAttach.getCodRes());
                attachedReservaList.add(reservaListReservaToAttach);
            }
            motel.setReservaList(attachedReservaList);
            em.persist(motel);
            if (codUsu != null) {
                codUsu.getMotelList().add(motel);
                codUsu = em.merge(codUsu);
            }
            if (codCiud != null) {
                codCiud.getMotelList().add(motel);
                codCiud = em.merge(codCiud);
            }
            for (Promocionmotel promocionmotelListPromocionmotel : motel.getPromocionmotelList()) {
                Motel oldNitMotOfPromocionmotelListPromocionmotel = promocionmotelListPromocionmotel.getNitMot();
                promocionmotelListPromocionmotel.setNitMot(motel);
                promocionmotelListPromocionmotel = em.merge(promocionmotelListPromocionmotel);
                if (oldNitMotOfPromocionmotelListPromocionmotel != null) {
                    oldNitMotOfPromocionmotelListPromocionmotel.getPromocionmotelList().remove(promocionmotelListPromocionmotel);
                    oldNitMotOfPromocionmotelListPromocionmotel = em.merge(oldNitMotOfPromocionmotelListPromocionmotel);
                }
            }
            for (Habitacion habitacionListHabitacion : motel.getHabitacionList()) {
                Motel oldNitMotOfHabitacionListHabitacion = habitacionListHabitacion.getNitMot();
                habitacionListHabitacion.setNitMot(motel);
                habitacionListHabitacion = em.merge(habitacionListHabitacion);
                if (oldNitMotOfHabitacionListHabitacion != null) {
                    oldNitMotOfHabitacionListHabitacion.getHabitacionList().remove(habitacionListHabitacion);
                    oldNitMotOfHabitacionListHabitacion = em.merge(oldNitMotOfHabitacionListHabitacion);
                }
            }
            for (Reserva reservaListReserva : motel.getReservaList()) {
                Motel oldNitMotOfReservaListReserva = reservaListReserva.getNitMot();
                reservaListReserva.setNitMot(motel);
                reservaListReserva = em.merge(reservaListReserva);
                if (oldNitMotOfReservaListReserva != null) {
                    oldNitMotOfReservaListReserva.getReservaList().remove(reservaListReserva);
                    oldNitMotOfReservaListReserva = em.merge(oldNitMotOfReservaListReserva);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findMotel(motel.getNitMot()) != null) {
                throw new PreexistingEntityException("Motel " + motel + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Motel motel) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Motel persistentMotel = em.find(Motel.class, motel.getNitMot());
            Usuario codUsuOld = persistentMotel.getCodUsu();
            Usuario codUsuNew = motel.getCodUsu();
            Ciudad codCiudOld = persistentMotel.getCodCiud();
            Ciudad codCiudNew = motel.getCodCiud();
            List<Promocionmotel> promocionmotelListOld = persistentMotel.getPromocionmotelList();
            List<Promocionmotel> promocionmotelListNew = motel.getPromocionmotelList();
            List<Habitacion> habitacionListOld = persistentMotel.getHabitacionList();
            List<Habitacion> habitacionListNew = motel.getHabitacionList();
            List<Reserva> reservaListOld = persistentMotel.getReservaList();
            List<Reserva> reservaListNew = motel.getReservaList();
            List<String> illegalOrphanMessages = null;
            for (Promocionmotel promocionmotelListOldPromocionmotel : promocionmotelListOld) {
                if (!promocionmotelListNew.contains(promocionmotelListOldPromocionmotel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Promocionmotel " + promocionmotelListOldPromocionmotel + " since its nitMot field is not nullable.");
                }
            }
            for (Habitacion habitacionListOldHabitacion : habitacionListOld) {
                if (!habitacionListNew.contains(habitacionListOldHabitacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Habitacion " + habitacionListOldHabitacion + " since its nitMot field is not nullable.");
                }
            }
            for (Reserva reservaListOldReserva : reservaListOld) {
                if (!reservaListNew.contains(reservaListOldReserva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reserva " + reservaListOldReserva + " since its nitMot field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codUsuNew != null) {
                codUsuNew = em.getReference(codUsuNew.getClass(), codUsuNew.getCodUsu());
                motel.setCodUsu(codUsuNew);
            }
            if (codCiudNew != null) {
                codCiudNew = em.getReference(codCiudNew.getClass(), codCiudNew.getCodCiud());
                motel.setCodCiud(codCiudNew);
            }
            List<Promocionmotel> attachedPromocionmotelListNew = new ArrayList<Promocionmotel>();
            for (Promocionmotel promocionmotelListNewPromocionmotelToAttach : promocionmotelListNew) {
                promocionmotelListNewPromocionmotelToAttach = em.getReference(promocionmotelListNewPromocionmotelToAttach.getClass(), promocionmotelListNewPromocionmotelToAttach.getCodpromoMot());
                attachedPromocionmotelListNew.add(promocionmotelListNewPromocionmotelToAttach);
            }
            promocionmotelListNew = attachedPromocionmotelListNew;
            motel.setPromocionmotelList(promocionmotelListNew);
            List<Habitacion> attachedHabitacionListNew = new ArrayList<Habitacion>();
            for (Habitacion habitacionListNewHabitacionToAttach : habitacionListNew) {
                habitacionListNewHabitacionToAttach = em.getReference(habitacionListNewHabitacionToAttach.getClass(), habitacionListNewHabitacionToAttach.getCodHab());
                attachedHabitacionListNew.add(habitacionListNewHabitacionToAttach);
            }
            habitacionListNew = attachedHabitacionListNew;
            motel.setHabitacionList(habitacionListNew);
            List<Reserva> attachedReservaListNew = new ArrayList<Reserva>();
            for (Reserva reservaListNewReservaToAttach : reservaListNew) {
                reservaListNewReservaToAttach = em.getReference(reservaListNewReservaToAttach.getClass(), reservaListNewReservaToAttach.getCodRes());
                attachedReservaListNew.add(reservaListNewReservaToAttach);
            }
            reservaListNew = attachedReservaListNew;
            motel.setReservaList(reservaListNew);
            motel = em.merge(motel);
            if (codUsuOld != null && !codUsuOld.equals(codUsuNew)) {
                codUsuOld.getMotelList().remove(motel);
                codUsuOld = em.merge(codUsuOld);
            }
            if (codUsuNew != null && !codUsuNew.equals(codUsuOld)) {
                codUsuNew.getMotelList().add(motel);
                codUsuNew = em.merge(codUsuNew);
            }
            if (codCiudOld != null && !codCiudOld.equals(codCiudNew)) {
                codCiudOld.getMotelList().remove(motel);
                codCiudOld = em.merge(codCiudOld);
            }
            if (codCiudNew != null && !codCiudNew.equals(codCiudOld)) {
                codCiudNew.getMotelList().add(motel);
                codCiudNew = em.merge(codCiudNew);
            }
            for (Promocionmotel promocionmotelListNewPromocionmotel : promocionmotelListNew) {
                if (!promocionmotelListOld.contains(promocionmotelListNewPromocionmotel)) {
                    Motel oldNitMotOfPromocionmotelListNewPromocionmotel = promocionmotelListNewPromocionmotel.getNitMot();
                    promocionmotelListNewPromocionmotel.setNitMot(motel);
                    promocionmotelListNewPromocionmotel = em.merge(promocionmotelListNewPromocionmotel);
                    if (oldNitMotOfPromocionmotelListNewPromocionmotel != null && !oldNitMotOfPromocionmotelListNewPromocionmotel.equals(motel)) {
                        oldNitMotOfPromocionmotelListNewPromocionmotel.getPromocionmotelList().remove(promocionmotelListNewPromocionmotel);
                        oldNitMotOfPromocionmotelListNewPromocionmotel = em.merge(oldNitMotOfPromocionmotelListNewPromocionmotel);
                    }
                }
            }
            for (Habitacion habitacionListNewHabitacion : habitacionListNew) {
                if (!habitacionListOld.contains(habitacionListNewHabitacion)) {
                    Motel oldNitMotOfHabitacionListNewHabitacion = habitacionListNewHabitacion.getNitMot();
                    habitacionListNewHabitacion.setNitMot(motel);
                    habitacionListNewHabitacion = em.merge(habitacionListNewHabitacion);
                    if (oldNitMotOfHabitacionListNewHabitacion != null && !oldNitMotOfHabitacionListNewHabitacion.equals(motel)) {
                        oldNitMotOfHabitacionListNewHabitacion.getHabitacionList().remove(habitacionListNewHabitacion);
                        oldNitMotOfHabitacionListNewHabitacion = em.merge(oldNitMotOfHabitacionListNewHabitacion);
                    }
                }
            }
            for (Reserva reservaListNewReserva : reservaListNew) {
                if (!reservaListOld.contains(reservaListNewReserva)) {
                    Motel oldNitMotOfReservaListNewReserva = reservaListNewReserva.getNitMot();
                    reservaListNewReserva.setNitMot(motel);
                    reservaListNewReserva = em.merge(reservaListNewReserva);
                    if (oldNitMotOfReservaListNewReserva != null && !oldNitMotOfReservaListNewReserva.equals(motel)) {
                        oldNitMotOfReservaListNewReserva.getReservaList().remove(reservaListNewReserva);
                        oldNitMotOfReservaListNewReserva = em.merge(oldNitMotOfReservaListNewReserva);
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
                Integer id = motel.getNitMot();
                if (findMotel(id) == null) {
                    throw new NonexistentEntityException("The motel with id " + id + " no longer exists.");
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
            Motel motel;
            try {
                motel = em.getReference(Motel.class, id);
                motel.getNitMot();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The motel with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Promocionmotel> promocionmotelListOrphanCheck = motel.getPromocionmotelList();
            for (Promocionmotel promocionmotelListOrphanCheckPromocionmotel : promocionmotelListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Motel (" + motel + ") cannot be destroyed since the Promocionmotel " + promocionmotelListOrphanCheckPromocionmotel + " in its promocionmotelList field has a non-nullable nitMot field.");
            }
            List<Habitacion> habitacionListOrphanCheck = motel.getHabitacionList();
            for (Habitacion habitacionListOrphanCheckHabitacion : habitacionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Motel (" + motel + ") cannot be destroyed since the Habitacion " + habitacionListOrphanCheckHabitacion + " in its habitacionList field has a non-nullable nitMot field.");
            }
            List<Reserva> reservaListOrphanCheck = motel.getReservaList();
            for (Reserva reservaListOrphanCheckReserva : reservaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Motel (" + motel + ") cannot be destroyed since the Reserva " + reservaListOrphanCheckReserva + " in its reservaList field has a non-nullable nitMot field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario codUsu = motel.getCodUsu();
            if (codUsu != null) {
                codUsu.getMotelList().remove(motel);
                codUsu = em.merge(codUsu);
            }
            Ciudad codCiud = motel.getCodCiud();
            if (codCiud != null) {
                codCiud.getMotelList().remove(motel);
                codCiud = em.merge(codCiud);
            }
            em.remove(motel);
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

    public List<Motel> findMotelEntities() {
        return findMotelEntities(true, -1, -1);
    }

    public List<Motel> findMotelEntities(int maxResults, int firstResult) {
        return findMotelEntities(false, maxResults, firstResult);
    }

    private List<Motel> findMotelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Motel.class));
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

    public Motel findMotel(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Motel.class, id);
        } finally {
            em.close();
        }
    }

    public int getMotelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Motel> rt = cq.from(Motel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
