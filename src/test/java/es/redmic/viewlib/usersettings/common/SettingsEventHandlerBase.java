package es.redmic.viewlib.usersettings.common;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.testutils.oauth.IntegrationTestBase;
import es.redmic.usersettingslib.events.SettingsEventTypes;
import es.redmic.usersettingslib.events.clearselection.ClearSelectionConfirmedEvent;
import es.redmic.usersettingslib.events.clearselection.ClearSelectionEvent;
import es.redmic.usersettingslib.events.clearselection.ClearSelectionFailedEvent;
import es.redmic.usersettingslib.events.delete.DeleteSettingsConfirmedEvent;
import es.redmic.usersettingslib.events.delete.DeleteSettingsEvent;
import es.redmic.usersettingslib.events.delete.DeleteSettingsFailedEvent;
import es.redmic.usersettingslib.events.deselect.DeselectConfirmedEvent;
import es.redmic.usersettingslib.events.deselect.DeselectEvent;
import es.redmic.usersettingslib.events.deselect.DeselectFailedEvent;
import es.redmic.usersettingslib.events.save.SaveSettingsConfirmedEvent;
import es.redmic.usersettingslib.events.save.SaveSettingsEvent;
import es.redmic.usersettingslib.events.save.SaveSettingsFailedEvent;
import es.redmic.usersettingslib.events.select.SelectConfirmedEvent;
import es.redmic.usersettingslib.events.select.SelectEvent;
import es.redmic.usersettingslib.events.select.SelectFailedEvent;
import es.redmic.usersettingslib.model.Settings;
import es.redmic.usersettingslib.unit.utils.SettingsDataUtil;
import es.redmic.viewlib.usersettings.mapper.SettingsESMapper;
import es.redmic.viewlib.usersettings.repository.SettingsRepository;

public abstract class SettingsEventHandlerBase extends IntegrationTestBase {

	@Autowired
	SettingsRepository repository;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.settings}")
	private String SETTINGS_TOPIC;

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void sendSelectEvent_AddItemToSelection_IfEventIsOk() throws Exception {

		SelectEvent event = SettingsDataUtil.getSelectEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getSettings().getId());

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.SELECT_CONFIRMED, confirm.getType());

		Settings settings = (Settings) item.get_source();
		assertEquals(event.getAggregateId(), settings.getId());
		assertEquals(event.getSettings().getName(), settings.getName());
		assertEquals(event.getSettings().getSelection(), settings.getSelection());
		assertEquals(1, settings.getSelection().size());
		assertEquals(event.getSettings().getService(), settings.getService());
		assertEquals(event.getSettings().getShared(), settings.getShared());
	}

	@Test
	public void sendDeselectEvent_RemoveItemToSelection_IfEventIsOk() throws Exception {

		DeselectEvent event = SettingsDataUtil.getDeselectEvent();

		repository.save(Mappers.getMapper(SettingsESMapper.class).map(event.getSettings()));

		event.getSettings().getSelection().clear();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getSettings().getId());

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.DESELECT_CONFIRMED, confirm.getType());

		Settings settings = (Settings) item.get_source();
		assertEquals(event.getAggregateId(), settings.getId());
		assertEquals(event.getSettings().getName(), settings.getName());
		assertEquals(event.getSettings().getSelection(), settings.getSelection());
		assertEquals(0, settings.getSelection().size());
		assertEquals(event.getSettings().getService(), settings.getService());
		assertEquals(event.getSettings().getShared(), settings.getShared());
	}

	@Test
	public void sendClearSelectionEvent_ClearSelection_IfEventIsOk() throws Exception {

		ClearSelectionEvent event = SettingsDataUtil.getClearEvent();

		repository.save(Mappers.getMapper(SettingsESMapper.class).map(SettingsDataUtil.getSelectEvent().getSettings()));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getSettings().getId());

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.CLEAR_SELECTION_CONFIRMED, confirm.getType());

		Settings settings = (Settings) item.get_source();
		assertEquals(event.getAggregateId(), settings.getId());
		assertEquals(event.getSettings().getName(), settings.getName());
		assertEquals(event.getSettings().getSelection(), settings.getSelection());
		assertEquals(0, settings.getSelection().size());
		assertEquals(event.getSettings().getService(), settings.getService());
		assertEquals(event.getSettings().getShared(), settings.getShared());
	}

	@Test
	public void sendSaveSettingsEvent_SaveSettings_IfEventIsOk() throws Exception {

		SaveSettingsEvent event = SettingsDataUtil.getSaveEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getSettings().getId());

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.SAVE_CONFIRMED, confirm.getType());

		Settings settings = (Settings) item.get_source();
		assertEquals(event.getAggregateId(), settings.getId());
		assertEquals(event.getSettings().getName(), settings.getName());
		assertNotNull(event.getSettings().getName());
		assertEquals(event.getSettings().getSelection(), settings.getSelection());
		assertEquals(1, settings.getSelection().size());
		assertEquals(event.getSettings().getService(), settings.getService());
		assertEquals(event.getSettings().getShared(), settings.getShared());
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendDeleteSettingsEvent_DeleteSettings_IfEventIsOk() throws Exception {

		DeleteSettingsEvent event = SettingsDataUtil.getDeleteEvent();

		repository.save(Mappers.getMapper(SettingsESMapper.class).map(SettingsDataUtil.getSavedEvent().getSettings()));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.DELETE_CONFIRMED, confirm.getType());

		repository.findById(event.getAggregateId());
	}

	@Test
	public void sendSelectEvent_PublishSelectFailedEvent_IfItemNotFound() throws Exception {

		SelectEvent event = SettingsDataUtil.getSelectEvent();
		event.getSettings().setUpdated(DateTime.now().plus(20));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.SELECT_FAILED, confirm.getType());
	}

	@Test
	public void sendDeselectEvent_PublishDeselectFailedEvent_IfItemNotFound() throws Exception {

		DeselectEvent event = SettingsDataUtil.getDeselectEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.DESELECT_FAILED, confirm.getType());
	}

	@Test
	public void sendClearEvent_PublishClearSelectionFailedEvent_IfItemNotFound() throws Exception {

		ClearSelectionEvent event = SettingsDataUtil.getClearEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.CLEAR_SELECTION_FAILED, confirm.getType());
	}

	@Test
	public void sendSaveSettingsEvent_PublishSaveSettingsFailedEvent_IfItemNotFound() throws Exception {

		SaveSettingsEvent event = SettingsDataUtil.getSaveEvent();
		event.getSettings().setUpdated(DateTime.now().plus(20));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(SETTINGS_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(SettingsEventTypes.SAVE_FAILED, confirm.getType());
	}

	@KafkaHandler
	public void selectConfirmed(SelectConfirmedEvent selectConfirmedEvent) {

		blockingQueue.offer(selectConfirmedEvent);
	}

	@KafkaHandler
	public void selectFailed(SelectFailedEvent selectFailedEvent) {

		blockingQueue.offer(selectFailedEvent);
	}

	@KafkaHandler
	public void deselectConfirmed(DeselectConfirmedEvent deselectConfirmedEvent) {

		blockingQueue.offer(deselectConfirmedEvent);
	}

	@KafkaHandler
	public void deselectFailed(DeselectFailedEvent deselectFailedEvent) {

		blockingQueue.offer(deselectFailedEvent);
	}

	@KafkaHandler
	public void clearSelectionConfirmed(ClearSelectionConfirmedEvent clearSelectionConfirmedEvent) {

		blockingQueue.offer(clearSelectionConfirmedEvent);
	}

	@KafkaHandler
	public void clearSelectionFailed(ClearSelectionFailedEvent clearSelectionFailedEvent) {

		blockingQueue.offer(clearSelectionFailedEvent);
	}

	@KafkaHandler
	public void saveSettingsConfirmed(SaveSettingsConfirmedEvent saveSettingsConfirmedEvent) {

		blockingQueue.offer(saveSettingsConfirmedEvent);
	}

	@KafkaHandler
	public void saveSettingsFailed(SaveSettingsFailedEvent saveSettingsFailedEvent) {

		blockingQueue.offer(saveSettingsFailedEvent);
	}

	@KafkaHandler
	public void deleteSettingsConfirmed(DeleteSettingsConfirmedEvent deleteSettingsConfirmedEvent) {

		blockingQueue.offer(deleteSettingsConfirmedEvent);
	}

	@KafkaHandler
	public void deleteSettingsFailed(DeleteSettingsFailedEvent deleteSettingsFailedEvent) {

		blockingQueue.offer(deleteSettingsFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
