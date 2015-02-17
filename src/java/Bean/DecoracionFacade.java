/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Bean;

import Entidad.Decoracion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cpe
 */
@Stateless
public class DecoracionFacade extends AbstractFacade<Decoracion> {
    @PersistenceContext(unitName = "EasyPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DecoracionFacade() {
        super(Decoracion.class);
    }
    
}
