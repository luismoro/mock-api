package br.com.concrete.mock.infra.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RunWith(MockitoJUnitRunner.class)
public class JsonValueCompilerImplTest {

    @InjectMocks
    private JsonValueCompilerImpl jsonValueCompiler;

    @Test
    public void shouldBeEqualWhenNotExistVariables() {
        // given
        final String jsonWithValue = "{ \"id\": \"first\", \"name\": \"Paul\" }";

        // when
        final String jsonCompiled = jsonValueCompiler.compile(jsonWithValue);

        // then
        JSONAssert.assertEquals(jsonWithValue, jsonCompiled, false);
    }

    @Test
    public void shouldCompileVariable3daysAgo() {
        // given
        final String format = "YYYY/MM/dd";
        final String jsonWithValue = "{ \"date1\": \"#{{3daysAgo:" + format + "}}\", \"name\": \"Paul\" }";
        final String date = LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern(format));
        final String expectedJson = "{ \"date1\": \"" + date + "\", \"name\": \"Paul\" }";

        // when
        final String jsonCompiled = jsonValueCompiler.compile(jsonWithValue);

        // then
        JSONAssert.assertEquals(expectedJson, jsonCompiled, false);
    }

    @Test
    public void shouldCompileVariable3daysAgoRepeated() {
        // given
        final String format = "YYYY/MM/dd";
        final String jsonWithValue = "{ \"date1\": \"#{{3daysAgo:" + format + "}}\", \"date2\": \"#{{3daysAgo:" + format + "}}\", \"name\": \"Paul\" }";
        final String date = LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern(format));
        final String expectedJson = "{ \"date1\": \"" + date + "\", \"date2\": \"" + date + "\", \"name\": \"Paul\" }";

        // when
        final String jsonCompiled = jsonValueCompiler.compile(jsonWithValue);

        // then
        JSONAssert.assertEquals(expectedJson, jsonCompiled, false);
    }

    @Test
    public void shouldCompileVariableNdaysAgo() {
        // given
        final String format = "YYYY/MM/dd";
        final String jsonWithValue = "{ \"date1\": \"#{{3daysAgo:" + format + "}}\", \"date2\": \"#{{3daysAgo:" + format + "}}\", \"date3\": \"#{{7daysAgo:" + format + "}}\", \"date4\": \"#{{8daysAgo:" + format + "}}\", \"name\": \"Paul\" }";
        final String date3daysAgo = LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern(format));
        final String date7daysAgo = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern(format));
        final String date8daysAgo = LocalDate.now().minusDays(8).format(DateTimeFormatter.ofPattern(format));
        final String expectedJson = "{ \"date1\": \"" + date3daysAgo + "\", \"date2\": \"" + date3daysAgo + "\", \"date3\": \"" + date7daysAgo + "\", \"date4\": \"" + date8daysAgo + "\", \"name\": \"Paul\" }";

        // when
        final String jsonCompiled = jsonValueCompiler.compile(jsonWithValue);

        // then
        JSONAssert.assertEquals(expectedJson, jsonCompiled, false);
    }

    // format error

}
