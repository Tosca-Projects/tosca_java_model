package model

import java.util.List
import java.util.Map

class Workflow {

	String name
	Map model

	Workflow(String name, model) {
		if (!(model instanceof Map)) {
			throw new Exception("a workflow should be a map")
		}
		this.name = name
		this.model = model
	}

	String getDescription() {
		return model.'description'
	}
	
	Map<String, String> getMetadata() {
		return ToscaModel.getMetadata(model)
	}
	
	List<Parameter> getInputs() {
		return ToscaModel.getInputs(model)
	}
	
	List<Precondition> getPreconditions() {
		if (model.'preconditions' == null) {
			return []
		}
		ToscaModel.checkIsList(model, 'preconditions')
		def result = []
		model.'preconditions'.each {
			result << new Precondition(it)
		}
		return result
	}
	
}
