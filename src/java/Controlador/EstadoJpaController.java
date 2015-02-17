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
import Entidad.Estado;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Estadohabitacion;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class EstadoJpaController implements Serializable {

    public EstadoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estado estado) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (estado.getEstadohabitacionList() == null) {
            estado.setEstadohabitacionList(new ArrayList<Estadohabitacion>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Estadohabitacion> attachedEstadohabitacionList = new ArrayList<Estadohabitacion>();
            for (Estadohabitacion estadohabitacionListEstadohabitacionToAttach : estado.getEstadohabitacionList()) {
                estadohabitacionListEstadohabitacionToAttach = em.getReference(estadohabitacionListEstadohabitacionToAttach.getClass(), estadohabitacionListEstadohabitacionToAttach.getCodestHab());
                attachedEstadohabitacionList.add(estadohabitacionListEstadohabitacionToAttach);
            }
            estado.setEstadohabitacionList(attachedEstadohabitacionList);
            em.persist(estado);
            for (Estadohabitacion estadohabitacionListEstadohabitacion : estado.getEstadohabitacionList()) {
                Estado oldCodEstOfEstadohabitacionListEstadohabitacion = estadohabitacionListEstadohabitacion.getCodEst();
                estadohabitacionListEstadohabitacion.setCodEst(estado);
                estadohabitacionListEstadohabitacion = em.merge(estadohabitacionListEstadohabitacion);
                if (oldCodEstOfEstadohabitacionListEstadohabitacion != null) {
                    oldCodEstOfEstadohabitacionListEstadohabitacion.getEstadohabitacionList().remove(estadohabitacionListEstadohabitacion);
                    oldCodEstOfEstadohabitacionListEstadohabitacion = em.merge(oldCodEstOfEstadohabitacionListEstadohabitacion);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEstado(estado.getCodEst()) != null) {
                throw new PreexistingEntityException("Estado " + estado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estado estado) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Estado persistentEstado = em.find(Estado.class, estado.getCodEst());
            List<Estadohabitacion> estadohabitacionListOld = persistentEstado.getEstadohabitacionList();
            List<Estadohabitacion> estadohabitacionListNew = estado.getEstadohabitacionList();
            List<String> illegalOrphanMessages = null;
            for (Estadohabitacion estadohabitacionListOldEstadohabitacion : estadohabitacionListOld) {
                if (!estadohabitacionListNew.contains(estadohabitacionListOldEstadohabitacion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Estadohabitacion " + estadohabitacionListOldEstadohabitacion + " since its codEst field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Estadohabitacion> attachedEstadohabitacionListNew = new ArrayList<Estadohabitacion>();
            for (Estadohabitacion estadohabitacionListNewEstadohabitacionToAttach : estadohabitacionListNew) {
                estadohabitacionListNewEstadohabitacionToAttach = em.getReference(estadohabitacionListNewEstadohabitacionToAttach.getClass(), estadohabitacionListNewEstadohabitacionToAttach.getCodestHab());
                attachedEstadohabitacionListNew.add(estadohabitacionListNewEstadohabitacionToAttach);
            }
            estadohabitacionListNew = attachedEstadohabitacionListNew;
            estado.setEstadohabitacionList(estadohabitacionListNew);
            estado = em.merge(estado);
            for (Estadohabitacion estadohabitacionListNewEstadohabitacion : estadohabitacionListNew) {
                if (!estadohabitacionListOld.contains(estadohabitacionListNewEstadohabitacion)) {
                    Estado oldCodEstOfEstadohabitacionListNewEstadohabitacion = estadohabitacionListNewEstadohabitacion.getCodEst();
                    estadohabitacionListNewEstadohabitacion.setCodEst(estado);
                    estadohabitacionListNewEstadohabitacion = em.merge(estadohabitacionListNewEstadohabitacion);
                    if (oldCodEstOfEstadohabitacionListNewEstadohabitacion != null && !oldCodEstOfEstadohabitacionListNewEstadohabitacion.equals(estado)) {
                        oldCodEstOfEstadohabitacionListNewEstadohabitacion.getEstadohabitacionList().remove(estadohabitacionListNewEstadohabitacion);
                        oldCodEstOfEstadohabitacionListNewEstadohabitacion = em.merge(oldCodEstOfEstadohabitacionListNewEstadohabitacion);
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
                Integer id = estado.getCodEst();
                if (findEstado(id) == null) {
                    throw new NonexistentEntityException("The estado with id " + id + " no longer exists.");
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
            Estado estado;
            try {
                estado = em.getReference(Estado.class, id);
                estado.getCodEst();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Estadohabitacion> estadohabitacionListOrphanCheck = estado.getEstadohabitacionList();
            for (Estadohabitacion estadohabitacionListOrphanCheckEstadohabitacion : estadohabitacionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estado (" + estado + ") cannot be destroyed since the Estadohabitacion " + estadohabitacionListOrphanCheckEstadohabitacion + " in its estadohabitacionList field has a non-nullable codEst field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(estado);
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

    public List<Estado> findEstadoEntities() {
        return findEstadoEntities(true, -1, -1);
    }

    public List<Estado> findEstadoEntities(int maxResults, int firstResult) {
        return findEstadoEntities(false, maxResults, firstResult);
    }

    private List<Estado> findEstadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estado.class));
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

    public Estado findEstado(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estado.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estado> rt = cq.from(Estado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
