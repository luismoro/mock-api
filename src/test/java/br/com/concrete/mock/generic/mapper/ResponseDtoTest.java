package br.com.concrete.mock.generic.mapper;

import br.com.concrete.mock.generic.model.Response;
import br.com.concrete.mock.generic.model.template.ResponseTemplate;
import br.com.concrete.mock.infra.component.impl.FromJsonStringToObjectConverterImpl;
import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResponseDtoTest {

    @BeforeClass
    public static void initClass() {
        FixtureFactoryLoader.loadTemplates("br.com.concrete.mock.generic.model.template");
    }

    @Test
    public void shouldConvertFromJsonAnObject() {
        // given
        final String json = "{ \"body\": { \"tt\": \"789\" } }";

        // when
        final ResponseDto responseDto = new Gson().fromJson(json, ResponseDto.class);
        final Response response = responseDto.toModel();

        // then
        assertNotNull(response);
        assertNotNull(response.getBody());
        JSONAssert.assertEquals("{ \"tt\": \"789\" }", response.getBody(), false);
    }

    @Test
    public void shouldConvertFromJsonAListOfObjects() {
        // given
        final String json = "{ \"body\": [{ \"age\": 10 }, { \"age\": 11 }] }";

        // when
        final ResponseDto responseDto = new Gson().fromJson(json, ResponseDto.class);
        final Response response = responseDto.toModel();

        // then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("[{\"age\":10.0},{\"age\":11.0}]", response.getBody());
    }

    @Test
    public void shouldBeSerializableWhenIsObject() {
        // given
        final Response model = Fixture.from(Response.class).gimme(ResponseTemplate.VALID_FULL);
        final String expectedJson = "{ \"body\": {\"name\": \"Paul\"} }";

        // when
        final ResponseDto modelDto = new ResponseDto(model, new FromJsonStringToObjectConverterImpl());
        final String json = new Gson().toJson(modelDto);

        // then
        JSONAssert.assertEquals(expectedJson, json, false);
    }

    @Test
    public void shouldBeSerializableWhenIsArray() {
        // given
        final Response model = Fixture.from(Response.class).gimme(ResponseTemplate.VALID_WITH_LIST);
        final String expectedJson = "{ \"body\": [ {\"name\": \"Paul\"}, {\"name\": \"Peter\"} ] }";

        // when
        final ResponseDto modelDto = new ResponseDto(model, new FromJsonStringToObjectConverterImpl());
        final String json = new Gson().toJson(modelDto);

        // then
        JSONAssert.assertEquals(expectedJson, json, false);
    }

}
