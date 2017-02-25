package builder

import java.util.Map

/**
 * Helper class to build tosca service templates as maps
 *
 * @author zebig
 *
 */

class ToscaBuilder {

	static Map default_host() {
		return [num_cpus:1, disk_size:'10 GB', mem_size:'4096 MB']
	}

	static Map default_os() {
		return [architecture:'x86_64', type:'linux', distribution:'rhel', version:'7.2']
	}
	
	static Map default_compute() {
		return [
			'type':'tosca.nodes.Compute',
			'capabilities':[
				'host':default_host(),
				'os':default_os()
				]
			]
	}

	static Map simple_topology_template() {
		return [
			'tosca_definitions_version':'tosca_simple_yaml_1_0',
			'description':'Template for deploying a single server with predefined properties.',
			'topology_template':[
				'node_templates':[
					'my_server':default_compute()
				]
			]
		]
	}

	static Map simple_workflow() {
		return [
			'steps':[
				'create_my_node':[
					'target':'my_node',
					'activities':[
						['set_state':'creating'],
						['call_operation':'tosca.interfaces.node.lifecycle.Standard.create'],
						['set_state':'created']
					]
				]
			]
		]
	}
}
