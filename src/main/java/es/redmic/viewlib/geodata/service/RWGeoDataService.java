package es.redmic.viewlib.geodata.service;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.viewlib.geodata.repository.IGeoDataRepository;

public abstract class RWGeoDataService<TModel extends Feature<?, ?>, TDTO extends CommonDTO, TQueryDTO extends SimpleQueryDTO>
		extends RGeoDataService<TModel, TDTO, TQueryDTO> {

	IGeoDataRepository<TModel, TQueryDTO> repository;

	public RWGeoDataService(IGeoDataRepository<TModel, TQueryDTO> repository) {
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