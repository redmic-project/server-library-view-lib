package es.redmic.viewlib.data.service;

/*-
 * #%L
 * view-lib
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.Map;
import java.util.Set;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.viewlib.common.service.RBaseService;
import es.redmic.viewlib.data.dto.MetaDTO;
import es.redmic.viewlib.data.repository.IDataRepository;

public abstract class RDataService<TModel extends BaseES<?>, TDTO extends CommonDTO, TQueryDTO extends SimpleQueryDTO>
		extends RBaseService<TModel, TDTO, TQueryDTO> implements IDataService<TModel, TDTO, TQueryDTO> {

	IDataRepository<TModel, TQueryDTO> repository;

	public RDataService(IDataRepository<TModel, TQueryDTO> repository) {
		this.repository = repository;
	}

	@Override
	public MetaDTO<?> findById(String id) {

		return viewResultToDTO(repository.findById(id));
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

		return viewResultToDTO(repository.find(query));
	}

	@Override
	public JSONCollectionDTO mget(MgetDTO dto) {

		return viewResultToDTO(repository.mget(dto));
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

	protected abstract MetaDTO<?> viewResultToDTO(DataHitWrapper<?> viewResult);

	protected abstract JSONCollectionDTO viewResultToDTO(DataSearchWrapper<?> viewResult);

	protected abstract JSONCollectionDTO viewResultToDTO(DataHitsWrapper<?> viewResult);

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
