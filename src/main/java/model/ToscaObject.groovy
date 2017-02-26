package model

import java.util.Map

abstract class ToscaObject {
	
	static Map<String,Map<String,ToscaObject>> instances = [:]
	
	String super_type
	String name
	Map model
	
	public ToscaObject(String super_type, String name, model) {
		if (!(model instanceof Map)) {
			throw new Exception("Invalid $super_type definition $name: should be a map")
		}
		ToscaModel.checkRequired(model, ['type'])
		if (instances[super_type] == null) {
			instances[super_type] = [:]
		}
		this.super_type = super_type
		this.name = name
		this.model = model
		instances[super_type][name] = this
	}
	
	String getType() {
		return model.'type'
	}
	
	String getDescription() {
		return model.'description'
	}
	
	static ToscaObject getInstance(String super_type, String name) {
		return instances[super_type][name]
	}

}
