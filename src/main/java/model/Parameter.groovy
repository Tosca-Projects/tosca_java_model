package model

class Parameter extends Property {
	
	Parameter(String name, prop_def) {
		super(name, prop_def, false)
	}
	
	String toString() {
		return "$name=$model"
	}

}
