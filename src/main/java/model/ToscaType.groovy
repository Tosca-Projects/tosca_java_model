package model

abstract class ToscaType {
	
	static Map<String,Map<String,ToscaType>> instances = [:]
	
	String type
	String name
	Map model
	
	ToscaType(String type, String name, model) {
		if (!(model instanceof Map)) {
			throw new Exception("Invalid $type type '$name': should be a map")
		}
		this.type = type
		this.name = name
		this.model = model
		if (instances[type] == null) {
			instances[type] = [:]
		}
		if (instances[type][name]) {
			throw new Exception("Invalid $type type '$name': duplicate")
		}
		instances[type][name] = this
	}
	
	ToscaType getDerived_from() {
		if (model.'derived_from' == null) {
			return null
		}
		if (instances[type][model.'derived_from'] == null) {
			throw new Exception("$type '$name' derives from unknown type: "+model.'derived_from')
		}
		return instances[type][model.'derived_from']
	}
	
	String getVersion() { return model.'version' }
	
	String getDescription() { return model.'description' }
	
	static ToscaType get(String type, String name) {
		if (instances[type] == null) {
			throw new Exception("Unknown type '$type'")
		}
		if (instances[type][name] == null) {
			throw new Exception("Unknown $type '$name'")
		}
		return instances[type][name]
	}
	
	Map<String, String> getMetadata() {
		return ToscaModel.getMetadata(this.model)
	}
	
}
