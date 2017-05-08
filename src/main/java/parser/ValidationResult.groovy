package parser

import java.util.List;
import java.util.Stack;

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
	
	void print() {
		if (OK) {
			println "OK"
		}
		else {
			messages.each {
				println it
			}
		}
	}
}