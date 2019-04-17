package es.redmic.viewlib.common.controller;

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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.models.es.common.dto.ElasticSearchDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.viewlib.common.service.IBaseService;

public abstract class RController<TModel extends BaseES<?>, TDTO extends CommonDTO, TQueryDTO extends SimpleQueryDTO> {

	private static Logger logger = LogManager.getLogger();

	@Autowired
	protected KafkaTemplate<String, Event> kafkaTemplate;

	protected Class<TDTO> typeOfTDTO;

	protected Class<TQueryDTO> typeOfTQueryDTO;

	protected Set<String> fieldsExcludedOnQuery = new HashSet<String>();

	protected Map<String, Object> fixedQuery = new HashMap<String, Object>();

	IBaseService<TModel, TDTO, TQueryDTO> service;

	public RController(IBaseService<TModel, TDTO, TQueryDTO> service) {
		this.service = service;
		defineTypeOfArguments();
	}

	@RequestMapping(value = { "${controller.mapping.FILTER_SCHEMA}" }, method = RequestMethod.GET)
	@ResponseBody
	public ElasticSearchDTO getFilterSchema() {

		return new ElasticSearchDTO(service.getFilterSchema(fieldsExcludedOnQuery), 1);
	}

	@SuppressWarnings("unchecked")
	private void defineTypeOfArguments() {

		int numberOfArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments().length;
		Type[] arguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

		switch (numberOfArguments) {
		case 4:
			this.typeOfTDTO = (Class<TDTO>) (arguments[2]);
			this.typeOfTQueryDTO = (Class<TQueryDTO>) (arguments[3]);
			break;
		case 3:
			this.typeOfTDTO = (Class<TDTO>) (arguments[1]);
			this.typeOfTQueryDTO = (Class<TQueryDTO>) (arguments[2]);
			break;
		}
	}

	protected void setFieldsExcludedOnQuery(Set<String> fieldsExcludedOnQuery) {
		this.fieldsExcludedOnQuery = fieldsExcludedOnQuery;
	}

	protected void setFixedQuery(Map<String, Object> fixedQuery) {
		this.fixedQuery.putAll(fixedQuery);
	}

	protected void setFixedQuery(String term, Object value) {
		fixedQuery.put(term, value);
	}

	protected void publishFailedEvent(Event event, String topic) {

		logger.error("sending FailedEvent='{}' to topic='{}'", event, topic);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(topic, event.getAggregateId(), event);

		future.addCallback(new SendListener());
	}

	protected void publishConfirmedEvent(Event event, String topic) {

		logger.debug("sending ConfirmEvent='{}' to topic='{}'", event, topic);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(topic, event.getAggregateId(), event);

		future.addCallback(new SendListener());
	}
}
