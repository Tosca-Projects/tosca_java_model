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
		return [
			properties:[
				num_cpus:1,
				disk_size:'10 GB',
				mem_size:'4096 MB'
			]
		]
	}

	static Map default_os() {
		return [
			properties:[
				architecture:'x86_64',
				type:'linux',
				distribution:'rhel', version:'7.2'
			]
		]
	}

	static Map default_compute() {
		return [
			type:'tosca.nodes.Compute',
			description:'default compute node',
			capabilities:[
				'host':default_host(),
				'os':default_os()
			]
		]
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


	static Map create_my_node_step_3_activities() {
		return [
			target: 'my_node',
			activities:[
				[set_state:'creating'],
				[call_operation:'tosca.interfaces.node.lifecycle.Standard.create'],
				[set_state:'created']
			]
		]
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
			activities: [[set_state:'created']]
		]
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
}
