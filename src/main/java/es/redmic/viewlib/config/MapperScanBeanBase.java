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

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.Filter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * A bean mapper designed for Spring suitable for dependency injection.
 * 
 * Provides an implementation of {@link MapperFacade} which can be injected. In
 * addition it is "Spring aware" in that it can autodiscover any implementations
 * of {@link Mapper} or {@link Converter} that are managed beans within it's
 * parent {@link ApplicationContext}.
 * 
 * @author Ken Blair
 */

public abstract class MapperScanBeanBase extends ConfigurableMapper implements ApplicationContextAware {

	public MapperScanBeanBase() {
		super(false);
	}

	protected MapperFactory factory;

	private ApplicationContext applicationContext;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureFactoryBuilder(final DefaultMapperFactory.Builder factoryBuilder) {
		// customize the factoryBuilder as needed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final MapperFactory factory) {

		this.factory = factory;
		addAllSpringBeans();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		this.init();
	}

	/**
	 * Adds all managed beans of type {@link Mapper} or {@link Converter} to the
	 * parent {@link MapperFactory}.
	 * 
	 * @param applicationContext
	 *            The application context to look for managed beans in.
	 */

	public void addAllSpringBeans() {
		@SuppressWarnings("rawtypes")
		final Map<String, Converter> converters = applicationContext.getBeansOfType(Converter.class);
		for (@SuppressWarnings("rawtypes")
		final Converter converter : converters.values()) {
			addConverter(converter);
		}

		@SuppressWarnings("rawtypes")
		final Map<String, CustomMapper> mappers = applicationContext.getBeansOfType(CustomMapper.class);
		for (@SuppressWarnings("rawtypes")
		final CustomMapper mapper : mappers.values()) {
			addMapper(mapper);
		}

		@SuppressWarnings("rawtypes")
		final Map<String, Filter> filters = applicationContext.getBeansOfType(Filter.class);
		for (@SuppressWarnings("rawtypes")
		final Filter filter : filters.values()) {
			addFilter(filter);
		}

		addObjectFactory();
		addDefaultActions();
	}

	/**
	 * Add a {@link Converter}.
	 * 
	 * @param converter
	 *            The converter.
	 */
	public void addConverter(final Converter<?, ?> converter) {
		factory.getConverterFactory().registerConverter(converter);
	}

	/**
	 * Add a {@link Mapper}.
	 * 
	 * @param mapper
	 *            The mapper.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addMapper(final CustomMapper<?, ?> mapper) {
		factory.classMap(mapper.getAType(), mapper.getBType()).byDefault().customize((CustomMapper) mapper).register();
	}

	public void addFilter(final Filter<?, ?> filter) {
		factory.registerFilter(filter);
	}

	protected abstract void addObjectFactory();

	protected abstract void addDefaultActions();

	public MapperFacade getMapperFacade() {
		return factory.getMapperFacade();
	}
}
