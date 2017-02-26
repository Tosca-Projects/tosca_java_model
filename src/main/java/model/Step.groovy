package model

import java.util.Map;

class Step {

	String name
	Map model

	Step(String name, model) {
		if (!(model instanceof Map)) {
			throw new Exception("a step should be a map")
		}
		if (model['target'] == null) {
			throw new Exception("a step should have a target")
		}
		this.name = name
		this.model = model
	}

	String getTarget() {
		return model.'target'
	}

	String getTarget_relationship() {
		return model.'target_relationship'
	}

	String getOperation_host() {
		return model.'operation_host'
	}

	List<ActivityDefinition> getFilter() {
		if (model.'filter' == null) {
			return []
		}
		ToscaModel.checkIsList(model, 'filter')
		def result = []
		model.'filter'.each {
			result << new ActivityDefinition(it)
		}
		return result
	}
}
