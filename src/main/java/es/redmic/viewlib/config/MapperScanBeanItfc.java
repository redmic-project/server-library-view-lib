package es.redmic.viewlib.config;

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

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
public interface MapperScanBeanItfc {
	
	public void configureFactoryBuilder(final DefaultMapperFactory.Builder factoryBuilder);

	public void configure(final MapperFactory factory);

	public void setApplicationContext(final ApplicationContext applicationContext);

	public void addAllSpringBeans();

	public void addConverter(final Converter<?, ?> converter);

	public void addMapper(final CustomMapper<?, ?> mapper);
	
	public MapperFacade getMapperFacade();
}
