package model

import java.util.List
import java.util.Map

class RelationshipTemplate extends ToscaObject {
	
	RelationshipTemplate(String name, model) {
		super('relationship_template', name, model)
	}
	
	Map<String, String> getPropertyAssignments() {
		return ToscaModel.getPropertyAssignments(model)
	}
	
	Map<String, String> getAttributeAssignments() {
		return ToscaModel.getAttributeAssignments(model)
	}
	
	List<Interface> getInterfaces() {
		return ToscaModel.getInterfaces(model)
	}
	
	String getCopy() {
		return model.'copy'
	}
	
	Map<String, String> getMetadata() {
		def result = [:]
		if (model.'metadata' != null) {
			if (!(model.'metadata' instanceof Map)) {
				throw new Exception("relationship template ${this.name} metadata should be a map")
			}
		}
		return result
	}

}
