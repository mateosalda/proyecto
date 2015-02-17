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
import Entidad.Permisousuario;
import java.util.ArrayList;
import java.util.List;
import Entidad.Cliente;
import Entidad.Permiso;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cpe
 */
public class PermisoJpaController implements Serializable {

    public PermisoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Permiso permiso) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (permiso.getPermisousuarioList() == null) {
            permiso.setPermisousuarioList(new ArrayList<Permisousuario>());
        }
        if (permiso.getClienteList() == null) {
            permiso.setClienteList(new ArrayList<Cliente>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Permisousuario> attachedPermisousuarioList = new ArrayList<Permisousuario>();
            for (Permisousuario permisousuarioListPermisousuarioToAttach : permiso.getPermisousuarioList()) {
                permisousuarioListPermisousuarioToAttach = em.getReference(permisousuarioListPermisousuarioToAttach.getClass(), permisousuarioListPermisousuarioToAttach.getCodpermiUsu());
                attachedPermisousuarioList.add(permisousuarioListPermisousuarioToAttach);
            }
            permiso.setPermisousuarioList(attachedPermisousuarioList);
            List<Cliente> attachedClienteList = new ArrayList<Cliente>();
            for (Cliente clienteListClienteToAttach : permiso.getClienteList()) {
                clienteListClienteToAttach = em.getReference(clienteListClienteToAttach.getClass(), clienteListClienteToAttach.getPinCli());
                attachedClienteList.add(clienteListClienteToAttach);
            }
            permiso.setClienteList(attachedClienteList);
            em.persist(permiso);
            for (Permisousuario permisousuarioListPermisousuario : permiso.getPermisousuarioList()) {
                Permiso oldCodPermiOfPermisousuarioListPermisousuario = permisousuarioListPermisousuario.getCodPermi();
                permisousuarioListPermisousuario.setCodPermi(permiso);
                permisousuarioListPermisousuario = em.merge(permisousuarioListPermisousuario);
                if (oldCodPermiOfPermisousuarioListPermisousuario != null) {
                    oldCodPermiOfPermisousuarioListPermisousuario.getPermisousuarioList().remove(permisousuarioListPermisousuario);
                    oldCodPermiOfPermisousuarioListPermisousuario = em.merge(oldCodPermiOfPermisousuarioListPermisousuario);
                }
            }
            for (Cliente clienteListCliente : permiso.getClienteList()) {
                Permiso oldCodPermiOfClienteListCliente = clienteListCliente.getCodPermi();
                clienteListCliente.setCodPermi(permiso);
                clienteListCliente = em.merge(clienteListCliente);
                if (oldCodPermiOfClienteListCliente != null) {
                    oldCodPermiOfClienteListCliente.getClienteList().remove(clienteListCliente);
                    oldCodPermiOfClienteListCliente = em.merge(oldCodPermiOfClienteListCliente);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPermiso(permiso.getCodPermi()) != null) {
                throw new PreexistingEntityException("Permiso " + permiso + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Permiso permiso) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Permiso persistentPermiso = em.find(Permiso.class, permiso.getCodPermi());
            List<Permisousuario> permisousuarioListOld = persistentPermiso.getPermisousuarioList();
            List<Permisousuario> permisousuarioListNew = permiso.getPermisousuarioList();
            List<Cliente> clienteListOld = persistentPermiso.getClienteList();
            List<Cliente> clienteListNew = permiso.getClienteList();
            List<String> illegalOrphanMessages = null;
            for (Permisousuario permisousuarioListOldPermisousuario : permisousuarioListOld) {
                if (!permisousuarioListNew.contains(permisousuarioListOldPermisousuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Permisousuario " + permisousuarioListOldPermisousuario + " since its codPermi field is not nullable.");
                }
            }
            for (Cliente clienteListOldCliente : clienteListOld) {
                if (!clienteListNew.contains(clienteListOldCliente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cliente " + clienteListOldCliente + " since its codPermi field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permisousuario> attachedPermisousuarioListNew = new ArrayList<Permisousuario>();
            for (Permisousuario permisousuarioListNewPermisousuarioToAttach : permisousuarioListNew) {
                permisousuarioListNewPermisousuarioToAttach = em.getReference(permisousuarioListNewPermisousuarioToAttach.getClass(), permisousuarioListNewPermisousuarioToAttach.getCodpermiUsu());
                attachedPermisousuarioListNew.add(permisousuarioListNewPermisousuarioToAttach);
            }
            permisousuarioListNew = attachedPermisousuarioListNew;
            permiso.setPermisousuarioList(permisousuarioListNew);
            List<Cliente> attachedClienteListNew = new ArrayList<Cliente>();
            for (Cliente clienteListNewClienteToAttach : clienteListNew) {
                clienteListNewClienteToAttach = em.getReference(clienteListNewClienteToAttach.getClass(), clienteListNewClienteToAttach.getPinCli());
                attachedClienteListNew.add(clienteListNewClienteToAttach);
            }
            clienteListNew = attachedClienteListNew;
            permiso.setClienteList(clienteListNew);
            permiso = em.merge(permiso);
            for (Permisousuario permisousuarioListNewPermisousuario : permisousuarioListNew) {
                if (!permisousuarioListOld.contains(permisousuarioListNewPermisousuario)) {
                    Permiso oldCodPermiOfPermisousuarioListNewPermisousuario = permisousuarioListNewPermisousuario.getCodPermi();
                    permisousuarioListNewPermisousuario.setCodPermi(permiso);
                    permisousuarioListNewPermisousuario = em.merge(permisousuarioListNewPermisousuario);
                    if (oldCodPermiOfPermisousuarioListNewPermisousuario != null && !oldCodPermiOfPermisousuarioListNewPermisousuario.equals(permiso)) {
                        oldCodPermiOfPermisousuarioListNewPermisousuario.getPermisousuarioList().remove(permisousuarioListNewPermisousuario);
                        oldCodPermiOfPermisousuarioListNewPermisousuario = em.merge(oldCodPermiOfPermisousuarioListNewPermisousuario);
                    }
                }
            }
            for (Cliente clienteListNewCliente : clienteListNew) {
                if (!clienteListOld.contains(clienteListNewCliente)) {
                    Permiso oldCodPermiOfClienteListNewCliente = clienteListNewCliente.getCodPermi();
                    clienteListNewCliente.setCodPermi(permiso);
                    clienteListNewCliente = em.merge(clienteListNewCliente);
                    if (oldCodPermiOfClienteListNewCliente != null && !oldCodPermiOfClienteListNewCliente.equals(permiso)) {
                        oldCodPermiOfClienteListNewCliente.getClienteList().remove(clienteListNewCliente);
                        oldCodPermiOfClienteListNewCliente = em.merge(oldCodPermiOfClienteListNewCliente);
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
                Integer id = permiso.getCodPermi();
                if (findPermiso(id) == null) {
                    throw new NonexistentEntityException("The permiso with id " + id + " no longer exists.");
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
            Permiso permiso;
            try {
                permiso = em.getReference(Permiso.class, id);
                permiso.getCodPermi();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The permiso with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Permisousuario> permisousuarioListOrphanCheck = permiso.getPermisousuarioList();
            for (Permisousuario permisousuarioListOrphanCheckPermisousuario : permisousuarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Permiso (" + permiso + ") cannot be destroyed since the Permisousuario " + permisousuarioListOrphanCheckPermisousuario + " in its permisousuarioList field has a non-nullable codPermi field.");
            }
            List<Cliente> clienteListOrphanCheck = permiso.getClienteList();
            for (Cliente clienteListOrphanCheckCliente : clienteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Permiso (" + permiso + ") cannot be destroyed since the Cliente " + clienteListOrphanCheckCliente + " in its clienteList field has a non-nullable codPermi field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(permiso);
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

    public List<Permiso> findPermisoEntities() {
        return findPermisoEntities(true, -1, -1);
    }

    public List<Permiso> findPermisoEntities(int maxResults, int firstResult) {
        return findPermisoEntities(false, maxResults, firstResult);
    }

    private List<Permiso> findPermisoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Permiso.class));
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

    public Permiso findPermiso(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Permiso.class, id);
        } finally {
            em.close();
        }
    }

    public int getPermisoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Permiso> rt = cq.from(Permiso.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
