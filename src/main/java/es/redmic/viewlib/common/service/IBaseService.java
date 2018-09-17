package es.redmic.viewlib.common.service;

import java.util.Set;

import es.redmic.models.es.common.query.dto.FilterSchemaDTO;

public abstract interface IBaseService<TModel, TDTO, TQueryDTO> {

	public FilterSchemaDTO getFilterSchema(Set<String> ignorableFieldNames);
}
