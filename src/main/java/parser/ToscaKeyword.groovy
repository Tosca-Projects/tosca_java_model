package parser

import utils.Logger

class ToscaKeyword {

	String keyword
	Map<String,ToscaKeyword> children = [:]
	boolean is_mandatory = false
	static final VALID_TYPES = ['string', 'map', 'list', 'boolean', 'scalar', 'range']
	Set<String> valid_types = VALID_TYPES // by default all types
	List<String> valid_values

	ToscaKeyword(String keyword, ToscaKeyword parent = null) {
		this.keyword = keyword
	}

	ToscaKeyword valid_values(List<String> values) {
		this.valid_values = values
		return this
	}

	ToscaKeyword entry(String child) {
		if (children[child]) {
			throw new Exception("entry '$child' already exists for '${this.keyword}'")
		}
		children[child] = new ToscaKeyword(child, this)
		return children[child]
	}

	ToscaKeyword string_entry(String child) {
		return entry(child).a("string")
	}

	ToscaKeyword map_entry(String child) {
		return entry(child).a("map")
	}

	ToscaKeyword list_entry(String child) {
		return entry(child).a("list")
	}

	ToscaKeyword range_entry(String child) {
		return entry(child).a("range")
	}

	ToscaKeyword boolean_entry(String child) {
		return entry(child).a("boolean")
	}

	ToscaKeyword scalar_entry(String child) {
		return entry(child).a("scalar")
	}

	ToscaKeyword property_definitions_entry() {
		this.map_entry("properties").with {
			any_map_entry().with {
				string_entry("type").mandatory()
				string_entry "description"
				boolean_entry "required"
				scalar_entry "default"
				string_entry("status").valid_values(
						["supported", "unsupported", "experimental", "deprecated"])
				constraints_entry()
				string_entry "entry_schema"
			}
		}
		return this
	}

	ToscaKeyword property_assignments_entry() {
		this.map_entry("properties")
		return this
	}

	ToscaKeyword attributes_entry() {
		this.map_entry("attributes").with {
			any_map_entry().with {
				string_entry("type").mandatory()
				string_entry "description"
				scalar_entry "default"
				string_entry("status").valid_values(
						["supported", "unsupported", "experimental", "deprecated"])
				string_entry "entry_schema"
			}
		}
		return this
	}

	ToscaKeyword metadata_entry() {
		map_entry("metadata").with {
			string_entry "template_name"
			string_entry "template_version"
			string_entry "template_author"
		}
		return this
	}

	ToscaKeyword entity_entry() {
		string_entry("derived_from")
		string_entry "version"
		metadata_entry
		string_entry "description"
		return this
	}

	ToscaKeyword capability_assignments_entry() {
		this.map_entry("capabilities").with {
			any_map_entry().with {
				map_entry("properties")
				attributes_entry()
			}
		}
		return this
	}

	ToscaKeyword operation_definitions_entry(boolean used_in_type_definition) {
		any_map_entry().with {
			// ex: "Standard"
			any_map_entry().with {
				// ex: "configure"
				string_entry("description")
				string_entry("implementation")
				map_entry("inputs")
				or_a("string") // syntaxe compacte <verbe>: <implémentation>
			}
		}
		return this
	}

	ToscaKeyword interface_definitions_entry(boolean used_in_type_definition=false) {
		this.map_entry("interfaces").with {
			if (used_in_type_definition) {
				string_entry("type").mandatory()
				map_entry("inputs").with {
					any_map_entry()
				}
			}
			else {
				map_entry("inputs").with {
					any_map_entry()
				}
			}
			operation_definitions_entry(used_in_type_definition)
		}
		return this
	}

	ToscaKeyword requirements_entry() {
		list_entry("requirements").with {
			any_map_entry().with {
				string_entry "capability"
				string_entry "node"
				string_entry "relationship"
				map_entry("node_filter").with {
					property_definitions_entry()
					capability_assignments_entry()
				}
			}
		}
		return this
	}

	ToscaKeyword constraints_entry() {
		this.list_entry("constraints").with {
			map_entry("equal").with { any_scalar_entry() }
			map_entry("greater_than").with { any_scalar_entry() }
			map_entry("greater_or_equal").with { any_scalar_entry() }
			map_entry("less_than").with { any_scalar_entry() }
			map_entry("less_or_equal").with { any_scalar_entry() }
			map_entry("in_range").with { any_list_entry() }
			map_entry("valid_values").with { any_list_entry() }
			map_entry("length").with { any_scalar_entry() }
			map_entry("min_length").with { any_scalar_entry() }
			map_entry("max_length").with { any_scalar_entry() }
			map_entry("pattern").with { any_string_entry() }
		}
		return this
	}

	ToscaKeyword mandatory() {
		this.is_mandatory = true
		return this
	}

	ToscaKeyword a(String type) {
		if (!(type in VALID_TYPES)) {
			throw new Exception("'$type' is not a valid type: '$VALID_TYPES'")
		}
		if (type == "scalar") {
			this.valid_types = ["string", "boolean"]
		} else {
			this.valid_types = [type]
		}
		return this
	}

	ToscaKeyword or_a(String type) {
		if (!(type in VALID_TYPES)) {
			throw new Exception("'$type' is not a valid type: '$VALID_TYPES'")
		}
		this.valid_types << type
		return this
	}

	ToscaKeyword any_entry() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*']
	}

	ToscaKeyword any_map_entry() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*'].a("map")
	}

	ToscaKeyword any_list_entry() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*'].a("list")
	}

	ToscaKeyword any_scalar_entry() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*'].a("scalar")
	}

	ToscaKeyword any_string_entry() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*'].a("string")
	}

	boolean check_type(model, Set valid_types, ValidationResult vr, stack = '') {
		def model_type = "string" // default
		if (model instanceof Map) {
			model_type = "map"
		}
		else if (model instanceof List) {
			model_type = "list"
		}
		if (!(model_type in valid_types)) {
			vr.fail("$keyword type should be one of the following: $valid_types", stack)
			return false
		}
		return true
	}

	void check(model, ValidationResult vr, Stack stack = []) {
		if (!check_type(model, this.valid_types, vr, stack)) {
			return // no need to check further
		}
		if (model instanceof Map && children.size() > 0) {
			Logger.debug "model=$model stack=$stack"
			model.each { k,v ->
				def child = children[k]
				if (child == null) {
					if (children['*'] == null) {
						vr.fail("child '$k' is not valid for node '$keyword'", stack)
						return
					}
					child = children['*']
				}
				child.check(v, vr, stack+keyword)
			}
			children.each { String k, ToscaKeyword v ->
				if (v.is_mandatory) {
					if (model[k] == null) {
						vr.fail("missing mandatory child '$k' for node '$keyword'", stack)
					}
				}
			}
			return
		}
		if (model instanceof List && children['*']) {
			def valid_types = children['*'].valid_types
			model.each { item ->
				check_type(item, valid_types, vr)
				if (item instanceof String && this.valid_values) {
					if (!(valid_values.contains(item))) {
						vr.fail("value '$item' in not a valid value for node '$keyword'", stack)
					}
				}
			}
		}
	}

	String toString() {
		return this.keyword
	}
}

