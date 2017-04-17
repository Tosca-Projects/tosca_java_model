package parser

import java.util.List;

class ToscaSyntax {

	Map<String, ToscaKeyword> root_keywords = [:]

	ToscaKeyword entry(String keyword) {
		if (root_keywords[keyword] == null) {
			root_keywords[keyword] = new ToscaKeyword(keyword)
		}
		return root_keywords[keyword]
	}

	ValidationResult check(String keyword, Map model) {
		if (root_keywords[keyword] == null) {
			return ValidationResult.failure("unknown keyword '$keyword' at root level")
		}
		def kw = root_keywords[keyword]
		def vr = new ValidationResult()
		kw.check(model, vr)
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
	String type = "string"

	static final VALID_TYPES = ['string', 'map', 'list']

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

	ToscaKeyword is_mandatory() {
		this.is_mandatory = true
		return this
	}

	ToscaKeyword is_a(String type) {
		if (!(type in VALID_TYPES)) {
			throw new Exception("'$type' is not a valid type: '$VALID_TYPES'")
		}
		this.type = type
		return this
	}

	ToscaKeyword any_keyword() {
		children['*'] = new ToscaKeyword('*', this)
		return children['*']
	}

	void check(model, ValidationResult vr) {
		if (this.type == "map" && !(model instanceof Map)) {
			vr.fail("$keyword should be a map")
			return
		}
		if (this.type == "list" && !(model instanceof List)) {
			vr.fail("$keyword should be a list")
			return
		}
		if (this.type == "map") {
			model.each { k,v ->
				def child = children[k]
				if (child == null) {
					if (children['*'] == null) {
						vr.fail("child '$k' is not valid for node '$keyword'")
						return
					}
					child = children['*']
				}
				child.check(v, vr)
			}
			return
		}
	}
	
	String toString() {
		return this.keyword
	}
}

