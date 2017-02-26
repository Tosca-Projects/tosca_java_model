package utils

class Logger {
	
	static print_debug = false
	static print_error = true
	static print_info = false
	static last_errors = []
	
	static void debug(msg) {
		if (print_debug) {
			println "DEBUG $msg"
		}
	}
	
	static void error(msg) {
		if (print_error) {
			println "ERROR $msg"
		}
		last_errors << msg 
	}
	
	static void info(msg) {
		if (print_info) {
			println "INFO $msg"
		}
	}
	
	static boolean gotError(String msg) {
		def found = false
		last_errors.each { String e ->
			if (e.contains(msg)) {
				found = true
			}
		}
		return found
	}
}
