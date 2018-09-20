package es.redmic.viewlib.geodata.service;

import java.util.List;

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.geojson.base.Feature;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.wrapper.GeoSearchWrapper;
import es.redmic.viewlib.common.service.RBaseService;
import es.redmic.viewlib.geodata.dto.GeoMetaDTO;
import es.redmic.viewlib.geodata.repository.IGeoDataRepository;

public abstract class RGeoDataService<TModel extends Feature<?, ?>, TDTO extends FeatureDTO<?, ?>, TQueryDTO extends SimpleQueryDTO>
		extends RBaseService<TModel, TDTO, TQueryDTO> implements IGeoDataService<TModel, TDTO, TQueryDTO> {

	IGeoDataRepository<TModel, TQueryDTO> repository;

	public RGeoDataService(IGeoDataRepository<TModel, TQueryDTO> repository) {
		this.repository = repository;
	}

	@Override
	public GeoMetaDTO<?> findById(String id, String parentId) {

		// TODO: comprobar mediante microservicio de credenciales que este usuario puede
		// buscar

		return mapper.getMapperFacade().map(repository.findById(id), GeoMetaDTO.class, getMappingContext());
	}

	@Override
	public List<String> suggest(TQueryDTO queryDTO, String parentId) {

		// TODO: comprobar mediante microservicio de credenciales que este usuario puede
		// buscar

		return repository.suggest(queryDTO);
	}

	@Override
	public GeoJSONFeatureCollectionDTO mget(MgetDTO dto, String parentId) {

		// TODO: comprobar mediante microservicio de credenciales que este usuario puede
		// buscar

		return mapper.getMapperFacade().map(repository.mget(dto), GeoJSONFeatureCollectionDTO.class,
				getMappingContext());
	}

	@Override
	public GeoJSONFeatureCollectionDTO find(TQueryDTO query, String parentId) {

		// TODO: comprobar mediante microservicio de credenciales que este usuario puede
		// buscar

		GeoSearchWrapper<?> result = repository.find(query);

		GeoJSONFeatureCollectionDTO collection = mapper.getMapperFacade().map(result.getHits(),
				GeoJSONFeatureCollectionDTO.class, getMappingContext());

		if (result.getAggregations() != null) {
			collection.set_aggs(
					mapper.getMapperFacade().map(result.getAggregations(), AggregationsDTO.class, getMappingContext()));
		}

		return collection;
	}
}
