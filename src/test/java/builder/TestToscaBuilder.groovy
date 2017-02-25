package builder;

import static org.junit.Assert.*

import org.junit.Test
import org.yaml.snakeyaml.Yaml

import parser.ToscaParser

class TestToscaBuilder {

	@Test
	public void testSimpleTopologyTemplate() {
		def model = ToscaBuilder.simple_topology_template()
		def src = new Yaml().dump(model)
		assert ToscaParser.validate_tosca_yaml(src)
		assert ToscaParser.validate_service_template(model)
	}
}
