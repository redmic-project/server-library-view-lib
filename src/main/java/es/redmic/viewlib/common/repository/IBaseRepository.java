package es.redmic.viewlib.common.repository;

import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.model.BaseES;

/**
 * Interfaz de repositorio que sirve para cualquier implementación ya que no
 * tiene dependencia de tipo de datos.
 * 
 **/

public interface IBaseRepository<TModel extends BaseES<?>> {

	public EventApplicationResult save(TModel modelToIndex);

	public EventApplicationResult update(TModel modelToIndex);

	public EventApplicationResult delete(String id);
}
