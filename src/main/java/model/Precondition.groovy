package model

class Precondition {
	
	Map model
	
	Precondition(model) {
		if (!(model instanceof Map)) {
			throw new Exception("a precondition should be a map")
		}
		if (model['target'] == null) {
			throw new Exception("a precondition should have a target")
		}
	}
	
	String getTarget() {
		return model.'target'
	}
	
	String getTarget_relationship() {
		return model.'target_relationship'
	}
	
	List<ConditionClause> getCondition() {
		if (model.'condition' == null) {
			return []
		}
		ToscaModel.checkIsList(model, 'condition')
		def result = []
		model.'condition'.each { 
			result << new ConditionClause(it)
		}
		return result
	}

}
