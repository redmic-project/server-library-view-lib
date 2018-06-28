package es.redmic.viewlib.data.service;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.data.repository.IDataRepository;

public abstract class RWDataService<TModel extends BaseES<?>, TDTO extends CommonDTO, TQueryDTO extends SimpleQueryDTO>
		extends RDataService<TModel, TDTO, TQueryDTO> {

	IDataRepository<TModel, TQueryDTO> repository;

	public RWDataService(IDataRepository<TModel, TQueryDTO> repository) {
		super(repository);
		this.repository = repository;
	}

	public EventApplicationResult save(TModel model) {
		return repository.save(model);
	}

	public EventApplicationResult update(TModel model) {
		return repository.update(model);
	}

	public EventApplicationResult delete(String id) {
		return repository.delete(id);
	}
}
