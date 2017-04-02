package model

import utils.Logger

abstract class ToscaModel {
	
	static checkIsMap(model, String name) {
		if (model[name] != null && !(model[name] instanceof Map)) {
			throw new Exception("'$name' should be a map")
		}
	}

	static checkIsList(model, String name) {
		if (model[name] != null && !(model[name] instanceof List)) {
			throw new Exception("'$name' should be a list")
		}
	}

	static checkRequired(Map model, List<String> keynames) {
		def ok = true
		keynames.each {
			if (model[it] == null) {
				Logger.error "Keyname '$it' is missing"
				ok = false
			}
		}
		if (!ok) {
			throw new Exception("Some required information is missing")
		}
	}

	static Map<String, Attribute> getAttributes(Map model) {
		if (model.'attributes' == null) {
			return [:]
		}
		checkIsMap(model, 'attributes')
		Map<String, Attribute> result = [:]
		Map atts = model.'attributes'
		atts.each { String att_name, att_def ->
			result[att_name] = new Attribute(att_name, att_def)
		}
		return result
	}

	static Map<String, Property> getProperties(Map model) {
		if (model.'properties' == null) {
			return [:]
		}
		checkIsMap(model, 'properties')
		Map<Property> result = [:]
		Map props = model.'properties'
		props.each { String prop_name, prop_def ->
			result[prop_name] = new Property(prop_name, prop_def)
		}
		return result
	}

	static List<RequirementAssignment> getRequirementAssignments(Map model) {
		if (model.'requirements' == null) {
			return []
		}
		checkIsList(model, "requirements")
		List requirements = model.'requirements'
		List<RequirementAssignment> result = []
		requirements.each { r ->
			if (!(r instanceof Map)&&!(r instanceof String)) {
				"a requirement should be map or a string"
			}
			result << new RequirementAssignment(r)
		}
		return result
	}

	static List<Requirement> getRequirements(Map model) {
		if (model.'requirements' == null) {
			return []
		}
		checkIsList(model, "requirements")
		List requirements = model.'requirements'
		List<Requirement> result = []
		requirements.each { r ->
			result << new Requirement(r)
		}
		return result
	}

	static List<CapabilityAssignment> getCapabilityAssignments(Map model) {
		if (model.'capabilities' == null) {
			return []
		}
		checkIsMap(model, "capabilities")
		Map capabilities = model.'capabilities'
		def result = []
		capabilities.each { cap_name, cap ->
			result << new CapabilityAssignment(cap_name, cap)
		}
		return result
	}

	static List<String> getListOfString(String name, Map model) {
		if (model[name] == null) {
			return []
		}
		checkIsList(model, name)
		List<String> result = []
		model[name].each { result << it.toString() }
		return result
	}

	static Map<String, String> getMetadata(Map model) {
		def result = [:]
		if (model.'metadata' != null) {
			checkIsMap(model, "metadata")
			model.'metadata'.each {k,s ->
				result[k] = s.toString()
			}
		}
		return result
	}

	static Map<String, String> getPropertyAssignments(model) {
		if (!(model instanceof Map)) {
			throw new Exception("A property assignment should be a map")
		}
		if (model['properties'] == null) {
			return [:]
		}
		checkIsMap(model, "properties")
		def result = [:]
		model['properties'].each {k,v ->
			result[k] = v.toString()
		}
		return result
	}

	static Map<String, String> getAttributeAssignments(Map model) {
		if (model['attributes'] == null) {
			return [:]
		}
		checkIsMap(model, "attributes")
		def result = [:]
		model['attributes'].each {k,v ->
			result[k] = v.toString()
		}
		return result
	}

	static List<Constraint> getConstraints(Map model) {
		if (model.'constraints' == null) {
			return []
		}
		checkIsList(model, "constraints")
		List constraints = model.'constraints'
		List<Constraint> result = []
		constraints.each { c ->
			if (!(c instanceof Map)) {
				"a constraint should be map"
			}
			result << new Constraint(c)
		}
		return result
	}

	static List<Parameter> getInputs(Map model) {
		def result = []
		if (model.'inputs' != null) {
			if (!(model.'inputs' instanceof Map)) {
				throw new Exception("'inputs' should be a map")
			}
			model.'inputs'.each { String input_name, input_def ->
				result << new Parameter(input_name, input_def)
			}
		}
		return result
	}

	static List<Interface> getInterfaces(Map model) {
		if (model.'interfaces' == null) {
			return []
		}
		if (!(model.'interfaces' instanceof List)) {
			throw new Exception("'interfaces' should be a list")
		}
		List interfaces = model.'interfaces'
		List<Interface> result = []
		interfaces.each { String iname, i ->
			if (!(i instanceof Map)) {
				"Interface '$iname' should be a map"
			}
			result << new Interface(iname, i)
		}
		return result
	}

	static Map<String,Artifact> getArtifacts(Map model) {
		if (model.'artifacts' == null) {
			return [:]
		}
		if (!(model.'artifacts' instanceof Map)) {
			throw new Exception("'artifacts' should be a map")
		}
		def result = []
		model.'artifacts'.each { String aname, a ->
			if (!(a instanceof Map) && !(a instanceof String)) {
				"Artifact '$aname' should be a map or a string"
			}
			result[aname] = new Artifact(aname, a)
		}
		return result
	}

	static NodeFilter getNode_filter(Map model) {
		if (model."node_filter" == null) {
			return null
		}
		return new NodeFilter(model."node_filter")
	}

}
