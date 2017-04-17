package parser

import static org.junit.Assert.*

import org.junit.Test
import org.yaml.snakeyaml.Yaml

import builder.ToscaBuilder;
import utils.Logger

class TestToscaValidator extends GroovyTestCase {

	@Test
	public void test_simple_yaml() {
		def model = ToscaBuilder.simple_service_template()
		assert ToscaValidator.validate(model).OK
	}

	@Test
	public void test_simple_yaml2() {
		def model = ToscaBuilder.simple_service_template2()
		assert ToscaValidator.validate(model).OK
	}

}
