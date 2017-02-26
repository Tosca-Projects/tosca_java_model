package parser

import static org.junit.Assert.*

import org.junit.Test
import org.yaml.snakeyaml.Yaml

import utils.Logger

class TestToscaParser extends GroovyTestCase {

	@Test
	public void test_invalid_yaml() {
		def p = new ToscaParser()
		def invalid_yaml = '''\
hello:
:world
'''
		assert p.validate_yaml(invalid_yaml) == false
		assert Logger.gotError('Not a valid YAML source')
	}

	@Test
	public void test_valid_yaml() {
		def p = new ToscaParser()
		def valid_yaml = '''\
hello:
  # superb comment
  world:
    - indeed
    - a nice day
'''
		assert p.validate_yaml(valid_yaml)
		def m = new Yaml().load(valid_yaml)
		assert m != null
		assert m.toString() == '[hello:[world:[indeed, a nice day]]]'
	}
	
	@Test
	public void test_invalid_tosca() {
		def p = new ToscaParser()
		def invalid_tosca = '''\
hello:
'''
		assert p.validate_tosca_yaml(invalid_tosca) == false
		assert Logger.gotError("Source must begin with 'tosca_definitions_version'")
	}

	@Test
	public void test_duplicate_keys() {
		def p = new ToscaParser()
		def invalid_tosca = '''\
hello: world
how: are_you
hello: again
'''
		assert p.validate_tosca_yaml(invalid_tosca) == false
		assert Logger.gotError("Source should not have duplicate keys")
	}
	
	@Test
	public void test_invalid_level0_keywords() {
		def p = new ToscaParser() 
		def invalid_tosca = new File('src/test/resources/junit/invalid_level0_keywords.yml').text
		assert p.validate_tosca_yaml(invalid_tosca) == false
		assert Logger.gotError("'topology_templates' is not a valid keyword for a service template")
	}

	@Test
	public void test_valid_level0_keywords() {
		def p = new ToscaParser()
		def valid_tosca = new File('src/test/resources/junit/valid_level0_keywords.yml').text
		Logger.print_error = true
		assert p.validate_tosca_yaml(valid_tosca)
	}

	@Test
	public void test_hello_world_tosca() {
		def p = new ToscaParser()
		def valid_tosca = new File('src/test/resources/junit/hello_world_tosca.yml').text
		assert p.validate_tosca_yaml(valid_tosca)
	}

	@Test
	public void test_invalid_topology_template_keywords() {
		def p = new ToscaParser()
		def invalid_tosca = new File('src/test/resources/junit/invalid_topology_template_keywords.yml').text
		assert p.validate_tosca_yaml(invalid_tosca) == false
		assert Logger.gotError("'node_template' is not a valid keyword in topology_template block")
	}

	@Test
	public void test_invalid_node_templates_keywords() {
		def p = new ToscaParser()
		def invalid_tosca = new File('src/test/resources/junit/invalid_node_templates_keywords.yml').text
		assert p.validate_tosca_yaml(invalid_tosca) == false
		assert Logger.gotError("'capability' is not a valid keyword")
	}

	@Test
	public void test_invalid_properties_keywords() {
		def p = new ToscaParser()
		def invalid_tosca = new File('src/test/resources/junit/invalid_properties_keywords.yml').text
		assert p.validate_tosca_yaml(invalid_tosca) == false
		assert Logger.gotError("'capability' is not a valid keyword")
	}


}
