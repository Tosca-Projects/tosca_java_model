package model

class ActivityDefinition {
	
	Map model
	
	ActivityDefinition(model) {
		if (!(model instanceof Map)) {
			throw new Exception("an activity definition should be a map")
		}
		if (!model.size() == 1) {
			throw new Exception("an activity definition should have only one key")
		}
		def valid_keynames = [
			'delegate',
			'set_state',
			'call_operation',
			'inline']
		if (!model.keySet()[0] in valid_keynames) {
			throw new Exception("an activity definition must be one of ${valid_keynames}")
		}
		this.model = model
	}

}
