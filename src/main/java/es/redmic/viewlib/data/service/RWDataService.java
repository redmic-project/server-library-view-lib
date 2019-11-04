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

	public EventApplicationResult rollback(TModel model, String id) {
		return repository.rollback(model, id);
	}
}
