package model

class CapabilityAssignment {
	
	String name
	Map model
	
	CapabilityAssignment(String name, model) {
		if (!(model instanceof Map)) {
			"a capability assignment should be map"
		}
		this.name = name
		this.model = model
	}
	
	Map<String,String> getProperties() {
		return ToscaModel.getPropertyAssignments(model)
	}

	Map<String,String> getAttributes() {
		return ToscaModel.getAttributeAssignments(model)
	}

}
