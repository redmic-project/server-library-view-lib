package es.redmic.viewlib.usersettings.controller;

import org.mapstruct.factory.Mappers;
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
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.usersettingslib.dto.SettingsDTO;
import es.redmic.usersettingslib.events.SettingsEventFactory;
import es.redmic.usersettingslib.events.SettingsEventTypes;
import es.redmic.usersettingslib.events.clearselection.ClearSelectionEvent;
import es.redmic.usersettingslib.events.delete.DeleteSettingsEvent;
import es.redmic.usersettingslib.events.deselect.DeselectEvent;
import es.redmic.usersettingslib.events.save.SaveSettingsEvent;
import es.redmic.usersettingslib.events.select.SelectEvent;
import es.redmic.usersettingslib.model.Settings;
import es.redmic.viewlib.data.controller.DataController;
import es.redmic.viewlib.usersettings.mapper.SettingsESMapper;
import es.redmic.viewlib.usersettings.service.SelectionService;

@Controller
@ConditionalOnProperty(name = "redmic.user-settings.enabled", havingValue = "true")
@RequestMapping(value = "${controller.mapping.SETTINGS}")
@KafkaListener(topics = "${broker.topic.settings}")
public class SelectionController extends DataController<Settings, SettingsDTO, SimpleQueryDTO> {

	@Value("${broker.topic.settings}")
	private String settings_topic;

	SelectionService service;

	public SelectionController(SelectionService service) {
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

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
