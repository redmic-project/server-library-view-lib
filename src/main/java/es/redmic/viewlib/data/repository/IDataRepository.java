package es.redmic.viewlib.data.repository;

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
