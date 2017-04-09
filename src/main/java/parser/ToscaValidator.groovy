package parser

class ToscaValidator {
	
	static Map<String,Closure> rules = [
		
		// Root level
		'':{ Map model ->
			def result = new ValidationResult()
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
			checkPresent(model, 'tosca_definitions_version', result)
			checkString(model, 'tosca_definitions_version', result)
			checkMap(model, 'metadata', result)
			checkString(model, 'template_name', result)
			checkString(model, 'template_version', result)
			checkString(model, 'template_author', result)
			checkString(model, 'description', result)
			checkList(model, 'imports', result)
			checkMap(model, 'dsl_definitions', result)
			checkMap(model, 'repositories', result)
			checkMap(model, 'artifact_types', result)
			checkMap(model, 'data_types', result)
			checkMap(model, 'capability_types', result)
			checkMap(model, 'interface_types', result)
			checkMap(model, 'relationship_types', result)
			checkMap(model, 'node_types', result)
			checkMap(model, 'group_types', result)
			checkMap(model, 'policy_types', result)
			checkMap(model, 'topology_template', result)
			return result
		}
	]
	
	static ValidationResult validate(Map model, String node_name = '') {
		return rules[node_name].call(model)
	}
	
	static checkKeys(Map model, List<String> valid_keywords, ValidationResult vr) {
		model.keySet().each { k ->
			if (!(k in valid_keywords)) {
				vr.fail("keyword $k is not a valid keyword")
			}
		}
	}
	
	static checkPresent(Map model, String k, ValidationResult vr) {
		if (!model[k]) {
			vr.fail("keyword '$k' should be present")
		}
	}

	static checkString(Map model, String k, ValidationResult vr) {
		if (model[k] && !(model[k] instanceof String)) {
			vr.fail("'$k' should be a string")
		}
	}

	static checkMap(Map model, String k, ValidationResult vr) {
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

class ValidationResult {
	
	boolean OK = true
	List<String> messages = [] 
	
	void fail(String msg) {
		OK = false
		messages << msg
	}
	
}
