package model

import java.util.Map;

class Property extends ToscaState {
	
	Property(String name, prop_def, boolean typed=true) {
		super(name, prop_def, typed)
	}

	List<Constraint> getConstraints() {
		if (model.'constraints' == null) {
			return  []
		}
		if (!(model.'constraints' instanceof List)) {
			throw new Exception("'constraints' should be a list in property '$name'")
		}
		List constraints = model.'constraints'
		List<Constraint> result = []
		constraints.each {
			result << new Constraint(it)
		}
		return result
	}
		
}
