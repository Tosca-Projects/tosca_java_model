package builder;

import static org.junit.Assert.*
import model.NodeTemplate
import model.ServiceTemplate
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
}
