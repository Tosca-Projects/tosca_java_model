package parser

import java.util.List;

class ToscaSyntax {

	String root
	ToscaKeyword root_entry

	ToscaSyntax(String root) {
		this.root = root
		root_entry = new ToscaKeyword(root)
	}

	ValidationResult check(model) {
		def vr = new ValidationResult()
		root_entry.check(model, vr)
		return vr
	}
}

class ValidationResult {

	boolean OK = true
	List<String> messages = []

	static ValidationResult failure(String msg) {
		def vr = new ValidationResult()
		vr.fail(msg)
		return vr
	}

	void fail(String msg, Stack stack) {
		if (stack && stack.size() > 0) {
			fail "$msg ($stack)"
		}
		else {
			fail "msg"
		}
	}

	void fail(String msg) {
		OK = false
		messages << msg
	}

	String toString() {
		if (OK) {
			return "OK"
		}
		else {
			return "KO: $messages"
		}
	}
}

class ToscaKeyword {

	String keyword
	Map<String,ToscaKeyword> children = [:]
	boolean is_mandatory = false
	static final VALID_TYPES = ['string', 'map', 'list', 'boolean']
	Set<String> valid_types = VALID_TYPES // by default all types
	List<String> valid_values

	ToscaKeyword(String keyword, ToscaKeyword parent = null) {
		this.keyword = keyword
	}

	ToscaKeyword values(List<String> values) {
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

	ToscaKeyword boolean_entry(String child) {
		return entry(child).a("boolean")
	}

	ToscaKeyword properties_entry() {
		this.map_entry("properties").with {
			any_map_entry().with {
				string_entry("type").mandatory()
				string_entry "description"
				boolean_entry "required"
				entry "default"
				string_entry("status").values(
					[   "supported", 
						"unsupported", 
						"experimental", 
						"deprecated"])
				constraints_entry()
				string_entry "entry_schema"
			}
		}
		return this
	}

	ToscaKeyword constraints_entry() {
		// TODO
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
		this.valid_types = [type]
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

