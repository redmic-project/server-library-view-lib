package es.redmic.viewlib.common.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.FilterSchemaDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.viewlib.common.dto.MetaDTO;

public abstract interface IBaseService<TModel, TDTO, TQueryDTO> {

	public MetaDTO<?> findById(String id);

	public JSONCollectionDTO find(TQueryDTO query, Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery);

	public JSONCollectionDTO find(String[] fields, String text, Integer from, Integer size,
			Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery);

	public JSONCollectionDTO mget(MgetDTO dto);

	public List<String> suggest(TQueryDTO queryDTO, Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery);

	public List<String> suggest(String[] fields, String text, Integer size, Map<String, Object> fixedQuery,
			Set<String> fieldsExcludedOnQuery);

	public FilterSchemaDTO getFilterSchema(Set<String> ignorableFieldNames);
}
