package model

class ConditionClause {
	
	Map model
	
	ConditionClause(model) {
		if (!(model instanceof Map)) {
			throw new Exception("a condition clause should be a Map")
		}
		this.model = model
		if (model.size() != 1) {
			throw new Exception("condition clause map should have only one key")
		}
		this.model.keySet().each {
			if (!(it in ['and','or','assert'])) {
				throw new Exception("a condition definition can only be 'and', 'or', or 'assert'")
			}
		}
	}
}