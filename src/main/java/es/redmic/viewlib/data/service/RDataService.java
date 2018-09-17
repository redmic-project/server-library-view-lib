package es.redmic.viewlib.data.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.viewlib.common.dto.MetaDTO;
import es.redmic.viewlib.common.service.RBaseService;
import es.redmic.viewlib.config.MapperScanBeanItfc;
import es.redmic.viewlib.data.repository.IDataRepository;

public abstract class RDataService<TModel extends BaseES<?>, TDTO extends CommonDTO, TQueryDTO extends SimpleQueryDTO>
		extends RBaseService<TModel, TDTO, TQueryDTO> implements IDataService<TModel, TDTO, TQueryDTO> {

	IDataRepository<TModel, TQueryDTO> repository;

	@Autowired
	protected MapperScanBeanItfc mapper;

	public RDataService(IDataRepository<TModel, TQueryDTO> repository) {
		this.repository = repository;
	}

	@Override
	public MetaDTO<?> findById(String id) {

		return mapper.getMapperFacade().map(repository.findById(id), MetaDTO.class, getMappingContext());
	}

	@Override
	public JSONCollectionDTO find(String[] fields, String text, Integer from, Integer size,
			Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery) {

		TQueryDTO queryDTO;
		try {
			queryDTO = typeOfTQueryDTO.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}

		queryDTO.createSimpleQueryDTOFromTextQueryParams(fields, text, from, size);

		return find(queryDTO, fixedQuery, fieldsExcludedOnQuery);
	}

	@Override
	public JSONCollectionDTO find(TQueryDTO query, Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery) {

		processQuery(query, fixedQuery, fieldsExcludedOnQuery);

		DataSearchWrapper<?> result = repository.find(query);

		JSONCollectionDTO collection = mapper.getMapperFacade().map(result.getHits(), JSONCollectionDTO.class);
		collection.set_aggs(mapper.getMapperFacade().map(result.getAggregations(), AggregationsDTO.class));
		return collection;
	}

	@Override
	public JSONCollectionDTO mget(MgetDTO dto) {

		return mapper.getMapperFacade().map(repository.mget(dto), JSONCollectionDTO.class);
	}

	@Override
	public List<String> suggest(String[] fields, String text, Integer size, Map<String, Object> fixedQuery,
			Set<String> fieldsExcludedOnQuery) {

		TQueryDTO queryDTO;
		try {
			queryDTO = typeOfTQueryDTO.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}

		queryDTO.createSimpleQueryDTOFromSuggestQueryParams(fields, text, size);

		return suggest(queryDTO, fixedQuery, fieldsExcludedOnQuery);
	}

	@Override
	public List<String> suggest(TQueryDTO queryDTO, Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery) {

		processQuery(queryDTO, fixedQuery, fieldsExcludedOnQuery);
		return repository.suggest(queryDTO);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		return new String[] { "name", "name.suggest" };
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		return new String[] { "name", "name.suggest" };
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		return new String[] { "name" };
	}
}
