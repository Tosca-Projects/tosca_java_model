package parser

class ToscaValidator {
	
	static Map<String,Closure> rules = [
		
		// Root level
		'':{ model ->
			def result = new ValidationResult()
			checkIsMap(model, "root level", result)
			def valid_keywords = [
				'tosca_definitions_version',
				'metadata',
				'template_name', // should be inside metadata map but seems accepted at root level
				'template_version', // should be inside metadata map but seems accepted at root level
				'template_author', // should be inside metadata map but seems accepted at root level
				'description',
				'imports',
				'dsl_definitions',
				'repositories',
				'artifact_types',
				'data_types',
				'capability_types',
				'interface_types',
				'relationship_types',
				'node_types',
				'group_types',
				'policy_types',
				'topology_template'
				]
			checkKeys(model, valid_keywords, result)
			checkIsPresent(model, 'tosca_definitions_version', result)
			checkIsString(model, 'tosca_definitions_version', result)
			checkIsMap(model, 'metadata', result)
			checkIsString(model, 'template_name', result)
			checkIsString(model, 'template_version', result)
			checkIsString(model, 'template_author', result)
			checkIsString(model, 'description', result)
			checkList(model, 'imports', result)
			checkIsMap(model, 'dsl_definitions', result)
			checkIsMap(model, 'repositories', result)
			checkIsMap(model, 'artifact_types', result)
			checkIsMap(model, 'data_types', result)
			checkIsMap(model, 'capability_types', result)
			checkIsMap(model, 'interface_types', result)
			checkIsMap(model, 'relationship_types', result)
			checkIsMap(model, 'node_types', result)
			checkIsMap(model, 'group_types', result)
			checkIsMap(model, 'policy_types', result)
			checkIsMap(model, 'topology_template', result)
			return result
		},
	
		'metadata': { model ->
			def result = new ValidationResult()
			checkList(model, "metadata", result)
			
		}
	]
	
	static ValidationResult validate(model, String node_name = '') {
		return rules[node_name].call(model)
	}
	
	static checkKeys(Map model, List<String> valid_keywords, ValidationResult vr) {
		model.keySet().each { k ->
			if (!(k in valid_keywords)) {
				vr.fail("keyword $k is not a valid keyword")
			}
		}
	}
	
	static checkIsPresent(Map model, String k, ValidationResult vr) {
		if (!model[k]) {
			vr.fail("keyword '$k' should be present")
		}
	}

	static checkIsString(Map model, String k, ValidationResult vr) {
		if (model[k] && !(model[k] instanceof String)) {
			vr.fail("'$k' should be a string")
		}
	}

	static checkIsMap(Map model, String k, ValidationResult vr) {
		if (model[k] && !(model[k] instanceof Map)) {
			vr.fail("'$k' should be a map")
		}
	}

	static checkList(Map model, String k, ValidationResult vr) {
		if (model[k] && !(model[k] instanceof List)) {
			vr.fail("'$k' should be a list")
		}
	}

}

