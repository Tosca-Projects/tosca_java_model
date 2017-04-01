package model;

import static org.junit.Assert.*

import org.junit.Test
import org.yaml.snakeyaml.Yaml

import utils.Logger
import builder.ToscaBuilder

class TestServiceTemplate {

	@Test
	public void testRepositories_ok() {
		def valid_tosca = new File('src/test/resources/junit/st_repository.yml').text
		def model = new Yaml().load(valid_tosca)
		def st = new ServiceTemplate(model)
		assert st.tosca_definitions_version == 'tosca_simple_yaml_1_0'
		assert st.description == 'Template for deploying a single server with predefined properties.'
		assert st.repositories.size() == 1
		assert st.repositories[0].name == 'my_code_repo'
		assert st.repositories[0].description == 'My project’s code repository in GitHub'
		assert st.repositories[0].url == 'https://github.com/my-project/'
	}

	@Test
	public void testRepositories_ko() {
		def valid_tosca = new File('src/test/resources/junit/st_repository_ko.yml').text
		def model = new Yaml().load(valid_tosca)
		def st = new ServiceTemplate(model)
		try {
			def repos = st.getRepositories()
			fail("aurait dû planter car manque url")
		}
		catch (Exception e) {
			assert Logger.gotError("Keyname 'url' is missing")
		}
	}
	
	@Test
	public void testSimpleTopologyTemplate() {
		def model = ToscaBuilder.simple_topology_template()
		ServiceTemplate st = new ServiceTemplate(model)
		assert st.tosca_definitions_version == 'tosca_simple_yaml_1_0'
		assert st.description == 'Template for deploying a single server with predefined properties.'
		assert st.metadata == [:]
	}
}
