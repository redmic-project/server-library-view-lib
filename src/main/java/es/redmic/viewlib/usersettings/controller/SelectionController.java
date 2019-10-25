package es.redmic.viewlib.usersettings.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.redmic.brokerlib.avro.fail.RollbackFailedEvent;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.exception.databinding.DTONotValidException;
import es.redmic.models.es.common.dto.ElasticSearchDTO;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.dto.SuperDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.restlib.config.UserService;
import es.redmic.usersettingslib.dto.SettingsDTO;
import es.redmic.usersettingslib.events.SettingsEventFactory;
import es.redmic.usersettingslib.events.SettingsEventTypes;
import es.redmic.usersettingslib.events.clearselection.ClearSelectionEvent;
import es.redmic.usersettingslib.events.delete.DeleteSettingsEvent;
import es.redmic.usersettingslib.events.deselect.DeselectEvent;
import es.redmic.usersettingslib.events.fail.SettingsRollbackEvent;
import es.redmic.usersettingslib.events.save.SaveSettingsEvent;
import es.redmic.usersettingslib.events.select.SelectEvent;
import es.redmic.usersettingslib.model.Settings;
import es.redmic.viewlib.common.controller.RController;
import es.redmic.viewlib.usersettings.mapper.SettingsESMapper;
import es.redmic.viewlib.usersettings.service.SettingsService;

@Controller
@ConditionalOnProperty(name = "redmic.user-settings.enabled", havingValue = "true")
@RequestMapping(value = "${controller.mapping.SETTINGS}")
@KafkaListener(topics = "${broker.topic.settings}")
public class SelectionController extends RController<Settings, SettingsDTO, SimpleQueryDTO> {

	@Value("${broker.topic.settings}")
	private String settings_topic;

	@Autowired
	UserService userService;

	SettingsService service;

	public SelectionController(SettingsService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(SelectEvent event) {

		EventApplicationResult result = null;

		try {

			if (event.getSettings().getInserted().equals(event.getSettings().getUpdated()))
				result = service.save(Mappers.getMapper(SettingsESMapper.class).map(event.getSettings()));
			else
				result = service.update(Mappers.getMapper(SettingsESMapper.class).map(event.getSettings()));
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.SELECT_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), settings_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.SELECT_CONFIRMED),
					settings_topic);
		} else {
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.SELECT_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), settings_topic);
		}
	}

	@KafkaHandler
	public void listen(DeselectEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.update(Mappers.getMapper(SettingsESMapper.class).map(event.getSettings()));
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.DESELECT_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), settings_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.DESELECT_CONFIRMED),
					settings_topic);
		} else {
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.DESELECT_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), settings_topic);
		}
	}

	@KafkaHandler
	public void listen(ClearSelectionEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.update(Mappers.getMapper(SettingsESMapper.class).map(event.getSettings()));
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.CLEAR_SELECTION_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), settings_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.CLEAR_SELECTION_CONFIRMED),
					settings_topic);
		} else {
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.CLEAR_SELECTION_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), settings_topic);
		}
	}

	@KafkaHandler
	public void listen(SaveSettingsEvent event) {

		EventApplicationResult result = null;

		try {
			if (event.getSettings().getInserted().equals(event.getSettings().getUpdated()))
				result = service.save(Mappers.getMapper(SettingsESMapper.class).map(event.getSettings()));
			else
				result = service.update(Mappers.getMapper(SettingsESMapper.class).map(event.getSettings()));
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.SAVE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), settings_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.SAVE_CONFIRMED),
					settings_topic);
		} else {
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.SAVE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), settings_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteSettingsEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), settings_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.DELETE_CONFIRMED),
					settings_topic);
		} else {
			publishFailedEvent(SettingsEventFactory.getEvent(event, SettingsEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), settings_topic);
		}
	}

	@KafkaHandler
	public void listen(SettingsRollbackEvent event) {

		EventApplicationResult result = null;

		try {

			result = service.rollback(Mappers.getMapper(SettingsESMapper.class).map(event.getLastSnapshotItem()),
					event.getAggregateId());
		} catch (Exception e) {
			e.printStackTrace();
			publishFailedEvent(new RollbackFailedEvent(event.getFailEventType()).buildFrom(event), settings_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(
					SettingsEventFactory.getEvent(event,
							SettingsEventTypes.getEventFailedTypeByActionType(event.getFailEventType())),
					settings_topic);
		} else {
			publishFailedEvent(new RollbackFailedEvent(event.getFailEventType()).buildFrom(event), settings_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}

	// REST

	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public SuperDTO _search(@RequestParam(required = false, value = "fields") String[] fields,
			@RequestParam(required = false, value = "text") String text,
			@RequestParam(required = false, value = "from") Integer from,
			@RequestParam(required = false, value = "size") Integer size) {

		Map<String, Object> newFixedQuery = new HashMap<String, Object>();
		newFixedQuery.put("userId", userService.getUserId());
		newFixedQuery.put("name", null);
		return new ElasticSearchDTO(service.find(fields, text, from, size, newFixedQuery, fieldsExcludedOnQuery));
	}

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	@ResponseBody
	public SuperDTO _advancedSearch(@Valid @RequestBody SimpleQueryDTO queryDTO, BindingResult bindingResult) {

		if (bindingResult != null && bindingResult.hasErrors())
			throw new DTONotValidException(bindingResult);

		Map<String, Object> newFixedQuery = new HashMap<String, Object>();
		newFixedQuery.put("userId", userService.getUserId());
		newFixedQuery.put("name", null);
		return new ElasticSearchDTO(service.find(queryDTO, newFixedQuery, fieldsExcludedOnQuery));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public SuperDTO _get(@PathVariable("id") String id) {

		Map<String, Object> newFixedQuery = new HashMap<String, Object>();
		newFixedQuery.put("userId", userService.getUserId());
		newFixedQuery.put("id", id);

		JSONCollectionDTO result = service.find(new SimpleQueryDTO(), newFixedQuery, fieldsExcludedOnQuery);

		if (result.getTotal() == 1)
			return new ElasticSearchDTO(result.getData().get(0), 1);
		throw new ItemNotFoundException(id, id);
	}

	@RequestMapping(value = "/_suggest", method = RequestMethod.GET)
	@ResponseBody
	public SuperDTO _suggest(@RequestParam(required = false, value = "fields") String[] fields,
			@RequestParam("text") String text, @RequestParam(required = false, value = "size") Integer size) {

		Map<String, Object> newFixedQuery = new HashMap<String, Object>();
		newFixedQuery.put("userId", userService.getUserId());
		newFixedQuery.put("name", null);
		return new ElasticSearchDTO(service.suggest(fields, text, size, newFixedQuery, fieldsExcludedOnQuery));
	}

	@RequestMapping(value = "/_suggest", method = RequestMethod.POST)
	@ResponseBody
	public SuperDTO _advancedSuggest(@Valid @RequestBody SimpleQueryDTO queryDTO, BindingResult bindingResult) {

		Map<String, Object> newFixedQuery = new HashMap<String, Object>();
		newFixedQuery.put("userId", userService.getUserId());
		newFixedQuery.put("name", null);
		return new ElasticSearchDTO(service.suggest(queryDTO, newFixedQuery, fieldsExcludedOnQuery));
	}
}
