package builder;

import static org.junit.Assert.*
import model.NodeTemplate
import model.ServiceTemplate
import model.ToscaModel
import model.Workflow

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
		def st = new ServiceTemplate(model)
		assert st.topology_template != null
		def tt = st.topology_template
		assert tt.description == 'sample topology template'
		assert tt.inputs == []
		assert tt.node_templates.size() == 1
		assert tt.node_templates[0] instanceof NodeTemplate
		def nt = tt.node_templates[0]
		assert nt.name == 'my_server'
		assert nt.description == 'default compute node'
		assert nt.capabilities.size() == 2
		nt.capabilities.each { cap ->
			assert cap.name in ['os','host']
			if (cap.name == 'os') {
				assert cap.properties != null
				assert cap.properties.size() == 4
			}
			else if (cap.name == 'host') {
				assert cap.properties != null
				assert cap.properties.size() == 3
				assert cap.properties.'disk_size' == '10 GB'
			}
		}
	}
	
	@Test
	public void testTopologyTemplateWithInputs() {
		def model = ToscaBuilder.simple_inputs()
		def src = new Yaml().dump(model)
		assert ToscaParser.validate_tosca_yaml(src)
		assert ToscaParser.validate_service_template(model)
		def st = new ServiceTemplate(model)
		def tt = st.getTopology_template()
		def i = tt.getInputs()
		assert i.size() == 1
		def p = i[0]
		assert p.name == 'cpus'
		assert p.model instanceof Map
		assert p.model.'type' == 'integer'
		assert p.model.'description' == 'Number of CPUs for the server.'
		assert p.model.'constraints' instanceof List
		assert p.model.'constraints'.size() == 1
		assert p.model.'constraints'[0] instanceof Map
		assert p.model.'constraints'[0].'valid_values' == [1,2,4,8]
	}

	@Test
	public void testTopologyTemplateWithOutputs() {
		def model = ToscaBuilder.topology_template_with_outputs()
		def src = new Yaml().dump(model)
		assert ToscaParser.validate_tosca_yaml(src)
		assert ToscaParser.validate_service_template(model)
		def st = new ServiceTemplate(model)
		def tt = st.getTopology_template()
		def o = tt.getOutputs()
		assert o.size() == 1
		def p = o[0]
		assert p.name == 'server_ip'
		assert p.model instanceof Map
		assert p.model.'description' == 'The private IP address of the provisioned server.'
		assert p.model.'value' instanceof Map
		assert p.model.'value'.'get_attribute' instanceof List
		assert p.model.'value'.'get_attribute'.size() == 2
		assert p.model.'value'.'get_attribute' == ['my_server','private_address']
	}

	@Test
	public void testSimpleWorkflow() {
		def model = ToscaBuilder.simple_workflow()
		def src = new Yaml().dump(model)
		def wf = new Workflow('simple_workflow', model)
		assert wf.name == 'simple_workflow'
		assert wf.description == 'a simple workflow with 1 step and 3 activities'
		assert wf.inputs == []
		assert wf.preconditions == []
		assert wf.steps.size() == 1
	}
	
	@Test
	public void testSimpleServiceTemplate() {
		def model = ToscaBuilder.simple_service_template()
		def src = new Yaml().dump(model)
		assert ToscaParser.validate_tosca_yaml(src)
		assert ToscaParser.validate_service_template(model)
		def st = new ServiceTemplate(model)
		assert st.getTosca_definitions_version() == 'tosca_simple_yaml_1_1'
		assert st.getMetadata() != null
		assert st.getTemplate_name() == 'my_template'
		assert st.getTemplate_author() == 'OASIS TOSCA TC'
		assert st.getTemplate_version() == '1.0'
	}

	@Test
	public void testSimpleServiceTemplate2() {
		def model = ToscaBuilder.simple_service_template2()
		def src = new Yaml().dump(model)
		assert ToscaParser.validate_tosca_yaml(src)
		assert ToscaParser.validate_service_template(model)
		def st = new ServiceTemplate(model)
		assert st.getTosca_definitions_version() == 'tosca_simple_yaml_1_1'
		assert st.getMetadata() != null
		assert st.getTemplate_name() == 'my_template'
		assert st.getTemplate_author() == 'OASIS TOSCA TC'
		assert st.getTemplate_version() == '1.0'
	}
}
