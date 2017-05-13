package builder

import com.esotericsoftware.yamlbeans.YamlReader

/**
 * Helper class to build tosca service templates as maps
 *
 * @author zebig
 *
 */

class ToscaBuilder {
	
	static Map simple_service_template() {
		return [
			tosca_definitions_version: 'tosca_simple_yaml_1_1',
			metadata: [
			  template_name: 'my_template',
			  template_author: 'OASIS TOSCA TC',			
			  template_version: '1.0'
			  ]
		]
	}

	static Map wrong_service_template() {
		def model = simple_service_template()
		model["wrong_keyword"] = "should fail"
		return model
	}

	static Map wrong_service_template2() {
		def model = simple_service_template()
		model.remove("tosca_definitions_version")
		return model
	}

	static Map simple_service_template2() { // autre syntaxe
		return [
			tosca_definitions_version: 'tosca_simple_yaml_1_1',
			template_name: 'my_template',
			template_author: 'OASIS TOSCA TC',			
			template_version: '1.0'
		]
	}
	
	static Map simple_dsl_definitions() {
		def text = '''\
dsl_definitions:
  my_compute_node_props: &my_compute_node_props
    disk_size: 10 GB
    num_cpus: 1
    mem_size: 2 GB
topology_template:
  node_templates:
    my_server:
      type: Compute
      capabilities:
        host:
          properties: *my_compute_node_props
    my_database:
      type: Compute
      capabilities:
        host:
          properties: *my_compute_node_props
'''
		def model = new YamlReader(text).read()
		return simple_service_template() + model
	}
	
	static Map simple_repositories() {
		def text = '''\
repositories:
  my_code_repo:
    description: "My project's code repository in GitHub"
    url: "https://github.com/my-project/"
'''
		def model = new YamlReader(text).read()
		return simple_service_template() + model
	}

	static Map simple_imports() { // multi-line grammar
		def text = '''\
imports: 
  - file: <file_URI>
    repository: <repository_name>
    namespace_uri: <definition_namespace_uri>
    namespace_prefix: <definition_namespace_prefix>
'''
		def model = new YamlReader(text).read()
		return simple_service_template() + model
	}

	static Map simple_imports2() { // single-line grammar
		def text = '''\
imports: 
  - fichier1.yaml
  - fichier2.yaml
  - fichier3.yaml
'''
		def model = new YamlReader(text).read()
		return simple_service_template() + model
	}

	static Map host(int num_cpus, String disk_size, String mem_size) {
		return [
			properties:[
				num_cpus:num_cpus,
				disk_size:disk_size,
				mem_size:mem_size
			]
		]
	}

	static Map default_host() {
		return host(1, '10 GB', '4096 MB')
	}

	static Map os(String architecture, String type, String distribution, String version) {
		return [
			properties:[
				architecture:architecture,
				type:type,
				distribution:distribution,
				version:version
			]
		]
	}

	static Map default_os() {
		return os('x86_64','linux','rhel','7.2')
	}

	static Map compute(String type, String description, Map host, Map os) {
		return [
			type:type,
			description:description,
			capabilities:[
				'host':host,
				'os':os
			]
		]
	}

	static Map default_compute() {
		return compute('tosca.nodes.Compute','default compute node',default_host(),default_os())
	}

	static Map simple_topology_template() {
		return [
			tosca_definitions_version:'tosca_simple_yaml_1_0',
			description:'Template for deploying a single server with predefined properties.',
			topology_template:[
				description:'sample topology template',
				node_templates:[
					'my_server':default_compute()
				]
			]
		]
	}

	static Map simple_inputs() {
		def t = simple_topology_template()
		t."topology_template"."inputs" = cpus_inputs()
		return t
	}

	static Map topology_template_with_outputs() {
		def t = simple_topology_template()
		t."topology_template"."outputs" = server_ip_output()
		return t
	}


	static Map create_my_node_step_3_activities() {
		return [
			target: 'my_node',
			activities:[[set_state:'creating'], [call_operation:'tosca.interfaces.node.lifecycle.Standard.create'], [set_state:'created']]]
	}

	static Map simple_workflow() {
		return [
			description:'a simple workflow with 1 step and 3 activities',
			steps:[
				create_my_node:create_my_node_step_3_activities()
			]
		]
	}

	static Map creating_my_node_step() {
		return [
			target: 'my_node',
			activities: [[set_state:'creating']],
			on_success: 'create_my_node'
		]
	}

	static Map create_my_node_step_1_activity() {
		return [
			target: 'my_node',
			activities:[[call_operation:'tosca.interfaces.node.lifecycle.Standard.create']],
			on_success: 'created_my_node'
		]
	}

	static Map created_my_node_step() {
		return [
			target: 'my_node',
			activities: [[set_state:'created']]]
	}

	static Map simple_workflow_3steps() {
		return [
			steps:[
				creating_my_node:creating_my_node_step(),
				create_my_node:create_my_node_step_1_activity(),
				created_my_node:created_my_node_step()
			]
		]
	}

	static Map state_available() {
		return [[state:
				[[equal:'available']]]]
	}

	static Map state_started_or_available() {
		return [[state:
				[[valid_values:['started', 'available']]]]]
	}

	static List sample_preconditions() {
		return [
			[
				target: 'my_server',
				condition: [['assert':state_available()]]],
			[
				target: 'mysql',
				condition: [['assert':state_started_or_available()], [my_attribute:[[equal:'ready']]]]]
		]
	}

	static Map cpus_inputs() {
		return [
			'cpus':[
				type:'integer',
				description: 'Number of CPUs for the server.',
				constraints: [[valid_values:[1, 2, 4, 8]]
				]]
		]
	}

	static Map get_attribute(String node_name, String attribute_name) {
		return [
			get_attribute:[node_name, attribute_name]]
	}

	static Map server_ip_output() {
		return [
			server_ip:[
				description:'The private IP address of the provisioned server.',
				value:get_attribute('my_server','private_address')
			]
		]
	}
	
	static Map complex_service_template() {
		def text = new File('src/test/resources/junit/valid_level0_keywords.yml').text
		return new YamlReader(text).read()
	}
	
	static Map simple_imperative_workflow() {
		def text = new File('src/test/resources/junit/sample_workflow_template.yml').text
		return new YamlReader(text).read()
	}
}