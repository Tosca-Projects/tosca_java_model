package model

class Import {

	def model

	Import(model) {
		if ((model instanceof String) || (model instanceof Map)) {
			this.model = model
		}
		throw new Exception("an import definition should be a string or a map")
	}

	String getFile() {
		if (model instanceof String) {
			// Single-line grammar:
			return model
		}
		if (model instanceof Map) {
			// Multi-line grammar:
			if (!model.'file') {
				throw new Exception("a multi-line import definition should have a 'file' keyname")
			}
			return model.'file'
		}
	}
	String getRepository() {
		if (model instanceof Map) {
			return model.'repository'
		}
		return null
	}
	
	String getNamespace_uri() {
		if (model instanceof Map) {
			return model.'namespace_uri'
		}
		return null
	}
	
	String geNamespace_prefix() {
		if (model instanceof Map) {
			return model.'namespace_prefix'
		}
		return null
	}
}
