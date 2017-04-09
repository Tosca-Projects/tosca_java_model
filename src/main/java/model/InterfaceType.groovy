package model

class InterfaceType extends ToscaType {
	
	InterfaceType(String name, model) {
		super('interface_type', name, model)
	}
	
	List<Property> getInputs() {
		return ToscaModel.getInputs(this.model)
	}
	
	List<Operation> getOperations() {
		// TODO
	}

}
