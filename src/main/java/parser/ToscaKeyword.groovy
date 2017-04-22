package parser

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

class ToscaKeyword {

	String keyword
	Map<String,ToscaKeyword> children = [:]
	boolean is_mandatory = false
	static final VALID_TYPES = ['string', 'map', 'list', 'boolean', 'scalar']
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

	ToscaKeyword a_string(String child) {
		return entry(child).a("string")
	}

	ToscaKeyword a_map(String child) {
		return entry(child).a("map")
	}

	ToscaKeyword a_list(String child) {
		return entry(child).a("list")
	}

	ToscaKeyword a_boolean(String child) {
		return entry(child).a("boolean")
	}

	ToscaKeyword a_scalar(String child) {
		return entry(child).a("scalar")
	}

	ToscaKeyword a_properties_entry() {
		this.a_map("properties").with {
			any_map().with {
				a_string("type").mandatory()
				a_string "description"
				a_boolean "required"
				a_scalar "default"
				a_string("status").valid_values(
						["supported", "unsupported", "experimental", "deprecated"])
				a_constraints_entry()
				a_string "entry_schema"
			}
		}
		return this
	}

	ToscaKeyword a_constraints_entry() {
		this.a_list("constraints").with {
			a_map("equal").with { a_scalar() }
			a_map("greater_than").with { a_scalar() }
			a_map("greater_or_equal").with { a_scalar() }
			a_map("less_than").with { a_scalar() }
			a_map("less_or_equal").with { a_scalar() }
			a_map("in_range").with { any_list() }
			a_map("valid_values").with { any_list() }
			a_map("length").with { a_scalar() }
			a_map("min_length").with { a_scalar() }
			a_map("max_length").with { a_scalar() }
			a_map("pattern").with { a_string() }
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

	ToscaKeyword any_map() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*'].a("map")
	}

	ToscaKeyword any_list() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*'].a("list")
	}

	ToscaKeyword a_scalar() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*'].a("scalar")
	}

	ToscaKeyword a_string() {
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
		if (model instanceof Map) {
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

