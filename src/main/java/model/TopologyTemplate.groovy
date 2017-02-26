package model

class TopologyTemplate {
	
	Map model
	
	TopologyTemplate(model) {
		if (!(model instanceof Map)) {
			throw new Exception("topology template should be a map")
		}
		this.model = model
	}
	
	String getDescription() {
		return model.'description'
	}
	
	List<Parameter> getInputs() {
		return ToscaModel.getInputs(model)
	}
	
	List<NodeTemplate> getNode_templates() {
		def result = []
		model.'node_templates'.each { String node_template_name, node_template_def ->
			result << new NodeTemplate(node_template_name, node_template_def)
		}
		return result
	}
	
	List<RelationshipTemplate> getRelationship_templates() {
		def result = []
		model.'relationship_templates'.each { String rel_template_name, rel_template_def ->
			result << new RelationshipTemplate(rel_template_name, rel_template_def)
		}
		return result
	}
	
	List<Group> getGroups() {
		def result = []
		model.'groups'.each { String group_name, group_def ->
			result << new Group(group_name, group_def)
		}
		return result
	}
	
	List<Policy> getPolicies() {
		def result = []
		model.'policies'.each { String policy_name, policy_def ->
			result << new Policy(policy_name, policy_def)
		}
		return result
	}

	List<Parameter> getOutputs() {
		def result = []
		model.'outputs'.each { String output_name, output_def ->
			result << new Parameter(output_name, output_def)
		}
		return result
	}

	// TODO substitution_mappings quand la doc officielle contiendra autre chose que N/A
	Map getSubstitution_mappings() {
		throw new Exception("Not available")
	}
	
	List<Workflow> getWorkflows() {
		throw new Exception("Not available")
	}

}
