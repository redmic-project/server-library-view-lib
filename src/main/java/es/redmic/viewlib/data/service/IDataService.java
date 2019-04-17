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

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.viewlib.common.service.IBaseService;
import es.redmic.viewlib.data.dto.MetaDTO;

public abstract interface IDataService<TModel, TDTO, TQueryDTO> extends IBaseService<TModel, TDTO, TQueryDTO> {

	public MetaDTO<?> findById(String id);

	public JSONCollectionDTO find(TQueryDTO query, Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery);

	public JSONCollectionDTO find(String[] fields, String text, Integer from, Integer size,
			Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery);

	public JSONCollectionDTO mget(MgetDTO dto);

	public List<String> suggest(TQueryDTO queryDTO, Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery);

	public List<String> suggest(String[] fields, String text, Integer size, Map<String, Object> fixedQuery,
			Set<String> fieldsExcludedOnQuery);
}
