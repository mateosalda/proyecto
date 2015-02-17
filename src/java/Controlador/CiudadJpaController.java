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
import Entidad.Ciudad;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidad.Departamento;
import Entidad.Motel;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class CiudadJpaController implements Serializable {

    public CiudadJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ciudad ciudad) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (ciudad.getMotelList() == null) {
            ciudad.setMotelList(new ArrayList<Motel>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Departamento codDept = ciudad.getCodDept();
            if (codDept != null) {
                codDept = em.getReference(codDept.getClass(), codDept.getCodDept());
                ciudad.setCodDept(codDept);
            }
            List<Motel> attachedMotelList = new ArrayList<Motel>();
            for (Motel motelListMotelToAttach : ciudad.getMotelList()) {
                motelListMotelToAttach = em.getReference(motelListMotelToAttach.getClass(), motelListMotelToAttach.getNitMot());
                attachedMotelList.add(motelListMotelToAttach);
            }
            ciudad.setMotelList(attachedMotelList);
            em.persist(ciudad);
            if (codDept != null) {
                codDept.getCiudadList().add(ciudad);
                codDept = em.merge(codDept);
            }
            for (Motel motelListMotel : ciudad.getMotelList()) {
                Ciudad oldCodCiudOfMotelListMotel = motelListMotel.getCodCiud();
                motelListMotel.setCodCiud(ciudad);
                motelListMotel = em.merge(motelListMotel);
                if (oldCodCiudOfMotelListMotel != null) {
                    oldCodCiudOfMotelListMotel.getMotelList().remove(motelListMotel);
                    oldCodCiudOfMotelListMotel = em.merge(oldCodCiudOfMotelListMotel);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCiudad(ciudad.getCodCiud()) != null) {
                throw new PreexistingEntityException("Ciudad " + ciudad + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ciudad ciudad) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ciudad persistentCiudad = em.find(Ciudad.class, ciudad.getCodCiud());
            Departamento codDeptOld = persistentCiudad.getCodDept();
            Departamento codDeptNew = ciudad.getCodDept();
            List<Motel> motelListOld = persistentCiudad.getMotelList();
            List<Motel> motelListNew = ciudad.getMotelList();
            List<String> illegalOrphanMessages = null;
            for (Motel motelListOldMotel : motelListOld) {
                if (!motelListNew.contains(motelListOldMotel)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Motel " + motelListOldMotel + " since its codCiud field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codDeptNew != null) {
                codDeptNew = em.getReference(codDeptNew.getClass(), codDeptNew.getCodDept());
                ciudad.setCodDept(codDeptNew);
            }
            List<Motel> attachedMotelListNew = new ArrayList<Motel>();
            for (Motel motelListNewMotelToAttach : motelListNew) {
                motelListNewMotelToAttach = em.getReference(motelListNewMotelToAttach.getClass(), motelListNewMotelToAttach.getNitMot());
                attachedMotelListNew.add(motelListNewMotelToAttach);
            }
            motelListNew = attachedMotelListNew;
            ciudad.setMotelList(motelListNew);
            ciudad = em.merge(ciudad);
            if (codDeptOld != null && !codDeptOld.equals(codDeptNew)) {
                codDeptOld.getCiudadList().remove(ciudad);
                codDeptOld = em.merge(codDeptOld);
            }
            if (codDeptNew != null && !codDeptNew.equals(codDeptOld)) {
                codDeptNew.getCiudadList().add(ciudad);
                codDeptNew = em.merge(codDeptNew);
            }
            for (Motel motelListNewMotel : motelListNew) {
                if (!motelListOld.contains(motelListNewMotel)) {
                    Ciudad oldCodCiudOfMotelListNewMotel = motelListNewMotel.getCodCiud();
                    motelListNewMotel.setCodCiud(ciudad);
                    motelListNewMotel = em.merge(motelListNewMotel);
                    if (oldCodCiudOfMotelListNewMotel != null && !oldCodCiudOfMotelListNewMotel.equals(ciudad)) {
                        oldCodCiudOfMotelListNewMotel.getMotelList().remove(motelListNewMotel);
                        oldCodCiudOfMotelListNewMotel = em.merge(oldCodCiudOfMotelListNewMotel);
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
                Integer id = ciudad.getCodCiud();
                if (findCiudad(id) == null) {
                    throw new NonexistentEntityException("The ciudad with id " + id + " no longer exists.");
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
            Ciudad ciudad;
            try {
                ciudad = em.getReference(Ciudad.class, id);
                ciudad.getCodCiud();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ciudad with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Motel> motelListOrphanCheck = ciudad.getMotelList();
            for (Motel motelListOrphanCheckMotel : motelListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ciudad (" + ciudad + ") cannot be destroyed since the Motel " + motelListOrphanCheckMotel + " in its motelList field has a non-nullable codCiud field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Departamento codDept = ciudad.getCodDept();
            if (codDept != null) {
                codDept.getCiudadList().remove(ciudad);
                codDept = em.merge(codDept);
            }
            em.remove(ciudad);
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

    public List<Ciudad> findCiudadEntities() {
        return findCiudadEntities(true, -1, -1);
    }

    public List<Ciudad> findCiudadEntities(int maxResults, int firstResult) {
        return findCiudadEntities(false, maxResults, firstResult);
    }

    private List<Ciudad> findCiudadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ciudad.class));
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

    public Ciudad findCiudad(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ciudad.class, id);
        } finally {
            em.close();
        }
    }

    public int getCiudadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ciudad> rt = cq.from(Ciudad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
