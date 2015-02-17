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
import Entidad.Decoracion;
import Entidad.Habitacion;
import Entidad.Estadoreserva;
import Entidad.Cliente;
import Entidad.Motel;
import Entidad.Reserva;
import Entidad.Reservadetalle;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class ReservaJpaController implements Serializable {

    public ReservaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Reserva reserva) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (reserva.getReservadetalleList() == null) {
            reserva.setReservadetalleList(new ArrayList<Reservadetalle>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Decoracion codDec = reserva.getCodDec();
            if (codDec != null) {
                codDec = em.getReference(codDec.getClass(), codDec.getCodDec());
                reserva.setCodDec(codDec);
            }
            Habitacion codHab = reserva.getCodHab();
            if (codHab != null) {
                codHab = em.getReference(codHab.getClass(), codHab.getCodHab());
                reserva.setCodHab(codHab);
            }
            Estadoreserva codestRes = reserva.getCodestRes();
            if (codestRes != null) {
                codestRes = em.getReference(codestRes.getClass(), codestRes.getCodestRes());
                reserva.setCodestRes(codestRes);
            }
            Cliente pinCli = reserva.getPinCli();
            if (pinCli != null) {
                pinCli = em.getReference(pinCli.getClass(), pinCli.getPinCli());
                reserva.setPinCli(pinCli);
            }
            Motel nitMot = reserva.getNitMot();
            if (nitMot != null) {
                nitMot = em.getReference(nitMot.getClass(), nitMot.getNitMot());
                reserva.setNitMot(nitMot);
            }
            List<Reservadetalle> attachedReservadetalleList = new ArrayList<Reservadetalle>();
            for (Reservadetalle reservadetalleListReservadetalleToAttach : reserva.getReservadetalleList()) {
                reservadetalleListReservadetalleToAttach = em.getReference(reservadetalleListReservadetalleToAttach.getClass(), reservadetalleListReservadetalleToAttach.getCodresDet());
                attachedReservadetalleList.add(reservadetalleListReservadetalleToAttach);
            }
            reserva.setReservadetalleList(attachedReservadetalleList);
            em.persist(reserva);
            if (codDec != null) {
                codDec.getReservaList().add(reserva);
                codDec = em.merge(codDec);
            }
            if (codHab != null) {
                codHab.getReservaList().add(reserva);
                codHab = em.merge(codHab);
            }
            if (codestRes != null) {
                codestRes.getReservaList().add(reserva);
                codestRes = em.merge(codestRes);
            }
            if (pinCli != null) {
                pinCli.getReservaList().add(reserva);
                pinCli = em.merge(pinCli);
            }
            if (nitMot != null) {
                nitMot.getReservaList().add(reserva);
                nitMot = em.merge(nitMot);
            }
            for (Reservadetalle reservadetalleListReservadetalle : reserva.getReservadetalleList()) {
                Reserva oldCodResOfReservadetalleListReservadetalle = reservadetalleListReservadetalle.getCodRes();
                reservadetalleListReservadetalle.setCodRes(reserva);
                reservadetalleListReservadetalle = em.merge(reservadetalleListReservadetalle);
                if (oldCodResOfReservadetalleListReservadetalle != null) {
                    oldCodResOfReservadetalleListReservadetalle.getReservadetalleList().remove(reservadetalleListReservadetalle);
                    oldCodResOfReservadetalleListReservadetalle = em.merge(oldCodResOfReservadetalleListReservadetalle);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findReserva(reserva.getCodRes()) != null) {
                throw new PreexistingEntityException("Reserva " + reserva + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Reserva reserva) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Reserva persistentReserva = em.find(Reserva.class, reserva.getCodRes());
            Decoracion codDecOld = persistentReserva.getCodDec();
            Decoracion codDecNew = reserva.getCodDec();
            Habitacion codHabOld = persistentReserva.getCodHab();
            Habitacion codHabNew = reserva.getCodHab();
            Estadoreserva codestResOld = persistentReserva.getCodestRes();
            Estadoreserva codestResNew = reserva.getCodestRes();
            Cliente pinCliOld = persistentReserva.getPinCli();
            Cliente pinCliNew = reserva.getPinCli();
            Motel nitMotOld = persistentReserva.getNitMot();
            Motel nitMotNew = reserva.getNitMot();
            List<Reservadetalle> reservadetalleListOld = persistentReserva.getReservadetalleList();
            List<Reservadetalle> reservadetalleListNew = reserva.getReservadetalleList();
            List<String> illegalOrphanMessages = null;
            for (Reservadetalle reservadetalleListOldReservadetalle : reservadetalleListOld) {
                if (!reservadetalleListNew.contains(reservadetalleListOldReservadetalle)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reservadetalle " + reservadetalleListOldReservadetalle + " since its codRes field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codDecNew != null) {
                codDecNew = em.getReference(codDecNew.getClass(), codDecNew.getCodDec());
                reserva.setCodDec(codDecNew);
            }
            if (codHabNew != null) {
                codHabNew = em.getReference(codHabNew.getClass(), codHabNew.getCodHab());
                reserva.setCodHab(codHabNew);
            }
            if (codestResNew != null) {
                codestResNew = em.getReference(codestResNew.getClass(), codestResNew.getCodestRes());
                reserva.setCodestRes(codestResNew);
            }
            if (pinCliNew != null) {
                pinCliNew = em.getReference(pinCliNew.getClass(), pinCliNew.getPinCli());
                reserva.setPinCli(pinCliNew);
            }
            if (nitMotNew != null) {
                nitMotNew = em.getReference(nitMotNew.getClass(), nitMotNew.getNitMot());
                reserva.setNitMot(nitMotNew);
            }
            List<Reservadetalle> attachedReservadetalleListNew = new ArrayList<Reservadetalle>();
            for (Reservadetalle reservadetalleListNewReservadetalleToAttach : reservadetalleListNew) {
                reservadetalleListNewReservadetalleToAttach = em.getReference(reservadetalleListNewReservadetalleToAttach.getClass(), reservadetalleListNewReservadetalleToAttach.getCodresDet());
                attachedReservadetalleListNew.add(reservadetalleListNewReservadetalleToAttach);
            }
            reservadetalleListNew = attachedReservadetalleListNew;
            reserva.setReservadetalleList(reservadetalleListNew);
            reserva = em.merge(reserva);
            if (codDecOld != null && !codDecOld.equals(codDecNew)) {
                codDecOld.getReservaList().remove(reserva);
                codDecOld = em.merge(codDecOld);
            }
            if (codDecNew != null && !codDecNew.equals(codDecOld)) {
                codDecNew.getReservaList().add(reserva);
                codDecNew = em.merge(codDecNew);
            }
            if (codHabOld != null && !codHabOld.equals(codHabNew)) {
                codHabOld.getReservaList().remove(reserva);
                codHabOld = em.merge(codHabOld);
            }
            if (codHabNew != null && !codHabNew.equals(codHabOld)) {
                codHabNew.getReservaList().add(reserva);
                codHabNew = em.merge(codHabNew);
            }
            if (codestResOld != null && !codestResOld.equals(codestResNew)) {
                codestResOld.getReservaList().remove(reserva);
                codestResOld = em.merge(codestResOld);
            }
            if (codestResNew != null && !codestResNew.equals(codestResOld)) {
                codestResNew.getReservaList().add(reserva);
                codestResNew = em.merge(codestResNew);
            }
            if (pinCliOld != null && !pinCliOld.equals(pinCliNew)) {
                pinCliOld.getReservaList().remove(reserva);
                pinCliOld = em.merge(pinCliOld);
            }
            if (pinCliNew != null && !pinCliNew.equals(pinCliOld)) {
                pinCliNew.getReservaList().add(reserva);
                pinCliNew = em.merge(pinCliNew);
            }
            if (nitMotOld != null && !nitMotOld.equals(nitMotNew)) {
                nitMotOld.getReservaList().remove(reserva);
                nitMotOld = em.merge(nitMotOld);
            }
            if (nitMotNew != null && !nitMotNew.equals(nitMotOld)) {
                nitMotNew.getReservaList().add(reserva);
                nitMotNew = em.merge(nitMotNew);
            }
            for (Reservadetalle reservadetalleListNewReservadetalle : reservadetalleListNew) {
                if (!reservadetalleListOld.contains(reservadetalleListNewReservadetalle)) {
                    Reserva oldCodResOfReservadetalleListNewReservadetalle = reservadetalleListNewReservadetalle.getCodRes();
                    reservadetalleListNewReservadetalle.setCodRes(reserva);
                    reservadetalleListNewReservadetalle = em.merge(reservadetalleListNewReservadetalle);
                    if (oldCodResOfReservadetalleListNewReservadetalle != null && !oldCodResOfReservadetalleListNewReservadetalle.equals(reserva)) {
                        oldCodResOfReservadetalleListNewReservadetalle.getReservadetalleList().remove(reservadetalleListNewReservadetalle);
                        oldCodResOfReservadetalleListNewReservadetalle = em.merge(oldCodResOfReservadetalleListNewReservadetalle);
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
                Integer id = reserva.getCodRes();
                if (findReserva(id) == null) {
                    throw new NonexistentEntityException("The reserva with id " + id + " no longer exists.");
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
            Reserva reserva;
            try {
                reserva = em.getReference(Reserva.class, id);
                reserva.getCodRes();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reserva with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Reservadetalle> reservadetalleListOrphanCheck = reserva.getReservadetalleList();
            for (Reservadetalle reservadetalleListOrphanCheckReservadetalle : reservadetalleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Reserva (" + reserva + ") cannot be destroyed since the Reservadetalle " + reservadetalleListOrphanCheckReservadetalle + " in its reservadetalleList field has a non-nullable codRes field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Decoracion codDec = reserva.getCodDec();
            if (codDec != null) {
                codDec.getReservaList().remove(reserva);
                codDec = em.merge(codDec);
            }
            Habitacion codHab = reserva.getCodHab();
            if (codHab != null) {
                codHab.getReservaList().remove(reserva);
                codHab = em.merge(codHab);
            }
            Estadoreserva codestRes = reserva.getCodestRes();
            if (codestRes != null) {
                codestRes.getReservaList().remove(reserva);
                codestRes = em.merge(codestRes);
            }
            Cliente pinCli = reserva.getPinCli();
            if (pinCli != null) {
                pinCli.getReservaList().remove(reserva);
                pinCli = em.merge(pinCli);
            }
            Motel nitMot = reserva.getNitMot();
            if (nitMot != null) {
                nitMot.getReservaList().remove(reserva);
                nitMot = em.merge(nitMot);
            }
            em.remove(reserva);
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

    public List<Reserva> findReservaEntities() {
        return findReservaEntities(true, -1, -1);
    }

    public List<Reserva> findReservaEntities(int maxResults, int firstResult) {
        return findReservaEntities(false, maxResults, firstResult);
    }

    private List<Reserva> findReservaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Reserva.class));
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

    public Reserva findReserva(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Reserva.class, id);
        } finally {
            em.close();
        }
    }

    public int getReservaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Reserva> rt = cq.from(Reserva.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
