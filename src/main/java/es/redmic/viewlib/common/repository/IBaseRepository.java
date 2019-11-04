package es.redmic.viewlib.common.repository;

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

import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.model.BaseES;

/**
 * Interfaz de repositorio que sirve para cualquier implementación ya que no
 * tiene dependencia de tipo de datos.
 * 
 **/

public interface IBaseRepository<TModel extends BaseES<?>> {

	// W

	public EventApplicationResult save(TModel modelToIndex);

	public EventApplicationResult update(TModel modelToIndex);

	public EventApplicationResult delete(String id);

	public EventApplicationResult rollback(TModel modelToIndex, String id);
}
