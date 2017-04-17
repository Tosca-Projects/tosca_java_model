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
	static final VALID_TYPES = ['string', 'map', 'list']
	Set<String> valid_types = VALID_TYPES // by default all types
	
	ToscaKeyword(String keyword, ToscaKeyword parent = null) {
		this.keyword = keyword
	}

	ToscaKeyword entry(String child) {
		if (children[child]) {
			throw new Exception("entry '$child' already exists for '${this.keyword}'")
		}
		children[child] = new ToscaKeyword(child, this)
		return children[child]
	}

	ToscaKeyword mandatory() {
		this.is_mandatory = true
		return this
	}

	ToscaKeyword a(String type) {
		if (!(type in VALID_TYPES)) {
			throw new Exception("'$type' is not a valid type: '$VALID_TYPES'")
		}
		this.valid_types = [ type ]
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
	
	boolean check_type(model, Set valid_types, ValidationResult vr, stack = '') {
		def model_type = "string" // default
		if (model instanceof Map) {
			model_type = "map"
		}
		else if (model instanceof List) {
			model_type = "list"
		}
		if (!(model_type in valid_types)) {
			vr.fail("$keyword type should be one of the following: $valid_types $stack")
			return false
		}
		return true
	} 

	void check(model, ValidationResult vr, stack = []) {
		if (!check_type(model, this.valid_types, vr, stack)) {
			return // no need to check further
		}
		if (model instanceof Map) {
			model.each { k,v ->
				def child = children[k]
				if (child == null) {
					if (children['*'] == null) {
						vr.fail("child '$k' is not valid for node '$keyword' (stack: $stack)")
						return
					}
					child = children['*']
				}
				child.check(v, vr, stack+keyword)
			}
			children.each { k,v ->
				if (v.is_mandatory) {
					if (model[k] == null) {
						vr.fail("missing mandatory child '$k' for node '$keyword'")
					}
				}
			}
			return
		}
		if (model instanceof List && children['*']) {
			def valid_types = children['*'].valid_types
			model.each { item ->
				check_type(item, valid_types, vr)
			}
		}
	}
	
	String toString() {
		return this.keyword
	}
}

