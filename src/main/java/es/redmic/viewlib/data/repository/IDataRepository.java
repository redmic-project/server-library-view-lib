package es.redmic.viewlib.data.repository;

import java.util.List;

import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.viewlib.common.repository.IBaseRepository;

/**
 * Interfaz de repositorio específica para tipo "data" ya que no tiene
 * dependencia de tipo de datos.
 * 
 **/

public interface IDataRepository<TModel extends BaseES<?>, TQueryDTO extends SimpleQueryDTO>
		extends IBaseRepository<TModel> {

	// R

	public DataHitWrapper<?> findById(String id);

	public List<String> suggest(TQueryDTO queryDTO);

	public DataHitsWrapper<?> mget(MgetDTO dto);

	public DataSearchWrapper<?> find(TQueryDTO queryDTO);
}
