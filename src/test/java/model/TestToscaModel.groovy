package model;

import static org.junit.Assert.*

import org.junit.Test
import org.yaml.snakeyaml.Yaml

import builder.ToscaBuilder

class TestToscaModel {

	@Test
	public void testDumpYaml() {
		def model = ToscaBuilder.simple_service_template2()
		def src = ToscaModel.dumpYaml(model)
		def model2 = new Yaml().load(src)
		assert model2.equals(model)
	}
}
