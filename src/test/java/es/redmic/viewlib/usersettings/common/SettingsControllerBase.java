package es.redmic.viewlib.usersettings.common;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;
import es.redmic.usersettingslib.dto.SettingsDTO;
import es.redmic.usersettingslib.unit.utils.SettingsDataUtil;
import es.redmic.viewlib.usersettings.mapper.SettingsESMapper;
import es.redmic.viewlib.usersettings.repository.SettingsRepository;

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

public class SettingsControllerBase extends DocumentationViewBaseTest {

	@Value("${documentation.MICROSERVICE_HOST}")
	private String HOST;

	@Value("${controller.mapping.SETTINGS}")
	private String SETTINGS_PATH;

	@Autowired
	SettingsRepository repository;

	SettingsDTO settings = new SettingsDTO();

	@Override
	@Before
	public void setUp() {

		settings = SettingsDataUtil.getSettingsDTO();

		repository.save(Mappers.getMapper(SettingsESMapper.class).map(settings));

		// @formatter:off
		
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme(SCHEME).withHost(HOST).withPort(PORT))
				.alwaysDo(this.document).build();

		// @formatter:on
	}

	@After
	public void clean() {
		repository.delete(settings.getId());
	}

	@Test
	public void getCategory_Return200_WhenItemExist() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(SETTINGS_PATH + "/" + settings.getId())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.body", notNullValue()))
			.andExpect(jsonPath("$.body.id", is(settings.getId())));
		
		// @formatter:on
	}

	@Test
	public void searchSettingsPost_Return200_WhenSearchIsCorrect() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);

		// @formatter:off
		
		this.mockMvc
				.perform(post(SETTINGS_PATH + "/_search")
					.header("Authorization", "Bearer " + getTokenOAGUser())	
					.content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getSimpleQueryFieldsDescriptor());
		
		// @formatter:on
	}

	@Test
	public void searchSettingsPost_ReturnUnauthorized_IfUserIsNotLoggedIn() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);

		// @formatter:off
		
		this.mockMvc
				.perform(post(SETTINGS_PATH + "/_search")
					.content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
		
		// @formatter:on
	}

	@Test
	public void searchSettingsQueryString_Return200_WhenSearchIsCorrect() throws Exception {

		// @formatter:off
		
		this.mockMvc
			.perform(get(SETTINGS_PATH)
					.param("fields", "{name}")
					.param("text", settings.getName())
					.param("from", "0")
					.param("size", "1")
					.header("Authorization", "Bearer " + getTokenOAGUser())
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getSearchSimpleParametersDescription());
		
		// @formatter:off
	}
	
	@Test
	public void searchSettingsQueryString_ReturnUnauthorized_IfUserIsNotLoggedIn() throws Exception {

		// @formatter:off
		
		this.mockMvc
		.perform(get(SETTINGS_PATH)
				.param("fields", "{name}")
				.param("text", settings.getName())
				.param("from", "0")
				.param("size", "1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
		
		// @formatter:on
	}

	@Test
	public void mgetSettings_Return200_WhenSettingsExists() throws Exception {

		MgetDTO mgetQuery = new MgetDTO();
		mgetQuery.setIds(Arrays.asList(settings.getId()));
		mgetQuery.setFields(Arrays.asList("id"));

		// @formatter:off
		
		this.mockMvc
			.perform(post(SETTINGS_PATH + "/_mget")
					.header("Authorization", "Bearer " + getTokenOAGUser())
					.content(mapper.writeValueAsString(mgetQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body.data", notNullValue()))
				.andExpect(jsonPath("$.body.data[0]", notNullValue()))
				.andExpect(jsonPath("$.body.data[0].id", is(settings.getId())))
				.andExpect(jsonPath("$.body.data.length()", is(1)))
					.andDo(getMgetRequestDescription());
		
		// @formatter:on
	}

	@Test
	public void mgetSettings_ReturnUnauthorized_IfUserIsNotLoggedIn() throws Exception {

		MgetDTO mgetQuery = new MgetDTO();
		mgetQuery.setIds(Arrays.asList(settings.getId()));
		mgetQuery.setFields(Arrays.asList("id"));

		// @formatter:off
		
		this.mockMvc
			.perform(post(SETTINGS_PATH + "/_mget")
					.content(mapper.writeValueAsString(mgetQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
		
		// @formatter:on
	}

	@Test
	public void suggestSettingsQueryString_Return200_WhenSuggestIsCorrect() throws Exception {

		// @formatter:off
		
		this.mockMvc
			.perform(get(SETTINGS_PATH + "/_suggest")
					.param("fields", new String[] { "name" })
					.param("text", settings.getName())
					.param("size", "1")
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.length()", is(1)))
				.andExpect(jsonPath("$.body[0]", startsWith("<b>")))
				.andExpect(jsonPath("$.body[0]", endsWith("</b>")))
					.andDo(getSuggestParametersDescription());
		
		// @formatter:on
	}

	@Test
	public void suggestSettingsQueryString_ReturnUnauthorized_IfUserIsNotLoggedIn() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);

		// @formatter:off
		
		this.mockMvc
			.perform(get(SETTINGS_PATH + "/_suggest")
				.param("fields", new String[] { "name" })
				.param("text", settings.getName())
				.param("size", "1")
					.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
		
		// @formatter:on
	}

	@Test
	public void suggestSettingsPost_Return200_WhenSuggestIsCorrect() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);
		dataQuery.createSimpleQueryDTOFromSuggestQueryParams(new String[] { "name" }, settings.getName(), 1);

		// @formatter:off
		
		this.mockMvc
			.perform(post(SETTINGS_PATH + "/_suggest")
					.header("Authorization", "Bearer " + getTokenOAGUser())
					.content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.length()", is(1)))
				.andExpect(jsonPath("$.body[0]", startsWith("<b>")))
				.andExpect(jsonPath("$.body[0]", endsWith("</b>")))
					.andDo(getSimpleQueryFieldsDescriptor());;
				
		// @formatter:on
	}

	@Test
	public void suggestSettingsPost_ReturnUnauthorized_IfUserIsNotLoggedIn() throws Exception {

		SimpleQueryDTO dataQuery = new SimpleQueryDTO();
		dataQuery.setSize(1);
		dataQuery.createSimpleQueryDTOFromSuggestQueryParams(new String[] { "name" }, settings.getName(), 1);

		// @formatter:off
		
		this.mockMvc
			.perform(post(SETTINGS_PATH + "/_suggest")
					.content(mapper.writeValueAsString(dataQuery))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
		
		// @formatter:on
	}
}
